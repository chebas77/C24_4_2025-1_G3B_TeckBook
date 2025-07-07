import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  BookOpen, 
  Users, 
  Book, 
  Save, 
  ArrowLeft, 
  Building, 
  GraduationCap,
  UserPlus,
  Mail,
  Calendar,
  AlertCircle,
  CheckCircle,
  ChevronDown
} from 'lucide-react';
import InvitarEstudiantesModal from '../components/InvitarEstudiantesModal';
import "../css/CrearAula.css";
import Header from '../components/Header';
import apiService from '../services/apiService';
import { ENDPOINTS, ROUTES } from '../config/apiConfig';
import { API_CONFIG } from '../config/apiConfig'; 

function CrearAula() {
  const navigate = useNavigate();
  
  // Estados para el formulario
  const [formData, setFormData] = useState({
    nombre: '',
    titulo: '',
    descripcion: '',
    departamentoId: '',
    carreraId: '',
    cicloId: '',
    seccionId: '',
    fechaInicio: '',
    fechaFin: ''
  });
  
  // Estados para los filtros en cascada conectados al backend
  const [departamentos, setDepartamentos] = useState([]);
  const [carreras, setCarreras] = useState([]);
  const [ciclos, setCiclos] = useState([]);
  const [secciones, setSecciones] = useState([]);
  
  // Estados de loading para cada filtro
  const [loadingDepartamentos, setLoadingDepartamentos] = useState(false);
  const [loadingCarreras, setLoadingCarreras] = useState(false);
  const [loadingCiclos, setLoadingCiclos] = useState(false);
  const [loadingSecciones, setLoadingSecciones] = useState(false);
  
  // Estados de UI
  const [isLoading, setIsLoading] = useState(false);
  const [success, setSuccess] = useState(null);
  const [error, setError] = useState(null);
  const [aulaCreada, setAulaCreada] = useState(null);
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [userData, setUserData] = useState(null);

  // Obtener la fecha de hoy en formato YYYY-MM-DD
  const today = new Date().toISOString().split('T')[0];

  // 🔥 EFECTO: Verificar autenticación y cargar datos iniciales
  useEffect(() => {
  const token = localStorage.getItem('token');
  if (!token) {
    navigate(ROUTES.PUBLIC.LOGIN);
    return;
  }

  fetchUserData(token);
  fetchDepartamentos(token);
}, [navigate]);

  // 🔥 FUNCIÓN: Obtener datos del usuario
  const fetchUserData = async (token) => {
  try {
    const response = await fetch(`${API_CONFIG.API_BASE_URL}/api/auth/user`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!response.ok) {
      throw new Error('No se pudo obtener la información del usuario');
    }

    const data = await response.json();
    console.log('🔍 Usuario obtenido:', data);

    setUserData(data);

    const rol = data.rol?.toUpperCase();
    if (rol !== 'PROFESOR') {
      setError(`Solo los profesores pueden crear aulas. Tu rol actual es: ${data.rol}`);
      setTimeout(() => navigate(ROUTES.PROTECTED.AULAS), 3000);
      return;
    }

  } catch (error) {
    console.error("Error al obtener datos del usuario:", error);
    setError(error.message || 'Error desconocido');
    setTimeout(() => navigate(ROUTES.PUBLIC.LOGIN), 3000);
  }
};

  // 🔥 FUNCIÓN: Cargar departamentos desde el backend
  const fetchDepartamentos = async (token) => {
  try {
    setLoadingDepartamentos(true);
    setError(null);

    console.log('🏢 Cargando departamentos...');
    const response = await fetch(`${API_CONFIG.API_BASE_URL}/api/departamentos/activos`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`Error HTTP: ${response.status}`);
    }

    const data = await response.json();
    console.log('🏢 Departamentos obtenidos:', data.departamentos);

    setDepartamentos(data.departamentos || []);

  } catch (error) {
    console.error('❌ Error al cargar departamentos:', error);
    setError('Error al cargar departamentos: ' + error.message);
    setDepartamentos([]);
  } finally {
    setLoadingDepartamentos(false);
  }
};

  // 🔥 FUNCIÓN: Cargar carreras por departamento
  const fetchCarreras = async (departamentoId, token) => {
  if (!departamentoId) {
    setCarreras([]);
    return;
  }

  try {
    setLoadingCarreras(true);
    setError(null);

    console.log(`🎓 Cargando carreras del departamento ${departamentoId}...`);
    const response = await fetch(`${API_CONFIG.API_BASE_URL}/api/carreras/departamento/${departamentoId}/activas`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`Error HTTP: ${response.status}`);
    }

    const data = await response.json();
    console.log(`🎓 Carreras obtenidas:`, data.carreras);

    setCarreras(data.carreras || []);

  } catch (error) {
    console.error(`❌ Error al cargar carreras del departamento ${departamentoId}:`, error);
    setError('Error al cargar carreras: ' + error.message);
    setCarreras([]);
  } finally {
    setLoadingCarreras(false);
  }
};

  // 🔥 FUNCIÓN: Cargar ciclos por carrera
  const fetchCiclos = async (carreraId, token) => {
  if (!carreraId) {
    setCiclos([]);
    return;
  }

  try {
    setLoadingCiclos(true);
    setError(null);

    console.log(`📚 Cargando ciclos de la carrera ${carreraId}...`);
    const response = await fetch(`${API_CONFIG.API_BASE_URL}/api/ciclos/carrera/${carreraId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`Error HTTP: ${response.status}`);
    }

    const data = await response.json();
    console.log(`📚 Ciclos obtenidos:`, data.ciclos);

    setCiclos(data.ciclos || []);

  } catch (error) {
    console.error(`❌ Error al cargar ciclos de la carrera ${carreraId}:`, error);
    setError('Error al cargar ciclos: ' + error.message);
    setCiclos([]);
  } finally {
    setLoadingCiclos(false);
  }
};

  // 🔥 FUNCIÓN: Cargar secciones por carrera y ciclo
  const fetchSecciones = async (carreraId, cicloId, token) => {
  if (!carreraId || !cicloId) {
    setSecciones([]);
    return;
  }

  try {
    setLoadingSecciones(true);
    setError(null);

    console.log(`🏫 Cargando secciones de carrera ${carreraId} y ciclo ${cicloId}...`);
    const response = await fetch(`${API_CONFIG.API_BASE_URL}/api/secciones/carrera/${carreraId}/ciclo/${cicloId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`Error HTTP: ${response.status}`);
    }

    const data = await response.json();
    console.log(`🏫 Secciones obtenidas:`, data.secciones);

    setSecciones(data.secciones || []);

  } catch (error) {
    console.error(`❌ Error al cargar secciones:`, error);
    setError('Error al cargar secciones: ' + error.message);
    setSecciones([]);
  } finally {
    setLoadingSecciones(false);
  }
};

  // 🔥 FUNCIÓN: Manejar cambios en el formulario con lógica de cascada
  const handleChange = (e) => {
    const { name, value } = e.target;
    
    console.log(`🔄 Cambio en ${name}:`, value);
    
    // Limpiar errores cuando el usuario empiece a escribir
    if (error) setError(null);
    
    if (name === 'departamentoId') {
      console.log('🔄 Reseteando cascada desde departamento');
      setFormData({
        ...formData,
        departamentoId: value,
        carreraId: '',
        cicloId: '',
        seccionId: ''
      });
      
      // Limpiar dependientes
      setCarreras([]);
      setCiclos([]);
      setSecciones([]);
      
      // Cargar carreras si se seleccionó un departamento
      if (value) {
        fetchCarreras(value);
      }
      
    } else if (name === 'carreraId') {
      console.log('🔄 Reseteando cascada desde carrera');
      setFormData({
        ...formData,
        carreraId: value,
        cicloId: '',
        seccionId: ''
      });
      
      // Limpiar dependientes
      setCiclos([]);
      setSecciones([]);
      
      // Cargar ciclos si se seleccionó una carrera
      if (value) {
        fetchCiclos(value);
      }
      
    } else if (name === 'cicloId') {
      console.log('🔄 Reseteando cascada desde ciclo');
      setFormData({
        ...formData,
        cicloId: value,
        seccionId: ''
      });
      
      // Limpiar dependientes
      setSecciones([]);
      
      // Cargar secciones si se seleccionó un ciclo
      if (value && formData.carreraId) {
        fetchSecciones(formData.carreraId, value);
      }
      
    } else {
      // Para otros campos, actualizar normalmente
      setFormData({ ...formData, [name]: value });
    }
  };

  // 🔥 FUNCIÓN: Validar formulario
  const validateForm = () => {
    if (!formData.nombre.trim()) {
      return 'El nombre del aula es requerido';
    }
    
    if (!formData.titulo.trim()) {
      return 'El título del aula es requerido';
    }
    
    if (!formData.departamentoId) {
      return 'Debe seleccionar un departamento';
    }
    
    if (!formData.carreraId) {
      return 'Debe seleccionar una carrera';
    }
    
    if (!formData.cicloId) {
      return 'Debe seleccionar un ciclo';
    }
    
    if (formData.fechaInicio && formData.fechaFin) {
      if (new Date(formData.fechaInicio) >= new Date(formData.fechaFin)) {
        return 'La fecha de fin debe ser posterior a la fecha de inicio';
      }
    }
    
    return null;
  };

  // 🔥 FUNCIÓN: Generar código de acceso único
  const generateCodigoAcceso = () => {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let result = '';
    for (let i = 0; i < 8; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
  };

  // 🔥 FUNCIÓN: Enviar formulario
 const handleSubmit = async (e) => {
  e.preventDefault();
  setError(null);
  setSuccess(null);

  // Validar formulario
  const validationError = validateForm();
  if (validationError) {
    setError(validationError);
    return;
  }

  setIsLoading(true);

  try {
    const token = localStorage.getItem('token');

    // Preparar datos del aula
    const aulaData = {
      nombre: formData.nombre.trim(),
      titulo: formData.titulo.trim(),
      descripcion: formData.descripcion.trim(),
      codigoAcceso: generateCodigoAcceso(),
      profesorId: userData.id,
      seccionId: formData.seccionId ? parseInt(formData.seccionId) : null,
      estado: 'activa',
      fechaInicio: formData.fechaInicio || null,
      fechaFin: formData.fechaFin || null,
      departamentoId: parseInt(formData.departamentoId),
      carreraId: parseInt(formData.carreraId),
      cicloId: parseInt(formData.cicloId)
    };

    console.log('📤 Enviando datos del aula:', aulaData);

    const response = await fetch(`${API_CONFIG.API_BASE_URL}/api/aulas`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(aulaData)
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || 'Error al crear aula');
    }

    const result = await response.json();
    console.log('✅ Aula creada exitosamente:', result);

    setAulaCreada(result.aula || result);
    setSuccess(`¡Aula "${formData.nombre}" creada exitosamente! Código de acceso: ${aulaData.codigoAcceso}`);

    // Limpiar formulario
    setFormData({
      nombre: '',
      titulo: '',
      descripcion: '',
      departamentoId: '',
      carreraId: '',
      cicloId: '',
      seccionId: '',
      fechaInicio: '',
      fechaFin: ''
    });

    // Limpiar filtros relacionados
    setCarreras([]);
    setCiclos([]);
    setSecciones([]);

  } catch (err) {
    console.error('❌ Error al crear aula:', err);
    setError('Error al crear el aula: ' + err.message);
  } finally {
    setIsLoading(false);
  }
};

  const handleInviteStudents = () => {
  if (aulaCreada) {
    setShowInviteModal(true);
  }
};

  const handleCloseInviteModal = () => {
    setShowInviteModal(false);
    setAulaCreada(null);
    setSuccess(null);
    navigate(ROUTES.PROTECTED.AULAS);
  };
  
  const handleLogout = async () => {
  try {
    const token = localStorage.getItem('token');

    if (token) {
      await fetch(`${API_CONFIG.API_BASE_URL}/api/auth/logout`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
    }

    localStorage.removeItem('token');
    navigate(ROUTES.PUBLIC.LOGIN);
    
  } catch (error) {
    console.error("❌ Error durante logout:", error);
    localStorage.removeItem('token');
    navigate(ROUTES.PUBLIC.LOGIN);
  }
};

  return (
    <div className="full-page">
      <Header active="Crear Aula" />
      <div className="main-content">
        <div className="modal-content">
          <div className="modal-header">
            <h3>
              <BookOpen size={24} style={{ marginRight: '8px' }} />
              Crear Nueva Aula Virtual
            </h3>
            <button onClick={() => navigate(ROUTES.PROTECTED.AULAS)} className="close-button">
              <ArrowLeft size={20} />
            </button>
          </div>

          <form onSubmit={handleSubmit} className="form">
            {/* Información Básica */}
            <div className="form-section">
              <h4 className="form-section-title">Información Básica</h4>
              
              <div className="form-row">
                <div className="input-container">
                  <label className="label">Nombre del Aula *</label>
                  <div className="input-wrapper">
                    <BookOpen size={18} className="input-icon" />
                    <input
                      type="text"
                      name="nombre"
                      placeholder="Ej: PROGRAMACIÓN BÁSICA"
                      value={formData.nombre}
                      onChange={handleChange}
                      className="input"
                      required
                    />
                  </div>
                </div>

                <div className="input-container">
                  <label className="label">Título/Código *</label>
                  <div className="input-wrapper">
                    <Book size={18} className="input-icon" />
                    <input
                      type="text"
                      name="titulo"
                      placeholder="Ej: PROG-2025-1"
                      value={formData.titulo}
                      onChange={handleChange}
                      className="input"
                      required
                    />
                  </div>
                </div>
              </div>

              <div className="input-container">
                <label className="label">Descripción</label>
                <textarea
                  name="descripcion"
                  placeholder="Describe brevemente el contenido y objetivos del aula..."
                  value={formData.descripcion}
                  onChange={handleChange}
                  className="textarea"
                  rows={3}
                />
              </div>
            </div>

            {/* 🔥 FILTROS EN CASCADA CONECTADOS AL BACKEND */}
            <div className="form-section">
              <h4 className="form-section-title">Clasificación Académica</h4>
              
              <div className="form-row">
                {/* 🏢 DEPARTAMENTO */}
                <div className="input-container">
                  <label className="label">Departamento *</label>
                  <div className="input-wrapper">
                    <Building size={18} className="input-icon" />
                    <select
                      name="departamentoId"
                      value={formData.departamentoId}
                      onChange={handleChange}
                      className={`input ${loadingDepartamentos ? 'loading' : ''}`}
                      disabled={loadingDepartamentos}
                      required
                    >
                      <option value="">
                        {loadingDepartamentos ? 'Cargando departamentos...' : 'Seleccionar departamento'}
                      </option>
                      {departamentos.map(dept => (
                        <option key={dept.id} value={dept.id}>
                          {dept.nombre}
                        </option>
                      ))}
                    </select>
                    <ChevronDown size={16} className="select-arrow" />
                    {loadingDepartamentos && <div className="loading-spinner-small" />}
                  </div>
                </div>

                {/* 🎓 CARRERA */}
                <div className="input-container">
                  <label className="label">Carrera *</label>
                  <div className="input-wrapper">
                    <GraduationCap size={18} className="input-icon" />
                    <select
                      name="carreraId"
                      value={formData.carreraId}
                      onChange={handleChange}
                      className={`input ${loadingCarreras ? 'loading' : ''}`}
                      disabled={!formData.departamentoId || loadingCarreras}
                      required
                    >
                      <option value="">
                        {!formData.departamentoId 
                          ? 'Primero selecciona departamento'
                          : loadingCarreras 
                            ? 'Cargando carreras...' 
                            : 'Seleccionar carrera'
                        }
                      </option>
                      {carreras.map(carrera => (
                        <option key={carrera.id} value={carrera.id}>
                          {carrera.nombre}
                        </option>
                      ))}
                    </select>
                    <ChevronDown size={16} className="select-arrow" />
                    {loadingCarreras && <div className="loading-spinner-small" />}
                  </div>
                </div>
              </div>

              <div className="form-row">
                {/* 📚 CICLO */}
                <div className="input-container">
                  <label className="label">Ciclo *</label>
                  <div className="input-wrapper">
                    <BookOpen size={18} className="input-icon" />
                    <select
                      name="cicloId"
                      value={formData.cicloId}
                      onChange={handleChange}
                      className={`input ${loadingCiclos ? 'loading' : ''}`}
                      disabled={!formData.carreraId || loadingCiclos}
                      required
                    >
                      <option value="">
                        {!formData.carreraId 
                          ? 'Primero selecciona carrera'
                          : loadingCiclos 
                            ? 'Cargando ciclos...' 
                            : 'Seleccionar ciclo'
                        }
                      </option>
                      {ciclos.map(ciclo => (
                        <option key={ciclo.id} value={ciclo.id}>
                          {ciclo.nombre}
                        </option>
                      ))}
                    </select>
                    <ChevronDown size={16} className="select-arrow" />
                    {loadingCiclos && <div className="loading-spinner-small" />}
                  </div>
                </div>

                {/* 🏫 SECCIÓN */}
                <div className="input-container">
                  <label className="label">Sección (Opcional)</label>
                  <div className="input-wrapper">
                    <Users size={18} className="input-icon" />
                    <select
                      name="seccionId"
                      value={formData.seccionId}
                      onChange={handleChange}
                      className={`input ${loadingSecciones ? 'loading' : ''}`}
                      disabled={!formData.carreraId || !formData.cicloId || loadingSecciones}
                    >
                      <option value="">
                        {!formData.carreraId || !formData.cicloId
                          ? 'Primero selecciona ciclo'
                          : loadingSecciones 
                            ? 'Cargando secciones...' 
                            : 'Seleccionar sección (opcional)'
                        }
                      </option>
                      {secciones.map(seccion => (
                        <option key={seccion.id} value={seccion.id}>
                          Sección {seccion.nombre} - {seccion.codigo}
                        </option>
                      ))}
                    </select>
                    <ChevronDown size={16} className="select-arrow" />
                    {loadingSecciones && <div className="loading-spinner-small" />}
                  </div>
                </div>
              </div>
            </div>

            {/* Fechas */}
            <div className="form-section">
              <h4 className="form-section-title">Duración (Opcional)</h4>
              
              <div className="form-row">
                <div className="input-container">
                  <label className="label">Fecha de Inicio</label>
                  <div className="input-wrapper">
                    <Calendar size={18} className="input-icon" />
                    <input
                      type="date"
                      name="fechaInicio"
                      value={formData.fechaInicio}
                      onChange={handleChange}
                      className="input"
                      min={today}
                    />
                  </div>
                </div>

                <div className="input-container">
                  <label className="label">Fecha de Fin</label>
                  <div className="input-wrapper">
                    <Calendar size={18} className="input-icon" />
                    <input
                      type="date"
                      name="fechaFin"
                      value={formData.fechaFin}
                      onChange={handleChange}
                      className="input"
                      min={formData.fechaInicio}
                    />
                  </div>
                </div>
              </div>
            </div>

            {/* Botones */}
            <div className="form-actions">
              <button 
                type="button" 
                onClick={() => navigate(ROUTES.PROTECTED.AULAS)}
                className="cancel-button"
              >
                Cancelar
              </button>
              
              <button 
                type="submit" 
                className="save-button"
                disabled={isLoading}
              >
                {isLoading ? (
                  <>
                    <div className="loading-spinner-small"></div>
                    Creando...
                  </>
                ) : (
                  <>
                    <Save size={18} style={{ marginRight: '8px' }} />
                    Crear Aula
                  </>
                )}
              </button>
            </div>

            {/* 🔥 MENSAJES DE ESTADO MEJORADOS */}
            {error && (
              <div className="error-message">
                <AlertCircle size={16} style={{ marginRight: '8px' }} />
                {error}
              </div>
            )}
            
            {success && (
              <div className="success-message-container">
                <div className="success-message">
                  <CheckCircle size={16} style={{ marginRight: '8px' }} />
                  {success}
                </div>
                <div className="success-actions">
                  <button 
                    type="button"
                    onClick={handleInviteStudents}
                    className="invite-button"
                  >
                    <UserPlus size={16} style={{ marginRight: '6px' }} />
                    Invitar Estudiantes
                  </button>
                  <button 
                    type="button"
                    onClick={() => navigate(ROUTES.PROTECTED.AULAS)}
                    className="continue-button"
                  >
                    Ir a Mis Aulas
                  </button>
                </div>
              </div>
            )}
          </form>
        </div>
      </div>

      {/* Modal de Invitar Estudiantes */}
      {showInviteModal && aulaCreada && (
        <InvitarEstudiantesModal
          isOpen={showInviteModal}
          onClose={handleCloseInviteModal}
          aulaId={aulaCreada.id}
          aulaNombre={aulaCreada.nombre}
        />
      )}
    </div>
  );
}

export default CrearAula;