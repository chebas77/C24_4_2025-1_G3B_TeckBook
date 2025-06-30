import React, { useState, useEffect } from 'react';
import { Mail, Clock, Check, X, User, BookOpen, AlertCircle } from 'lucide-react';
import '../css/InvitacionesPendientes.css'; // Opcional: si prefieres mover los estilos a un archivo externo

function InvitacionesPendientes({ isOpen, onClose, onAulaAceptada }) {
  const [invitaciones, setInvitaciones] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [procesando, setProcesando] = useState(null);

  useEffect(() => {
    if (isOpen) {
      fetchInvitacionesPendientes();
    }
  }, [isOpen]);

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

  const aceptarInvitacion = async (codigo) => {
    try {
      setProcesando(codigo);
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/invitaciones/aceptar/${codigo}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        alert('¡Te has unido al aula exitosamente!');
        if (onAulaAceptada) onAulaAceptada();
        fetchInvitacionesPendientes();

        setTimeout(() => {
          if (invitaciones.length <= 1) {
            onClose();
          }
        }, 1000);
      } else {
        const errorData = await response.json();
        alert(`Error: ${errorData.error}`);
      }
    } catch (error) {
      alert('Error al aceptar invitación');
    } finally {
      setProcesando(null);
    }
  };

  const rechazarInvitacion = async (codigo) => {
    try {
      setProcesando(codigo);
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/invitaciones/rechazar/${codigo}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        alert('Invitación rechazada correctamente.');
        fetchInvitacionesPendientes();

        setTimeout(() => {
          if (invitaciones.length <= 1) {
            onClose();
          }
        }, 1000);
      } else {
        const errorData = await response.json();
        alert(`Error: ${errorData.error}`);
      }
    } catch (error) {
      alert('Error al rechazar invitación');
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

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>
            <Mail size={24} />
            Invitaciones Pendientes ({invitaciones.length})
          </h3>
          <button className="close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <div className="modal-body">
          {isLoading ? (
            <div className="spinner"></div>
          ) : invitaciones.length === 0 ? (
            <div className="invitaciones-empty">
              <Mail size={48} className="empty-icon" />
              <h3>No tienes invitaciones pendientes</h3>
              <p>Cuando recibas invitaciones a aulas virtuales, aparecerán aquí.</p>
            </div>
          ) : (
            <div className="invitaciones-list">
              {invitaciones.map((inv) => {
                const dias = diasRestantes(inv.fechaExpiracion);
                return (
                  <div key={inv.id} className="invitacion-card">
                    <div className="invitacion-header">
                      <div className="invitacion-info">
                        <div className="aula-icon">
                          <BookOpen size={20} />
                        </div>
                        <div className="invitacion-details">
                          <h4>Aula Virtual #{inv.aulaVirtualId}</h4>
                          <div className="invitacion-meta">
                            <span>
                              <User size={14} /> Invitado por ID: {inv.invitadoPorId}
                            </span>
                            <span>
                              <Clock size={14} /> {formatearFecha(inv.fechaInvitacion)}
                            </span>
                          </div>
                        </div>
                      </div>
                      <div className="tiempo-restante">
                        <span className={`dias-restantes ${dias <= 1 ? 'urgente' : dias <= 3 ? 'advertencia' : ''}`}>
                          {dias <= 1 && <AlertCircle size={16} />}
                          {dias} días restantes
                        </span>
                      </div>
                    </div>

                    {inv.mensaje && <p className="invitacion-mensaje">"{inv.mensaje}"</p>}

                    <div className="invitacion-actions">
                      <button
                        onClick={() => aceptarInvitacion(inv.codigoInvitacion)}
                        disabled={procesando === inv.codigoInvitacion}
                        className="btn-aceptar"
                      >
                        {procesando === inv.codigoInvitacion ? 'Procesando...' : (
                          <>
                            <Check size={16} /> Aceptar
                          </>
                        )}
                      </button>
                      <button
                        onClick={() => rechazarInvitacion(inv.codigoInvitacion)}
                        disabled={procesando === inv.codigoInvitacion}
                        className="btn-rechazar"
                      >
                        {procesando === inv.codigoInvitacion ? 'Procesando...' : (
                          <>
                            <X size={16} /> Rechazar
                          </>
                        )}
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default InvitacionesPendientes;
