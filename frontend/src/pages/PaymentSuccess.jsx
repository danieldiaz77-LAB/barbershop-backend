import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { paymentsApi } from '../services/endpoints';

export default function PaymentSuccess() {
  const [params] = useSearchParams();
  const sessionId = params.get('session_id');
  const [pago, setPago] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!sessionId) return;
    paymentsApi.status(sessionId)
      .then(setPago)
      .finally(() => setLoading(false));
  }, [sessionId]);

  return (
    <div style={{
      minHeight: '70vh', display: 'flex',
      alignItems: 'center', justifyContent: 'center',
      padding: '40px',
    }}>
      <div className="fadeup" style={{ textAlign: 'center', maxWidth: '480px' }}>
        <div style={{
          width: '80px', height: '80px', borderRadius: '50%',
          background: 'rgba(74,222,128,0.1)',
          border: '2px solid #4ade80',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: '36px', margin: '0 auto 28px',
        }}>
          ✓
        </div>
        <h1 style={{
          fontFamily: "'Playfair Display', serif",
          fontSize: '40px', fontWeight: '700',
          marginBottom: '12px',
        }}>
          ¡Pago exitoso!
        </h1>
        <p style={{ color: '#666', fontSize: '15px', marginBottom: '36px', lineHeight: '1.8' }}>
          Tu cita ha sido confirmada y pagada. <br />
          Nos vemos pronto en BLADE & CO.
        </p>
        {!loading && pago && (
          <div style={{
            border: '1px solid #1a1a1a', borderRadius: '4px',
            padding: '20px', marginBottom: '32px', textAlign: 'left',
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
              <span style={{ color: '#555', fontSize: '13px' }}>Total pagado</span>
              <span style={{ color: '#C5A059', fontWeight: '700' }}>
                ${Number(pago.amount).toFixed(2)} {pago.currency}
              </span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#555', fontSize: '13px' }}>Estado</span>
              <span style={{ color: '#4ade80', fontSize: '13px' }}>Pagado ✓</span>
            </div>
          </div>
        )}
        <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
          <Link to="/mis-citas" className="btn-gold">Ver mis citas →</Link>
          <Link to="/" className="btn-ghost">Inicio</Link>
        </div>
      </div>
    </div>
  );
}