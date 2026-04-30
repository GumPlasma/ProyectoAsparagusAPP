import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// AUTH
export const authService = {
  login: async (credentials) => {
    const payload = {
      email: credentials.username,
      password: credentials.password
    };
    
    const res = await api.post('/auth/login', payload);
    const data = res.data;
    
    if (!data.success) {
      throw new Error(data.message || 'Credenciales incorrectas');
    }
    
    return {
      id: data.id,
      username: data.email,
      nombre: data.nombre,
      email: data.email,
      rol: { nombre: data.rol },
      nombreCompleto: data.nombre
    };
  },
  register: (data) => api.post('/auth/register', data).then(res => res.data),
  getProfile: () => api.get('/auth/profile').then(res => res.data),
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
};

// USUARIOS
export const usuarioService = {
  getAll: () => api.get('/usuarios').then(res => res.data),
  getById: (id) => api.get(`/usuarios/${id}`).then(res => res.data),
  create: (data) => api.post('/usuarios', data).then(res => res.data),
  update: (id, data) => api.put(`/usuarios/${id}`, data).then(res => res.data),
  delete: (id) => api.delete(`/usuarios/${id}`),
  getRoles: () => api.get('/usuarios/roles').then(res => res.data),
};

// PRODUCTOS
export const productoService = {
  getAll: () => api.get('/productos').then(res => res.data),
  getById: (id) => api.get(`/productos/${id}`).then(res => res.data),
  getDisponibles: () => api.get('/productos').then(res => res.data),
  getByCategoria: (categoriaId) => api.get(`/productos/categoria/${categoriaId}`).then(res => res.data),
  buscar: (nombre) => api.get(`/productos/buscar?nombre=${encodeURIComponent(nombre)}`).then(res => res.data),
  create: (data) => api.post('/productos', data).then(res => res.data),
  update: (id, data) => api.put(`/productos/${id}`, data).then(res => res.data),
  delete: (id) => api.delete(`/productos/${id}`),
  // Categorías
  getCategorias: () => api.get('/categorias').then(res => res.data),
  getCategoriaById: (id) => api.get(`/categorias/${id}`).then(res => res.data),
  createCategoria: (data) => api.post('/categorias', data).then(res => res.data),
  updateCategoria: (id, data) => api.put(`/categorias/${id}`, data).then(res => res.data),
  deleteCategoria: (id) => api.delete(`/categorias/${id}`).then(res => res.data),
};

// INVENTARIO
export const inventarioService = {
  getAll: () => api.get('/inventario').then(res => res.data),
  getById: (id) => api.get(`/inventario/${id}`).then(res => res.data),
  getStockBajo: () => api.get('/inventario/stock-bajo').then(res => res.data),
  getResumen: () => api.get('/inventario/resumen').then(res => res.data),
  create: (data) => api.post('/inventario', data).then(res => res.data),
  update: (id, data) => api.put(`/inventario/${id}`, data).then(res => res.data),
  delete: (id) => api.delete(`/inventario/${id}`),
  ajuste: (id, data) => api.post(`/inventario/${id}/ajuste`, data).then(res => res.data),
  inicializar: (productoId) => api.post(`/inventario/inicializar/${productoId}`).then(res => res.data),
  getMovimientos: () => api.get('/inventario/movimientos').then(res => res.data),
  getMovimientosPorProducto: (productoId) => api.get(`/inventario/movimientos/producto/${productoId}`).then(res => res.data),
};

// MESAS
export const mesaService = {
  getAll: () => api.get('/mesas').then(res => res.data),
  getById: (id) => api.get(`/mesas/${id}`).then(res => res.data),
  getEstadisticas: () => api.get('/mesas/estadisticas').then(res => res.data),
  create: (data) => api.post('/mesas', data).then(res => res.data),
  update: (id, data) => api.put(`/mesas/${id}`, data).then(res => res.data),
  updatePosicion: (id, x, y) => api.put(`/mesas/${id}/posicion?posicionX=${x}&posicionY=${y}`).then(res => res.data),
  delete: (id) => api.delete(`/mesas/${id}`),
  abrir: (id) => api.post(`/mesas/${id}/abrir`).then(res => res.data),
  agregarProducto: (id, data) => api.post(`/mesas/${id}/productos`, data).then(res => res.data),
  eliminarProducto: (mesaId, pedidoId) => api.delete(`/mesas/${mesaId}/productos/${pedidoId}`).then(res => res.data),
  actualizarPropina: (id, propina) => api.put(`/mesas/${id}/propina?propina=${propina}`).then(res => res.data),
  pagar: (id, data) => api.post(`/mesas/${id}/pagar`, data).then(res => res.data),
};

// CLIENTES
export const clienteService = {
  getAll: () => api.get('/clientes').then(res => res.data),
  getById: (id) => api.get(`/clientes/${id}`).then(res => res.data),
  buscar: (termino) => api.get(`/clientes/buscar?termino=${termino}`).then(res => res.data),
  create: (data) => api.post('/clientes', data).then(res => res.data),
  update: (id, data) => api.put(`/clientes/${id}`, data).then(res => res.data),
  delete: (id) => api.delete(`/clientes/${id}`),
};

// PROVEEDORES
export const proveedorService = {
  getAll: () => api.get('/proveedores').then(res => res.data),
  getById: (id) => api.get(`/proveedores/${id}`).then(res => res.data),
  buscar: (termino) => api.get(`/proveedores/buscar?termino=${termino}`).then(res => res.data),
  getByCategoria: (categoria) => api.get(`/proveedores/categoria/${categoria}`).then(res => res.data),
  create: (data) => api.post('/proveedores', data).then(res => res.data),
  update: (id, data) => api.put(`/proveedores/${id}`, data).then(res => res.data),
  delete: (id) => api.delete(`/proveedores/${id}`),
};

// VENTAS
export const ventaService = {
  getAll: () => api.get('/ventas').then(res => res.data),
  getById: (id) => api.get(`/ventas/${id}`).then(res => res.data),
  create: (data) => api.post('/ventas/directa', data).then(res => res.data),
  buscar: (params) => api.get(`/ventas/buscar?${params}`).then(res => res.data),
  getByFecha: (inicio, fin) => api.get(`/ventas/fecha?inicio=${inicio}&fin=${fin}`).then(res => res.data),
  getEstadisticas: (inicio, fin) => api.get(`/ventas/estadisticas?inicio=${inicio}&fin=${fin}`).then(res => res.data),
  getResumen: (fecha) => api.get(`/ventas/resumen/${fecha}`).then(res => res.data).catch(() => ({ 
    totalVentas: 0, 
    montoTotal: 0
  })),
};

export default api;