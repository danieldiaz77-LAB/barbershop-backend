import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError(''); setLoading(true);
    try {
      const data = await login(email, password);
      navigate(data.role === 'BARBER_ADMIN' ? '/admin' : '/');
    } catch {
      setError('Email o contraseña incorrectos');
    } finally { setLoading(false); }
  };

  return (
    <div style={{
      minHeight: '80vh', display: 'flex',
      alignItems: 'center', justifyContent: 'center',
      padding: '40px 20px',
    }}>
      <div className="fadeup" style={{ width: '100%', maxWidth: '420px' }}>

        {/* encabezado */}
        <div style={{ marginBottom: '40px' }}>
          <div style={{
            fontFamily: "'Oswald', sans-serif",
            fontSize: '11px', letterSpacing: '0.3em',
            color: '#C5A059', textTransform: 'uppercase',
            marginBottom: '12px',
          }}>
            Bienvenido de vuelta
          </div>
          <h1 style={{
            fontFamily: "'Playfair Display', serif",
            fontSize: '40px', fontWeight: '700',
            letterSpacing: '-0.02em',
          }}>
            Iniciar sesión
          </h1>
        </div>

        <form onSubmit={onSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div>
            <label style={{
              fontFamily: "'Oswald', sans-serif",
              fontSize: '11px', letterSpacing: '0.2em',
              color: '#666', textTransform: 'uppercase',
              display: 'block', marginBottom: '8px',
            }}>
              Email
            </label>
            <input
              type="email" required
              value={email}
              onChange={e => setEmail(e.target.value)}
              placeholder="tu@email.com"
              className="input-dark"
            />
          </div>

          <div>
            <label style={{
              fontFamily: "'Oswald', sans-serif",
              fontSize: '11px', letterSpacing: '0.2em',
              color: '#666', textTransform: 'uppercase',
              display: 'block', marginBottom: '8px',
            }}>
              Contraseña
            </label>
            <input
              type="password" required
              value={password}
              onChange={e => setPassword(e.target.value)}
              placeholder="••••••••"
              className="input-dark"
            />
          </div>

          {error && (
            <div style={{
              background: 'rgba(153,27,27,0.2)',
              border: '1px solid rgba(248,113,113,0.3)',
              color: '#f87171', padding: '12px 16px',
              borderRadius: '2px', fontSize: '13px',
            }}>
              {error}
            </div>
          )}

          <button
            type="submit" disabled={loading}
            className="btn-gold"
            style={{ width: '100%', justifyContent: 'center', marginTop: '8px' }}
          >
            {loading ? 'Ingresando...' : 'Ingresar →'}
          </button>
        </form>

        <div style={{ marginTop: '24px', textAlign: 'center', color: '#555', fontSize: '13px' }}>
          ¿No tienes cuenta?{' '}
          <Link to="/register" style={{ color: '#C5A059' }}>Regístrate</Link>
        </div>

        {/* credenciales demo */}
        <div style={{
          marginTop: '40px',
          border: '1px solid #1a1a1a',
          borderRadius: '4px',
          padding: '16px',
        }}>
          <div style={{
            fontFamily: "'Oswald', sans-serif",
            fontSize: '10px', letterSpacing: '0.2em',
            color: '#C5A059', textTransform: 'uppercase',
            marginBottom: '10px',
          }}>
            Cuentas de prueba
          </div>
          <div style={{ fontSize: '12px', color: '#555', lineHeight: '2' }}>
            👤 Cliente: client@barbershop.com / Client123!<br />
            🔧 Admin: admin@barbershop.com / Admin123!
          </div>
        </div>

      </div>
    </div>
  );
}