import { useState, useEffect } from 'react';
import { clienteService } from '../services/api';
import './ClientesPage.css';

function ClientesPage() {
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingCliente, setEditingCliente] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    dni: '',
    telefono: '',
    email: '',
    direccion: '',
    notas: ''
  });

  useEffect(() => {
    cargarClientes();
  }, []);

  const cargarClientes = async () => {
    try {
      setLoading(true);
      const data = await clienteService.getAll();
      setClientes(data);
    } catch (error) {
      console.error('Error al cargar clientes:', error);
      alert('Error al cargar los clientes');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingCliente) {
        await clienteService.update(editingCliente.id, formData);
        alert('Cliente actualizado correctamente');
      } else {
        await clienteService.create(formData);
        alert('Cliente creado correctamente');
      }
      setShowModal(false);
      resetForm();
      cargarClientes();
    } catch (error) {
      console.error('Error al guardar cliente:', error);
      alert('Error al guardar el cliente: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleEdit = (cliente) => {
    setEditingCliente(cliente);
    setFormData({
      nombre: cliente.nombre || '',
      apellido: cliente.apellido || '',
      dni: cliente.dni || '',
      telefono: cliente.telefono || '',
      email: cliente.email || '',
      direccion: cliente.direccion || '',
      notas: cliente.notas || ''
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este cliente?')) {
      try {
        await clienteService.delete(id);
        alert('Cliente eliminado correctamente');
        cargarClientes();
      } catch (error) {
        console.error('Error al eliminar cliente:', error);
        alert('Error al eliminar el cliente');
      }
    }
  };

  const resetForm = () => {
    setFormData({
      nombre: '',
      apellido: '',
      dni: '',
      telefono: '',
      email: '',
      direccion: '',
      notas: ''
    });
    setEditingCliente(null);
  };

  const clientesFiltrados = clientes.filter(cliente =>
    `${cliente.nombre} ${cliente.apellido} ${cliente.dni} ${cliente.telefono}`
      .toLowerCase()
      .includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ height: '400px' }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="clientes-container">
      <div className="clientes-header">
        <h2>👥 Gestión de Clientes</h2>
        <button className="btn btn-primary" onClick={() => { resetForm(); setShowModal(true); }}>
          ➕ Nuevo Cliente
        </button>
      </div>

      <div className="search-bar mb-3">
        <input
          type="text"
          className="form-control"
          placeholder="🔍 Buscar por nombre, DNI o teléfono..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Nombre Completo</th>
              <th>DNI</th>
              <th>Teléfono</th>
              <th>Email</th>
              <th>Total Compras</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {clientesFiltrados.length === 0 ? (
              <tr>
                <td colSpan="7" className="text-center text-muted py-4">
                  No se encontraron clientes
                </td>
              </tr>
            ) : (
              clientesFiltrados.map(cliente => (
                <tr key={cliente.id}>
                  <td>{cliente.id}</td>
                  <td><strong>{cliente.nombre} {cliente.apellido}</strong></td>
                  <td>{cliente.dni || '-'}</td>
                  <td>{cliente.telefono || '-'}</td>
                  <td>{cliente.email || '-'}</td>
                  <td>
                    <span className="badge bg-success">
                      S/. {cliente.totalCompras?.toFixed(2) || '0.00'}
                    </span>
                  </td>
                  <td>
                    <div className="btn-group btn-group-sm">
                      <button className="btn btn-outline-info" onClick={() => handleEdit(cliente)} title="Editar">✏️</button>
                      <button className="btn btn-outline-danger" onClick={() => handleDelete(cliente.id)} title="Eliminar">🗑️</button>
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
                <h5 className="modal-title">{editingCliente ? '✏️ Editar Cliente' : '➕ Nuevo Cliente'}</h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => setShowModal(false)}></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body">
                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Nombre *</label>
                      <input type="text" className="form-control" value={formData.nombre} onChange={(e) => setFormData({ ...formData, nombre: e.target.value })} required />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Apellido *</label>
                      <input type="text" className="form-control" value={formData.apellido} onChange={(e) => setFormData({ ...formData, apellido: e.target.value })} required />
                    </div>
                  </div>
                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label">DNI</label>
                      <input type="text" className="form-control" value={formData.dni} onChange={(e) => setFormData({ ...formData, dni: e.target.value })} maxLength="8" />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Teléfono</label>
                      <input type="text" className="form-control" value={formData.telefono} onChange={(e) => setFormData({ ...formData, telefono: e.target.value })} />
                    </div>
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Email</label>
                    <input type="email" className="form-control" value={formData.email} onChange={(e) => setFormData({ ...formData, email: e.target.value })} />
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
                  <button type="submit" className="btn btn-primary">{editingCliente ? 'Actualizar' : 'Guardar'}</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ClientesPage;