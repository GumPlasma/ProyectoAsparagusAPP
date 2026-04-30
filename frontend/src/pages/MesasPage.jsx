import { useState, useEffect, useRef } from 'react';
import { mesaService, productoService } from '../services/api';
import './MesasPage.css';

function MesasPage() {
  const [mesas, setMesas] = useState([]);
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [showPedidoModal, setShowPedidoModal] = useState(false);
  const [showPagoModal, setShowPagoModal] = useState(false);
  const [selectedMesa, setSelectedMesa] = useState(null);
  const [estadisticas, setEstadisticas] = useState({});
  const [formData, setFormData] = useState({ numero: '', capacidad: 4 });
  const [pedidoData, setPedidoData] = useState({ productoId: '', cantidad: 1, notas: '' });
  const [pagoData, setPagoData] = useState({ metodoPago: 'EFECTIVO', montoRecibido: 0, propina: 0, tipoPropina: 'PORCENTAJE' });

  // Drag & Drop state
  const [draggedMesa, setDraggedMesa] = useState(null);
  const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 });
  const dragStartPos = useRef({ x: 0, y: 0 });
  const mesasMapRef = useRef(null);
  // Refs síncronas para evitar problemas de stale state y batching de React
  const draggedMesaRef = useRef(null);
  const draggedPosRef = useRef({ x: 0, y: 0 });
  const hasMovedRef = useRef(false);

  useEffect(() => { cargarDatos(); }, []);

  const cargarDatos = async () => {
    try {
      setLoading(true);
      const [mesasData, productosData] = await Promise.all([
        mesaService.getAll(),
        productoService.getAll()
      ]);
      setMesas(mesasData);
      setProductos(productosData);
      try {
        const stats = await mesaService.getEstadisticas();
        setEstadisticas(stats);
      } catch (e) { setEstadisticas({ libres: 0, ocupadas: 0, reservadas: 0 }); }
    } catch (error) {
      console.error('Error al cargar datos:', error);
    } finally {
      setLoading(false);
    }
  };

  // Calcular posición para nueva mesa (evitar solapamiento)
  const calcularNuevaPosicion = () => {
    const CARD_WIDTH = 140; // 120px + 20px margen
    const CARD_HEIGHT = 140;
    const PADDING = 30;
    const container = mesasMapRef.current;
    const containerWidth = container ? container.clientWidth - PADDING : 800;
    const containerHeight = container ? container.clientHeight - PADDING : 500;

    // Encontrar primera posición disponible
    for (let row = 0; row < Math.floor(containerHeight / CARD_HEIGHT); row++) {
      for (let col = 0; col < Math.floor(containerWidth / CARD_WIDTH); col++) {
        const x = PADDING + col * CARD_WIDTH;
        const y = PADDING + row * CARD_HEIGHT;

        // Verificar si hay solapamiento con mesas existentes
        const haySolapamiento = mesas.some(mesa => {
          const mesaX = mesa.posicionX || 100;
          const mesaY = mesa.posicionY || 100;
          return Math.abs(mesaX - x) < CARD_WIDTH && Math.abs(mesaY - y) < CARD_HEIGHT;
        });

        if (!haySolapamiento) {
          return { posicionX: x, posicionY: y };
        }
      }
    }
    // Si no encuentra espacio, usar posición aleatoria
    return { posicionX: Math.random() * (containerWidth - 120), posicionY: Math.random() * (containerHeight - 120) };
  };

  const handleCrearMesa = async (e) => {
    e.preventDefault();
    try {
      const posicion = calcularNuevaPosicion();
      await mesaService.create({ ...formData, ...posicion });
      setShowModal(false);
      setFormData({ numero: '', capacidad: 4 });
      cargarDatos();
    } catch (error) {
      alert('Error al crear mesa: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleAgregarProducto = async (e) => {
    e.preventDefault();
    try {
      await mesaService.agregarProducto(selectedMesa.id, {
        productoId: parseInt(pedidoData.productoId),
        cantidad: parseInt(pedidoData.cantidad),
        notas: pedidoData.notas
      });
      setPedidoData({ productoId: '', cantidad: 1, notas: '' });
      cargarDatos();
      const mesaActualizada = await mesaService.getById(selectedMesa.id);
      setSelectedMesa(mesaActualizada);
    } catch (error) {
      alert('Error al agregar producto: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleEliminarProducto = async (pedidoId) => {
    try {
      await mesaService.eliminarProducto(selectedMesa.id, pedidoId);
      cargarDatos();
      const mesaActualizada = await mesaService.getById(selectedMesa.id);
      setSelectedMesa(mesaActualizada);
    } catch (error) {
      alert('Error al eliminar producto');
    }
  };

  const handleCalcularPropina = (tipo, valor) => {
    const subtotal = selectedMesa?.totalPedido || 0;
    let propina = tipo === 'PORCENTAJE' ? subtotal * (valor / 100) : valor;
    setPagoData({ ...pagoData, propina, tipoPropina: tipo, valorPropina: valor });
  };

  const handleProcesarPago = async (e) => {
    e.preventDefault();
    try {
      const total = (selectedMesa?.totalPedido || 0) + pagoData.propina;
      if (pagoData.metodoPago === 'EFECTIVO' && pagoData.montoRecibido < total) {
        alert('El monto recibido es insuficiente');
        return;
      }
      await mesaService.pagar(selectedMesa.id, pagoData);
      setShowPagoModal(false);
      setSelectedMesa(null);
      setPagoData({ metodoPago: 'EFECTIVO', montoRecibido: 0, propina: 0, tipoPropina: 'PORCENTAJE' });
      cargarDatos();
      alert('¡Pago procesado correctamente!');
    } catch (error) {
      alert('Error al procesar pago: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleEliminarMesa = async (id) => {
    if (window.confirm('¿Eliminar esta mesa?')) {
      try {
        await mesaService.delete(id);
        cargarDatos();
      } catch (error) {
        alert('Error al eliminar mesa');
      }
    }
  };

  // Funciones de Drag & Drop
  const handleMouseDown = (e, mesa) => {
    if (e.target.closest('.mesa-actions') || e.target.closest('.btn')) return;
    e.preventDefault(); // Prevenir selección de texto durante drag

    const rect = e.currentTarget.getBoundingClientRect();
    setDraggedMesa(mesa);
    draggedMesaRef.current = mesa;
    setDragOffset({
      x: e.clientX - rect.left,
      y: e.clientY - rect.top
    });
    hasMovedRef.current = false;
    dragStartPos.current = { x: e.clientX, y: e.clientY };
    draggedPosRef.current = { x: mesa.posicionX || 100, y: mesa.posicionY || 100 };

    // Agregar listeners globales para capturar mousemove/mouseup fuera del contenedor
    window.addEventListener('mousemove', handleWindowMouseMove);
    window.addEventListener('mouseup', handleWindowMouseUp);
  };

  const handleWindowMouseMove = (e) => {
    if (!draggedMesaRef.current || !mesasMapRef.current) return;

    // Detectar si hubo movimiento significativo (más de 5px)
    const dx = Math.abs(e.clientX - dragStartPos.current.x);
    const dy = Math.abs(e.clientY - dragStartPos.current.y);
    if (dx > 5 || dy > 5) {
      hasMovedRef.current = true;
    }

    const containerRect = mesasMapRef.current.getBoundingClientRect();
    const newX = Math.max(0, Math.min(e.clientX - containerRect.left - dragOffset.x, containerRect.width - 120));
    const newY = Math.max(0, Math.min(e.clientY - containerRect.top - dragOffset.y, containerRect.height - 120));

    // Guardar en ref para tener acceso síncrono a la última posición
    draggedPosRef.current = { x: Math.round(newX), y: Math.round(newY) };

    setMesas(prevMesas =>
      prevMesas.map(m =>
        m.id === draggedMesaRef.current.id
          ? { ...m, posicionX: Math.round(newX), posicionY: Math.round(newY) }
          : m
      )
    );
  };

  const handleWindowMouseUp = async () => {
    // Remover listeners globales
    window.removeEventListener('mousemove', handleWindowMouseMove);
    window.removeEventListener('mouseup', handleWindowMouseUp);

    const mesaArrastrada = draggedMesaRef.current;
    const seMovio = hasMovedRef.current;

    if (mesaArrastrada && seMovio) {
      const { x, y } = draggedPosRef.current;
      try {
        await mesaService.updatePosicion(mesaArrastrada.id, x, y);
        // Recargar mesas para sincronizar estado con el servidor
        const mesasActualizadas = await mesaService.getAll();
        setMesas(mesasActualizadas);
      } catch (error) {
        console.error('Error al guardar posición:', error);
        // En caso de error, recargar para restaurar posiciones del servidor
        cargarDatos();
      }
    }

    setDraggedMesa(null);
    draggedMesaRef.current = null;
    hasMovedRef.current = false;
  };

  const handleMesaClick = async (mesa) => {
    // No hacer click si se estaba arrastrando
    if (hasMovedRef.current) return;

    if (mesa.estado === 'LIBRE') {
      try {
        await mesaService.abrir(mesa.id);
        const mesasActualizadas = await mesaService.getAll();
        setMesas(mesasActualizadas);
        const mesaActualizada = await mesaService.getById(mesa.id);
        setSelectedMesa(mesaActualizada);
        setShowPedidoModal(true);
      } catch (error) {
        alert('Error al abrir mesa: ' + (error.response?.data?.message || error.message));
      }
    } else if (mesa.estado === 'OCUPADA') {
      setSelectedMesa(mesa);
      setShowPedidoModal(true);
    } else if (mesa.estado === 'RESERVADA') {
      try {
        await mesaService.abrir(mesa.id);
        const mesaActualizada = await mesaService.getById(mesa.id);
        setSelectedMesa(mesaActualizada);
        await cargarDatos();
        setShowPedidoModal(true);
      } catch (error) {
        alert('Error al abrir mesa: ' + (error.response?.data?.message || error.message));
      }
    }
  };

  const getEstadoClass = (estado) => ({ 'LIBRE': 'libre', 'OCUPADA': 'ocupada', 'RESERVADA': 'reservada' }[estado] || 'libre');

  const getTotalConPropina = () => (selectedMesa?.totalPedido || 0) + (pagoData.propina || 0);
  const getVuelto = () => pagoData.metodoPago === 'EFECTIVO' ? Math.max(0, pagoData.montoRecibido - getTotalConPropina()) : 0;

  if (loading) return <div className="d-flex justify-content-center align-items-center" style={{ height: '400px' }}><div className="spinner-border text-primary"></div></div>;

  return (
    <div className="mesas-container">
      <div className="mesas-header">
        <h2>🪑 Gestión de Mesas</h2>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>➕ Nueva Mesa</button>
      </div>

      <div className="mesas-stats">
        <div className="stat-card libre"><span className="numero">{estadisticas.libres || 0}</span><span className="label">Libres</span></div>
        <div className="stat-card ocupada"><span className="numero">{estadisticas.ocupadas || 0}</span><span className="label">Ocupadas</span></div>
        <div className="stat-card reservada"><span className="numero">{estadisticas.reservadas || 0}</span><span className="label">Reservadas</span></div>
      </div>

      <div
        className="mesas-map"
        ref={mesasMapRef}
      >
        {mesas.length === 0 ? (
          <div className="text-center text-muted py-5"><h4>No hay mesas</h4><p>Haz clic en "Nueva Mesa"</p></div>
        ) : (
          mesas.map(mesa => (
            <div
              key={mesa.id}
              className={`mesa-card ${getEstadoClass(mesa.estado)} ${draggedMesa?.id === mesa.id ? 'dragging' : ''}`}
              style={{ left: mesa.posicionX || 100, top: mesa.posicionY || 100 }}
              onMouseDown={(e) => handleMouseDown(e, mesa)}
              onClick={() => handleMesaClick(mesa)}
            >
              <div className="mesa-actions"><button className="btn btn-sm btn-danger" onClick={(e) => { e.stopPropagation(); handleEliminarMesa(mesa.id); }}>✕</button></div>
              <div className="mesa-icon">{mesa.estado === 'LIBRE' ? '🪑' : '🍽️'}</div>
              <div className="mesa-numero">Mesa {mesa.numero}</div>
              <div className={`mesa-estado ${getEstadoClass(mesa.estado)}`}>{mesa.estado}</div>
              {mesa.totalPedido > 0 && <div className="mesa-total">S/. {mesa.totalPedido?.toFixed(2)}</div>}
              <div className="mesa-drag-hint">⋮⋮</div>
            </div>
          ))
        )}
      </div>

      {/* Modal Nueva Mesa */}
      {showModal && (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog">
            <div className="modal-content">
              <div className="modal-header bg-primary text-white">
                <h5 className="modal-title">➕ Nueva Mesa</h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => setShowModal(false)}></button>
              </div>
              <form onSubmit={handleCrearMesa}>
                <div className="modal-body">
                  <div className="mb-3"><label className="form-label">Número *</label><input type="number" className="form-control" value={formData.numero} onChange={(e) => setFormData({ ...formData, numero: e.target.value })} required min="1" /></div>
                  <div className="mb-3"><label className="form-label">Capacidad *</label><input type="number" className="form-control" value={formData.capacidad} onChange={(e) => setFormData({ ...formData, capacidad: e.target.value })} required min="1" /></div>
                </div>
                <div className="modal-footer"><button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancelar</button><button type="submit" className="btn btn-primary">Guardar</button></div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* Modal Pedido */}
      {showPedidoModal && selectedMesa && (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content">
              <div className="modal-header bg-info text-white">
                <h5 className="modal-title">🍽️ Mesa {selectedMesa.numero}</h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => { setShowPedidoModal(false); setSelectedMesa(null); }}></button>
              </div>
              <div className="modal-body">
                <div className="pedido-header"><h6>Productos</h6><div className="pedido-total">Total: S/. {selectedMesa.totalPedido?.toFixed(2) || '0.00'}</div></div>
                <div className="productos-lista">
                  {selectedMesa.pedidos?.map((p, i) => (
                    <div key={i} className="producto-item">
                      <div className="producto-info"><div className="producto-nombre">{p.productoNombre}</div><div className="producto-cantidad">Cant: {p.cantidad}</div></div>
                      <div className="producto-subtotal">S/. {p.subtotal?.toFixed(2)}</div>
                      <button className="btn btn-sm btn-outline-danger" onClick={() => handleEliminarProducto(p.id)}>🗑️</button>
                    </div>
                  ))}
                </div>
                <div className="agregar-producto-section">
                  <h6>➕ Agregar</h6>
                  <form onSubmit={handleAgregarProducto} className="row g-2">
                    <div className="col-md-6"><select className="form-select" value={pedidoData.productoId} onChange={(e) => setPedidoData({ ...pedidoData, productoId: e.target.value })} required><option value="">Producto...</option>{productos.map(p => <option key={p.id} value={p.id}>{p.nombre} - S/.{p.precio?.toFixed(2)}</option>)}</select></div>
                    <div className="col-md-2"><input type="number" className="form-control" value={pedidoData.cantidad} onChange={(e) => setPedidoData({ ...pedidoData, cantidad: e.target.value })} min="1" required /></div>
                    <div className="col-md-4"><button type="submit" className="btn btn-success w-100">Agregar</button></div>
                  </form>
                </div>
                <div className="propina-section">
                  <h6>💰 Propina</h6>
                  <div className="propina-opciones">{[5, 10, 15, 18, 20].map(pct => <button key={pct} type="button" className="propina-btn" onClick={() => handleCalcularPropina('PORCENTAJE', pct)}>{pct}%</button>)}</div>
                  <div className="propina-input"><span>Monto fijo:</span><input type="number" className="form-control" onChange={(e) => handleCalcularPropina('FIJO', parseFloat(e.target.value) || 0)} /></div>
                  <div className="propina-monto">Propina: S/. {pagoData.propina.toFixed(2)}</div>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => { setShowPedidoModal(false); setSelectedMesa(null); }}>Cerrar</button>
                <button type="button" className="btn btn-success" onClick={() => { setShowPedidoModal(false); setShowPagoModal(true); }} disabled={!selectedMesa.pedidos || selectedMesa.pedidos.length === 0}>💳 Pagar</button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Modal Pago */}
      {showPagoModal && selectedMesa && (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog">
            <div className="modal-content">
              <div className="modal-header bg-success text-white">
                <h5 className="modal-title">💳 Pago Mesa {selectedMesa.numero}</h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => { setShowPagoModal(false); setSelectedMesa(null); }}></button>
              </div>
              <form onSubmit={handleProcesarPago}>
                <div className="modal-body">
                  <div className="pago-resumen">
                    <div className="pago-linea"><span>Subtotal:</span><span>S/. {selectedMesa.totalPedido?.toFixed(2)}</span></div>
                    <div className="pago-linea propina"><span>Propina:</span><span>S/. {pagoData.propina.toFixed(2)}</span></div>
                    <div className="pago-linea total"><span>TOTAL:</span><span>S/. {getTotalConPropina().toFixed(2)}</span></div>
                  </div>
                  <div className="mb-3 mt-3">
                    <label className="form-label">Método de Pago</label>
                    <div className="metodos-pago">
                      {[{ v: 'EFECTIVO', l: '💵 Efectivo' }, { v: 'TARJETA', l: '💳 Tarjeta' }, { v: 'TRANSFERENCIA', l: '📱 Transferencia' }].map(m => (
                        <button key={m.v} type="button" className={`metodo-pago-btn ${pagoData.metodoPago === m.v ? 'active' : ''}`} onClick={() => setPagoData({ ...pagoData, metodoPago: m.v })}>{m.l}</button>
                      ))}
                    </div>
                  </div>
                  {pagoData.metodoPago === 'EFECTIVO' && (
                    <div className="pago-efectivo-section">
                      <div className="mb-3"><label className="form-label">Monto Recibido</label><input type="number" className="form-control" value={pagoData.montoRecibido} onChange={(e) => setPagoData({ ...pagoData, montoRecibido: parseFloat(e.target.value) || 0 })} min={getTotalConPropina()} required /></div>
                      {pagoData.montoRecibido >= getTotalConPropina() && <div className="vuelto-info"><span>Vuelto:</span><span className="vuelto-monto">S/. {getVuelto().toFixed(2)}</span></div>}
                    </div>
                  )}
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => { setShowPagoModal(false); setShowPedidoModal(true); }}>Volver</button>
                  <button type="submit" className="btn btn-success">✅ Confirmar</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default MesasPage;