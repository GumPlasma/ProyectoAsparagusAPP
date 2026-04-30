import { useState, useEffect, useCallback } from 'react';
import { ventaService } from '../services/api';
import './VentasPage.css';

function VentasPage() {
  const [ventas, setVentas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedVenta, setSelectedVenta] = useState(null);
  const [showDetail, setShowDetail] = useState(false);
  const [filtros, setFiltros] = useState({ fechaInicio: '', fechaFin: '', metodoPago: '', mesaNumero: '' });
  const [estadisticas, setEstadisticas] = useState({ totalVentas: 0, totalPropinas: 0, cantidadVentas: 0, promedioVenta: 0 });

  const calcularEstadisticas = (ventasData) => {
    const totalVentas = ventasData.reduce((sum, v) => sum + (parseFloat(v.total) || 0), 0);
    const totalPropinas = ventasData.reduce((sum, v) => sum + (parseFloat(v.propina) || 0), 0);
    const cantidad = ventasData.length;
    setEstadisticas({ totalVentas, totalPropinas, cantidadVentas: cantidad, promedioVenta: cantidad > 0 ? totalVentas / cantidad : 0 });
  };

  const cargarVentas = useCallback(async () => {
    try {
      setLoading(true);
      const data = await ventaService.getAll();
      setVentas(data);
      calcularEstadisticas(data);
    } catch (error) {
      console.error('Error al cargar ventas:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { cargarVentas(); }, [cargarVentas]);

  const buscarConFiltros = async () => {
    try {
      setLoading(true);
      const params = new URLSearchParams();
      if (filtros.fechaInicio) params.append('fechaInicio', filtros.fechaInicio);
      if (filtros.fechaFin) params.append('fechaFin', filtros.fechaFin);
      if (filtros.metodoPago) params.append('metodoPago', filtros.metodoPago);
      if (filtros.mesaNumero) params.append('mesaNumero', filtros.mesaNumero);
      const data = await ventaService.buscar(params.toString());
      setVentas(data);
      calcularEstadisticas(data);
    } catch (error) {
      console.error('Error al buscar:', error);
    } finally {
      setLoading(false);
    }
  };

  const limpiarFiltros = () => {
    setFiltros({ fechaInicio: '', fechaFin: '', metodoPago: '', mesaNumero: '' });
    cargarVentas();
  };

  const verDetalle = async (id) => {
    try {
      const venta = await ventaService.getById(id);
      setSelectedVenta(venta);
      setShowDetail(true);
    } catch (error) {
      alert('Error al obtener detalle');
    }
  };

  const formatearFecha = (fechaHora) => {
    if (!fechaHora) return '-';
    const fecha = new Date(fechaHora);
    return fecha.toLocaleString('es-PE', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  };

  const getMetodoPagoIcon = (metodo) => ({ 'EFECTIVO': '💵', 'TARJETA': '💳', 'TRANSFERENCIA': '📱' }[metodo] || '💰');
  const getMetodoPagoClass = (metodo) => ({ 'EFECTIVO': 'metodo-efectivo', 'TARJETA': 'metodo-tarjeta', 'TRANSFERENCIA': 'metodo-transferencia' }[metodo] || '');

  if (loading) {
    return <div className="d-flex justify-content-center align-items-center" style={{ height: '400px' }}><div className="spinner-border text-primary"></div></div>;
  }

  return (
    <div className="ventas-container">
      <div className="ventas-header"><h2>📊 Historial de Ventas</h2></div>

      <div className="estadisticas-grid">
        <div className="stat-card stat-total"><div className="stat-icon">💰</div><div className="stat-info"><span className="stat-value">S/. {estadisticas.totalVentas.toFixed(2)}</span><span className="stat-label">Total Ventas</span></div></div>
        <div className="stat-card stat-propinas"><div className="stat-icon">🎁</div><div className="stat-info"><span className="stat-value">S/. {estadisticas.totalPropinas.toFixed(2)}</span><span className="stat-label">Total Propinas</span></div></div>
        <div className="stat-card stat-cantidad"><div className="stat-icon">🧾</div><div className="stat-info"><span className="stat-value">{estadisticas.cantidadVentas}</span><span className="stat-label">Transacciones</span></div></div>
        <div className="stat-card stat-promedio"><div className="stat-icon">📈</div><div className="stat-info"><span className="stat-value">S/. {estadisticas.promedioVenta.toFixed(2)}</span><span className="stat-label">Promedio</span></div></div>
      </div>

      <div className="filtros-section">
        <h5>🔍 Filtros de Búsqueda</h5>
        <div className="filtros-grid">
          <div className="filtro-item"><label>Fecha Inicio</label><input type="date" className="form-control" value={filtros.fechaInicio} onChange={(e) => setFiltros({ ...filtros, fechaInicio: e.target.value })} /></div>
          <div className="filtro-item"><label>Fecha Fin</label><input type="date" className="form-control" value={filtros.fechaFin} onChange={(e) => setFiltros({ ...filtros, fechaFin: e.target.value })} /></div>
          <div className="filtro-item"><label>Método de Pago</label><select className="form-select" value={filtros.metodoPago} onChange={(e) => setFiltros({ ...filtros, metodoPago: e.target.value })}><option value="">Todos</option><option value="EFECTIVO">Efectivo</option><option value="TARJETA">Tarjeta</option><option value="TRANSFERENCIA">Transferencia</option></select></div>
          <div className="filtro-item"><label>N° Mesa</label><input type="number" className="form-control" placeholder="Todas" value={filtros.mesaNumero} onChange={(e) => setFiltros({ ...filtros, mesaNumero: e.target.value })} /></div>
          <div className="filtro-item filtro-buttons"><button className="btn btn-primary" onClick={buscarConFiltros}>🔍 Buscar</button><button className="btn btn-secondary" onClick={limpiarFiltros}>🔄 Limpiar</button></div>
        </div>
      </div>

      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="table-dark">
            <tr><th>ID</th><th>Fecha/Hora</th><th>Mesa</th><th>Subtotal</th><th>Propina</th><th>Total</th><th>Método</th><th>Acciones</th></tr>
          </thead>
          <tbody>
            {ventas.length === 0 ? (
              <tr><td colSpan="8" className="text-center text-muted py-4">No se encontraron ventas</td></tr>
            ) : (
              ventas.map(venta => (
                <tr key={venta.id}>
                  <td><strong>#{venta.id}</strong></td>
                  <td>{formatearFecha(venta.fecha || venta.hora)}</td>
                  <td>{venta.mesaNumero ? <span className="badge bg-info">Mesa {venta.mesaNumero}</span> : <span className="badge bg-secondary">Sin mesa</span>}</td>
                  <td>S/. {venta.subtotal?.toFixed(2)}</td>
                  <td className="text-success">{venta.propina > 0 ? `S/. ${venta.propina?.toFixed(2)}` : '-'}</td>
                  <td><strong>S/. {venta.total?.toFixed(2)}</strong></td>
                  <td><span className={`metodo-pago ${getMetodoPagoClass(venta.metodoPago)}`}>{getMetodoPagoIcon(venta.metodoPago)} {venta.metodoPago}</span></td>
                  <td><button className="btn btn-sm btn-outline-primary" onClick={() => verDetalle(venta.id)}>👁️ Ver</button></td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showDetail && selectedVenta && (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content">
              <div className="modal-header bg-dark text-white">
                <h5 className="modal-title">🧾 Detalle de Venta #{selectedVenta.id}</h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => setShowDetail(false)}></button>
              </div>
              <div className="modal-body">
                <div className="venta-info">
                  <div className="info-row"><span>📅 Fecha:</span><strong>{formatearFecha(selectedVenta.fecha || selectedVenta.hora)}</strong></div>
                  <div className="info-row"><span>🪑 Mesa:</span><strong>{selectedVenta.mesaNumero || 'Sin mesa'}</strong></div>
                  <div className="info-row"><span>💳 Método:</span><strong>{getMetodoPagoIcon(selectedVenta.metodoPago)} {selectedVenta.metodoPago}</strong></div>
                </div>
                <h6 className="mt-4 mb-3">📋 Productos</h6>
                <table className="table table-sm">
                  <thead className="table-light"><tr><th>Producto</th><th className="text-center">Cant.</th><th className="text-end">Precio</th><th className="text-end">Subtotal</th></tr></thead>
                  <tbody>
                    {selectedVenta.detalles?.map((d, i) => (
                      <tr key={i}><td>{d.producto?.nombre || 'Producto'}</td><td className="text-center">{d.cantidad}</td><td className="text-end">S/. {d.precioUnitario?.toFixed(2)}</td><td className="text-end">S/. {d.subtotal?.toFixed(2)}</td></tr>
                    ))}
                  </tbody>
                </table>
                <div className="venta-totales">
                  <div className="total-row"><span>Subtotal:</span><span>S/. {selectedVenta.subtotal?.toFixed(2)}</span></div>
                  <div className="total-row propina"><span>Propina:</span><span>S/. {selectedVenta.propina?.toFixed(2) || '0.00'}</span></div>
                  <div className="total-row total-final"><span>TOTAL:</span><span>S/. {selectedVenta.total?.toFixed(2)}</span></div>
                  {selectedVenta.metodoPago === 'EFECTIVO' && (
                    <><div className="total-row"><span>Monto Recibido:</span><span>S/. {selectedVenta.montoRecibido?.toFixed(2)}</span></div><div className="total-row cambio"><span>Cambio:</span><span>S/. {selectedVenta.vuelto?.toFixed(2)}</span></div></>
                  )}
                </div>
              </div>
              <div className="modal-footer"><button className="btn btn-secondary" onClick={() => setShowDetail(false)}>Cerrar</button></div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default VentasPage;