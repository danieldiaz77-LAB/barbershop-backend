import axios from 'axios';

// todas las llamadas van a /api que Vite redirige al backend en 8080
const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || '' });

// agrega el token JWT automáticamente en cada request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('blade_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// si el servidor responde 401, limpia la sesión
api.interceptors.response.use(
  (r) => r,
  (err) => {
    if (err?.response?.status === 401) {
      localStorage.removeItem('blade_token');
      localStorage.removeItem('blade_user');
    }
    return Promise.reject(err);
  }
);

export default api;