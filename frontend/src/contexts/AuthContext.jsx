import { createContext, useContext, useState, useCallback } from 'react';
import { authService } from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const savedUser = localStorage.getItem('user');
      return savedUser ? JSON.parse(savedUser) : null;
    } catch {
      return null;
    }
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Función de login que hace la autenticación
  const login = useCallback(async (username, password) => {
    try {
      setLoading(true);
      setError(null);
      
      // Llamar a la API de autenticación
      const usuario = await authService.login(username, password);
      
      // Guardar en localStorage
      localStorage.setItem('token', 'dev-token-' + usuario.id);
      localStorage.setItem('user', JSON.stringify(usuario));
      
      setUser(usuario);
      return true;
    } catch (err) {
      setError(err.message || 'Error al iniciar sesión');
      return false;
    } finally {
      setLoading(false);
    }
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  }, []);

  const hasRole = useCallback((rolName) => {
    return user?.rol?.nombre === rolName;
  }, [user]);

  return (
    <AuthContext.Provider value={{ 
      user, 
      login, 
      logout, 
      loading,
      error,
      hasRole,
      isAuthenticated: !!user 
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }
  return context;
}