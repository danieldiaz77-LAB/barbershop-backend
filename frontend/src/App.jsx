import { Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import ProtectedRoute from './components/ProtectedRoute';

// páginas
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Servicios from './pages/Servicios';
import Reservar from './pages/Reservar';
import MisCitas from './pages/MisCitas';
import AdminDashboard from './pages/AdminDashboard';

export default function App() {
  return (
    <AuthProvider>
      <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
        <Navbar />
        <main style={{ flex: 1 }}>
          <Routes>
            <Route path="/"          element={<Home />} />
            <Route path="/login"     element={<Login />} />
            <Route path="/register"  element={<Register />} />
            <Route path="/servicios" element={<Servicios />} />

            {/* rutas protegidas — requieren login */}
            <Route path="/reservar" element={
              <ProtectedRoute><Reservar /></ProtectedRoute>
            } />
            <Route path="/mis-citas" element={
              <ProtectedRoute><MisCitas /></ProtectedRoute>
            } />

            {/* solo BARBER_ADMIN */}
            <Route path="/admin" element={
              <ProtectedRoute requireRole="BARBER_ADMIN">
                <AdminDashboard />
              </ProtectedRoute>
            } />
          </Routes>
        </main>
        <Footer />
      </div>
    </AuthProvider>
  );
}