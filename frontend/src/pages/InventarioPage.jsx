import { useState, useEffect } from 'react';
import { inventarioService, productoService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { FaExclamationTriangle, FaBox, FaHistory, FaPlus, FaMinus, FaTimes } from 'react-icons/fa';
import { toast } from 'react-toastify';
import './InventarioPage.css';

function InventarioPage() {
  const [inventario, setInventario] = useState([]);
  const [resumen, setResumen] = useState(null);
  const [loading, setLoading] = useState(true);
  const [filtro, setFiltro] = useState('todos');
  
  // Modal ajuste
  const [mostrarModalAjuste, setMostrarModalAjuste] = useState(false);
  const [itemSeleccionado, setItemSeleccionado] = useState(null);
  const [ajusteData, setAjusteData] = useState({ cantidad: 0, motivo: 'AJUSTE', observaciones: '' });

  const { hasRole } = useAuth();
  const puedeAjustar = hasRole('ADMIN');

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      setLoading(true);
      const [invRes, resumenRes] = await Promise.all([
        inventarioService.getAll(),
        inventarioService.getResumen()
      ]);
      setInventario(invRes.data?.datos || []);
      setResumen(resumenRes.data?.datos);
    } catch (error) {
      toast.error('Error al cargar inventario');
    } finally {
      setLoading(false);
    }
  };

  // Filtrar inventario
  const inventarioFiltrado = inventario.filter(item => {
    if (filtro === 'bajo') return item.stockBajo && item.cantidad > 0;
    if (filtro === 'agotados') return item.stockAgotado || item.cantidad === 0;
    return true;
  });

  // Abrir modal de ajuste
  const abrirModalAjuste = (item) => {
    setItemSeleccionado(item);
    setAjusteData({ cantidad: 0, motivo: 'AJUSTE', observaciones: '' });
    setMostrarModalAjuste(true);
  };

  // Realizar ajuste
  const handleAjuste = async (e) => {
    e.preventDefault();
    if (!ajusteData.cantidad || ajusteData.cantidad === 0) {
      toast.error('Ingrese una cantidad');
      return;
    }

    try {
      await inventarioService.ajuste(itemSeleccionado.id, {
        cantidad: Math.abs(ajusteData.cantidad),
        motivo: ajusteData.motivo,
        observaciones: ajusteData.observaciones
      });
      toast.success('Ajuste realizado');
      setMostrarModalAjuste(false);
      cargarDatos();
    } catch (error) {
      toast.error('Error al realizar ajuste');
    }
  };

  // Inicializar inventario de productos nuevos
  const inicializarProducto = async (productoId) => {
    try {
      await inventarioService.inicializar(productoId);
      toast.success('Inventario inicializado');
      cargarDatos();
    } catch (error) {
      toast.error('Error al inicializar inventario');
    }
  };

  return (
    <div className="inventario-container">
      {/* Resumen */}
      <div className="resumen-grid">
        <div className="resumen-card">
          <FaBox className="icon" />
          <div className="info">
            <span className="valor">{resumen?.totalProductos || 0}</span>
            <span className="label">Total Productos</span>
          </div>
        </div>
        <div className="resumen-card warning">
          <FaExclamationTriangle className="icon" />
          <div className="info">
            <span className="valor">{resumen?.productosStockBajo || 0}</span>
            <span className="label">Stock Bajo</span>
          </div>
        </div>
        <div className="resumen-card danger">
          <FaBox className="icon" />
          <div className="info">
            <span className="valor">{resumen?.productosAgotados || 0}</span>
            <span className="label">Agotados</span>
          </div>
        </div>
      </div>

      {/* Filtros */}
      <div className="filtros-inventario">
        <button 
          className={`filtro-btn ${filtro === 'todos' ? 'active' : ''}`}
          onClick={() => setFiltro('todos')}
        >
          Todos
        </button>
        <button 
          className={`filtro-btn ${filtro === 'bajo' ? 'active' : ''}`}
          onClick={() => setFiltro('bajo')}
        >
          Stock Bajo
        </button>
        <button 
          className={`filtro-btn ${filtro === 'agotados' ? 'active' : ''}`}
          onClick={() => setFiltro('agotados')}
        >
          Agotados
        </button>
      </div>

      {/* Tabla */}
      {loading ? (
        <div className="loading-center">
          <div className="spinner-border text-primary" />
        </div>
      ) : (
        <div className="tabla-container">
          <table className="table table-hover">
            <thead>
              <tr>
                <th>Producto</th>
                <th>Código</th>
                <th>Stock Actual</th>
                <th>Stock Mínimo</th>
                <th>Stock Máximo</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {inventarioFiltrado.map(item => (
                <tr key={item.id} className={
                  item.cantidad === 0 ? 'row-danger' : 
                  item.stockBajo ? 'row-warning' : ''
                }>
                  <td><strong>{item.producto?.nombre}</strong></td>
                  <td><code>{item.producto?.codigo || '-'}</code></td>
                  <td className="stock-actual">{item.cantidad}</td>
                  <td>{item.stockMinimo}</td>
                  <td>{item.stockMaximo}</td>
                  <td>
                    {item.cantidad === 0 ? (
                      <span className="badge bg-danger">Agotado</span>
                    ) : item.stockBajo ? (
                      <span className="badge bg-warning text-dark">Stock Bajo</span>
                    ) : (
                      <span className="badge bg-success">Normal</span>
                    )}
                  </td>
                  <td>
                    {puedeAjustar && (
                      <button 
                        className="btn btn-sm btn-outline-primary"
                        onClick={() => abrirModalAjuste(item)}
                      >
                        Ajustar
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {inventarioFiltrado.length === 0 && (
            <div className="sin-datos">No hay productos en esta categoría</div>
          )}
        </div>
      )}

      {/* Modal Ajuste */}
      {mostrarModalAjuste && (
        <div className="modal-overlay">
          <div className="modal-content modal-ajuste">
            <div className="modal-header">
              <h4>Ajustar Inventario</h4>
              <button onClick={() => setMostrarModalAjuste(false)}><FaTimes /></button>
            </div>
            <form onSubmit={handleAjuste}>
              <div className="modal-body">
                <p className="producto-info">
                  <strong>{itemSeleccionado?.producto?.nombre}</strong>
                  <br />
                  Stock actual: <strong>{itemSeleccionado?.cantidad}</strong>
                </p>

                <div className="mb-3">
                  <label className="form-label">Tipo de ajuste</label>
                  <select 
                    className="form-select"
                    value={ajusteData.motivo}
                    onChange={(e) => setAjusteData({...ajusteData, motivo: e.target.value})}
                  >
                    <option value="AJUSTE">Ajuste (conteo físico)</option>
                    <option value="MERMA">Merma (pérdida)</option>
                    <option value="DEVOLUCION">Devolución</option>
                  </select>
                </div>

                <div className="mb-3">
                  <label className="form-label">Cantidad</label>
                  <div className="cantidad-input">
                    <button 
                      type="button"
                      className="btn btn-outline-secondary"
                      onClick={() => setAjusteData({...ajusteData, cantidad: ajusteData.cantidad - 1})}
                    >
                      <FaMinus />
                    </button>
                    <input
                      type="number"
                      className="form-control"
                      value={ajusteData.cantidad}
                      onChange={(e) => setAjusteData({...ajusteData, cantidad: parseInt(e.target.value) || 0})}
                    />
                    <button 
                      type="button"
                      className="btn btn-outline-secondary"
                      onClick={() => setAjusteData({...ajusteData, cantidad: ajusteData.cantidad + 1})}
                    >
                      <FaPlus />
                    </button>
                  </div>
                  <small className="text-muted">
                    Positivo = suma stock, Negativo = resta stock
                  </small>
                </div>

                <div className="mb-3">
                  <label className="form-label">Observaciones</label>
                  <textarea
                    className="form-control"
                    rows="2"
                    value={ajusteData.observaciones}
                    onChange={(e) => setAjusteData({...ajusteData, observaciones: e.target.value})}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setMostrarModalAjuste(false)}>
                  Cancelar
                </button>
                <button type="submit" className="btn btn-primary">
                  Guardar Ajuste
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default InventarioPage;