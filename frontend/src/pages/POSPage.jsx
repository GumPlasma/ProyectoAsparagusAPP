import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { productoService, ventaService, clienteService } from '../services/api';
import { 
  FaSearch, 
  FaPlus, 
  FaMinus, 
  FaTrash, 
  FaShoppingCart,
  FaUser,
  FaCreditCard,
  FaMoneyBillWave,
  FaTimes
} from 'react-icons/fa';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './POSPage.css';

function POSPage() {
  // Productos y categorías
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState(null);
  const [busqueda, setBusqueda] = useState('');
  
  // Carrito
  const [carrito, setCarrito] = useState([]);
  
  // Cliente
  const [clienteSeleccionado, setClienteSeleccionado] = useState(null);
  const [buscandoCliente, setBuscandoCliente] = useState(false);
  const [busquedaCliente, setBusquedaCliente] = useState('');
  const [clientes, setClientes] = useState([]);
  
  // Venta
  const [metodoPago, setMetodoPago] = useState('EFECTIVO');
  const [montoRecibido, setMontoRecibido] = useState('');
  const [procesando, setProcesando] = useState(false);
  
  // UI
  const [loading, setLoading] = useState(true);
  const [mostrarModalCliente, setMostrarModalCliente] = useState(false);
  const [mostrarModalPago, setMostrarModalPago] = useState(false);
  
  const { user } = useAuth();

  useEffect(() => {
    cargarDatosIniciales();
  }, []);

  const cargarDatosIniciales = async () => {
    try {
      setLoading(true);
      const [productosRes, categoriasRes] = await Promise.all([
        productoService.getDisponibles(),
        productoService.getCategorias()
      ]);
      
      setProductos(productosRes.data?.datos || []);
      setCategorias(categoriasRes.data?.datos || []);
    } catch (error) {
      console.error('Error al cargar datos:', error);
      toast.error('Error al cargar productos');
    } finally {
      setLoading(false);
    }
  };

  // FUNCIONES DEL CARRITO
  const agregarAlCarrito = (producto) => {
    setCarrito(prevCarrito => {
      const existe = prevCarrito.find(item => item.productoId === producto.id);
      
      if (existe) {
        return prevCarrito.map(item =>
          item.productoId === producto.id
            ? { ...item, cantidad: item.cantidad + 1, subtotal: (item.cantidad + 1) * item.precioUnitario }
            : item
        );
      } else {
        return [...prevCarrito, {
          productoId: producto.id,
          nombre: producto.nombre,
          precioUnitario: producto.precio,
          cantidad: 1,
          subtotal: producto.precio
        }];
      }
    });
  };

  const cambiarCantidad = (productoId, delta) => {
    setCarrito(prevCarrito => {
      return prevCarrito.map(item => {
        if (item.productoId === productoId) {
          const nuevaCantidad = item.cantidad + delta;
          if (nuevaCantidad <= 0) return null;
          return {
            ...item,
            cantidad: nuevaCantidad,
            subtotal: nuevaCantidad * item.precioUnitario
          };
        }
        return item;
      }).filter(Boolean);
    });
  };

  const eliminarDelCarrito = (productoId) => {
    setCarrito(prevCarrito => prevCarrito.filter(item => item.productoId !== productoId));
  };

  const vaciarCarrito = () => {
    setCarrito([]);
    setClienteSeleccionado(null);
  };

  // CÁLCULOS
  const calcularSubtotal = () => {
    return carrito.reduce((total, item) => total + item.subtotal, 0);
  };

  const calcularImpuesto = () => {
    return calcularSubtotal() * 0.18;
  };

  const calcularTotal = () => {
    return calcularSubtotal() * 1.18;
  };

  const calcularVuelto = () => {
    const recibido = parseFloat(montoRecibido) || 0;
    return recibido - calcularTotal();
  };

  // FUNCIONES DE CLIENTE
  const buscarClientes = async () => {
    if (!busquedaCliente.trim()) return;
    
    try {
      setBuscandoCliente(true);
      const response = await clienteService.buscar(busquedaCliente);
      setClientes(response.data?.datos || []);
    } catch (error) {
      console.error('Error al buscar clientes:', error);
    } finally {
      setBuscandoCliente(false);
    }
  };

  const seleccionarCliente = (cliente) => {
    setClienteSeleccionado(cliente);
    setMostrarModalCliente(false);
    setBusquedaCliente('');
    setClientes([]);
  };

  // PROCESAR VENTA
  const procesarVenta = async () => {
    if (carrito.length === 0) {
      toast.error('El carrito está vacío');
      return;
    }

    if (metodoPago === 'EFECTIVO') {
      const recibido = parseFloat(montoRecibido) || 0;
      if (recibido < calcularTotal()) {
        toast.error('El monto recibido es insuficiente');
        return;
      }
    }

    try {
      setProcesando(true);
      
      const ventaData = {
        vendedorId: user.id,
        clienteId: clienteSeleccionado?.id || null,
        tipoVenta: 'LLEVAR',
        tipoComprobante: 'TICKET',
        metodoPago: metodoPago,
        montoRecibido: parseFloat(montoRecibido) || 0,
        detalles: carrito.map(item => ({
          productoId: item.productoId,
          cantidad: item.cantidad,
          descuento: 0,
          notas: ''
        }))
      };

      const response = await ventaService.create(ventaData);
      
      if (response.data?.exitoso) {
        toast.success('¡Venta procesada exitosamente!');
        console.log('Venta creada:', response.data.datos);
        vaciarCarrito();
        setMontoRecibido('');
        setMostrarModalPago(false);
      } else {
        toast.error(response.data?.mensaje || 'Error al procesar la venta');
      }
    } catch (error) {
      console.error('Error al procesar venta:', error);
      toast.error('Error al procesar la venta');
    } finally {
      setProcesando(false);
    }
  };

  // PRODUCTOS FILTRADOS
  const productosFiltrados = productos.filter(producto => {
    const coincideBusqueda = producto.nombre.toLowerCase().includes(busqueda.toLowerCase());
    const coincideCategoria = !categoriaSeleccionada || producto.categoria?.id === categoriaSeleccionada;
    return coincideBusqueda && coincideCategoria;
  });

  if (loading) {
    return (
      <div className="pos-loading">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="pos-container">
      {/* PANEL IZQUIERDO - PRODUCTOS */}
      <div className="pos-productos">
        {/* Barra de búsqueda */}
        <div className="pos-search">
          <FaSearch className="search-icon" />
          <input
            type="text"
            placeholder="Buscar producto..."
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
          />
        </div>

        {/* Categorías */}
        <div className="pos-categorias">
          <button
            className={`categoria-btn ${!categoriaSeleccionada ? 'active' : ''}`}
            onClick={() => setCategoriaSeleccionada(null)}
          >
            Todos
          </button>
          {categorias.map(cat => (
            <button
              key={cat.id}
              className={`categoria-btn ${categoriaSeleccionada === cat.id ? 'active' : ''}`}
              onClick={() => setCategoriaSeleccionada(cat.id)}
            >
              {cat.nombre}
            </button>
          ))}
        </div>

        {/* Grid de productos */}
        <div className="pos-productos-grid">
          {productosFiltrados.map(producto => (
            <div
              key={producto.id}
              className="producto-card"
              onClick={() => agregarAlCarrito(producto)}
            >
              <div className="producto-imagen">
                {producto.imagenUrl ? (
                  <img src={producto.imagenUrl} alt={producto.nombre} />
                ) : (
                  <div className="producto-placeholder">🍽️</div>
                )}
              </div>
              <div className="producto-info">
                <h5>{producto.nombre}</h5>
                <span className="producto-precio">
                  ${producto.precio?.toFixed(2)}
                </span>
              </div>
            </div>
          ))}
        </div>

        {productosFiltrados.length === 0 && (
          <div className="sin-productos">
            <p>No se encontraron productos</p>
          </div>
        )}
      </div>

      {/* PANEL DERECHO - CARRITO */}
      <div className="pos-carrito">
        <div className="carrito-header">
          <h3>
            <FaShoppingCart /> Ticket
          </h3>
          {carrito.length > 0 && (
            <button className="btn-vaciar" onClick={vaciarCarrito}>
              <FaTimes /> Vaciar
            </button>
          )}
        </div>

        {/* Cliente */}
        <div className="carrito-cliente">
          {clienteSeleccionado ? (
            <div className="cliente-info">
              <FaUser />
              <span>{clienteSeleccionado.nombreCompleto}</span>
              <button 
                className="btn-remove-cliente"
                onClick={() => setClienteSeleccionado(null)}
              >
                <FaTimes />
              </button>
            </div>
          ) : (
            <button 
              className="btn-agregar-cliente"
              onClick={() => setMostrarModalCliente(true)}
            >
              <FaUser /> Agregar cliente (opcional)
            </button>
          )}
        </div>

        {/* Lista del carrito */}
        <div className="carrito-lista">
          {carrito.length === 0 ? (
            <div className="carrito-vacio">
              <FaShoppingCart />
              <p>Carrito vacío</p>
              <small>Selecciona productos para agregar</small>
            </div>
          ) : (
            carrito.map(item => (
              <div key={item.productoId} className="carrito-item">
                <div className="item-info">
                  <span className="item-nombre">{item.nombre}</span>
                  <span className="item-precio">${item.precioUnitario?.toFixed(2)}</span>
                </div>
                <div className="item-controles">
                  <button onClick={() => cambiarCantidad(item.productoId, -1)}>
                    <FaMinus />
                  </button>
                  <span className="item-cantidad">{item.cantidad}</span>
                  <button onClick={() => cambiarCantidad(item.productoId, 1)}>
                    <FaPlus />
                  </button>
                  <button 
                    className="btn-eliminar"
                    onClick={() => eliminarDelCarrito(item.productoId)}
                  >
                    <FaTrash />
                  </button>
                </div>
                <div className="item-subtotal">
                  ${item.subtotal?.toFixed(2)}
                </div>
              </div>
            ))
          )}
        </div>

        {/* Totales */}
        <div className="carrito-totales">
          <div className="total-row">
            <span>Subtotal:</span>
            <span>${calcularSubtotal().toFixed(2)}</span>
          </div>
          <div className="total-row">
            <span>IGV (18%):</span>
            <span>${calcularImpuesto().toFixed(2)}</span>
          </div>
          <div className="total-row total-final">
            <span>TOTAL:</span>
            <span>${calcularTotal().toFixed(2)}</span>
          </div>
        </div>

        {/* Botón cobrar */}
        <button
          className="btn-cobrar"
          disabled={carrito.length === 0}
          onClick={() => setMostrarModalPago(true)}
        >
          COBRAR ${calcularTotal().toFixed(2)}
        </button>
      </div>

      {/* MODAL SELECCIONAR CLIENTE */}
      {mostrarModalCliente && (
        <div className="modal-overlay">
          <div className="modal-content modal-cliente">
            <div className="modal-header">
              <h4>Buscar Cliente</h4>
              <button onClick={() => setMostrarModalCliente(false)}>
                <FaTimes />
              </button>
            </div>
            <div className="modal-body">
              <div className="busqueda-cliente">
                <input
                  type="text"
                  placeholder="Nombre o documento..."
                  value={busquedaCliente}
                  onChange={(e) => setBusquedaCliente(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && buscarClientes()}
                />
                <button onClick={buscarClientes}>Buscar</button>
              </div>
              
              <div className="lista-clientes">
                {buscandoCliente ? (
                  <div className="loading-clientes">Buscando...</div>
                ) : clientes.length > 0 ? (
                  clientes.map(cliente => (
                    <div
                      key={cliente.id}
                      className="cliente-option"
                      onClick={() => seleccionarCliente(cliente)}
                    >
                      <FaUser />
                      <div>
                        <strong>{cliente.nombreCompleto}</strong>
                        <small>{cliente.telefono || 'Sin teléfono'}</small>
                      </div>
                    </div>
                  ))
                ) : busquedaCliente && (
                  <div className="sin-resultados">No se encontraron clientes</div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* MODAL PAGO */}
      {mostrarModalPago && (
        <div className="modal-overlay">
          <div className="modal-content modal-pago">
            <div className="modal-header">
              <h4>Procesar Pago</h4>
              <button onClick={() => setMostrarModalPago(false)}>
                <FaTimes />
              </button>
            </div>
            <div className="modal-body">
              <div className="pago-total">
                <span>Total a pagar:</span>
                <strong>${calcularTotal().toFixed(2)}</strong>
              </div>

              <div className="pago-metodos">
                <h5>Método de pago</h5>
                <div className="metodos-grid">
                  <button
                    className={`metodo-btn ${metodoPago === 'EFECTIVO' ? 'active' : ''}`}
                    onClick={() => setMetodoPago('EFECTIVO')}
                  >
                    <FaMoneyBillWave />
                    Efectivo
                  </button>
                  <button
                    className={`metodo-btn ${metodoPago === 'TARJETA' ? 'active' : ''}`}
                    onClick={() => setMetodoPago('TARJETA')}
                  >
                    <FaCreditCard />
                    Tarjeta
                  </button>
                </div>
              </div>

              {metodoPago === 'EFECTIVO' && (
                <div className="pago-efectivo">
                  <label>Monto recibido:</label>
                  <input
                    type="number"
                    value={montoRecibido}
                    onChange={(e) => setMontoRecibido(e.target.value)}
                    placeholder="0.00"
                  />
                  {parseFloat(montoRecibido) >= calcularTotal() && (
                    <div className="vuelto">
                      <span>Vuelto:</span>
                      <strong>${calcularVuelto().toFixed(2)}</strong>
                    </div>
                  )}
                </div>
              )}

              <button
                className="btn-procesar"
                onClick={procesarVenta}
                disabled={procesando || (metodoPago === 'EFECTIVO' && parseFloat(montoRecibido) < calcularTotal())}
              >
                {procesando ? 'Procesando...' : 'Confirmar Pago'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default POSPage;