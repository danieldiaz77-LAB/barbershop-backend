import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();

  // rol siempre CLIENT, no se expone al usuario
  const [form, setForm] = useState({
    fullName: '', email: '', password: '', phone: '', role: 'CLIENT',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const change = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    setError(''); setLoading(true);
    try {
      await register(form);
      navigate('/');
    } catch (err) {
      setError(err?.response?.data?.message || 'Error al registrarse');
    } finally { setLoading(false); }
  };

  const labelStyle = {
    fontFamily: "'Oswald', sans-serif",
    fontSize: '11px', letterSpacing: '0.2em',
    color: '#666', textTransform: 'uppercase',
    display: 'block', marginBottom: '8px',
  };

  return (
    <div style={{
      minHeight: '80vh', display: 'flex',
      alignItems: 'center', justifyContent: 'center',
      padding: '40px 20px',
    }}>
      <div className="fadeup" style={{ width: '100%', maxWidth: '420px' }}>

        <div style={{ marginBottom: '40px' }}>
          <div style={{
            fontFamily: "'Oswald', sans-serif",
            fontSize: '11px', letterSpacing: '0.3em',
            color: '#C5A059', textTransform: 'uppercase',
            marginBottom: '12px',
          }}>
            Únete
          </div>
          <h1 style={{
            fontFamily: "'Playfair Display', serif",
            fontSize: '40px', fontWeight: '700',
            letterSpacing: '-0.02em',
          }}>
            Crear cuenta
          </h1>
        </div>

        <form onSubmit={onSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div>
            <label style={labelStyle}>Nombre completo</label>
            <input className="input-dark" value={form.fullName}
              onChange={change('fullName')} required placeholder="Tu nombre" />
          </div>
          <div>
            <label style={labelStyle}>Email</label>
            <input type="email" className="input-dark" value={form.email}
              onChange={change('email')} required placeholder="tu@email.com" />
          </div>
          <div>
            <label style={labelStyle}>Teléfono</label>
            <input className="input-dark" value={form.phone}
              onChange={change('phone')} placeholder="+56 9 1234 5678" />
          </div>
          <div>
            <label style={labelStyle}>Contraseña</label>
            <input type="password" minLength={6} className="input-dark"
              value={form.password} onChange={change('password')}
              required placeholder="Mínimo 6 caracteres" />
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

          <button type="submit" disabled={loading} className="btn-gold"
            style={{ width: '100%', justifyContent: 'center', marginTop: '8px' }}>
            {loading ? 'Creando cuenta...' : 'Crear cuenta →'}
          </button>
        </form>

        <div style={{ marginTop: '24px', textAlign: 'center', color: '#555', fontSize: '13px' }}>
          ¿Ya tienes cuenta?{' '}
          <Link to="/login" style={{ color: '#C5A059' }}>Inicia sesión</Link>
        </div>

      </div>
    </div>
  );
}