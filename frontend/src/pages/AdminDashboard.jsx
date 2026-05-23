import { useEffect, useState } from 'react';
import { barbersApi, servicesApi, appointmentsApi} from '../services/endpoints';

const TAB = { CITAS: 'citas', BARBEROS: 'barberos', SERVICIOS: 'servicios' };

const estadoColor = {
  PENDING:   { color: '#fbbf24', label: 'Pendiente' },
  PAID:      { color: '#4ade80', label: 'Pagada' },
  COMPLETED: { color: '#60a5fa', label: 'Completada' },
  CANCELLED: { color: '#f87171', label: 'Cancelada' },
};

export default function AdminDashboard() {
  const [tab, setTab] = useState(TAB.CITAS);
  const [citas, setCitas] = useState([]);
  const [barberos, setBarberos] = useState([]);
  const [servicios, setServicios] = useState([]);
  const [loading, setLoading] = useState(false);
  const [fechaFiltro, setFechaFiltro] = useState('');

  // modal barbero
  const [modalBarbero, setModalBarbero] = useState(false);
  const [editBarbero, setEditBarbero] = useState(null);
  const [formBarbero, setFormBarbero] = useState({ name:'', specialty:'', bio:'', workStart:'09:00', workEnd:'19:00' });

  // modal servicio
  const [modalServicio, setModalServicio] = useState(false);
  const [editServicio, setEditServicio] = useState(null);
  const [formServicio, setFormServicio] = useState({ name:'', description:'', durationMinutes:30, price:'', active:true });

  const [error, setError] = useState('');
  const [exito, setExito] = useState('');

  // carga según tab activo
  useEffect(() => {
    setError(''); setExito('');
    if (tab === TAB.CITAS) cargarCitas();
    if (tab === TAB.BARBEROS) barbersApi.list().then(setBarberos);
    if (tab === TAB.SERVICIOS) servicesApi.listAll().then(setServicios);
  }, [tab]);

  const cargarCitas = () => {
    setLoading(true);
    // carga citas de todos los barberos del día de hoy si no hay filtro
    const hoy = fechaFiltro || new Date().toISOString().split('T')[0];
    appointmentsApi.mine?.()
      .catch(() => [])
      .finally(() => setLoading(false));

    // usamos el endpoint de barberos para traer todas las citas
    barbersApi.list().then(async (bs) => {
      const todas = [];
      for (const b of bs) {
        const data = await appointmentsApi.byBarber(b.id, fechaFiltro || null);
        todas.push(...data);
      }
      setCitas(todas.sort((a, b) => new Date(a.startTime) - new Date(b.startTime)));
      setLoading(false);
    });
  };

  useEffect(() => {
    if (tab === TAB.CITAS) cargarCitas();
  }, [fechaFiltro]);

  const cancelarCita = async (id) => {
    if (!confirm('¿Cancelar esta cita?')) return;
    try {
      await appointmentsApi.cancel(id);
      setCitas(prev => prev.map(c => c.id === id ? { ...c, status: 'CANCELLED' } : c));
      setExito('Cita cancelada');
    } catch { setError('No se pudo cancelar'); }
  };

  // ── BARBEROS CRUD ──
  const abrirModalBarbero = (b = null) => {
    setEditBarbero(b);
    setFormBarbero(b ? { name: b.name, specialty: b.specialty, bio: b.bio || '', workStart: b.workStart, workEnd: b.workEnd } : { name:'', specialty:'', bio:'', workStart:'09:00', workEnd:'19:00' });
    setModalBarbero(true);
  };

  const guardarBarbero = async () => {
    try {
      if (editBarbero) {
        const upd = await barbersApi.update(editBarbero.id, formBarbero);
        setBarberos(prev => prev.map(b => b.id === editBarbero.id ? upd : b));
      } else {
        const nuevo = await barbersApi.create(formBarbero);
        setBarberos(prev => [...prev, nuevo]);
      }
      setModalBarbero(false);
      setExito(editBarbero ? 'Barbero actualizado' : 'Barbero creado');
    } catch { setError('Error al guardar barbero'); }
  };

  const eliminarBarbero = async (id) => {
    if (!confirm('¿Eliminar este barbero?')) return;
    try {
      await barbersApi.remove(id);
      setBarberos(prev => prev.filter(b => b.id !== id));
      setExito('Barbero eliminado');
    } catch { setError('No se pudo eliminar'); }
  };

  // ── SERVICIOS CRUD ──
  const abrirModalServicio = (s = null) => {
    setEditServicio(s);
    setFormServicio(s ? { name: s.name, description: s.description || '', durationMinutes: s.durationMinutes, price: s.price, active: s.active } : { name:'', description:'', durationMinutes:30, price:'', active:true });
    setModalServicio(true);
  };

  const guardarServicio = async () => {
    try {
      const payload = { ...formServicio, durationMinutes: Number(formServicio.durationMinutes), price: Number(formServicio.price) };
      if (editServicio) {
        const upd = await servicesApi.update(editServicio.id, payload);
        setServicios(prev => prev.map(s => s.id === editServicio.id ? upd : s));
      } else {
        const nuevo = await servicesApi.create(payload);
        setServicios(prev => [...prev, nuevo]);
      }
      setModalServicio(false);
      setExito(editServicio ? 'Servicio actualizado' : 'Servicio creado');
    } catch { setError('Error al guardar servicio'); }
  };

  const eliminarServicio = async (id) => {
    if (!confirm('¿Desactivar este servicio?')) return;
    try {
      await servicesApi.remove(id);
      setServicios(prev => prev.map(s => s.id === id ? { ...s, active: false } : s));
      setExito('Servicio desactivado');
    } catch { setError('No se pudo desactivar'); }
  };

  const formatHora = (dt) => new Date(dt).toLocaleTimeString('es-CL', { hour: '2-digit', minute: '2-digit' });
  const formatFecha = (dt) => new Date(dt).toLocaleDateString('es-CL', { day: 'numeric', month: 'short' });

  const labelStyle = {
    fontFamily: "'Oswald', sans-serif",
    fontSize: '11px', letterSpacing: '0.2em',
    color: '#666', textTransform: 'uppercase',
    display: 'block', marginBottom: '8px',
  };

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '60px 40px' }}>

      {/* encabezado */}
      <div className="fadeup" style={{ marginBottom: '40px' }}>
        <div style={{ fontFamily: "'Oswald', sans-serif", fontSize: '11px', letterSpacing: '0.3em', color: '#C5A059', textTransform: 'uppercase', marginBottom: '12px' }}>
          Panel de control
        </div>
        <h1 style={{ fontFamily: "'Playfair Display', serif", fontSize: '48px', fontWeight: '700', letterSpacing: '-0.02em' }}>
          Admin Dashboard
        </h1>
      </div>

      {/* alertas */}
      {error && (
        <div style={{ background: 'rgba(153,27,27,0.2)', border: '1px solid rgba(248,113,113,0.3)', color: '#f87171', padding: '12px 16px', borderRadius: '2px', fontSize: '13px', marginBottom: '20px' }}>
          {error} <button onClick={() => setError('')} style={{ float: 'right', background: 'none', border: 'none', color: '#f87171', cursor: 'pointer' }}>✕</button>
        </div>
      )}
      {exito && (
        <div style={{ background: 'rgba(74,222,128,0.1)', border: '1px solid rgba(74,222,128,0.3)', color: '#4ade80', padding: '12px 16px', borderRadius: '2px', fontSize: '13px', marginBottom: '20px' }}>
          {exito} <button onClick={() => setExito('')} style={{ float: 'right', background: 'none', border: 'none', color: '#4ade80', cursor: 'pointer' }}>✕</button>
        </div>
      )}

      {/* tabs */}
      <div style={{ display: 'flex', gap: '0', marginBottom: '40px', border: '1px solid #1a1a1a', borderRadius: '2px', overflow: 'hidden' }}>
        {[
          { key: TAB.CITAS,     label: '📅 Citas' },
          { key: TAB.BARBEROS,  label: '✂️ Barberos' },
          { key: TAB.SERVICIOS, label: '📋 Servicios' },
        ].map(({ key, label }) => (
          <button key={key} onClick={() => setTab(key)} style={{
            flex: 1, padding: '14px', border: 'none',
            background: tab === key ? '#C5A059' : 'transparent',
            color: tab === key ? '#0A0A0A' : '#666',
            fontFamily: "'Oswald', sans-serif",
            fontSize: '12px', letterSpacing: '0.1em',
            textTransform: 'uppercase', cursor: 'pointer',
            borderRight: '1px solid #1a1a1a',
            transition: 'all 0.2s',
            fontWeight: tab === key ? '700' : '400',
          }}>
            {label}
          </button>
        ))}
      </div>

      {/* ── TAB CITAS ── */}
      {tab === TAB.CITAS && (
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '12px' }}>
            <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: '24px' }}>
              Citas {fechaFiltro ? `— ${fechaFiltro}` : '— Todas'}
            </h2>
            <input
              type="date" value={fechaFiltro}
              onChange={e => setFechaFiltro(e.target.value)}
              className="input-dark" style={{ maxWidth: '200px' }}
            />
          </div>

          {loading ? (
            <div style={{ color: '#555', textAlign: 'center', padding: '60px' }}>Cargando citas...</div>
          ) : citas.length === 0 ? (
            <div style={{ color: '#444', textAlign: 'center', padding: '60px', border: '1px solid #1a1a1a', borderRadius: '4px' }}>
              No hay citas para mostrar.
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {citas.map((c) => {
                const est = estadoColor[c.status] || estadoColor.PENDING;
                return (
                  <div key={c.id} style={{
                    border: '1px solid #1a1a1a', borderLeft: `3px solid ${est.color}`,
                    borderRadius: '4px', padding: '16px 20px',
                    display: 'flex', justifyContent: 'space-between',
                    alignItems: 'center', flexWrap: 'wrap', gap: '12px',
                    background: '#111',
                  }}>
                    <div>
                      <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '16px', marginBottom: '4px' }}>
                        {c.clientName} → {c.serviceName}
                      </div>
                      <div style={{ color: '#555', fontSize: '12px' }}>
                        ✂️ {c.barberName} &nbsp;·&nbsp;
                        📅 {formatFecha(c.startTime)} &nbsp;·&nbsp;
                        🕐 {formatHora(c.startTime)} — {formatHora(c.endTime)} &nbsp;·&nbsp;
                        <span style={{ color: '#C5A059' }}>${Number(c.servicePrice).toFixed(2)}</span>
                      </div>
                    </div>
                    <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                      <span style={{
                        padding: '3px 10px', borderRadius: '2px',
                        background: `${est.color}15`,
                        border: `1px solid ${est.color}40`,
                        color: est.color,
                        fontFamily: "'Oswald', sans-serif",
                        fontSize: '10px', letterSpacing: '0.1em',
                        textTransform: 'uppercase',
                      }}>
                        {est.label}
                      </span>
                      {c.status !== 'CANCELLED' && c.status !== 'COMPLETED' && (
                        <button onClick={() => cancelarCita(c.id)} className="btn-ghost"
                          style={{ padding: '6px 12px', fontSize: '11px' }}>
                          Cancelar
                        </button>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* ── TAB BARBEROS ── */}
      {tab === TAB.BARBEROS && (
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
            <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: '24px' }}>Barberos</h2>
            <button onClick={() => abrirModalBarbero()} className="btn-gold" style={{ padding: '10px 20px', fontSize: '12px' }}>
              + Nuevo barbero
            </button>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '16px' }}>
            {barberos.map((b) => (
              <div key={b.id} className="card" style={{ padding: '24px' }}>
                <div style={{
                  width: '48px', height: '48px', borderRadius: '50%',
                  background: '#C5A059', display: 'flex',
                  alignItems: 'center', justifyContent: 'center',
                  fontFamily: "'Playfair Display', serif",
                  fontSize: '20px', fontWeight: '700',
                  color: '#0A0A0A', marginBottom: '14px',
                }}>
                  {b.name.charAt(0)}
                </div>
                <h3 style={{ fontFamily: "'Playfair Display', serif", fontSize: '20px', marginBottom: '4px' }}>{b.name}</h3>
                <div style={{ fontFamily: "'Oswald', sans-serif", fontSize: '10px', color: '#C5A059', letterSpacing: '0.2em', textTransform: 'uppercase', marginBottom: '8px' }}>
                  {b.specialty}
                </div>
                <div style={{ color: '#555', fontSize: '12px', marginBottom: '16px' }}>
                  🕐 {b.workStart} — {b.workEnd}
                </div>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <button onClick={() => abrirModalBarbero(b)} className="btn-ghost" style={{ flex: 1, padding: '8px', fontSize: '11px' }}>
                    Editar
                  </button>
                  <button onClick={() => eliminarBarbero(b.id)} style={{
                    padding: '8px 12px', background: 'transparent',
                    border: '1px solid rgba(248,113,113,0.3)',
                    color: '#f87171', borderRadius: '2px',
                    cursor: 'pointer', fontSize: '11px',
                  }}>
                    Eliminar
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ── TAB SERVICIOS ── */}
      {tab === TAB.SERVICIOS && (
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
            <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: '24px' }}>Servicios</h2>
            <button onClick={() => abrirModalServicio()} className="btn-gold" style={{ padding: '10px 20px', fontSize: '12px' }}>
              + Nuevo servicio
            </button>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {servicios.map((s) => (
              <div key={s.id} style={{
                border: `1px solid ${s.active ? '#1a1a1a' : '#111'}`,
                borderRadius: '4px', padding: '18px 24px',
                display: 'flex', justifyContent: 'space-between',
                alignItems: 'center', flexWrap: 'wrap', gap: '12px',
                background: s.active ? '#111' : '#0a0a0a',
                opacity: s.active ? 1 : 0.5,
              }}>
                <div>
                  <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '18px', marginBottom: '4px' }}>
                    {s.name}
                  </div>
                  <div style={{ color: '#555', fontSize: '12px' }}>
                    ⏱ {s.durationMinutes} min &nbsp;·&nbsp;
                    <span style={{ color: '#C5A059', fontWeight: '700' }}>${Number(s.price).toFixed(2)}</span>
                    {!s.active && <span style={{ color: '#f87171', marginLeft: '8px' }}>· Inactivo</span>}
                  </div>
                </div>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <button onClick={() => abrirModalServicio(s)} className="btn-ghost" style={{ padding: '8px 14px', fontSize: '11px' }}>
                    Editar
                  </button>
                  {s.active && (
                    <button onClick={() => eliminarServicio(s.id)} style={{
                      padding: '8px 12px', background: 'transparent',
                      border: '1px solid rgba(248,113,113,0.3)',
                      color: '#f87171', borderRadius: '2px',
                      cursor: 'pointer', fontSize: '11px',
                    }}>
                      Desactivar
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ── MODAL BARBERO ── */}
      {modalBarbero && (
        <div style={{
          position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.8)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          zIndex: 1000, padding: '20px',
        }}>
          <div style={{ background: '#141414', border: '1px solid #222', borderRadius: '6px', padding: '36px', width: '100%', maxWidth: '480px' }}>
            <h3 style={{ fontFamily: "'Playfair Display', serif", fontSize: '24px', marginBottom: '24px' }}>
              {editBarbero ? 'Editar barbero' : 'Nuevo barbero'}
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              {[
                { key: 'name',      label: 'Nombre',      placeholder: 'Marco Aurelio' },
                { key: 'specialty', label: 'Especialidad', placeholder: 'Classic Cuts' },
                { key: 'bio',       label: 'Bio',          placeholder: 'Descripción breve...' },
                { key: 'workStart', label: 'Inicio',       placeholder: '09:00' },
                { key: 'workEnd',   label: 'Fin',          placeholder: '19:00' },
              ].map(({ key, label, placeholder }) => (
                <div key={key}>
                  <label style={labelStyle}>{label}</label>
                  <input
                    className="input-dark"
                    value={formBarbero[key]}
                    onChange={e => setFormBarbero({ ...formBarbero, [key]: e.target.value })}
                    placeholder={placeholder}
                  />
                </div>
              ))}
            </div>
            <div style={{ display: 'flex', gap: '12px', marginTop: '24px' }}>
              <button onClick={guardarBarbero} className="btn-gold" style={{ flex: 1, justifyContent: 'center' }}>
                {editBarbero ? 'Actualizar' : 'Crear'} →
              </button>
              <button onClick={() => setModalBarbero(false)} className="btn-ghost">
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ── MODAL SERVICIO ── */}
      {modalServicio && (
        <div style={{
          position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.8)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          zIndex: 1000, padding: '20px',
        }}>
          <div style={{ background: '#141414', border: '1px solid #222', borderRadius: '6px', padding: '36px', width: '100%', maxWidth: '480px' }}>
            <h3 style={{ fontFamily: "'Playfair Display', serif", fontSize: '24px', marginBottom: '24px' }}>
              {editServicio ? 'Editar servicio' : 'Nuevo servicio'}
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div>
                <label style={labelStyle}>Nombre</label>
                <input className="input-dark" value={formServicio.name}
                  onChange={e => setFormServicio({ ...formServicio, name: e.target.value })}
                  placeholder="Classic Haircut" />
              </div>
              <div>
                <label style={labelStyle}>Descripción</label>
                <input className="input-dark" value={formServicio.description}
                  onChange={e => setFormServicio({ ...formServicio, description: e.target.value })}
                  placeholder="Descripción del servicio..." />
              </div>
              <div>
                <label style={labelStyle}>Duración (minutos)</label>
                <input type="number" className="input-dark" value={formServicio.durationMinutes}
                  onChange={e => setFormServicio({ ...formServicio, durationMinutes: e.target.value })}
                  placeholder="45" />
              </div>
              <div>
                <label style={labelStyle}>Precio ($)</label>
                <input type="number" className="input-dark" value={formServicio.price}
                  onChange={e => setFormServicio({ ...formServicio, price: e.target.value })}
                  placeholder="28.00" />
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                <input type="checkbox" checked={formServicio.active}
                  onChange={e => setFormServicio({ ...formServicio, active: e.target.checked })}
                  id="activo" />
                <label htmlFor="activo" style={{ ...labelStyle, marginBottom: 0 }}>Activo</label>
              </div>
            </div>
            <div style={{ display: 'flex', gap: '12px', marginTop: '24px' }}>
              <button onClick={guardarServicio} className="btn-gold" style={{ flex: 1, justifyContent: 'center' }}>
                {editServicio ? 'Actualizar' : 'Crear'} →
              </button>
              <button onClick={() => setModalServicio(false)} className="btn-ghost">
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
} 