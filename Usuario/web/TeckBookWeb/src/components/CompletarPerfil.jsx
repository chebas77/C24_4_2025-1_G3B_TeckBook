import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  X, 
  GraduationCap, 
  BookOpen, 
  User, 
  CheckCircle,
  AlertCircle,
  Phone 
} from 'lucide-react';
import "../css/CompletarPerfil.css";

function CompletarPerfil({ isOpen, onClose, token, userData, onComplete, isNewUser = false }) {
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    carreraId: '',
    cicloActual: '',
    departamentoId: '',
    telefono: ''
  });
  
  const [carreras, setCarreras] = useState([]);
  const [departamentos, setDepartamentos] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingCarreras, setIsLoadingCarreras] = useState(true);
  const [isLoadingDepartamentos, setIsLoadingDepartamentos] = useState(true);
  const [error, setError] = useState('');

  // Cargar carreras al abrir el modal
  useEffect(() => {
    if (isOpen) {
      // Pequeño delay para evitar múltiples ejecuciones
      const timer = setTimeout(() => {
        fetchCarreras();
        fetchDepartamentos();
      }, 100);
      
      // Prevenir scroll del body cuando el modal está abierto
      document.body.style.overflow = 'hidden';
      
      return () => {
        clearTimeout(timer);
        document.body.style.overflow = 'unset';
      };
    } else {
      // Restaurar scroll del body cuando el modal se cierra
      document.body.style.overflow = 'unset';
    }
  }, [isOpen]);

  const fetchCarreras = async () => {
    try {
      setIsLoadingCarreras(true);
      console.log('Cargando carreras...');
      const response = await fetch('http://localhost:8080/api/carreras/activas');
      if (response.ok) {
        const data = await response.json();
        setCarreras(data.carreras || []);
        console.log('Carreras cargadas:', data.carreras?.length || 0);
      } else {
        console.error('Error al cargar carreras:', response.status);
        setError('Error al cargar carreras');
      }
    } catch (err) {
      console.error('Error de conexión:', err);
      setError('Error de conexión al cargar carreras');
    } finally {
      setIsLoadingCarreras(false);
    }
  };

  const fetchDepartamentos = async () => {
    try {
      setIsLoadingDepartamentos(true);
      const response = await fetch('http://localhost:8080/api/departamentos/activos');
      if (response.ok) {
        const data = await response.json();
        setDepartamentos(data.departamentos || []);
      } else {
        setError('Error al cargar departamentos');
      }
    } catch (err) {
      setError('Error de conexión al cargar departamentos');
    } finally {
      setIsLoadingDepartamentos(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    if (error) setError('');
  };

  const validateForm = () => {
    if (!formData.carreraId) {
      setError('Por favor selecciona una carrera');
      return false;
    }
    
    if (!formData.cicloActual) {
      setError('Por favor selecciona tu ciclo actual');
      return false;
    }
    
    if (!formData.departamentoId) {
      setError('Por favor selecciona un departamento');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    setIsLoading(true);
    setError('');

    try {
      // Endpoint para completar perfil
      const response = await fetch(`http://localhost:8080/api/usuarios/${userData.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          ...userData,
          carreraId: parseInt(formData.carreraId),
          cicloActual: parseInt(formData.cicloActual),
          departamentoId: parseInt(formData.departamentoId),
          telefono: formData.telefono || null
        })
      });

      if (response.ok) {
        const result = await response.json();
        console.log('Perfil completado exitosamente:', result);
        
        // Callback para el padre
        if (onComplete) {
          onComplete(result);
        }
        
        // Guardar token si es nuevo usuario
        if (isNewUser && token) {
          localStorage.setItem('token', token);
        }

        // Cerrar modal
        onClose();
        
        // Navegar si es nuevo usuario
        if (isNewUser) {
          setTimeout(() => {
            navigate('/home');
          }, 500);
        }
      } else {
        const errorData = await response.text();
        console.error('Error del servidor:', errorData);
        setError('Error al completar perfil: ' + errorData);
      }
    } catch (err) {
      console.error('Error de red:', err);
      setError('Error de conexión. Intenta nuevamente.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleClose = () => {
    if (!isLoading) {
      onClose();
    }
  };

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget && !isLoading) {
      handleClose();
    }
  };

  // No renderizar si no está abierto
  if (!isOpen) return null;

  return (
    <div className="perfil-modal-overlay" onClick={handleBackdropClick}>
      <div className="perfil-modal-container">
        {/* Header del modal */}
        <div className="perfil-modal-header">
          <div className="perfil-modal-title">
            <GraduationCap size={24} className="perfil-modal-icon" />
            <h2>¡Completa tu perfil!</h2>
          </div>
          <button 
            onClick={handleClose} 
            className="perfil-modal-close-button" 
            disabled={isLoading}
          >
            <X size={20} />
          </button>
        </div>

        {/* Contenido del modal */}
        <div className="perfil-modal-content">
          <div className="welcome-section">
            <div className="user-info">
              <div className="user-avatar">
                {userData?.profileImageUrl ? (
                  <img src={userData.profileImageUrl} alt="Perfil" />
                ) : (
                  <span>
                    {userData?.nombre?.charAt(0)}{userData?.apellidos?.charAt(0)}
                  </span>
                )}
              </div>
              <div className="user-details">
                <h3>¡Hola {userData?.nombre || 'Usuario'}! 👋</h3>
                <p>Para completar tu registro, necesitamos algunos datos adicionales</p>
              </div>
            </div>
          </div>

          {error && (
            <div className="error-message">
              <AlertCircle size={16} />
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="profile-form">
            {/* Selección de Carrera */}
            <div className="form-group">
              <label htmlFor="carreraId" className="form-label">
                <GraduationCap size={18} />
                Carrera
              </label>
              <select
                id="carreraId"
                name="carreraId"
                value={formData.carreraId}
                onChange={handleChange}
                className="form-select"
                disabled={isLoadingCarreras || isLoading}
                required
              >
                <option value="">
                  {isLoadingCarreras ? 'Cargando carreras...' : 'Selecciona tu carrera'}
                </option>
                {carreras.map(carrera => (
                  <option key={carrera.id} value={carrera.id}>
                    {carrera.nombre}
                  </option>
                ))}
              </select>
            </div>

            {/* Ciclo y Departamento */}
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="cicloActual" className="form-label">
                  <BookOpen size={18} />
                  Ciclo Actual
                </label>
                <select
                  id="cicloActual"
                  name="cicloActual"
                  value={formData.cicloActual}
                  onChange={handleChange}
                  className="form-select"
                  disabled={isLoading}
                  required
                >
                  <option value="">Selecciona tu ciclo</option>
                  {[1, 2, 3, 4, 5, 6].map(ciclo => (
                    <option key={ciclo} value={ciclo}>
                      {ciclo}° Ciclo
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="departamentoId" className="form-label">
                  <GraduationCap size={18} />
                  Departamento
                </label>
                <select
                  id="departamentoId"
                  name="departamentoId"
                  value={formData.departamentoId}
                  onChange={handleChange}
                  className="form-select"
                  disabled={isLoadingDepartamentos || isLoading}
                  required
                >
                  <option value="">
                    {isLoadingDepartamentos ? 'Cargando departamentos...' : 'Selecciona departamento'}
                  </option>
                  {departamentos.map(depto => (
                    <option key={depto.id} value={depto.id}>
                      {depto.nombre}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Campo de Teléfono */}
            <div className="form-group">
              <label htmlFor="telefono" className="form-label">
                <Phone size={18} />
                Teléfono (Opcional)
              </label>
              <input
                type="tel"
                id="telefono"
                name="telefono"
                value={formData.telefono}
                onChange={handleChange}
                placeholder="Ej: +51 999 888 777"
                className="form-select"
                disabled={isLoading}
              />
            </div>

            {/* Información adicional */}
            <div className="info-box">
              <CheckCircle size={16} />
              <span>Esta información nos ayuda a personalizar tu experiencia y mantenerte conectado</span>
            </div>

            {/* Botones de acción */}
            <div className="perfil-modal-actions">
              <button
                type="button"
                onClick={handleClose}
                className="btn-secondary"
                disabled={isLoading}
              >
                Completar después
              </button>
              <button
                type="submit"
                className="btn-primary"
                disabled={isLoading || isLoadingCarreras || isLoadingDepartamentos}
              >
                {isLoading ? (
                  <>
                    <div className="loading-spinner" />
                    Guardando...
                  </>
                ) : (
                  <>
                    <CheckCircle size={18} />
                    Completar perfil
                  </>
                )}
              </button>
            </div>

            <div className="form-note">
              Los campos marcados con * son obligatorios. El teléfono es opcional.
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default CompletarPerfil;