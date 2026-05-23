import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { servicesApi } from '../services/endpoints';

export default function Servicios() {
  const [servicios, setServicios] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    servicesApi.list()
      .then(setServicios)
      .finally(() => setLoading(false));
  }, []);

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '80px 40px' }}>

      {/* encabezado */}
      <div className="fadeup" style={{ marginBottom: '60px' }}>
        <div style={{
          fontFamily: "'Oswald', sans-serif",
          fontSize: '11px', letterSpacing: '0.3em',
          color: '#C5A059', textTransform: 'uppercase',
          marginBottom: '12px',
        }}>
          Lo que ofrecemos
        </div>
        <h1 style={{
          fontFamily: "'Playfair Display', serif",
          fontSize: 'clamp(36px, 5vw, 60px)',
          fontWeight: '700', letterSpacing: '-0.02em',
          marginBottom: '16px',
        }}>
          Nuestros Servicios
        </h1>
        <p style={{ color: '#666', fontSize: '15px', maxWidth: '500px', lineHeight: '1.8' }}>
          Cada servicio es un ritual. Elige el que más se adapte a lo que necesitas
          y reserva tu hora en minutos.
        </p>
      </div>

      {/* grid de servicios */}
      {loading ? (
        <div style={{ color: '#555', textAlign: 'center', padding: '80px' }}>
          Cargando servicios...
        </div>
      ) : (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
          gap: '24px',
        }}>
          {servicios.map((s, i) => (
            <div key={s.id} className="card fadeup" style={{
              padding: '36px',
              animationDelay: `${i * 0.08}s`,
            }}>
              {/* duración */}
              <div style={{
                fontFamily: "'Oswald', sans-serif",
                fontSize: '10px', letterSpacing: '0.25em',
                color: '#C5A059', textTransform: 'uppercase',
                marginBottom: '14px',
              }}>
                ⏱ {s.durationMinutes} minutos
              </div>

              {/* nombre */}
              <h3 style={{
                fontFamily: "'Playfair Display', serif",
                fontSize: '24px', fontWeight: '700',
                marginBottom: '12px', color: '#F3F2EE',
              }}>
                {s.name}
              </h3>

              {/* descripción */}
              <p style={{
                color: '#666', fontSize: '13px',
                lineHeight: '1.7', marginBottom: '24px',
              }}>
                {s.description}
              </p>

              {/* línea divisora */}
              <div style={{
                height: '1px',
                background: 'linear-gradient(90deg, #C5A059, transparent)',
                marginBottom: '20px',
              }} />

              {/* precio */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{
                  fontFamily: "'Playfair Display', serif",
                  fontSize: '32px', fontWeight: '700',
                  color: '#C5A059',
                }}>
                  ${Number(s.price).toFixed(0)}
                </div>
                <Link to="/reservar" className="btn-gold" style={{
                  padding: '10px 20px', fontSize: '12px',
                }}>
                  Reservar
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* CTA final */}
      <div style={{ marginTop: '80px', textAlign: 'center' }}>
        <div style={{
          fontFamily: "'Oswald', sans-serif",
          fontSize: '11px', letterSpacing: '0.3em',
          color: '#C5A059', textTransform: 'uppercase',
          marginBottom: '16px',
        }}>
          ¿Listo?
        </div>
        <h2 style={{
          fontFamily: "'Playfair Display', serif",
          fontSize: '36px', fontWeight: '700',
          marginBottom: '20px',
        }}>
          Elige tu servicio y reserva ahora
        </h2>
        <Link to="/reservar" className="btn-gold" style={{ fontSize: '14px', padding: '14px 36px' }}>
          Reservar mi hora →
        </Link>
      </div>

    </div>
  );
} 