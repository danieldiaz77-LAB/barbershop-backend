import { Link, useSearchParams } from 'react-router-dom';

export default function PaymentCancel() {
  const [params] = useSearchParams();
  const appointmentId = params.get('appointment_id');

  return (
    <div style={{
      minHeight: '70vh', display: 'flex',
      alignItems: 'center', justifyContent: 'center',
      padding: '40px',
    }}>
      <div className="fadeup" style={{ textAlign: 'center', maxWidth: '480px' }}>
        <div style={{
          width: '80px', height: '80px', borderRadius: '50%',
          background: 'rgba(248,113,113,0.1)',
          border: '2px solid #f87171',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: '36px', margin: '0 auto 28px',
        }}>
          ✕
        </div>
        <h1 style={{
          fontFamily: "'Playfair Display', serif",
          fontSize: '40px', fontWeight: '700',
          marginBottom: '12px',
        }}>
          Pago cancelado
        </h1>
        <p style={{ color: '#666', fontSize: '15px', marginBottom: '36px', lineHeight: '1.8' }}>
          No se realizó ningún cobro. Tu cita sigue reservada
          y puedes intentar pagar de nuevo cuando quieras.
        </p>
        <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
          <Link to="/mis-citas" className="btn-gold">Ver mis citas →</Link>
          <Link to="/" className="btn-ghost">Inicio</Link>
        </div>
      </div>
    </div>
  );
}