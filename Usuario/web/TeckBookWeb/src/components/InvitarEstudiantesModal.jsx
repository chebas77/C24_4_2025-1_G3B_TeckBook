import React, { useState } from 'react';
import { X, Mail, Send, Users, AlertCircle, CheckCircle } from 'lucide-react';

function InvitarEstudiantesModal({ isOpen, onClose, aulaId, aulaNombre }) {
  const [correos, setCorreos] = useState('');
  const [mensaje, setMensaje] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [invitacionesEnviadas, setInvitacionesEnviadas] = useState([]);
  const [errores, setErrores] = useState([]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setInvitacionesEnviadas([]);
    setErrores([]);

    // Separar correos por líneas o comas
    const listaCorreos = correos
      .split(/[,\n]/)
      .map(email => email.trim())
      .filter(email => email.length > 0);

    if (listaCorreos.length === 0) {
      setErrores(['Debe ingresar al menos un correo electrónico']);
      setIsLoading(false);
      return;
    }

    const token = localStorage.getItem('token');
    const exitosas = [];
    const fallidas = [];

    // Enviar invitaciones una por una
    for (const correo of listaCorreos) {
      try {
        const response = await fetch('http://localhost:8080/api/invitaciones/enviar', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            aulaId: aulaId,
            correoInvitado: correo,
            mensaje: mensaje
          })
        });

        if (response.ok) {
          const data = await response.json();
          exitosas.push({ correo, data });
        } else {
          const errorData = await response.json();
          fallidas.push({ correo, error: errorData.error || 'Error desconocido' });
        }
      } catch (error) {
        fallidas.push({ correo, error: 'Error de conexión' });
      }
    }

    setInvitacionesEnviadas(exitosas);
    setErrores(fallidas);
    setIsLoading(false);

    // Si todas fueron exitosas, limpiar el formulario
    if (fallidas.length === 0) {
      setCorreos('');
      setMensaje('');
    }
  };

  const handleClose = () => {
    setCorreos('');
    setMensaje('');
    setInvitacionesEnviadas([]);
    setErrores([]);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content" style={{ maxWidth: '600px' }}>
        <div className="modal-header">
          <div className="modal-title-section">
            <h3 className="modal-title">
              <Users size={24} style={{ marginRight: '8px' }} />
              Invitar Estudiantes
            </h3>
            <p className="modal-subtitle">Aula: {aulaNombre}</p>
          </div>
          <button onClick={handleClose} className="modal-close-btn">
            <X size={20} />
          </button>
        </div>

        <div className="modal-body">
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">
                <Mail size={16} style={{ marginRight: '6px' }} />
                Correos Electrónicos
              </label>
              <textarea
                value={correos}
                onChange={(e) => setCorreos(e.target.value)}
                placeholder="Ingresa los correos separados por comas o en líneas separadas:&#10;estudiante1@tecsup.edu.pe&#10;estudiante2@tecsup.edu.pe"
                className="form-textarea"
                rows={4}
                required
              />
              <p className="form-help">
                Solo se aceptan correos institucionales (@tecsup.edu.pe)
              </p>
            </div>

            <div className="form-group">
              <label className="form-label">Mensaje personalizado (opcional)</label>
              <textarea
                value={mensaje}
                onChange={(e) => setMensaje(e.target.value)}
                placeholder="Escribe un mensaje de bienvenida para los estudiantes..."
                className="form-textarea"
                rows={3}
              />
            </div>

            {/* Resultados de invitaciones enviadas */}
            {invitacionesEnviadas.length > 0 && (
              <div className="results-section success-results">
                <div className="results-header">
                  <CheckCircle size={20} className="success-icon" />
                  <h4>Invitaciones enviadas exitosamente ({invitacionesEnviadas.length})</h4>
                </div>
                <div className="results-list">
                  {invitacionesEnviadas.map((item, index) => (
                    <div key={index} className="result-item success">
                      <span className="result-email">{item.correo}</span>
                      <span className="result-status">✓ Enviada</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Errores */}
            {errores.length > 0 && (
              <div className="results-section error-results">
                <div className="results-header">
                  <AlertCircle size={20} className="error-icon" />
                  <h4>Errores en el envío ({errores.length})</h4>
                </div>
                <div className="results-list">
                  {errores.map((item, index) => (
                    <div key={index} className="result-item error">
                      <span className="result-email">{item.correo}</span>
                      <span className="result-error">{item.error}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            <div className="modal-actions">
              <button type="button" onClick={handleClose} className="btn-secondary">
                Cancelar
              </button>
              <button 
                type="submit" 
                className="btn-primary"
                disabled={isLoading}
              >
                {isLoading ? (
                  <>
                    <div className="loading-spinner-small"></div>
                    Enviando...
                  </>
                ) : (
                  <>
                    <Send size={16} style={{ marginRight: '6px' }} />
                    Enviar Invitaciones
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>

      <style jsx>{`
        .modal-overlay {
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(15, 23, 42, 0.6);
          backdrop-filter: blur(8px);
          display: flex;
          justify-content: center;
          align-items: center;
          z-index: 1000;
          animation: fadeIn 0.3s ease;
        }

        .modal-content {
          background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
          border-radius: 20px;
          box-shadow: 0 32px 80px rgba(0, 93, 171, 0.2);
          width: 90%;
          max-height: 85vh;
          overflow: auto;
          animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        .modal-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          padding: 24px 32px;
          border-bottom: 1px solid #e2e8f0;
          background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
        }

        .modal-title-section {
          flex: 1;
        }

        .modal-title {
          font-size: 24px;
          font-weight: 700;
          color: #1e293b;
          margin: 0 0 4px 0;
          display: flex;
          align-items: center;
        }

        .modal-subtitle {
          font-size: 14px;
          color: #64748b;
          margin: 0;
        }

        .modal-close-btn {
          background: rgba(148, 163, 184, 0.1);
          border: none;
          border-radius: 12px;
          padding: 12px;
          cursor: pointer;
          color: #64748b;
          transition: all 0.3s ease;
        }

        .modal-close-btn:hover {
          background: rgba(239, 68, 68, 0.1);
          color: #ef4444;
        }

        .modal-body {
          padding: 32px;
        }

        .form-group {
          margin-bottom: 24px;
        }

        .form-label {
          display: flex;
          align-items: center;
          font-size: 14px;
          font-weight: 600;
          color: #374151;
          margin-bottom: 8px;
        }

        .form-textarea {
          width: 100%;
          padding: 12px 16px;
          border: 1px solid #e2e8f0;
          border-radius: 12px;
          font-size: 14px;
          font-family: inherit;
          resize: vertical;
          transition: all 0.3s ease;
          box-sizing: border-box;
        }

        .form-textarea:focus {
          border-color: #005DAB;
          box-shadow: 0 0 0 3px rgba(0, 93, 171, 0.1);
          outline: none;
        }

        .form-help {
          font-size: 12px;
          color: #64748b;
          margin: 4px 0 0 0;
        }

        .results-section {
          margin: 20px 0;
          border-radius: 12px;
          overflow: hidden;
        }

        .success-results {
          border: 1px solid #10b981;
          background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
        }

        .error-results {
          border: 1px solid #ef4444;
          background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
        }

        .results-header {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 12px 16px;
          border-bottom: 1px solid rgba(0, 0, 0, 0.1);
        }

        .results-header h4 {
          margin: 0;
          font-size: 14px;
          font-weight: 600;
        }

        .success-results .results-header {
          background: rgba(16, 185, 129, 0.1);
          color: #065f46;
        }

        .error-results .results-header {
          background: rgba(239, 68, 68, 0.1);
          color: #7f1d1d;
        }

        .success-icon {
          color: #10b981;
        }

        .error-icon {
          color: #ef4444;
        }

        .results-list {
          padding: 0;
        }

        .result-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 16px;
          border-bottom: 1px solid rgba(0, 0, 0, 0.05);
        }

        .result-item:last-child {
          border-bottom: none;
        }

        .result-email {
          font-size: 14px;
          font-weight: 500;
        }

        .result-status {
          font-size: 12px;
          font-weight: 600;
          color: #10b981;
        }

        .result-error {
          font-size: 12px;
          color: #ef4444;
          text-align: right;
          flex: 1;
          margin-left: 12px;
        }

        .modal-actions {
          display: flex;
          justify-content: flex-end;
          gap: 16px;
          margin-top: 32px;
          padding-top: 24px;
          border-top: 1px solid #e2e8f0;
        }

        .btn-secondary {
          background: #f1f5f9;
          color: #64748b;
          border: none;
          padding: 12px 24px;
          border-radius: 12px;
          font-size: 14px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.3s ease;
        }

        .btn-secondary:hover {
          background: #e2e8f0;
          color: #475569;
        }

        .btn-primary {
          background: linear-gradient(135deg, #005DAB 0%, #0066c7 100%);
          color: white;
          border: none;
          padding: 12px 24px;
          border-radius: 12px;
          font-size: 14px;
          font-weight: 600;
          cursor: pointer;
          display: flex;
          align-items: center;
          transition: all 0.3s ease;
          box-shadow: 0 4px 12px rgba(0, 93, 171, 0.2);
        }

        .btn-primary:hover:not(:disabled) {
          transform: translateY(-1px);
          box-shadow: 0 8px 24px rgba(0, 93, 171, 0.3);
        }

        .btn-primary:disabled {
          background: #94a3b8;
          cursor: not-allowed;
          transform: none;
          box-shadow: none;
        }

        .loading-spinner-small {
          width: 16px;
          height: 16px;
          border: 2px solid rgba(255, 255, 255, 0.3);
          border-top: 2px solid white;
          border-radius: 50%;
          animation: spin 1s linear infinite;
          margin-right: 8px;
        }

        @keyframes fadeIn {
          from { opacity: 0; }
          to { opacity: 1; }
        }

        @keyframes slideUp {
          from { 
            opacity: 0;
            transform: translateY(40px);
          }
          to { 
            opacity: 1;
            transform: translateY(0);
          }
        }

        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }

        @media (max-width: 768px) {
          .modal-content {
            width: 95%;
            margin: 20px;
          }

          .modal-header {
            padding: 20px;
          }

          .modal-body {
            padding: 20px;
          }

          .modal-actions {
            flex-direction: column;
          }

          .btn-secondary,
          .btn-primary {
            width: 100%;
            justify-content: center;
          }
        }
      `}</style>
    </div>
  );
}

export default InvitarEstudiantesModal;