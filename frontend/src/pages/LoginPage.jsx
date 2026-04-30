import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/api';
import logo from '../assets/AsparagusAPP_logo.png';
import './LoginPage.css';

function LoginPage() {
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await authService.login(credentials);

      // Guardar directamente en localStorage
      localStorage.setItem('user', JSON.stringify(response));
      localStorage.setItem('token', 'dev-token-' + response.id);

      // Redirigir
      navigate('/');

      // Recargar para actualizar el estado
      window.location.reload();
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Error al iniciar sesión');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-branding">
        <div className="branding-content">
          <img src={logo} alt="AsparagusApp" className="login-logo" />
          <h1>AsparagusApp</h1>
          <p>Sistema de gestión para restaurantes</p>
          <ul className="features-list">
            <li>🪑 Gestión de mesas interactiva</li>
            <li>📦 Control de inventario</li>
            <li>💰 Reportes de ventas en tiempo real</li>
            <li>👥 Administración de clientes</li>
          </ul>
        </div>
      </div>

      <div className="login-form-container">
        <div className="login-form-wrapper">
          <div className="login-header">
            <img src={logo} alt="AsparagusApp" className="login-logo-small" />
            <h2>Iniciar Sesión</h2>
            <p>Ingresa tus credenciales para continuar</p>
          </div>

          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Usuario</label>
              <input
                type="text"
                className="form-control"
                value={credentials.username}
                onChange={(e) => setCredentials({ ...credentials, username: e.target.value })}
                required
                autoFocus
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Contraseña</label>
              <input
                type="password"
                className="form-control"
                value={credentials.password}
                onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
                required
              />
            </div>
            <button
              type="submit"
              className="btn btn-primary w-100"
              disabled={loading}
            >
              {loading ? 'Iniciando...' : 'Iniciar Sesión'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;