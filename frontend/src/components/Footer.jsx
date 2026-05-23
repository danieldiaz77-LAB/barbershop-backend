import { Link } from 'react-router-dom';

const NOMBRE    = 'BLADE & CO.';
const INSTAGRAM = 'https://www.instagram.com/elpipebarber?igsh=MXN5eWF6bzZvZnoy';
const gold      = '#C5A059';

export default function Footer() {
  return (
    <footer style={{ background: '#050505', borderTop: '1px solid #111', marginTop: '0' }}>

      {/* ── BLOQUE PRINCIPAL ── */}
      <div style={{
        maxWidth: '1200px', margin: '0 auto',
        padding: '72px 48px 56px',
        display: 'grid',
        gridTemplateColumns: '1.4fr 1fr 1fr',
        gap: '60px',
        borderBottom: '1px solid #111',
      }}>

        {/* columna 1 — marca + tagline */}
        <div>
          <div style={{
            fontFamily: "'Playfair Display', serif",
            fontSize: '28px', fontWeight: '700',
            color: gold, letterSpacing: '-0.02em',
            marginBottom: '4px',
          }}>
            {NOMBRE}
          </div>
          <div style={{
            fontFamily: "'Oswald', sans-serif",
            fontSize: '9px', letterSpacing: '0.22em',
            color: '#333', textTransform: 'uppercase',
            marginBottom: '20px',
          }}>
            Barbería Premium · Est. 2024
          </div>
          <p style={{
            color: '#444', fontSize: '13px',
            lineHeight: '1.85', maxWidth: '260px',
          }}>
            Experiencia premium en cada corte.<br />
            Tradición y estilo en un solo lugar.
          </p>

          {/* línea dorada decorativa */}
          <div style={{ width: '36px', height: '1px', background: gold, margin: '24px 0' }} />

          {/* redes sociales */}
          <a href={INSTAGRAM} target="_blank" rel="noreferrer"
            style={{
              display: 'inline-flex', alignItems: 'center', gap: '8px',
              fontFamily: "'Oswald', sans-serif",
              fontSize: '10px', letterSpacing: '0.2em', textTransform: 'uppercase',
              color: '#555', textDecoration: 'none',
              transition: 'color 0.2s',
            }}
            onMouseOver={e => e.currentTarget.style.color = gold}
            onMouseOut={e  => e.currentTarget.style.color = '#555'}
          >
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <rect x="2" y="2" width="20" height="20" rx="5" ry="5"/>
              <circle cx="12" cy="12" r="4"/>
              <circle cx="17.5" cy="6.5" r="1" fill="currentColor" stroke="none"/>
            </svg>
            @elpipebarber
          </a>
        </div>

        {/* columna 2 — horarios */}
        <div>
          <div style={{
            fontFamily: "'Oswald', sans-serif",
            fontSize: '10px', letterSpacing: '0.25em',
            color: gold, textTransform: 'uppercase',
            marginBottom: '20px',
          }}>
            Horarios
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            {[
              { dia: 'Lunes — Viernes', hora: '09:00 — 19:00', open: true },
              { dia: 'Sábado',          hora: '09:00 — 20:00', open: true },
              { dia: 'Domingo',         hora: 'Cerrado',       open: false },
            ].map(({ dia, hora, open }) => (
              <div key={dia} style={{
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                borderBottom: '1px solid #0f0f0f',
                paddingBottom: '10px',
              }}>
                <span style={{ fontFamily: "'Manrope', sans-serif", fontSize: '12px', color: '#555' }}>
                  {dia}
                </span>
                <span style={{
                  fontFamily: "'Oswald', sans-serif",
                  fontSize: '12px', letterSpacing: '0.06em',
                  color: open ? '#888' : '#2a2a2a',
                }}>
                  {hora}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* columna 3 — contacto + nav */}
        <div>
          <div style={{
            fontFamily: "'Oswald', sans-serif",
            fontSize: '10px', letterSpacing: '0.25em',
            color: gold, textTransform: 'uppercase',
            marginBottom: '20px',
          }}>
            Contacto
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '36px' }}>
            {[
              { icon: 'phone',  texto: '+56 9 1234 5678',       href: 'tel:+56912345678' },
              { icon: 'pin',    texto: 'Av. Principal 123, Santiago', href: null },
              { icon: 'mail',   texto: 'hola@bladeandco.cl',    href: 'mailto:hola@bladeandco.cl' },
            ].map(({ icon, texto, href }) => {
              const svgMap = {
                phone: <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke={gold} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.88 12a19.79 19.79 0 0 1-3-8.59A2 2 0 0 1 4 1.18h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 8.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/></svg>,
                pin:   <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke={gold} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>,
                mail:  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke={gold} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>,
              };
              const inner = (
                <span style={{
                  display: 'flex', alignItems: 'flex-start', gap: '10px',
                  fontSize: '12px', color: '#555',
                  transition: 'color 0.2s',
                }}>
                  <span style={{ marginTop: '1px', flexShrink: 0 }}>{svgMap[icon]}</span>
                  {texto}
                </span>
              );
              return href ? (
                <a key={icon} href={href} style={{ textDecoration: 'none' }}
                  onMouseOver={e => e.currentTarget.querySelector('span').style.color = '#999'}
                  onMouseOut={e  => e.currentTarget.querySelector('span').style.color = '#555'}
                >
                  {inner}
                </a>
              ) : (
                <div key={icon}>{inner}</div>
              );
            })}
          </div>

          {/* nav rápida */}
          <div style={{
            fontFamily: "'Oswald', sans-serif",
            fontSize: '10px', letterSpacing: '0.25em',
            color: gold, textTransform: 'uppercase',
            marginBottom: '14px',
          }}>
            Navegación
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            {[
              { to: '/',          label: 'Inicio'    },
              { to: '/servicios', label: 'Servicios' },
              { to: '/reservar',  label: 'Reservar'  },
            ].map(({ to, label }) => (
              <Link key={to} to={to} style={{
                fontFamily: "'Oswald', sans-serif",
                fontSize: '11px', letterSpacing: '0.12em', textTransform: 'uppercase',
                color: '#3a3a3a', textDecoration: 'none',
                transition: 'color 0.2s',
              }}
                onMouseOver={e => e.currentTarget.style.color = gold}
                onMouseOut={e  => e.currentTarget.style.color = '#3a3a3a'}
              >
                {label}
              </Link>
            ))}
          </div>
        </div>
      </div>

      {/* ── BOTTOM BAR ── */}
      <div style={{
        maxWidth: '1200px', margin: '0 auto',
        padding: '18px 48px',
        display: 'flex', justifyContent: 'space-between', alignItems: 'center',
        flexWrap: 'wrap', gap: '8px',
      }}>
        <span style={{
          fontFamily: "'Oswald', sans-serif",
          fontSize: '10px', letterSpacing: '0.15em',
          color: '#2a2a2a', textTransform: 'uppercase',
        }}>
          © {new Date().getFullYear()} {NOMBRE} — Todos los derechos reservados.
        </span>
        <div style={{ width: '24px', height: '1px', background: '#1a1a1a' }} />
        <span style={{
          fontFamily: "'Oswald', sans-serif",
          fontSize: '10px', letterSpacing: '0.12em',
          color: '#222', textTransform: 'uppercase',
        }}>
          Barbería Premium · Santiago, Chile
        </span>
      </div>

    </footer>
  );
}