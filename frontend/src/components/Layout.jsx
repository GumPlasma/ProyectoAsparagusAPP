import { Nav, Navbar, Container, Button } from 'react-bootstrap';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import logo from '../assets/AsparagusAPP_logo.png';
import './Layout.css';

function Layout() {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const isActive = (path) => location.pathname === path;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <Navbar className="asparagus-navbar" variant="dark" fixed="top" expand="lg">
      <Container fluid>
        <Navbar.Brand as={Link} to="/" className="navbar-brand-custom">
          <img src={logo} alt="AsparagusApp" className="navbar-logo" />
          <span className="navbar-title">AsparagusApp</span>
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="navbar-nav" />
        <Navbar.Collapse id="navbar-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/" active={isActive('/')}>
              📊 Dashboard
            </Nav.Link>
            <Nav.Link as={Link} to="/pos" active={isActive('/pos')}>
              🧾 POS
            </Nav.Link>
            <Nav.Link as={Link} to="/mesas" active={isActive('/mesas')}>
              🪑 Mesas
            </Nav.Link>
            <Nav.Link as={Link} to="/productos" active={isActive('/productos')}>
              📦 Productos
            </Nav.Link>
            <Nav.Link as={Link} to="/inventario" active={isActive('/inventario')}>
              📊 Inventario
            </Nav.Link>
            <Nav.Link as={Link} to="/clientes" active={isActive('/clientes')}>
              👥 Clientes
            </Nav.Link>
            <Nav.Link as={Link} to="/proveedores" active={isActive('/proveedores')}>
              🚚 Proveedores
            </Nav.Link>
            <Nav.Link as={Link} to="/ventas" active={isActive('/ventas')}>
              💰 Ventas
            </Nav.Link>
            {user?.rol === 'ADMIN' && (
              <Nav.Link as={Link} to="/usuarios" active={isActive('/usuarios')}>
                👤 Usuarios
              </Nav.Link>
            )}
          </Nav>
          <Nav>
            <span className="navbar-text me-3">
              👋 {user?.nombre}
            </span>
            <Button className="btn-logout" size="sm" onClick={handleLogout}>
              Cerrar Sesión
            </Button>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}

export default Layout;