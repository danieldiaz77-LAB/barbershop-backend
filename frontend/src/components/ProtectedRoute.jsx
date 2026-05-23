import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// redirige a login si no hay sesión, o a inicio si no tiene el rol requerido
export default function ProtectedRoute({ children, requireRole }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (requireRole && user.role !== requireRole) return <Navigate to="/" replace />;
  return children;
}