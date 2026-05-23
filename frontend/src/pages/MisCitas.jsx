import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { appointmentsApi } from '../services/endpoints';

// colores según estado de la cita
const estadoColor = {
  PENDING:   { color: '#fbbf24', bg: 'rgba(251,191,36,0.08)',   label: 'Pendiente' },
  PAID:      { color: '#4ade80', bg: 'rgba(74,222,128,0.08)',   label: 'Pagada' },
  COMPLETED: { color: '#60a5fa', bg: 'rgba(96,165,250,0.08)',   label: 'Completada' },
  CANCELLED: { color: '#f87171', bg: 'rgba(248,113,113,0.08)',  label: 'Cancelada' },
};

export default function MisCitas() {
  const [citas, setCitas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [cancelando, setCancelando] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    appointmentsApi.mine()
      .then(setCitas)
      .finally(() => setLoading(false));
  }, []);

  const cancelar = async (id) => {
    if (!confirm('¿Cancelar esta cita?')) return;
    setCancelando(id);
    try {
      const updated = await appointmentsApi.cancel(id);
      setCitas(prev => prev.map(c => c.id === id ? updated : c));
    } catch {
      setError('No se pudo cancelar la cita');
    } finally { setCancelando(null); }
  };


  const formatFecha = (dt) => new Date(dt).toLocaleDateString('es-CL', {
    weekday: 'short', day: 'numeric', month: 'long', year: 'numeric',
  });

  const formatHora = (dt) => new Date(dt).toLocaleTimeString('es-CL', {
    hour: '2-digit', minute: '2-digit',
  });

  return (
    <div style={{ maxWidth: '900px', margin: '0 auto', padding: '60px 40px' }}>

      {/* encabezado */}
      <div className="fadeup" style={{ marginBottom: '48px' }}>
        <div style={{
          fontFamily: "'Oswald', sans-serif",
          fontSize: '11px', letterSpacing: '0.3em',
          color: '#C5A059', textTransform: 'uppercase',
          marginBottom: '12px',
        }}>
          Tu historial
        </div>
        <h1 style={{
          fontFamily: "'Playfair Display', serif",
          fontSize: '48px', fontWeight: '700',
          letterSpacing: '-0.02em',
        }}>
          Mis Citas
        </h1>
      </div>

      {error && (
        <div style={{
          background: 'rgba(153,27,27,0.2)',
          border: '1px solid rgba(248,113,113,0.3)',
          color: '#f87171', padding: '12px 16px',
          borderRadius: '2px', fontSize: '13px',
          marginBottom: '24px',
        }}>
          {error}
        </div>
      )}

      {loading ? (
        <div style={{ color: '#555', textAlign: 'center', padding: '80px' }}>
          Cargando citas...
        </div>
      ) : citas.length === 0 ? (
        /* sin citas */
        <div style={{
          textAlign: 'center', padding: '80px 40px',
          border: '1px solid #1a1a1a', borderRadius: '4px',
        }}>
          <div style={{ fontSize: '48px', marginBottom: '16px' }}>✂️</div>
          <h3 style={{
            fontFamily: "'Playfair Display', serif",
            fontSize: '24px', marginBottom: '12px',
          }}>
            Aún no tienes citas
          </h3>
          <p style={{ color: '#555', marginBottom: '28px', fontSize: '14px' }}>
            Reserva tu primera cita y vive la experiencia BLADE & CO.
          </p>
          <Link to="/reservar" className="btn-gold">
            Reservar ahora →
          </Link>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {citas.map((c) => {
            const estado = estadoColor[c.status] || estadoColor.PENDING;
            return (
              <div key={c.id} className="card fadeup" style={{ padding: '0', overflow: 'hidden' }}>

                {/* barra de color según estado */}
                <div style={{ height: '3px', background: estado.color }} />

                <div style={{ padding: '24px 28px' }}>
                  <div style={{
                    display: 'flex', justifyContent: 'space-between',
                    alignItems: 'flex-start', flexWrap: 'wrap', gap: '12px',
                  }}>

                    {/* info principal */}
                    <div>
                      <h3 style={{
                        fontFamily: "'Playfair Display', serif",
                        fontSize: '22px', marginBottom: '6px',
                      }}>
                        {c.serviceName}
                      </h3>
                      <div style={{ color: '#666', fontSize: '13px', lineHeight: '1.8' }}>
                        ✂️ {c.barberName} &nbsp;·&nbsp;
                        📅 {formatFecha(c.startTime)} &nbsp;·&nbsp;
                        🕐 {formatHora(c.startTime)} — {formatHora(c.endTime)}
                      </div>
                    </div>

                    {/* precio y estado */}
                    <div style={{ textAlign: 'right' }}>
                      <div style={{
                        fontFamily: "'Playfair Display', serif",
                        fontSize: '24px', fontWeight: '700',
                        color: '#C5A059', marginBottom: '8px',
                      }}>
                        ${Number(c.servicePrice).toFixed(2)}
                      </div>
                      <div style={{
                        display: 'inline-block',
                        padding: '4px 12px',
                        borderRadius: '2px',
                        background: estado.bg,
                        border: `1px solid ${estado.color}40`,
                        color: estado.color,
                        fontFamily: "'Oswald', sans-serif",
                        fontSize: '10px', letterSpacing: '0.15em',
                        textTransform: 'uppercase',
                      }}>
                        {estado.label}
                      </div>
                    </div>
                  </div>

                  {/* acciones */}
                  {(c.status === 'PENDING' || c.status === 'PAID') && (
                    <div style={{
                      display: 'flex', gap: '10px',
                      marginTop: '20px',
                      paddingTop: '20px',
                      borderTop: '1px solid #111',
                    }}>

                      <button
                        onClick={() => cancelar(c.id)}
                        disabled={cancelando === c.id}
                        className="btn-ghost"
                        style={{ padding: '10px 20px', fontSize: '12px' }}
                      >
                        {cancelando === c.id ? 'Cancelando...' : 'Cancelar cita'}
                      </button>
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {citas.length > 0 && (
        <div style={{ marginTop: '40px', textAlign: 'center' }}>
          <Link to="/reservar" className="btn-ghost">
            + Nueva reserva
          </Link>
        </div>
      )}
    </div>
  );
}