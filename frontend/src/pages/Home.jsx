import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { barbersApi } from '../services/endpoints';

const NOMBRE = 'BLADE & CO.';

const HERO_IMG      = 'https://images.unsplash.com/photo-1599351431202-1e0f0137899a?crop=entropy&cs=srgb&fm=jpg&q=90&w=1920';
const BARBER_WORKING = 'https://images.unsplash.com/photo-1503951914875-452162b0f3f1?crop=entropy&cs=srgb&fm=jpg&q=85&w=900';
const TOOLS_IMG     = 'https://images.unsplash.com/photo-1621605815971-fbc98d665033?crop=entropy&cs=srgb&fm=jpg&q=85&w=900';
const INTERIOR_IMG  = 'https://images.unsplash.com/photo-1585747860715-2ba37e788b70?crop=entropy&cs=srgb&fm=jpg&q=85&w=900';

/* Fotos reales de los maestros */
const BARBER_FALLBACK_PHOTOS = [
  'https://dbarbers.cl/wp-content/uploads/2025/11/AAF05397-683x1024.jpg',
  'https://tse3.mm.bing.net/th/id/OIP.DXJL3MCQ8Lq3YHVMnuonIQHaHc?cb=thfvnextfalcon&rs=1&pid=ImgDetMain&o=7&rm=3',
];

const gold = '#C5A059';

