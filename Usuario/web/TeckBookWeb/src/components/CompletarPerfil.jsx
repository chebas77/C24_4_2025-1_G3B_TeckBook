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
import { ENDPOINTS, ROUTES } from '../config/apiConfig';
import apiService from '../services/apiService';


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

  // Precargar datos del usuario cuando se abre el modal o cuando userData cambia
  useEffect(() => {
    if (isOpen && userData) {
      console.log('Precargando datos del usuario:', userData);
      
      setFormData({
        carreraId: userData.carreraId || '',
        cicloActual: userData.cicloActual || userData.ciclo || '',
        departamentoId: userData.departamentoId || '',
        telefono: userData.telefono || ''
      });
    }
  }, [isOpen, userData]);

  // Cargar carreras y departamentos al abrir el modal
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

    const data = await apiService.get(ENDPOINTS.CARRERAS.ACTIVAS);
    const carrerasList = data.carreras || [];
    setCarreras(carrerasList);
    console.log('Carreras cargadas:', carrerasList.length);

    if (userData?.carreraId) {
      const carreraExiste = carrerasList.find(c => c.id === parseInt(userData.carreraId));
      if (!carreraExiste) {
        console.warn('La carrera del usuario no está en la lista de carreras activas');
      }
    }
  } catch (err) {
    console.error('Error al cargar carreras:', err);
    setError('Error al cargar carreras');
  } finally {
    setIsLoadingCarreras(false);
  }
};


  const fetchDepartamentos = async () => {
  try {
    setIsLoadingDepartamentos(true);

    const data = await apiService.get(ENDPOINTS.DEPARTAMENTOS.ACTIVOS);
    const departamentosList = data.departamentos || [];
    setDepartamentos(departamentosList);
    console.log('Departamentos cargados:', departamentosList.length);

    if (userData?.departamentoId) {
      const departamentoExiste = departamentosList.find(d => d.id === parseInt(userData.departamentoId));
      if (!departamentoExiste) {
        console.warn('El departamento del usuario no está en la lista de departamentos activos');
      }
    }
  } catch (err) {
    console.error('Error al cargar departamentos:', err);
    setError('Error al cargar departamentos');
  } finally {
    setIsLoadingDepartamentos(false);
  }
};


  const handleChange = ({ target: { name, value } }) => {
  setFormData(prev => ({
    ...prev,
    [name]: value
  }));

  if (error) {
    setError('');
  }
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

  if (!userData?.correoInstitucional) {
    setError('Falta tu correo institucional. Comunícate con soporte.');
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
    // Preparar datos para enviar
    const dataToUpdate = {
      ...userData,
      carreraId: parseInt(formData.carreraId),
      cicloActual: formData.cicloActual, // string o convertir según tu backend
      departamentoId: parseInt(formData.departamentoId),
      telefono: formData.telefono.trim() || null
    };

    console.log('Enviando datos actualizados:', dataToUpdate);

    // Usar apiService
    const result = await apiService.put(`/api/usuarios/${userData.id}`, dataToUpdate);

    console.log('Perfil completado exitosamente:', result);

    if (onComplete) {
      onComplete(result);
    }

    if (isNewUser && token) {
      localStorage.setItem('token', token);
    }

    onClose();

    if (isNewUser) {
      setTimeout(() => {
        navigate(ROUTES.PROTECTED.DASHBOARD);
      }, 500);
    }
    
  } catch (err) {
    console.error('Error en el servidor o conexión:', err);
    setError('Error al completar perfil: ' + (err.message || 'Intenta nuevamente.'));
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

// Obtener iniciales del usuario
const getUserInitials = () => {
  if (userData?.nombre && userData?.apellidos) {
    return `${userData.nombre.charAt(0)}${userData.apellidos.charAt(0)}`.toUpperCase();
  }
  return 'GS'; // Valor por defecto si falta info
};

// Verificar qué campos faltan
const getMissingFields = () => {
  const missing = [];
  if (!userData?.carreraId && !formData.carreraId) missing.push('carrera');
  if (!userData?.cicloActual && !userData?.ciclo && !formData.cicloActual) missing.push('ciclo');
  if (!userData?.departamentoId && !formData.departamentoId) missing.push('departamento');
  if (!userData?.correoInstitucional) missing.push('correo institucional');
  return missing;
};

// Verificar si el formulario tiene cambios
const hasChanges = () => {
  return (
    formData.carreraId !== (userData?.carreraId || '') ||
    formData.cicloActual !== (userData?.cicloActual || userData?.ciclo || '') ||
    formData.departamentoId !== (userData?.departamentoId || '') ||
    formData.telefono !== (userData?.telefono || '')
  );
};

// No renderizar si el modal está cerrado
if (!isOpen) return null;

// Calcular campos faltantes y si el formulario está listo para enviar
const missingFields = getMissingFields();
const isFormComplete = missingFields.length === 0 && hasChanges();


  return (
    <div className="perfil-modal-overlay" onClick={handleBackdropClick}>
      <div className="perfil-modal-container">
        {/* Header del modal */}
        <div className="perfil-modal-header">
          <div className="perfil-modal-title">
            <GraduationCap size={24} className="perfil-modal-icon" />
            <h2>
              {missingFields.length > 0 ? '¡Completa tu perfil!' : '¡Actualiza tu perfil!'}
            </h2>
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
                  <span>{getUserInitials()}</span>
                )}
              </div>
              <div className="user-details">
                <h3>¡Hola {userData?.nombre || 'Usuario'}! 👋</h3>
                <p>
                  {missingFields.length > 0 
                    ? `Faltan algunos datos: ${missingFields.join(', ')}`
                    : 'Revisa y actualiza tu información académica'
                  }
                </p>
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
                {userData?.carreraId && <span className="field-status">✓ Configurado</span>}
              </label>
              <select
                id="carreraId"
                name="carreraId"
                value={formData.carreraId}
                onChange={handleChange}
                className={`form-select ${userData?.carreraId ? 'has-value' : ''}`}
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
                  {(userData?.cicloActual || userData?.ciclo) && <span className="field-status">✓ Configurado</span>}
                </label>
                <select
                  id="cicloActual"
                  name="cicloActual"
                  value={formData.cicloActual}
                  onChange={handleChange}
                  className={`form-select ${(userData?.cicloActual || userData?.ciclo) ? 'has-value' : ''}`}
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
                  {userData?.departamentoId && <span className="field-status">✓ Configurado</span>}
                </label>
                <select
                  id="departamentoId"
                  name="departamentoId"
                  value={formData.departamentoId}
                  onChange={handleChange}
                  className={`form-select ${userData?.departamentoId ? 'has-value' : ''}`}
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
                {userData?.telefono && <span className="field-status">✓ Configurado</span>}
              </label>
              <input
                type="tel"
                id="telefono"
                name="telefono"
                value={formData.telefono}
                onChange={handleChange}
                placeholder="Ej: +51 999 888 777"
                className={`form-select ${userData?.telefono ? 'has-value' : ''}`}
                disabled={isLoading}
              />
            </div>

            {/* Información adicional */}
            <div className="info-box">
              <CheckCircle size={16} />
              <span>
                {missingFields.length > 0 
                  ? 'Esta información nos ayuda a personalizar tu experiencia'
                  : 'Mantén tu información actualizada para una mejor experiencia'
                }
              </span>
            </div>

            {/* Botones de acción */}
            <div className="perfil-modal-actions">
              <button
                type="button"
                onClick={handleClose}
                className="btn-secondary"
                disabled={isLoading}
              >
                {missingFields.length > 0 ? 'Completar después' : 'Cancelar'}
              </button>
              <button
                type="submit"
                className="btn-primary"
                disabled={isLoading || isLoadingCarreras || isLoadingDepartamentos || (!hasChanges() && missingFields.length === 0)}
              >
                {isLoading ? (
                  <>
                    <div className="loading-spinner" />
                    Guardando...
                  </>
                ) : (
                  <>
                    <CheckCircle size={18} />
                    {missingFields.length > 0 ? 'Completar perfil' : 'Actualizar perfil'}
                  </>
                )}
              </button>
            </div>

            <div className="form-note">
              {missingFields.length > 0 
                ? `Campos faltantes: ${missingFields.join(', ')}. El teléfono es opcional.`
                : 'Todos los campos están completos. El teléfono es opcional.'
              }
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default CompletarPerfil;