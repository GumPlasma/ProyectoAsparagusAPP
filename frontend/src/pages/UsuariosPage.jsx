import { useState, useEffect } from 'react';
import { usuarioService } from '../services/api';
import './UsuariosPage.css';

function UsuariosPage() {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingUsuario, setEditingUsuario] = useState(null);
  const [formData, setFormData] = useState({
    nombre: '',
    username: '',
    password: '',
    email: '',
    rol: 'VENDEDOR'
  });

  const roles = ['ADMIN', 'VENDEDOR', 'AUDITOR'];

  useEffect(() => {
    cargarUsuarios();
  }, []);

  const cargarUsuarios = async () => {
    try {
      setLoading(true);
      const data = await usuarioService.getAll();
      setUsuarios(data);
    } catch (error) {
      console.error('Error al cargar usuarios:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingUsuario) {
        await usuarioService.update(editingUsuario.id, formData);
        alert('Usuario actualizado correctamente');
      } else {
        await usuarioService.create(formData);
        alert('Usuario creado correctamente');
      }
      setShowModal(false);
      resetForm();
      cargarUsuarios();
    } catch (error) {
      alert('Error al guardar: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleEdit = (usuario) => {
    setEditingUsuario(usuario);
    setFormData({
      nombre: usuario.nombre || '',
      username: usuario.username || '',
      password: '',
      email: usuario.email || '',
      rol: usuario.rol?.nombre || usuario.rol || 'VENDEDOR'
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este usuario?')) {
      try {
        await usuarioService.delete(id);
        cargarUsuarios();
      } catch (error) {
        alert('Error al eliminar usuario');
      }
    }
  };

  const resetForm = () => {
    setFormData({
      nombre: '',
      username: '',
      password: '',
      email: '',
      rol: 'VENDEDOR'
    });
    setEditingUsuario(null);
  };

  const getRolBadgeClass = (rol) => {
    const classes = {
      'ADMIN': 'bg-danger',
      'VENDEDOR': 'bg-primary',
      'AUDITOR': 'bg-warning text-dark'
    };
    return classes[rol] || 'bg-secondary';
  };

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
    <div className="usuarios-container">
      <div className="usuarios-header">
        <h2>👤 Gestión de Usuarios</h2>
        <button className="btn btn-primary" onClick={() => { resetForm(); setShowModal(true); }}>
          ➕ Nuevo Usuario
        </button>
      </div>

      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Username</th>
              <th>Email</th>
              <th>Rol</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {usuarios.length === 0 ? (
              <tr>
                <td colSpan="7" className="text-center text-muted py-4">
                  No se encontraron usuarios
                </td>
              </tr>
            ) : (
              usuarios.map(usuario => (
                <tr key={usuario.id}>
                  <td>{usuario.id}</td>
                  <td><strong>{usuario.nombre}</strong></td>
                  <td>{usuario.username}</td>
                  <td>{usuario.email || '-'}</td>
                  <td>
                    <span className={`badge ${getRolBadgeClass(usuario.rol?.nombre || usuario.rol)}`}>
                      {usuario.rol?.nombre || usuario.rol}
                    </span>
                  </td>
                  <td>
                    <span className={`badge ${usuario.activo !== false ? 'bg-success' : 'bg-secondary'}`}>
                      {usuario.activo !== false ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>
                  <td>
                    <div className="btn-group btn-group-sm">
                      <button
                        className="btn btn-outline-info"
                        onClick={() => handleEdit(usuario)}
                        title="Editar"
                      >
                        ✏️
                      </button>
                      <button
                        className="btn btn-outline-danger"
                        onClick={() => handleDelete(usuario.id)}
                        title="Eliminar"
                      >
                        🗑️
                      </button>
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
          <div className="modal-dialog">
            <div className="modal-content">
              <div className="modal-header bg-primary text-white">
                <h5 className="modal-title">
                  {editingUsuario ? '✏️ Editar Usuario' : '➕ Nuevo Usuario'}
                </h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => setShowModal(false)}></button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body">
                  <div className="mb-3">
                    <label className="form-label">Nombre *</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.nombre}
                      onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                      required
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Username *</label>
                    <input
                      type="text"
                      className="form-control"
                      value={formData.username}
                      onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                      required
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">
                      Contraseña {editingUsuario ? '(dejar vacío para mantener)' : '*'}
                    </label>
                    <input
                      type="password"
                      className="form-control"
                      value={formData.password}
                      onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                      required={!editingUsuario}
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Email</label>
                    <input
                      type="email"
                      className="form-control"
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Rol *</label>
                    <select
                      className="form-select"
                      value={formData.rol}
                      onChange={(e) => setFormData({ ...formData, rol: e.target.value })}
                      required
                    >
                      {roles.map(rol => (
                        <option key={rol} value={rol}>{rol}</option>
                      ))}
                    </select>
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                    Cancelar
                  </button>
                  <button type="submit" className="btn btn-primary">
                    {editingUsuario ? 'Actualizar' : 'Guardar'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default UsuariosPage;