export default function Home() {
  const [barberos, setBarberos] = useState([]);

  useEffect(() => {
    barbersApi.list().then(setBarberos).catch(() => {});
  }, []);

  return (
    <div style={{ overflowX: 'hidden' }}>

      {/* ══ HERO ══ */}
      <section style={{ position: 'relative', minHeight: '100vh', overflow: 'hidden' }}>
        <img src={HERO_IMG} alt="Barbería premium"
          style={{ position: 'absolute', inset: 0, width: '100%', height: '100%', objectFit: 'cover', objectPosition: 'center 30%' }}
        />
        <div style={{
          position: 'absolute', inset: 0,
          background: 'linear-gradient(100deg, rgba(0,0,0,0.93) 42%, rgba(0,0,0,0.25) 100%)',
        }} />
        <div style={{
          position: 'absolute', bottom: 0, left: 0, right: 0, height: '200px',
          background: 'linear-gradient(to top, #0A0A0A, transparent)',
        }} />

        {/* línea dorada vertical */}
        <div style={{
          position: 'absolute', left: '60px', top: '15%', height: '70%',
          width: '1px',
          background: `linear-gradient(to bottom, transparent, ${gold} 30%, ${gold} 70%, transparent)`,
        }} />

        <div className="fadeup" style={{
          position: 'relative', zIndex: 10,
          maxWidth: '1200px', margin: '0 auto',
          padding: '160px 100px 120px',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '28px' }}>
            <div style={{ width: '40px', height: '1px', background: gold }} />
            <span style={{ fontFamily: "'Oswald', sans-serif", fontSize: '11px', letterSpacing: '0.4em', color: gold, textTransform: 'uppercase' }}>
              Barbería Premium · Est. 2024
            </span>
          </div>

          <h1 style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(60px, 9vw, 120px)', fontWeight: '700', lineHeight: '0.92', letterSpacing: '-0.03em', color: '#F3F2EE', maxWidth: '650px', marginBottom: '0' }}>
            El arte
          </h1>
          <h1 style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(60px, 9vw, 120px)', fontWeight: '700', lineHeight: '0.92', letterSpacing: '-0.03em', color: gold, fontStyle: 'italic', maxWidth: '650px', marginBottom: '36px' }}>
            del corte.
          </h1>

          <p style={{ color: '#888', fontSize: '16px', lineHeight: '1.9', maxWidth: '380px', marginBottom: '52px', borderLeft: `2px solid ${gold}`, paddingLeft: '20px' }}>
            Maestros barberos, rituales clásicos y estilo moderno.
            Reserva tu hora en minutos y vive la experiencia.
          </p>

          <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', marginBottom: '80px' }}>
            <Link to="/reservar" className="btn-gold" style={{ padding: '16px 40px', fontSize: '13px' }}>
              Reservar ahora →
            </Link>
            <Link to="/servicios" className="btn-ghost" style={{ padding: '16px 40px', fontSize: '13px' }}>
              Ver servicios
            </Link>
          </div>

          {/* métricas */}
          <div style={{ display: 'flex', gap: '0' }}>
            {[
              ['10+', 'Años de oficio'],
              ['3',   'Maestros'],
              ['★ 5', 'Google'],
              ['500+','Clientes'],
            ].map(([n, l], i) => (
              <div key={l} style={{
                padding: '20px 32px',
                border: `1px solid rgba(197,160,89,0.25)`,
                borderRight: i < 3 ? 'none' : `1px solid rgba(197,160,89,0.25)`,
                background: 'rgba(0,0,0,0.45)',
              }}>
                <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '32px', color: gold, fontWeight: '700', lineHeight: '1' }}>{n}</div>
                <div style={{ fontFamily: "'Oswald', sans-serif", fontSize: '10px', color: '#444', letterSpacing: '0.2em', textTransform: 'uppercase', marginTop: '6px' }}>{l}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ══ BENTO GRID ══ */}
      <section style={{ maxWidth: '1200px', margin: '0 auto', padding: '80px 40px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 0.8fr 1fr', gridTemplateRows: '280px 280px', gap: '10px' }}>

          <div style={{ gridColumn: '1/2', gridRow: '1/3', position: 'relative', overflow: 'hidden', borderRadius: '4px' }}>
            <img src={INTERIOR_IMG} alt="Interior"
              style={{ width: '100%', height: '100%', objectFit: 'cover', transition: 'transform 0.7s ease' }}
              onMouseOver={e => e.currentTarget.style.transform = 'scale(1.05)'}
              onMouseOut={e  => e.currentTarget.style.transform = 'scale(1)'}
            />
            <div style={{ position: 'absolute', inset: 0, background: 'linear-gradient(to top, rgba(0,0,0,0.75) 35%, transparent)' }} />
            <div style={{ position: 'absolute', bottom: '28px', left: '28px' }}>
              <div style={{ fontFamily: "'Oswald', sans-serif", fontSize: '10px', color: gold, letterSpacing: '0.3em', textTransform: 'uppercase', marginBottom: '8px' }}>Ambiente</div>
              <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '26px', fontWeight: '700' }}>Espacio único</div>
            </div>
          </div>

          <div style={{ gridColumn: '2/3', gridRow: '1/2', background: gold, borderRadius: '4px', display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '36px' }}>
            <div style={{ fontFamily: "'Oswald', sans-serif", fontSize: '10px', letterSpacing: '0.3em', color: '#0A0A0A', textTransform: 'uppercase', marginBottom: '10px' }}>Nuestra promesa</div>
            <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '26px', fontWeight: '700', color: '#0A0A0A', lineHeight: '1.2' }}>Cada corte,<br />una obra de arte.</div>
          </div>

          <div style={{ gridColumn: '3/4', gridRow: '1/2', position: 'relative', overflow: 'hidden', borderRadius: '4px' }}>
            <img src={BARBER_WORKING} alt="Barbero trabajando" style={{ width: '100%', height: '100%', objectFit: 'cover', objectPosition: 'center top' }} />
            <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.2)' }} />
          </div>

          <div style={{ gridColumn: '2/3', gridRow: '2/3', position: 'relative', overflow: 'hidden', borderRadius: '4px' }}>
            <img src={TOOLS_IMG} alt="Herramientas" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
            <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.15)' }} />
          </div>

          <div style={{ gridColumn: '3/4', gridRow: '2/3', background: '#111', borderRadius: '4px', border: '1px solid #1a1a1a', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
            <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '80px', fontWeight: '700', color: gold, lineHeight: '1' }}>500+</div>
            <div style={{ fontFamily: "'Oswald', sans-serif", fontSize: '11px', letterSpacing: '0.3em', color: '#444', textTransform: 'uppercase', marginTop: '8px' }}>Clientes felices</div>
          </div>
        </div>
      </section>

      {/* ══ SERVICIOS ══ */}
      <section style={{ background: '#080808', borderTop: '1px solid #161616', borderBottom: '1px solid #161616', padding: '100px 0' }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '0 40px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', marginBottom: '56px', flexWrap: 'wrap', gap: '20px' }}>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '14px', marginBottom: '14px' }}>
                <div style={{ width: '36px', height: '1px', background: gold }} />
                <span style={{ fontFamily: "'Oswald', sans-serif", fontSize: '11px', letterSpacing: '0.35em', color: gold, textTransform: 'uppercase' }}>Lo que ofrecemos</span>
              </div>
              <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(36px, 5vw, 60px)', fontWeight: '700', letterSpacing: '-0.02em', margin: 0 }}>Nuestros Servicios</h2>
            </div>
            <Link to="/servicios" className="btn-ghost" style={{ fontSize: '12px', padding: '12px 24px' }}>Ver todos →</Link>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '0' }}>
            {[
              { nombre: 'Corte Clásico',    desc: 'Tijera y máquina, lavado y peinado profesional.',  precio: '$28', duracion: '45 min', num: '01', icon: '✂' },
              { nombre: 'Arreglo Barba',    desc: 'Escultura de barba con toalla caliente y aceites.', precio: '$18', duracion: '30 min', num: '02', icon: '🪒' },
              { nombre: 'Afeitado Clásico', desc: 'Navaja recta, ritual de toalla caliente.',          precio: '$32', duracion: '45 min', num: '03', icon: '💈' },
              { nombre: 'Combo Completo',   desc: 'Corte + barba. La experiencia completa.',           precio: '$42', duracion: '75 min', num: '04', icon: '👑' },
            ].map((s, i) => (
              <div key={s.nombre}
                style={{ padding: '44px 32px', borderRight: i < 3 ? '1px solid #1a1a1a' : 'none', borderTop: '2px solid transparent', transition: 'all 0.3s', position: 'relative' }}
                onMouseOver={e => { e.currentTarget.style.background = '#0f0f0f'; e.currentTarget.style.borderTopColor = gold; }}
                onMouseOut={e  => { e.currentTarget.style.background = 'transparent'; e.currentTarget.style.borderTopColor = 'transparent'; }}
              >
                <div style={{ position: 'absolute', top: '16px', right: '20px', fontFamily: "'Playfair Display', serif", fontSize: '80px', fontWeight: '700', color: 'rgba(197,160,89,0.06)', lineHeight: '1', userSelect: 'none' }}>{s.num}</div>
                <div style={{ fontSize: '28px', marginBottom: '20px' }}>{s.icon}</div>
                <div style={{ fontFamily: "'Oswald', sans-serif", fontSize: '10px', letterSpacing: '0.25em', color: gold, textTransform: 'uppercase', marginBottom: '10px' }}>{s.duracion}</div>
                <h3 style={{ fontFamily: "'Playfair Display', serif", fontSize: '20px', marginBottom: '12px', lineHeight: '1.2' }}>{s.nombre}</h3>
                <p style={{ color: '#555', fontSize: '13px', lineHeight: '1.75', marginBottom: '28px' }}>{s.desc}</p>
                <div style={{ width: '32px', height: '1px', background: gold, marginBottom: '16px' }} />
                <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '30px', fontWeight: '700', color: gold }}>{s.precio}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ══ BARBEROS — ESPECTACULAR ══ */}
      {barberos.length > 0 && (
        <section style={{ background: '#050505' }}>

          {/* ── TÍTULO lateral izquierdo pegado al borde ── */}
          <div style={{ display: 'grid', gridTemplateColumns: '220px 1fr', borderTop: '1px solid #111' }}>

            {/* columna izquierda — label vertical */}
            <div style={{
              borderRight: '1px solid #111',
              display: 'flex', flexDirection: 'column',
              justifyContent: 'space-between',
              padding: '48px 32px',
            }}>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '32px' }}>
                  <div style={{ width: '20px', height: '1px', background: gold }} />
                  <span style={{ fontFamily: "'Oswald', sans-serif", fontSize: '9px', letterSpacing: '0.4em', color: gold, textTransform: 'uppercase' }}>
                    Equipo
                  </span>
                </div>
                {/* título vertical */}
                <div style={{
                  writingMode: 'vertical-rl',
                  transform: 'rotate(180deg)',
                  fontFamily: "'Playfair Display', serif",
                  fontSize: '11px', letterSpacing: '0.2em',
                  color: '#2a2a2a', textTransform: 'uppercase',
                  marginBottom: '0',
                }}>
                  Los Maestros · Blade & Co.
                </div>
              </div>
              <div style={{ fontFamily: "'Oswald', sans-serif", fontSize: '9px', letterSpacing: '0.2em', color: '#222', textTransform: 'uppercase' }}>
                {barberos.length} barbers
              </div>
            </div>

            {/* columna derecha — filas de barberos */}
            <div>
              {barberos.map((b, idx) => {
                const foto    = b.photoUrl || BARBER_FALLBACK_PHOTOS[idx % BARBER_FALLBACK_PHOTOS.length];
                const num     = String(idx + 1).padStart(2, '0');
                const isEven  = idx % 2 === 0;

                return (
                  <div
                    key={b.id}
                    style={{
                      display: 'grid',
                      gridTemplateColumns: isEven ? '55% 45%' : '45% 55%',
                      borderBottom: idx < barberos.length - 1 ? '1px solid #111' : 'none',
                      height: '420px',
                    }}
                    onMouseOver={e => {
                      e.currentTarget.querySelector('.rp').style.transform = 'scale(1.04)';
                      e.currentTarget.querySelector('.rline').style.width  = '72px';
                    }}
                    onMouseOut={e => {
                      e.currentTarget.querySelector('.rp').style.transform = 'scale(1)';
                      e.currentTarget.querySelector('.rline').style.width  = '32px';
                    }}
                  >
                    {/* FOTO */}
                    <div style={{
                      order: isEven ? 0 : 1,
                      position: 'relative', overflow: 'hidden',
                    }}>
                      <img className="rp" src={foto} alt={b.name}
                        style={{
                          width: '100%', height: '100%',
                          objectFit: 'cover', objectPosition: 'center 15%',
                          transition: 'transform 0.9s cubic-bezier(.2,.7,.2,1)',
                          display: 'block',
                        }}
                      />
                      {/* overlay muy sutil */}
                      <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.18)' }} />

                      {/* número grande flotante */}
                      <div style={{
                        position: 'absolute',
                        top: '20px',
                        left: isEven ? 'auto' : '20px',
                        right: isEven ? '20px' : 'auto',
                        fontFamily: "'Playfair Display', serif",
                        fontSize: '120px', fontWeight: '700',
                        color: 'rgba(255,255,255,0.06)',
                        lineHeight: '1', userSelect: 'none',
                      }}>
                        {num}
                      </div>
                    </div>

                    {/* INFO */}
                    <div style={{
                      order: isEven ? 1 : 0,
                      display: 'flex', flexDirection: 'column', justifyContent: 'center',
                      padding: '52px 56px',
                      borderLeft: isEven ? '1px solid #111' : 'none',
                      borderRight: isEven ? 'none' : '1px solid #111',
                      background: '#050505',
                    }}>
                      {/* número + specialty */}
                      <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '24px' }}>
                        <span style={{ fontFamily: "'Playfair Display', serif", fontSize: '11px', color: gold, letterSpacing: '0.05em' }}>
                          {num}
                        </span>
                        <div style={{ width: '20px', height: '1px', background: 'rgba(197,160,89,0.4)' }} />
                        <span style={{ fontFamily: "'Oswald', sans-serif", fontSize: '9px', letterSpacing: '0.35em', color: gold, textTransform: 'uppercase' }}>
                          {b.specialty}
                        </span>
                      </div>

                      {/* nombre */}
                      <h3 style={{
                        fontFamily: "'Playfair Display', serif",
                        fontSize: 'clamp(36px, 4vw, 58px)',
                        fontWeight: '700', color: '#F3F2EE',
                        lineHeight: '0.95', letterSpacing: '-0.03em',
                        marginBottom: '28px',
                      }}>
                        {b.name}
                      </h3>

                      {/* línea animada */}
                      <div className="rline" style={{
                        height: '1px', background: gold,
                        width: '32px', marginBottom: '28px',
                        transition: 'width 0.6s cubic-bezier(.2,.7,.2,1)',
                      }} />

                      {/* bio */}
                      {b.bio && (
                        <p style={{ color: '#555', fontSize: '13px', lineHeight: '1.85', marginBottom: '32px', maxWidth: '280px' }}>
                          {b.bio}
                        </p>
                      )}

                      {/* horario */}
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '32px' }}>
                        <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke={gold} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                        </svg>
                        <span style={{ fontFamily: "'Oswald', sans-serif", fontSize: '10px', letterSpacing: '0.2em', color: '#3a3a3a', textTransform: 'uppercase' }}>
                          {b.workStart} — {b.workEnd}
                        </span>
                      </div>

                      {/* link reservar */}
                      <Link to="/reservar" style={{
                        display: 'inline-flex', alignItems: 'center', gap: '10px',
                        fontFamily: "'Oswald', sans-serif",
                        fontSize: '10px', letterSpacing: '0.3em', textTransform: 'uppercase',
                        color: gold, textDecoration: 'none',
                        transition: 'gap 0.3s ease',
                      }}
                        onMouseOver={e => e.currentTarget.style.gap = '18px'}
                        onMouseOut={e  => e.currentTarget.style.gap = '10px'}
                      >
                        Reservar con {b.name.split(' ')[0]}
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                          <line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/>
                        </svg>
                      </Link>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

        </section>
      )}

      {/* ══ SPLIT FILOSOFÍA ══ */}
      <section style={{ background: '#080808', borderTop: '1px solid #161616' }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '100px 40px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '80px', alignItems: 'center' }}>

          <div style={{ position: 'relative' }}>
            <img src={TOOLS_IMG} alt="Herramientas premium" style={{ width: '100%', borderRadius: '4px', border: '1px solid #1a1a1a', display: 'block' }} />
            <div style={{ position: 'absolute', bottom: '-16px', right: '-16px', background: gold, color: '#0A0A0A', padding: '20px 24px', fontFamily: "'Oswald', sans-serif", fontSize: '12px', letterSpacing: '0.12em', textTransform: 'uppercase', fontWeight: '700', lineHeight: '1.5' }}>
              10+ años<br />de experiencia
            </div>
          </div>

          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '14px', marginBottom: '20px' }}>
              <div style={{ width: '36px', height: '1px', background: gold }} />
              <span style={{ fontFamily: "'Oswald', sans-serif", fontSize: '11px', letterSpacing: '0.35em', color: gold, textTransform: 'uppercase' }}>Nuestra filosofía</span>
            </div>
            <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(30px, 4vw, 48px)', fontWeight: '700', lineHeight: '1.15', letterSpacing: '-0.02em', marginBottom: '24px' }}>
              Las herramientas importan.<br />
              <span style={{ color: gold, fontStyle: 'italic' }}>Las manos, más aún.</span>
            </h2>
            <p style={{ color: '#666', lineHeight: '1.9', fontSize: '14px', marginBottom: '16px' }}>
              Cada navaja, peine y clipper tiene historia. Pero son los años de experiencia detrás de cada corte lo que convierte un servicio simple en un ritual que recordarás.
            </p>
            <p style={{ color: '#444', lineHeight: '1.9', fontSize: '14px', marginBottom: '40px' }}>
              En {NOMBRE} no solo cortamos pelo — creamos una experiencia que te hace volver.
            </p>
            <Link to="/reservar" className="btn-gold" style={{ fontSize: '13px', padding: '14px 36px' }}>Reserva tu hora →</Link>
          </div>
        </div>
      </section>

      {/* ══ CTA FINAL ══ */}
      <section style={{ position: 'relative', overflow: 'hidden', padding: '140px 40px', textAlign: 'center' }}>
        <img src={HERO_IMG} alt="" aria-hidden="true"
          style={{ position: 'absolute', inset: 0, width: '100%', height: '100%', objectFit: 'cover', objectPosition: 'center 30%', filter: 'blur(10px) brightness(0.12)' }}
        />
        <div style={{ position: 'absolute', inset: '40px', border: `1px solid rgba(197,160,89,0.15)`, pointerEvents: 'none' }} />

        <div style={{ position: 'relative', zIndex: 10, maxWidth: '580px', margin: '0 auto' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '16px', marginBottom: '24px' }}>
            <div style={{ width: '36px', height: '1px', background: gold }} />
            <span style={{ fontFamily: "'Oswald', sans-serif", fontSize: '11px', letterSpacing: '0.35em', color: gold, textTransform: 'uppercase' }}>¿Listo para el cambio?</span>
            <div style={{ width: '36px', height: '1px', background: gold }} />
          </div>
          <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(44px, 7vw, 80px)', fontWeight: '700', letterSpacing: '-0.03em', lineHeight: '1', marginBottom: '24px' }}>
            Reserva tu<br />
            <span style={{ color: gold, fontStyle: 'italic' }}>cita hoy.</span>
          </h2>
          <p style={{ color: '#666', fontSize: '15px', lineHeight: '1.85', marginBottom: '44px' }}>
            Elige tu barbero, selecciona el servicio y escoge el horario.<br />
            Todo en menos de 2 minutos.
          </p>
          <Link to="/reservar" className="btn-gold" style={{ fontSize: '14px', padding: '18px 52px' }}>
            Reservar ahora →
          </Link>
        </div>
      </section>

    </div>
  );
}