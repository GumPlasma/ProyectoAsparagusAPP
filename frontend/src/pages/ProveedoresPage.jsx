import { useState, useEffect } from 'react';
import { proveedorService } from '../services/api';
import './ProveedoresPage.css';

function ProveedoresPage() {
  const [proveedores, setProveedores] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingProveedor, setEditingProveedor] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState({
    nombre: '', ruc: '', contacto: '', telefono: '', email: '', direccion: '', categoria: '', notas: ''
  });

  const categorias = ['ALIMENTOS', 'BEBIDAS', 'INSUMOS', 'EQUIPOS', 'LIMPIEZA', 'OTROS'];

  useEffect(() => { cargarProveedores(); }, []);

  const cargarProveedores = async () => {
    try {
      setLoading(true);
      const data = await proveedorService.getAll();
      setProveedores(data);
    } catch (error) {
      console.error('Error al cargar proveedores:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingProveedor) {
        await proveedorService.update(editingProveedor.id, formData);
        alert('Proveedor actualizado correctamente');
      } else {
        await proveedorService.create(formData);
        alert('Proveedor creado correctamente');
      }
      setShowModal(false);
      resetForm();
      cargarProveedores();
    } catch (error) {
      alert('Error al guardar: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleEdit = (proveedor) => {
    setEditingProveedor(proveedor);
    setFormData({
      nombre: proveedor.nombre || '', ruc: proveedor.ruc || '', contacto: proveedor.contacto || '',
      telefono: proveedor.telefono || '', email: proveedor.email || '', direccion: proveedor.direccion || '',
      categoria: proveedor.categoria || '', notas: proveedor.notas || ''
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este proveedor?')) {
      try {
        await proveedorService.delete(id);
        cargarProveedores();
      } catch (error) {
        alert('Error al eliminar');
      }
    }
  };

  const resetForm = () => {
    setFormData({ nombre: '', ruc: '', contacto: '', telefono: '', email: '', direccion: '', categoria: '', notas: '' });
    setEditingProveedor(null);
  };

  const proveedoresFiltrados = proveedores.filter(p =>
    `${p.nombre} ${p.ruc} ${p.contacto} ${p.categoria}`.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getCategoriaBadgeClass = (categoria) => {
    const classes = { 'ALIMENTOS': 'bg-success', 'BEBIDAS': 'bg-info', 'INSUMOS': 'bg-warning', 'EQUIPOS': 'bg-primary', 'LIMPIEZA': 'bg-secondary', 'OTROS': 'bg-dark' };
    return classes[categoria] || 'bg-secondary';
  };

  if (loading) {
    return <div className="d-flex justify-content-center align-items-center" style={{ height: '400px' }}><div className="spinner-border text-primary"></div></div>;
  }

  return (
    <div className="proveedores-container">
      <div className="proveedores-header">
        <h2>📦 Gestión de Proveedores</h2>
        <button className="btn btn-primary" onClick={() => { resetForm(); setShowModal(true); }}>➕ Nuevo Proveedor</button>
      </div>

      <div className="search-bar mb-3">
        <input type="text" className="form-control" placeholder="🔍 Buscar por nombre, RUC o contacto..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
      </div>

      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="table-dark">
            <tr><th>ID</th><th>Nombre</th><th>RUC</th><th>Contacto</th><th>Teléfono</th><th>Categoría</th><th>Estado</th><th>Acciones</th></tr>
          </thead>
          <tbody>
            {proveedoresFiltrados.length === 0 ? (
              <tr><td colSpan="8" className="text-center text-muted py-4">No se encontraron proveedores</td></tr>
            ) : (
              proveedoresFiltrados.map(p => (
                <tr key={p.id}>
                  <td>{p.id}</td>
                  <td><strong>{p.nombre}</strong></td>
                  <td>{p.ruc || '-'}</td>
                  <td>{p.contacto || '-'}</td>
                  <td>{p.telefono || '-'}</td>
                  <td><span className={`badge ${getCategoriaBadgeClass(p.categoria)}`}>{p.categoria}</span></td>
                  <td><span className={`badge ${p.activo ? 'bg-success' : 'bg-danger'}`}>{p.activo ? 'Activo' : 'Inactivo'}</span></td>
                  <td>
                    <div className="btn-group btn-group-sm">
                      <button className="btn btn-outline-info" onClick={() => handleEdit(p)}>✏️</button>
                      <button className="btn btn-outline-danger" onClick={() => handleDelete(p.id)}>🗑️</button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content">
              <div className="modal-header bg-primary text-white">
                <h5 className="modal-title">{editingProveedor ? '✏️ Editar Proveedor' : '➕ Nuevo Proveedor'}</h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => setShowModal(false)}></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body">
                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Nombre / Razón Social *</label>
                      <input type="text" className="form-control" value={formData.nombre} onChange={(e) => setFormData({ ...formData, nombre: e.target.value })} required />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label className="form-label">RUC</label>
                      <input type="text" className="form-control" value={formData.ruc} onChange={(e) => setFormData({ ...formData, ruc: e.target.value })} maxLength="11" />
                    </div>
                  </div>
                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Contacto</label>
                      <input type="text" className="form-control" value={formData.contacto} onChange={(e) => setFormData({ ...formData, contacto: e.target.value })} />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Teléfono</label>
                      <input type="text" className="form-control" value={formData.telefono} onChange={(e) => setFormData({ ...formData, telefono: e.target.value })} />
                    </div>
                  </div>
                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Email</label>
                      <input type="email" className="form-control" value={formData.email} onChange={(e) => setFormData({ ...formData, email: e.target.value })} />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Categoría</label>
                      <select className="form-select" value={formData.categoria} onChange={(e) => setFormData({ ...formData, categoria: e.target.value })}>
                        <option value="">Seleccionar...</option>
                        {categorias.map(cat => <option key={cat} value={cat}>{cat}</option>)}
                      </select>
                    </div>
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Dirección</label>
                    <input type="text" className="form-control" value={formData.direccion} onChange={(e) => setFormData({ ...formData, direccion: e.target.value })} />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Notas</label>
                    <textarea className="form-control" rows="3" value={formData.notas} onChange={(e) => setFormData({ ...formData, notas: e.target.value })}></textarea>
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancelar</button>
                  <button type="submit" className="btn btn-primary">{editingProveedor ? 'Actualizar' : 'Guardar'}</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ProveedoresPage;