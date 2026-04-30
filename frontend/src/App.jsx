import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Layout from './components/Layout';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import ProductosPage from './pages/ProductosPage';
import MesasPage from './pages/MesasPage';
import ClientesPage from './pages/ClientesPage';
import ProveedoresPage from './pages/ProveedoresPage';
import VentasPage from './pages/VentasPage';
import UsuariosPage from './pages/UsuariosPage';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

function PrivateRoute({ children }) {
  const { user, loading } = useAuth();
  
  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
      </div>
    );
  }
  
  return user ? children : <Navigate to="/login" />;
}

function AppRoutes() {
  const { user } = useAuth();
  
  return (
    <>
      {user && <Layout />}
      <div className="container-fluid" style={{ paddingTop: user ? '70px' : '0' }}>
        <Routes>
          <Route path="/login" element={!user ? <LoginPage /> : <Navigate to="/" />} />
          <Route path="/" element={<PrivateRoute><DashboardPage /></PrivateRoute>} />
          <Route path="/productos" element={<PrivateRoute><ProductosPage /></PrivateRoute>} />
          <Route path="/mesas" element={<PrivateRoute><MesasPage /></PrivateRoute>} />
          <Route path="/clientes" element={<PrivateRoute><ClientesPage /></PrivateRoute>} />
          <Route path="/proveedores" element={<PrivateRoute><ProveedoresPage /></PrivateRoute>} />
          <Route path="/ventas" element={<PrivateRoute><VentasPage /></PrivateRoute>} />
          <Route path="/usuarios" element={<PrivateRoute><UsuariosPage /></PrivateRoute>} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </div>
    </>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </Router>
  );
}

export default App;