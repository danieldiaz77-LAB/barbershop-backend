import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const NOMBRE    = 'BLADE & CO.';
const TELEFONO  = '+56 9 1234 5678';
const HORARIO   = 'Lun — Sáb  09:00 – 20:00';
const INSTAGRAM = 'https://www.instagram.com/elpipebarber?igsh=MXN5eWF6bzZvZnoy';

const gold = '#C5A059';

/* ── íconos SVG inline (sin emojis) ── */
const PhoneIcon = () => (
  <svg width="12" height="12" viewBox="0 0 24 24" fill="none"
    stroke={gold} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.88 12a19.79 19.79 0 0 1-3-8.59A2 2 0 0 1 4 1.18h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 8.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
  </svg>
);

const ClockIcon = () => (
  <svg width="12" height="12" viewBox="0 0 24 24" fill="none"
    stroke="#555" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
  </svg>
);

const IgIcon = () => (
  <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
    stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <rect x="2" y="2" width="20" height="20" rx="5" ry="5"/>
    <circle cx="12" cy="12" r="4"/>
    <circle cx="17.5" cy="6.5" r="1" fill="currentColor" stroke="none"/>
  </svg>
);

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const isAdmin = user?.role === 'BARBER_ADMIN';

  return (
    <header>

      {/* ── BARRA SUPERIOR ── */}
      <div style={{
        background: '#050505',
        borderBottom: '1px solid rgba(197,160,89,0.12)',
        padding: '0 48px',
        height: '36px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
      }}>

        {/* izquierda: teléfono + horario */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '28px' }}>
          <a href={`tel:${TELEFONO.replace(/\s/g,'')}`}
            style={{
              display: 'flex', alignItems: 'center', gap: '7px',
              fontFamily: "'Oswald', sans-serif",
              fontSize: '11px', letterSpacing: '0.12em',
              color: '#888', textDecoration: 'none',
              transition: 'color 0.2s',
            }}
            onMouseOver={e => e.currentTarget.style.color = gold}
            onMouseOut={e  => e.currentTarget.style.color = '#888'}
          >
            <PhoneIcon />
            <span style={{ color: gold }}>{TELEFONO}</span>
          </a>

          {/* divisor vertical */}
          <div style={{ width: '1px', height: '14px', background: '#222' }} />

          <span style={{
            display: 'flex', alignItems: 'center', gap: '7px',
            fontFamily: "'Oswald', sans-serif",
            fontSize: '11px', letterSpacing: '0.12em', color: '#555',
          }}>
            <ClockIcon />
            {HORARIO}
          </span>
        </div>

        {/* derecha: Instagram */}
        <a href={INSTAGRAM} target="_blank" rel="noreferrer"
          style={{
            display: 'flex', alignItems: 'center', gap: '6px',
            fontFamily: "'Oswald', sans-serif",
            fontSize: '11px', letterSpacing: '0.18em', textTransform: 'uppercase',
            color: '#555', textDecoration: 'none',
            transition: 'color 0.2s',
          }}
          onMouseOver={e => e.currentTarget.style.color = gold}
          onMouseOut={e  => e.currentTarget.style.color = '#555'}
        >
          <IgIcon />
          Instagram
        </a>
      </div>

      {/* ── NAVBAR PRINCIPAL ── */}
      <nav className="glass" style={{
        position: 'sticky', top: 0, zIndex: 50,
        padding: '0 48px',
        height: '64px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      }}>

        {/* logo */}
        <Link to="/" style={{ display: 'flex', flexDirection: 'column', lineHeight: '1', textDecoration: 'none' }}>
          <span style={{
            fontFamily: "'Playfair Display', serif",
            fontSize: '21px', fontWeight: '700',
            color: gold, letterSpacing: '-0.02em',
          }}>
            {NOMBRE}
          </span>
          <span style={{
            fontFamily: "'Oswald', sans-serif",
            fontSize: '9px', letterSpacing: '0.22em',
            color: '#444', textTransform: 'uppercase',
            marginTop: '2px',
          }}>
            Barbería Premium
          </span>
        </Link>

        {/* links centrales */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '2px' }}>
          {[
            { to: '/',          label: 'Inicio',      end: true },
            { to: '/servicios', label: 'Servicios' },
            { to: '/reservar',  label: 'Reservar'  },
            ...(user && !isAdmin ? [{ to: '/mis-citas', label: 'Mis Citas' }] : []),
            ...(isAdmin          ? [{ to: '/admin',     label: 'Panel Admin' }] : []),
          ].map(({ to, label, end }) => (
            <NavLink
              key={to} to={to} end={end}
              style={({ isActive }) => ({
                color: isActive ? gold : '#666',
                padding: '8px 16px',
                fontSize: '11px',
                letterSpacing: '0.18em',
                textTransform: 'uppercase',
                fontWeight: '500',
                transition: 'color 0.2s',
                fontFamily: "'Oswald', sans-serif",
                position: 'relative',
                textDecoration: 'none',
              })}
            >
              {({ isActive }) => (
                <>
                  {label}
                  {isActive && (
                    <span style={{
                      position: 'absolute', bottom: '-4px', left: '16px', right: '16px',
                      height: '1px', background: gold,
                    }} />
                  )}
                </>
              )}
            </NavLink>
          ))}
        </div>

        {/* auth */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          {!user ? (
            <>
              <Link to="/login" style={{
                color: '#666', fontSize: '11px',
                letterSpacing: '0.18em', textTransform: 'uppercase',
                fontFamily: "'Oswald', sans-serif", textDecoration: 'none',
                transition: 'color 0.2s',
              }}
                onMouseOver={e => e.currentTarget.style.color = '#fff'}
                onMouseOut={e  => e.currentTarget.style.color = '#666'}
              >
                Login
              </Link>
              <Link to="/reservar" className="btn-gold" style={{ padding: '9px 20px', fontSize: '11px' }}>
                Reservar →
              </Link>
            </>
          ) : (
            <>
              <span style={{
                fontFamily: "'Oswald', sans-serif",
                fontSize: '11px', color: gold,
                letterSpacing: '0.15em', textTransform: 'uppercase',
              }}>
                {user.fullName?.split(' ')[0]}
              </span>
              <button
                onClick={() => { logout(); navigate('/'); }}
                className="btn-ghost"
                style={{ padding: '7px 16px', fontSize: '11px' }}
              >
                Salir
              </button>
            </>
          )}
        </div>
      </nav>
    </header>
  );
}