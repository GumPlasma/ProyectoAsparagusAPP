import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { ventaService, inventarioService, productoService } from '../services/api';
import { 
  FaDollarSign, 
  FaShoppingCart, 
  FaBox, 
  FaExclamationTriangle,
  FaArrowRight,
  FaUtensils,
  FaUsers
} from 'react-icons/fa';
import './DashboardPage.css';

function DashboardPage() {
  const [loading, setLoading] = useState(true);
  const [resumenVentas, setResumenVentas] = useState(null);
  const [stockBajo, setStockBajo] = useState([]);
  const [productosCount, setProductosCount] = useState(0);
  
  const { user } = useAuth();
  const navigate = useNavigate();

  const today = new Date().toISOString().split('T')[0];

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      setLoading(true);
      
      const today = new Date().toISOString().split('T')[0];
      
      const [ventasRes, stockRes, productosRes] = await Promise.all([
        ventaService.getResumen(today).catch(() => ({ totalVentas: 0, montoTotal: 0 })),
        inventarioService.getStockBajo().catch(() => ({ datos: [] })),
        productoService.getDisponibles().catch(() => ({ datos: [] })),
      ]);
      
      setResumenVentas(ventasRes);
      setStockBajo(stockRes.datos || []);
      setProductosCount(productosRes.datos?.length || 0);
      
    } catch (error) {
      console.error('Error al cargar datos:', error);
    } finally {
      setLoading(false);
    }
  };

  const statsCards = [
    {
      title: 'Ventas Hoy',
      value: resumenVentas?.totalVentas || 0,
      subtitle: 'transacciones',
      icon: <FaShoppingCart />,
      color: 'primary',
      onClick: () => navigate('/ventas')
    },
    {
      title: 'Monto Total',
      value: `$${(resumenVentas?.montoTotal || 0).toFixed(2)}`,
      subtitle: 'ingresos del día',
      icon: <FaDollarSign />,
      color: 'success',
      onClick: () => navigate('/ventas')
    },
    {
      title: 'Productos',
      value: productosCount,
      subtitle: 'disponibles',
      icon: <FaUtensils />,
      color: 'info',
      onClick: () => navigate('/productos')
    },
    {
      title: 'Alertas',
      value: stockBajo.length,
      subtitle: 'stock bajo',
      icon: <FaExclamationTriangle />,
      color: 'warning',
      onClick: () => navigate('/inventario')
    },
  ];

  if (loading) {
    return (
      <div className="dashboard-loading">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      {/* Encabezado de bienvenida */}
      <div className="dashboard-header">
        <h2>¡Bienvenido, {user?.nombre || 'Usuario'}!</h2>
        <p className="text-muted">
          Resumen del día {new Date().toLocaleDateString('es-ES', { 
            weekday: 'long', 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
          })}
        </p>
      </div>

      {/* Tarjetas de estadísticas */}
      <div className="stats-grid">
        {statsCards.map((stat, index) => (
          <div 
            key={index}
            className={`stat-card stat-card-${stat.color}`}
            onClick={stat.onClick}
          >
            <div className="stat-icon">
              {stat.icon}
            </div>
            <div className="stat-info">
              <h3>{stat.value}</h3>
              <p className="stat-title">{stat.title}</p>
              <small>{stat.subtitle}</small>
            </div>
          </div>
        ))}
      </div>

      {/* Accesos rápidos */}
      <div className="quick-actions">
        <h4>Acciones Rápidas</h4>
        <div className="actions-grid">
          {user?.rol?.nombre !== 'AUDITOR' && (
            <button 
              className="action-btn action-btn-primary"
              onClick={() => navigate('/pos')}
            >
              <FaShoppingCart />
              <span>Nueva Venta</span>
              <FaArrowRight className="arrow" />
            </button>
          )}
          
          {user?.rol?.nombre === 'ADMIN' && (
            <button 
              className="action-btn action-btn-info"
              onClick={() => navigate('/productos/nuevo')}
            >
              <FaUtensils />
              <span>Nuevo Producto</span>
              <FaArrowRight className="arrow" />
            </button>
          )}
          
          <button 
            className="action-btn action-btn-success"
            onClick={() => navigate('/clientes')}
          >
            <FaUsers />
            <span>Clientes</span>
            <FaArrowRight className="arrow" />
          </button>
        </div>
      </div>

      {/* Alertas de stock bajo */}
      {stockBajo.length > 0 && (
        <div className="stock-alerts">
          <h4>
            <FaExclamationTriangle className="warning-icon" />
            Productos con Stock Bajo
          </h4>
          <div className="alerts-list">
            {stockBajo.slice(0, 5).map((item, index) => (
              <div key={index} className="alert-item">
                <div className="alert-info">
                  <span className="product-name">{item.nombre}</span>
                  <span className="stock-level">
                    Stock: {item.stockActual} / Mín: {item.stockMinimo}
                  </span>
                </div>
                <span className={`badge ${item.stockActual === 0 ? 'bg-danger' : 'bg-warning'}`}>
                  {item.mensaje}
                </span>
              </div>
            ))}
          </div>
          {stockBajo.length > 5 && (
            <button 
              className="btn btn-outline-warning w-100 mt-3"
              onClick={() => navigate('/inventario')}
            >
              Ver todos ({stockBajo.length} productos)
            </button>
          )}
        </div>
      )}
    </div>
  );
}

export default DashboardPage;