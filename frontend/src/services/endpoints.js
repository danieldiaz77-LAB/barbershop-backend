import api from './api';

// autenticación
export const authApi = {
  login:    (email, password) => api.post('/api/auth/login', { email, password }).then(r => r.data),
  register: (payload)         => api.post('/api/auth/register', payload).then(r => r.data),
  me:       ()                => api.get('/api/auth/me').then(r => r.data),
};

// barberos
export const barbersApi = {
  list:   ()        => api.get('/api/barbers').then(r => r.data),
  get:    (id)      => api.get(`/api/barbers/${id}`).then(r => r.data),
  create: (b)       => api.post('/api/barbers', b).then(r => r.data),
  update: (id, b)   => api.put(`/api/barbers/${id}`, b).then(r => r.data),
  remove: (id)      => api.delete(`/api/barbers/${id}`),
};

// servicios
export const servicesApi = {
  list:   ()        => api.get('/api/services').then(r => r.data),
  listAll:()        => api.get('/api/services/all').then(r => r.data),
  create: (s)       => api.post('/api/services', s).then(r => r.data),
  update: (id, s)   => api.put(`/api/services/${id}`, s).then(r => r.data),
  remove: (id)      => api.delete(`/api/services/${id}`),
};

// citas
export const appointmentsApi = {
  book:         (payload)              => api.post('/api/appointments', payload).then(r => r.data),
  mine:         ()                     => api.get('/api/appointments/mine').then(r => r.data),
  availability: (barberId, serviceId, date) =>
    api.get('/api/appointments/availability', { params: { barberId, serviceId, date } }).then(r => r.data),
  byBarber:     (barberId, date)       =>
    api.get(`/api/appointments/barber/${barberId}`, { params: date ? { date } : {} }).then(r => r.data),
  cancel:       (id)                   => api.post(`/api/appointments/${id}/cancel`).then(r => r.data),
  get:          (id)                   => api.get(`/api/appointments/${id}`).then(r => r.data),
};

// pagos
export const paymentsApi = {
  checkout: (appointmentId, originUrl) =>
    api.post('/api/payments/checkout', { appointmentId, originUrl }).then(r => r.data),
  status:   (sessionId)                =>
    api.get(`/api/payments/status/${sessionId}`).then(r => r.data),
};