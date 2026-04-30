import { useState, useEffect } from 'react';
import { productoService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { 
  FaPlus, 
  FaEdit, 
  FaTrash, 
  FaSearch,
  FaFilter,
  FaTimes
} from 'react-icons/fa';
import { toast } from 'react-toastify';
import './ProductosPage.css';

function ProductosPage() {
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busqueda, setBusqueda] = useState('');
  const [categoriaFiltro, setCategoriaFiltro] = useState('');
  
  // Modal
  const [mostrarModal, setMostrarModal] = useState(false);
  const [productoEditar, setProductoEditar] = useState(null);
  
  // Formulario
  const [formData, setFormData] = useState({
    nombre: '',
    codigo: '',
    descripcion: '',
    precio: '',
    costo: '',
    imagenUrl: '',
    categoriaId: '',
    disponible: true,
    requierePreparacion: true,
    tiempoPreparacion: ''
  });
  
  // Modal categorías
  const [mostrarModalCategorias, setMostrarModalCategorias] = useState(false);
  const [nuevaCategoria, setNuevaCategoria] = useState({ nombre: '', descripcion: '', imagenUrl: '' });
  
  const { hasRole } = useAuth();
  const puedeEditar = hasRole('ADMIN');

  useEffect(() => {
    cargarProductos();
    cargarCategorias();
  }, []);

  const cargarProductos = async () => {
    try {
      setLoading(true);
      const response = await productoService.getAll();
      setProductos(response || []);
    } catch (error) {
      toast.error('Error al cargar productos');
    } finally {
      setLoading(false);
    }
  };

  const cargarCategorias = async () => {
    try {
      const response = await productoService.getCategorias();
      setCategorias(response || []);
    } catch (error) {
      console.error('Error al cargar categorías:', error);
    }
  };

  // Filtrar productos
  const productosFiltrados = productos.filter(p => {
    const coincideBusqueda = p.nombre.toLowerCase().includes(busqueda.toLowerCase()) ||
      (p.codigo && p.codigo.toLowerCase().includes(busqueda.toLowerCase()));
    const coincideCategoria = !categoriaFiltro || p.categoria?.id === parseInt(categoriaFiltro);
    return coincideBusqueda && coincideCategoria;
  });

  // Abrir modal para crear
  const abrirModalCrear = () => {
    setProductoEditar(null);
    setFormData({
      nombre: '',
      codigo: '',
      descripcion: '',
      precio: '',
      costo: '',
      imagenUrl: '',
      categoriaId: categorias[0]?.id || '',
      disponible: true,
      requierePreparacion: true,
      tiempoPreparacion: ''
    });
    setMostrarModal(true);
  };

  // Abrir modal para editar
  const abrirModalEditar = (producto) => {
    setProductoEditar(producto);
    setFormData({
      nombre: producto.nombre || '',
      codigo: producto.codigo || '',
      descripcion: producto.descripcion || '',
      precio: producto.precio || '',
      costo: producto.costo || '',
      imagenUrl: producto.imagenUrl || '',
      categoriaId: producto.categoria?.id || '',
      disponible: producto.disponible ?? true,
      requierePreparacion: producto.requierePreparacion ?? true,
      tiempoPreparacion: producto.tiempoPreparacion || ''
    });
    setMostrarModal(true);
  };

  // Cerrar modal
  const cerrarModal = () => {
    setMostrarModal(false);
    setProductoEditar(null);
  };

  // Manejar cambios en el formulario
  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  // Guardar producto
  const handleGuardar = async (e) => {
    e.preventDefault();
    
    if (!formData.nombre || !formData.precio || !formData.categoriaId) {
      toast.error('Complete los campos obligatorios');
      return;
    }

    try {
      const data = {
        ...formData,
        precio: parseFloat(formData.precio),
        costo: formData.costo ? parseFloat(formData.costo) : null,
        categoriaId: parseInt(formData.categoriaId),
        tiempoPreparacion: formData.tiempoPreparacion ? parseInt(formData.tiempoPreparacion) : null,
        imagenUrl: formData.imagenUrl || null
      };

      if (productoEditar) {
        await productoService.update(productoEditar.id, data);
        toast.success('Producto actualizado');
      } else {
        await productoService.create(data);
        toast.success('Producto creado');
      }

      cerrarModal();
      cargarProductos();
    } catch (error) {
      toast.error(error.response?.data?.mensaje || 'Error al guardar producto');
    }
  };

  // Eliminar producto
  const handleEliminar = async (producto) => {
    if (!window.confirm(`¿Eliminar el producto "${producto.nombre}"?`)) {
      return;
    }

    try {
      await productoService.delete(producto.id);
      toast.success('Producto eliminado');
      cargarProductos();
    } catch (error) {
      toast.error('Error al eliminar producto');
    }
  };

  // Crear categoría
  const handleCrearCategoria = async (e) => {
    e.preventDefault();
    if (!nuevaCategoria.nombre) return;

    try {
      await productoService.createCategoria(nuevaCategoria);
      toast.success('Categoría creada');
      setNuevaCategoria({ nombre: '', descripcion: '', imagenUrl: '' });
      cargarCategorias();
    } catch (error) {
      toast.error('Error al crear categoría');
    }
  };

  // Eliminar categoría
  const handleEliminarCategoria = async (categoria) => {
    if (!window.confirm(`¿Eliminar la categoría "${categoria.nombre}"?`)) return;

    try {
      await productoService.deleteCategoria(categoria.id);
      toast.success('Categoría eliminada');
      cargarCategorias();
    } catch (error) {
      toast.error(error.response?.data?.mensaje || 'Error al eliminar categoría');
    }
  };

  return (
    <div className="productos-container">
      {/* Header */}
      <div className="page-header">
        <h2>Productos</h2>
        <div className="header-actions">
          <button className="btn btn-outline-secondary" onClick={() => setMostrarModalCategorias(true)}>
            Gestionar Categorías
          </button>
          {puedeEditar && (
            <button className="btn btn-primary" onClick={abrirModalCrear}>
              <FaPlus /> Nuevo Producto
            </button>
          )}
        </div>
      </div>

      {/* Filtros */}
      <div className="filtros-container">
        <div className="filtro-busqueda">
          <FaSearch />
          <input
            type="text"
            placeholder="Buscar por nombre o código..."
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
          />
        </div>
        <div className="filtro-categoria">
          <FaFilter />
          <select 
            value={categoriaFiltro} 
            onChange={(e) => setCategoriaFiltro(e.target.value)}
          >
            <option value="">Todas las categorías</option>
            {categorias.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.nombre}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Lista de productos */}
      {loading ? (
        <div className="loading-container">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Cargando...</span>
          </div>
        </div>
      ) : (
        <div className="productos-table-container">
          <table className="table table-hover">
            <thead>
              <tr>
                <th>Imagen</th>
                <th>Código</th>
                <th>Nombre</th>
                <th>Categoría</th>
                <th>Precio</th>
                <th>Costo</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {productosFiltrados.map(producto => (
                <tr key={producto.id}>
                  <td>
                    {producto.imagenUrl ? (
                      <img 
                        src={producto.imagenUrl} 
                        alt={producto.nombre}
                        style={{ width: '50px', height: '50px', objectFit: 'cover', borderRadius: '5px' }}
                        onError={(e) => e.target.src = 'https://via.placeholder.com/50?text=Sin+imagen'}
                      />
                    ) : (
                      <div style={{ 
                        width: '50px', height: '50px', backgroundColor: '#f0f0f0', 
                        borderRadius: '5px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '20px'
                      }}>
                        🍽️
                      </div>
                    )}
                  </td>
                  <td><code>{producto.codigo || '-'}</code></td>
                  <td>
                    <strong>{producto.nombre}</strong>
                    {producto.descripcion && (
                      <small className="d-block text-muted">{producto.descripcion.substring(0, 50)}...</small>
                    )}
                  </td>
                  <td>
                    <span className="badge bg-light text-dark">
                      {producto.categoria?.nombre || 'Sin categoría'}
                    </span>
                  </td>
                  <td className="precio">${producto.precio?.toFixed(2)}</td>
                  <td>{producto.costo ? `$${producto.costo.toFixed(2)}` : '-'}</td>
                  <td>
                    <span className={`badge ${producto.disponible ? 'bg-success' : 'bg-secondary'}`}>
                      {producto.disponible ? 'Disponible' : 'No disponible'}
                    </span>
                  </td>
                  <td>
                    {puedeEditar && (
                      <div className="acciones">
                        <button 
                          className="btn btn-sm btn-outline-primary"
                          onClick={() => abrirModalEditar(producto)}
                          title="Editar"
                        >
                          <FaEdit />
                        </button>
                        <button 
                          className="btn btn-sm btn-outline-danger"
                          onClick={() => handleEliminar(producto)}
                          title="Eliminar"
                        >
                          <FaTrash />
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {productosFiltrados.length === 0 && (
            <div className="sin-resultados">
              <p>No se encontraron productos</p>
            </div>
          )}
        </div>
      )}

      {/* Modal Producto */}
      {mostrarModal && (
        <div className="modal-overlay">
          <div className="modal-content modal-producto">
            <div className="modal-header">
              <h4>{productoEditar ? 'Editar Producto' : 'Nuevo Producto'}</h4>
              <button onClick={cerrarModal}><FaTimes /></button>
            </div>
            <form onSubmit={handleGuardar}>
              <div className="modal-body">
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Nombre *</label>
                    <input
                      type="text"
                      name="nombre"
                      className="form-control"
                      value={formData.nombre}
                      onChange={handleInputChange}
                      required
                    />
                  </div>
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Código</label>
                    <input
                      type="text"
                      name="codigo"
                      className="form-control"
                      value={formData.codigo}
                      onChange={handleInputChange}
                    />
                  </div>
                </div>
                
                <div className="mb-3">
                  <label className="form-label">Descripción</label>
                  <textarea
                    name="descripcion"
                    className="form-control"
                    rows="2"
                    value={formData.descripcion}
                    onChange={handleInputChange}
                  />
                </div>

                {/* Campo de imagen */}
                <div className="mb-3">
                  <label className="form-label">URL de Imagen</label>
                  <input
                    type="text"
                    name="imagenUrl"
                    className="form-control"
                    placeholder="https://ejemplo.com/imagen.jpg"
                    value={formData.imagenUrl}
                    onChange={handleInputChange}
                  />
                  {formData.imagenUrl && (
                    <div className="mt-2 text-center">
                      <img 
                        src={formData.imagenUrl} 
                        alt="Preview" 
                        className="img-thumbnail"
                        style={{ maxHeight: '150px', objectFit: 'cover' }}
                        onError={(e) => e.target.style.display = 'none'}
                      />
                    </div>
                  )}
                </div>

                <div className="row">
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Precio *</label>
                    <input
                      type="number"
                      name="precio"
                      className="form-control"
                      step="0.01"
                      min="0"
                      value={formData.precio}
                      onChange={handleInputChange}
                      required
                    />
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Costo</label>
                    <input
                      type="number"
                      name="costo"
                      className="form-control"
                      step="0.01"
                      min="0"
                      value={formData.costo}
                      onChange={handleInputChange}
                    />
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Categoría *</label>
                    <select
                      name="categoriaId"
                      className="form-select"
                      value={formData.categoriaId}
                      onChange={handleInputChange}
                      required
                    >
                      <option value="">Seleccionar...</option>
                      {categorias.map(cat => (
                        <option key={cat.id} value={cat.id}>{cat.nombre}</option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <div className="form-check">
                      <input
                        type="checkbox"
                        name="disponible"
                        className="form-check-input"
                        id="disponible"
                        checked={formData.disponible}
                        onChange={handleInputChange}
                      />
                      <label className="form-check-label" htmlFor="disponible">
                        Disponible para venta
                      </label>
                    </div>
                  </div>
                  <div className="col-md-6 mb-3">
                    <div className="form-check">
                      <input
                        type="checkbox"
                        name="requierePreparacion"
                        className="form-check-input"
                        id="requierePreparacion"
                        checked={formData.requierePreparacion}
                        onChange={handleInputChange}
                      />
                      <label className="form-check-label" htmlFor="requierePreparacion">
                        Requiere preparación
                      </label>
                    </div>
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label">Tiempo de preparación (min)</label>
                  <input
                    type="number"
                    name="tiempoPreparacion"
                    className="form-control"
                    min="0"
                    value={formData.tiempoPreparacion}
                    onChange={handleInputChange}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={cerrarModal}>
                  Cancelar
                </button>
                <button type="submit" className="btn btn-primary">
                  {productoEditar ? 'Actualizar' : 'Crear'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Categorías */}
      {mostrarModalCategorias && (
        <div className="modal-overlay">
          <div className="modal-content modal-categorias">
            <div className="modal-header">
              <h4>Gestionar Categorías</h4>
              <button onClick={() => setMostrarModalCategorias(false)}><FaTimes /></button>
            </div>
            <div className="modal-body">
              {/* Formulario nueva categoría */}
              <form onSubmit={handleCrearCategoria} className="mb-4">
                <div className="row g-2">
                  <div className="col">
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Nombre de la categoría"
                      value={nuevaCategoria.nombre}
                      onChange={(e) => setNuevaCategoria({...nuevaCategoria, nombre: e.target.value})}
                      required
                    />
                  </div>
                  <div className="col-auto">
                    <button type="submit" className="btn btn-success">
                      <FaPlus /> Crear
                    </button>
                  </div>
                </div>
              </form>

              {/* Lista de categorías */}
              <div className="categorias-lista">
                {categorias.map(cat => (
                  <div key={cat.id} className="categoria-item">
                    <span>{cat.nombre}</span>
                    <small className="text-muted">{cat.totalProductos || 0} productos</small>
                    <button
                      className="btn btn-sm btn-outline-danger"
                      onClick={() => handleEliminarCategoria(cat)}
                    >
                      <FaTrash />
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ProductosPage; 