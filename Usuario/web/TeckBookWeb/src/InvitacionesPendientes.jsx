import React, { useState, useEffect } from 'react';
import { Mail, Clock, Check, X, User, BookOpen } from 'lucide-react';

function InvitacionesPendientes() {
  const [invitaciones, setInvitaciones] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [procesando, setProcesando] = useState(null);

  useEffect(() => {
    fetchInvitacionesPendientes();
  }, []);

  const fetchInvitacionesPendientes = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/invitaciones/pendientes', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setInvitaciones(data.invitaciones || []);
      }
    } catch (error) {
      console.error('Error al obtener invitaciones:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const aceptarInvitacion = async (codigoInvitacion) => {
    try {
      setProcesando(codigoInvitacion);
      const token = localStorage.getItem('token');
      
      const response = await fetch(`http://localhost:8080/api/invitaciones/aceptar/${codigoInvitacion}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        
        // Mostrar mensaje de éxito
        alert('¡Te has unido al aula exitosamente!');
        
        // Actualizar la lista
        fetchInvitacionesPendientes();
        
        // Opcional: redirigir al aula
        // window.location.href = `/aulas/${data.aulaId}`;
      } else {
        const errorData = await response.json();
        alert(`Error: ${errorData.error}`);
      }
    } catch (error) {
      console.error('Error al aceptar invitación:', error);
      alert('Error de conexión al aceptar la invitación');
    } finally {
      setProcesando(null);
    }
  };

  const formatearFecha = (fechaString) => {
    const fecha = new Date(fechaString);
    return fecha.toLocaleDateString('es-PE', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const diasRestantes = (fechaExpiracion) => {
    const ahora = new Date();
    const expiracion = new Date(fechaExpiracion);
    const diferencia = expiracion - ahora;
    const dias = Math.ceil(diferencia / (1000 * 60 * 60 * 24));
    return Math.max(0, dias);
  };

  if (isLoading) {
    return (
      <div className="invitaciones-loading">
        <div className="loading-spinner"></div>
        <p>Cargando invitaciones...</p>
      </div>
    );
  }

  if (invitaciones.length === 0) {
    return (
      <div className="invitaciones-empty">
        <Mail size={48} className="empty-icon" />
        <h3>No tienes invitaciones pendientes</h3>
        <p>Cuando recibas invitaciones a aulas virtuales, aparecerán aquí.</p>
      </div>
    );
  }

  return (
    <div className="invitaciones-container">
      <div className="invitaciones-header">
        <h2 className="invitaciones-title">
          <Mail size={24} />
          Invitaciones Pendientes ({invitaciones.length})
        </h2>
        <p className="invitaciones-subtitle">
          Tienes invitaciones para unirte a aulas virtuales
        </p>
      </div>

      <div className="invitaciones-list">
        {invitaciones.map((invitacion) => (
          <div key={invitacion.id} className="invitacion-card">
            <div className="invitacion-header">
              <div className="invitacion-info">
                <div className="aula-icon">
                  <BookOpen size={20} />
                </div>
                <div className="invitacion-details">
                  <h3 className="aula-nombre">Aula Virtual #{invitacion.aulaVirtualId}</h3>
                  <div className="invitacion-meta">
                    <span className="invitado-por">
                      <User size={14} />
                      Invitado por ID: {invitacion.invitadoPorId}
                    </span>
                    <span className="fecha-invitacion">
                      <Clock size={14} />
                      {formatearFecha(invitacion.fechaInvitacion)}
                    </span>
                  </div>
                </div>
              </div>
              
              <div className="tiempo-restante">
                <span className={`dias-restantes ${diasRestantes(invitacion.fechaExpiracion) <= 1 ? 'urgente' : ''}`}>
                  {diasRestantes(invitacion.fechaExpiracion)} días restantes
                </span>
              </div>
            </div>

            {invitacion.mensaje && (
              <div className="invitacion-mensaje">
                <p>"{invitacion.mensaje}"</p>
              </div>
            )}

            <div className="invitacion-actions">
              <button
                onClick={() => aceptarInvitacion(invitacion.codigoInvitacion)}
                disabled={procesando === invitacion.codigoInvitacion}
                className="btn-aceptar"
              >
                {procesando === invitacion.codigoInvitacion ? (
                  <>
                    <div className="loading-spinner-small"></div>
                    Procesando...
                  </>
                ) : (
                  <>
                    <Check size={16} />
                    Aceptar Invitación
                  </>
                )}
              </button>
              
              <button className="btn-rechazar">
                <X size={16} />
                Rechazar
              </button>
            </div>
          </div>
        ))}
      </div>

      <style jsx>{`
        .invitaciones-loading {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          padding: 60px 20px;
          text-align: center;
        }

        .loading-spinner {
          width: 40px;
          height: 40px;
          border: 4px solid #e2e8f0;
          border-top: 4px solid #005DAB;
          border-radius: 50%;
          animation: spin 1s linear infinite;
          margin-bottom: 16px;
        }

        .invitaciones-empty {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          padding: 60px 20px;
          text-align: center;
          background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
          border-radius: 20px;
          border: 2px dashed #e2e8f0;
        }

        .empty-icon {
          color: #94a3b8;
          margin-bottom: 20px;
        }

        .invitaciones-empty h3 {
          font-size: 20px;
          color: #64748b;
          margin: 0 0 8px 0;
        }

        .invitaciones-empty p {
          color: #94a3b8;
          margin: 0;
        }

        .invitaciones-container {
          max-width: 800px;
          margin: 0 auto;
          padding: 20px;
        }

        .invitaciones-header {
          margin-bottom: 24px;
          text-align: center;
        }

        .invitaciones-title {
          font-size: 28px;
          font-weight: 700;
          color: #1e293b;
          margin: 0 0 8px 0;
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 12px;
        }

        .invitaciones-subtitle {
          font-size: 16px;
          color: #64748b;
          margin: 0;
        }

        .invitaciones-list {
          display: flex;
          flex-direction: column;
          gap: 16px;
        }

        .invitacion-card {
          background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
          border-radius: 16px;
          padding: 24px;
          border: 1px solid #e2e8f0;
          box-shadow: 0 4px 16px rgba(0, 93, 171, 0.08);
          transition: all 0.3s ease;
        }

        .invitacion-card:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 32px rgba(0, 93, 171, 0.12);
        }

        .invitacion-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 16px;
        }

        .invitacion-info {
          display: flex;
          gap: 16px;
          flex: 1;
        }

        .aula-icon {
          width: 48px;
          height: 48px;
          background: linear-gradient(135deg, #005DAB 0%, #0066c7 100%);
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          flex-shrink: 0;
        }

        .invitacion-details {
          flex: 1;
        }

        .aula-nombre {
          font-size: 18px;
          font-weight: 600;
          color: #1e293b;
          margin: 0 0 8px 0;
        }

        .invitacion-meta {
          display: flex;
          flex-direction: column;
          gap: 4px;
        }

        .invitado-por,
        .fecha-invitacion {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 14px;
          color: #64748b;
        }

        .tiempo-restante {
          text-align: right;
        }

        .dias-restantes {
          font-size: 12px;
          font-weight: 600;
          padding: 4px 8px;
          border-radius: 6px;
          background: #dcfce7;
          color: #16a34a;
        }

        .dias-restantes.urgente {
          background: #fee2e2;
          color: #dc2626;
        }

        .invitacion-mensaje {
          background: rgba(0, 93, 171, 0.05);
          border-left: 4px solid #005DAB;
          padding: 16px;
          border-radius: 8px;
          margin-bottom: 20px;
        }

        .invitacion-mensaje p {
          margin: 0;
          font-style: italic;
          color: #475569;
        }

        .invitacion-actions {
          display: flex;
          gap: 12px;
        }

        .btn-aceptar {
          background: linear-gradient(135deg, #10b981 0%, #059669 100%);
          color: white;
          border: none;
          padding: 12px 20px;
          border-radius: 10px;
          font-weight: 600;
          cursor: pointer;
          display: flex;
          align-items: center;
          gap: 8px;
          transition: all 0.3s ease;
          flex: 1;
          justify-content: center;
        }

        .btn-aceptar:hover:not(:disabled) {
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
        }

        .btn-aceptar:disabled {
          background: #94a3b8;
          cursor: not-allowed;
          transform: none;
        }

        .btn-rechazar {
          background: #f1f5f9;
          color: #64748b;
          border: 1px solid #e2e8f0;
          padding: 12px 20px;
          border-radius: 10px;
          font-weight: 600;
          cursor: pointer;
          display: flex;
          align-items: center;
          gap: 8px;
          transition: all 0.3s ease;
        }

        .btn-rechazar:hover {
          background: #fee2e2;
          color: #dc2626;
          border-color: #fecaca;
        }

        .loading-spinner-small {
          width: 16px;
          height: 16px;
          border: 2px solid rgba(255, 255, 255, 0.3);
          border-top: 2px solid white;
          border-radius: 50%;
          animation: spin 1s linear infinite;
        }

        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }

        @media (max-width: 768px) {
          .invitaciones-container {
            padding: 16px;
          }

          .invitacion-header {
            flex-direction: column;
            gap: 12px;
          }

          .invitacion-info {
            flex-direction: column;
            text-align: center;
          }

          .invitacion-actions {
            flex-direction: column;
          }

          .invitaciones-title {
            font-size: 24px;
          }
        }
      `}</style>
    </div>
  );
}

export default InvitacionesPendientes;