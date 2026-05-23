import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { barbersApi, servicesApi, appointmentsApi } from '../services/endpoints';

// pasos del flujo de reserva
const PASOS = ['Barbero', 'Servicio', 'Fecha y Hora', 'Confirmar'];

export default function Reservar() {
  const navigate = useNavigate();
  const [paso, setPaso] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // datos del backend
  const [barberos, setBarberos] = useState([]);
  const [servicios, setServicios] = useState([]);
  const [slots, setSlots] = useState([]);

  // selecciones del usuario
  const [barbero, setBarbero] = useState(null);
  const [servicio, setServicio] = useState(null);
  const [fecha, setFecha] = useState('');
  const [slot, setSlot] = useState(null);
  const [cita, setCita] = useState(null);

  // carga inicial
  useEffect(() => {
    barbersApi.list().then(setBarberos);
    servicesApi.list().then(setServicios);
  }, []);

  // carga slots cuando cambia barbero, servicio o fecha
  useEffect(() => {
    if (!barbero || !servicio || !fecha) return;
    setSlots([]); setSlot(null);
    appointmentsApi.availability(barbero.id, servicio.id, fecha)
      .then(setSlots)
      .catch(() => setSlots([]));
  }, [barbero, servicio, fecha]);

  // fecha mínima: mañana
  const fechaMin = () => {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return d.toISOString().split('T')[0];
  };

  // confirmar cita
  const confirmar = async () => {
    setError(''); setLoading(true);
    try {
      const data = await appointmentsApi.book({
        barberId: barbero.id,
        serviceId: servicio.id,
        startTime: slot,
      });
      setCita(data);
      setPaso(4); // paso de confirmación
    } catch (err) {
      setError(err?.response?.data?.message || 'Error al reservar');
    } finally { setLoading(false); }
  };

  // formatear hora
  const formatHora = (dt) => new Date(dt).toLocaleTimeString('es-CL', {
    hour: '2-digit', minute: '2-digit',
  });

  // formatear fecha
  const formatFecha = (dt) => new Date(dt).toLocaleDateString('es-CL', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric',
  });

  const titleStyle = {
    fontFamily: "'Playfair Display', serif",
    fontSize: '32px', fontWeight: '700',
    marginBottom: '8px',
  };

  const subtitleStyle = {
    fontFamily: "'Oswald', sans-serif",
    fontSize: '11px', letterSpacing: '0.3em',
    color: '#C5A059', textTransform: 'uppercase',
    marginBottom: '12px',
  };

  return (
    <div style={{ maxWidth: '900px', margin: '0 auto', padding: '60px 40px' }}>

      {/* título */}
      <div className="fadeup" style={{ marginBottom: '48px' }}>
        <div style={subtitleStyle}>Reserva tu cita</div>
        <h1 style={{ ...titleStyle, fontSize: '48px' }}>
          {paso < 4 ? PASOS[paso] : '¡Cita reservada!'}
        </h1>
      </div>

      {/* indicador de pasos */}
      {paso < 4 && (
        <div style={{
          display: 'flex', gap: '0', marginBottom: '48px',
          border: '1px solid #1a1a1a', overflow: 'hidden', borderRadius: '2px',
        }}>
          {PASOS.map((p, i) => (
            <div key={p} style={{
              flex: 1, padding: '12px 8px', textAlign: 'center',
              fontFamily: "'Oswald', sans-serif",
              fontSize: '11px', letterSpacing: '0.15em',
              textTransform: 'uppercase',
              background: i === paso ? '#C5A059' : i < paso ? '#1a1a1a' : 'transparent',
              color: i === paso ? '#0A0A0A' : i < paso ? '#C5A059' : '#444',
              borderRight: i < 3 ? '1px solid #1a1a1a' : 'none',
              cursor: i < paso ? 'pointer' : 'default',
              transition: 'all 0.3s',
            }}
              onClick={() => i < paso && setPaso(i)}
            >
              {i < paso ? '✓ ' : `${i + 1}. `}{p}
            </div>
          ))}
        </div>
      )}

      {/* ── PASO 0: BARBERO ── */}
      {paso === 0 && (
        <div className="fadeup">
          <p style={{ color: '#666', marginBottom: '32px', fontSize: '14px' }}>
            Elige tu barbero preferido para esta cita.
          </p>
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))',
            gap: '20px',
          }}>
            {barberos.map((b) => (
              <div key={b.id}
                onClick={() => { setBarbero(b); setPaso(1); }}
                style={{
                  border: `1px solid ${barbero?.id === b.id ? '#C5A059' : '#222'}`,
                  borderRadius: '4px', padding: '28px',
                  cursor: 'pointer', transition: 'all 0.3s',
                  background: barbero?.id === b.id ? 'rgba(197,160,89,0.08)' : '#111',
                }}
              >
                {/* avatar */}
                <div style={{
                  width: '56px', height: '56px', borderRadius: '50%',
                  background: '#C5A059', display: 'flex',
                  alignItems: 'center', justifyContent: 'center',
                  fontFamily: "'Playfair Display', serif",
                  fontSize: '22px', fontWeight: '700',
                  color: '#0A0A0A', marginBottom: '16px',
                }}>
                  {b.name.charAt(0)}
                </div>
                <h3 style={{
                  fontFamily: "'Playfair Display', serif",
                  fontSize: '20px', marginBottom: '6px',
                }}>
                  {b.name}
                </h3>
                <div style={{
                  fontFamily: "'Oswald', sans-serif",
                  fontSize: '10px', letterSpacing: '0.2em',
                  color: '#C5A059', textTransform: 'uppercase',
                  marginBottom: '10px',
                }}>
                  {b.specialty}
                </div>
                <div style={{ color: '#555', fontSize: '12px' }}>
                  🕐 {b.workStart} — {b.workEnd}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ── PASO 1: SERVICIO ── */}
      {paso === 1 && (
        <div className="fadeup">
          <p style={{ color: '#666', marginBottom: '32px', fontSize: '14px' }}>
            Selecciona el servicio que deseas con <strong style={{ color: '#F3F2EE' }}>{barbero?.name}</strong>.
          </p>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {servicios.map((s) => (
              <div key={s.id}
                onClick={() => { setServicio(s); setPaso(2); }}
                style={{
                  border: `1px solid ${servicio?.id === s.id ? '#C5A059' : '#222'}`,
                  borderRadius: '4px', padding: '20px 24px',
                  cursor: 'pointer', transition: 'all 0.3s',
                  background: servicio?.id === s.id ? 'rgba(197,160,89,0.08)' : '#111',
                  display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                }}
              >
                <div>
                  <div style={{
                    fontFamily: "'Playfair Display', serif",
                    fontSize: '18px', marginBottom: '4px',
                  }}>
                    {s.name}
                  </div>
                  <div style={{ color: '#555', fontSize: '12px' }}>
                    ⏱ {s.durationMinutes} minutos
                  </div>
                </div>
                <div style={{
                  fontFamily: "'Playfair Display', serif",
                  fontSize: '24px', fontWeight: '700',
                  color: '#C5A059',
                }}>
                  ${Number(s.price).toFixed(0)}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ── PASO 2: FECHA Y HORA ── */}
      {paso === 2 && (
        <div className="fadeup">
          <p style={{ color: '#666', marginBottom: '32px', fontSize: '14px' }}>
            Elige la fecha y el horario disponible.
          </p>

          {/* selector de fecha */}
          <div style={{ marginBottom: '36px' }}>
            <label style={{
              fontFamily: "'Oswald', sans-serif",
              fontSize: '11px', letterSpacing: '0.2em',
              color: '#666', textTransform: 'uppercase',
              display: 'block', marginBottom: '10px',
            }}>
              Fecha
            </label>
            <input
              type="date"
              min={fechaMin()}
              value={fecha}
              onChange={e => setFecha(e.target.value)}
              className="input-dark"
              style={{ maxWidth: '240px' }}
            />
          </div>

          {/* slots de horario */}
          {fecha && (
            <div>
              <div style={{
                fontFamily: "'Oswald', sans-serif",
                fontSize: '11px', letterSpacing: '0.2em',
                color: '#666', textTransform: 'uppercase',
                marginBottom: '16px',
              }}>
                Horarios disponibles
              </div>

              {slots.length === 0 ? (
                <div style={{
                  color: '#444', padding: '32px',
                  border: '1px solid #1a1a1a', borderRadius: '4px',
                  textAlign: 'center', fontSize: '14px',
                }}>
                  No hay horarios disponibles para esta fecha.
                </div>
              ) : (
                <div style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fill, minmax(90px, 1fr))',
                  gap: '10px',
                }}>
                  {slots.map((s) => (
                    <button
                      key={s}
                      onClick={() => setSlot(s)}
                      className={`slot ${slot === s ? 'selected' : ''}`}
                    >
                      {formatHora(s)}
                    </button>
                  ))}
                </div>
              )}

              {slot && (
                <button
                  onClick={() => setPaso(3)}
                  className="btn-gold"
                  style={{ marginTop: '32px' }}
                >
                  Continuar →
                </button>
              )}
            </div>
          )}
        </div>
      )}

      {/* ── PASO 3: CONFIRMAR ── */}
      {paso === 3 && (
        <div className="fadeup">
          <p style={{ color: '#666', marginBottom: '32px', fontSize: '14px' }}>
            Revisa los detalles antes de confirmar tu cita.
          </p>

          {/* resumen */}
          <div style={{
            border: '1px solid #222', borderRadius: '4px',
            overflow: 'hidden', marginBottom: '32px',
          }}>
            {[
              ['Barbero',   barbero?.name],
              ['Servicio',  servicio?.name],
              ['Duración',  `${servicio?.durationMinutes} minutos`],
              ['Fecha',     formatFecha(slot)],
              ['Hora',      formatHora(slot)],
              ['Precio',    `$${Number(servicio?.price).toFixed(2)}`],
            ].map(([k, v]) => (
              <div key={k} style={{
                display: 'flex', justifyContent: 'space-between',
                padding: '16px 24px',
                borderBottom: '1px solid #111',
              }}>
                <span style={{
                  fontFamily: "'Oswald', sans-serif",
                  fontSize: '11px', letterSpacing: '0.15em',
                  color: '#555', textTransform: 'uppercase',
                }}>
                  {k}
                </span>
                <span style={{
                  color: k === 'Precio' ? '#C5A059' : '#F3F2EE',
                  fontWeight: k === 'Precio' ? '700' : '400',
                  fontFamily: k === 'Precio' ? "'Playfair Display', serif" : 'inherit',
                  fontSize: k === 'Precio' ? '18px' : '14px',
                }}>
                  {v}
                </span>
              </div>
            ))}
          </div>

          {error && (
            <div style={{
              background: 'rgba(153,27,27,0.2)',
              border: '1px solid rgba(248,113,113,0.3)',
              color: '#f87171', padding: '12px 16px',
              borderRadius: '2px', fontSize: '13px',
              marginBottom: '20px',
            }}>
              {error}
            </div>
          )}

          <div style={{ display: 'flex', gap: '12px' }}>
            <button onClick={() => setPaso(2)} className="btn-ghost">
              ← Volver
            </button>
            <button onClick={confirmar} disabled={loading} className="btn-gold">
              {loading ? 'Reservando...' : 'Confirmar reserva →'}
            </button>
          </div>
        </div>
      )}

      {/* ── PASO 4: ÉXITO ── */}
      {paso === 4 && cita && (
        <div className="fadeup" style={{ textAlign: 'center' }}>

          <div style={{
            width: '80px', height: '80px', borderRadius: '50%',
            background: 'rgba(197,160,89,0.15)',
            border: '2px solid #C5A059',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: '36px', margin: '0 auto 32px',
          }}>
            ✓
          </div>

          <h2 style={{
            fontFamily: "'Playfair Display', serif",
            fontSize: '36px', fontWeight: '700',
            marginBottom: '12px',
          }}>
            ¡Cita reservada!
          </h2>
          <p style={{ color: '#666', marginBottom: '40px', fontSize: '15px' }}>
            Tu cita con <strong style={{ color: '#F3F2EE' }}>{cita.barberName}</strong> está confirmada
            para el <strong style={{ color: '#F3F2EE' }}>{formatFecha(cita.startTime)}</strong> a
            las <strong style={{ color: '#C5A059' }}>{formatHora(cita.startTime)}</strong>.
          </p>

          {/* resumen compacto */}
          <div style={{
            border: '1px solid #1a1a1a', borderRadius: '4px',
            padding: '24px', maxWidth: '400px', margin: '0 auto 36px',
            textAlign: 'left',
          }}>
            <div style={{
              fontFamily: "'Oswald', sans-serif",
              fontSize: '10px', letterSpacing: '0.2em',
              color: '#C5A059', textTransform: 'uppercase',
              marginBottom: '16px',
            }}>
              Resumen
            </div>
            {[
              ['Servicio', cita.serviceName],
              ['Duración', `${cita.durationMinutes} min`],
              ['Total',    `$${Number(cita.servicePrice).toFixed(2)}`],
            ].map(([k, v]) => (
              <div key={k} style={{
                display: 'flex', justifyContent: 'space-between',
                marginBottom: '10px', fontSize: '14px',
              }}>
                <span style={{ color: '#555' }}>{k}</span>
                <span style={{ color: k === 'Total' ? '#C5A059' : '#F3F2EE', fontWeight: k === 'Total' ? '700' : '400' }}>
                  {v}
                </span>
              </div>
            ))}
          </div>

          <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
            <button
              onClick={() => navigate('/mis-citas')}
              className="btn-ghost"
            >
              Ver mis citas
            </button>
          </div>
        </div>
      )}

    </div>
  );
}