import React, { useState, useEffect } from 'react';
import { Mail, Clock, Check, X, User, BookOpen, AlertCircle } from 'lucide-react';

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
        
        alert('¡Te has unido al aula exitosamente!');
        fetchInvitacionesPendientes();
        
        if (onAulaAceptada) onAulaAceptada();
        
        // Cerrar modal si no hay más invitaciones
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
      console.error('Error al aceptar invitación:', error);
      alert('Error de conexión al aceptar la invitación');
    } finally {
      setProcesando(null);
    }
  };

  const rechazarInvitacion = async (codigoInvitacion) => {
    try {
      setProcesando(codigoInvitacion);
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/invitaciones/rechazar/${codigoInvitacion}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (response.ok) {
        alert('Invitación rechazada correctamente.');
        fetchInvitacionesPendientes();
        
        // Cerrar modal si no hay más invitaciones
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
      console.error('Error al rechazar invitación:', error);
      alert('Error de conexión al rechazar la invitación');
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

  // No mostrar modal si no está abierto
  if (!isOpen) return null;

  if (isLoading) {
    return (
      <div className="modal-overlay" onClick={onClose}>
        <div className="modal-content" onClick={(e) => e.stopPropagation()}>
          <div className="invitaciones-loading">
            <div className="spinner"></div>
            <p>Cargando invitaciones...</p>
          </div>
        </div>
      </div>
    );
  }

  if (invitaciones.length === 0) {
    return (
      <div className="modal-overlay" onClick={onClose}>
        <div className="modal-content" onClick={(e) => e.stopPropagation()}>
          <div className="modal-header">
            <h3>Invitaciones Pendientes</h3>
            <button className="close-btn" onClick={onClose}>
              <X size={20} />
            </button>
          </div>
          <div className="invitaciones-empty">
            <Mail size={48} className="empty-icon" />
            <h3>No tienes invitaciones pendientes</h3>
            <p>Cuando recibas invitaciones a aulas virtuales, aparecerán aquí.</p>
          </div>
        </div>
      </div>
    );
  }

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
          <p className="invitaciones-subtitle">
            Tienes invitaciones para unirte a aulas virtuales
          </p>
          <div className="invitaciones-list">
            {invitaciones.map((invitacion) => {
              const dias = diasRestantes(invitacion.fechaExpiracion);
              return (
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
                      <span className={`dias-restantes ${dias <= 1 ? 'urgente' : dias <= 3 ? 'advertencia' : ''}`}>
                        {dias <= 1 && <AlertCircle size={16} />}
                        {dias} días restantes
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
                          <div className="spinner-small"></div>
                          Procesando...
                        </>
                      ) : (
                        <>
                          <Check size={16} />
                          Aceptar Invitación
                        </>
                      )}
                    </button>
                    <button
                      onClick={() => rechazarInvitacion(invitacion.codigoInvitacion)}
                      disabled={procesando === invitacion.codigoInvitacion}
                      className="btn-rechazar"
                    >
                      {procesando === invitacion.codigoInvitacion ? (
                        <>
                          <div className="spinner-small"></div>
                          Procesando...
                        </>
                      ) : (
                        <>
                          <X size={16} />
                          Rechazar
                        </>
                      )}
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
}

export default InvitacionesPendientes;