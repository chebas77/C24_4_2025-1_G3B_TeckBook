import { useState, useEffect } from 'react';
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
  Calendar
} from 'lucide-react';
import InvitarEstudiantesModal from './InvitarEstudiantesModal';
import './CrearAula.css';

function CrearAula() {
  // Simulación de navegación
  const navigate = (path) => {
    console.log('Navegando a:', path);
    // Aquí implementarías la navegación real
  };
  
  // Estados para el formulario
  const [formData, setFormData] = useState({
    nombre: '',
    titulo: '',
    descripcion: '',
    departamentoId: '',
    carreraId: '',
    ciclo: '',
    seccionId: '',
    fechaInicio: '',
    fechaFin: ''
  });
  
  // Estados para los filtros en cascada
  const [departamentos, setDepartamentos] = useState([]);
  const [carreras, setCarreras] = useState([]);
  const [ciclos] = useState([1, 2, 3, 4, 5, 6]); // Ciclos predefinidos
  const [secciones, setSecciones] = useState([]);
  
  // Estados de UI
  const [isLoading, setIsLoading] = useState(false);
  const [success, setSuccess] = useState(null);
  const [error, setError] = useState(null);
  const [aulaCreada, setAulaCreada] = useState(null);
  const [showInviteModal, setShowInviteModal] = useState(false);

  useEffect(() => {
    fetchDepartamentos();
  }, []);

  // Cargar departamentos
  const fetchDepartamentos = async () => {
    try {
      // Por ahora usamos datos mock, luego conectar al backend
      const mockDepartamentos = [
        { id: 1, nombre: 'Tecnología Digital' },
        { id: 2, nombre: 'Mecánica y Producción' },
        { id: 3, nombre: 'Electrónica y Automatización' },
        { id: 4, nombre: 'Administración' }
      ];
      setDepartamentos(mockDepartamentos);
    } catch (error) {
      console.error('Error al cargar departamentos:', error);
    }
  };

  // Cargar carreras cuando cambia el departamento
  const fetchCarreras = async (departamentoId) => {
    if (!departamentoId) {
      setCarreras([]);
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/carreras/departamento/${departamentoId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (response.ok) {
        const data = await response.json();
        setCarreras(data.carreras || []);
      } else {
        // Fallback con datos mock
        const mockCarreras = {
          1: [ // Tecnología Digital
            { id: 1, nombre: 'Desarrollo de Software' },
            { id: 2, nombre: 'Administración de Redes y Comunicaciones' }
          ],
          2: [ // Mecánica y Producción
            { id: 3, nombre: 'Mecánica Automotriz' },
            { id: 4, nombre: 'Producción Industrial' }
          ],
          3: [ // Electrónica y Automatización
            { id: 5, nombre: 'Electrónica Industrial' },
            { id: 6, nombre: 'Automatización Industrial' }
          ]
        };
        setCarreras(mockCarreras[departamentoId] || []);
      }
    } catch (error) {
      console.error('Error al cargar carreras:', error);
      setCarreras([]);
    }
  };

  // Cargar secciones cuando cambian carrera y ciclo
  const fetchSecciones = async (carreraId, ciclo) => {
    if (!carreraId || !ciclo) {
      setSecciones([]);
      return;
    }

    try {
      // Por ahora usamos datos mock, luego conectar al backend
      const mockSecciones = [
        { id: 1, nombre: 'A' },
        { id: 2, nombre: 'B' },
        { id: 3, nombre: 'C' }
      ];
      setSecciones(mockSecciones);
    } catch (error) {
      console.error('Error al cargar secciones:', error);
      setSecciones([]);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });

    // Manejar filtros en cascada
    if (name === 'departamentoId') {
      setFormData({
        ...formData,
        departamentoId: value,
        carreraId: '',
        ciclo: '',
        seccionId: ''
      });
      setCarreras([]);
      setSecciones([]);
      if (value) fetchCarreras(value);
    }
    
    if (name === 'carreraId') {
      setFormData({
        ...formData,
        carreraId: value,
        ciclo: '',
        seccionId: ''
      });
      setSecciones([]);
    }
    
    if (name === 'ciclo') {
      setFormData({
        ...formData,
        ciclo: value,
        seccionId: ''
      });
      if (formData.carreraId && value) {
        fetchSecciones(formData.carreraId, value);
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setIsLoading(true);

    try {
      const token = localStorage.getItem('token');
      
      // Preparar datos para el backend
      const aulaData = {
        nombre: formData.nombre,
        titulo: formData.titulo,
        descripcion: formData.descripcion,
        seccionId: formData.seccionId ? parseInt(formData.seccionId) : null,
        fechaInicio: formData.fechaInicio || null,
        fechaFin: formData.fechaFin || null
      };

      const response = await fetch('http://localhost:8080/api/aulas', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(aulaData)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Error al crear aula');
      }

      const result = await response.json();
      console.log('Aula creada:', result);
      
      setAulaCreada(result.aula);
      setSuccess('¡Aula creada exitosamente! ¿Quieres invitar estudiantes ahora?');
      
      // Limpiar formulario
      setFormData({
        nombre: '',
        titulo: '',
        descripcion: '',
        departamentoId: '',
        carreraId: '',
        ciclo: '',
        seccionId: '',
        fechaInicio: '',
        fechaFin: ''
      });
      
    } catch (err) {
      console.error('Error al crear aula:', err);
      setError(err.message);
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
    navigate('/aulas');
  };

  return (
    <div className="full-page">
      <header className="header">
        <div className="header-content">
          <h1 className="logo">TecBook</h1>
          <nav className="nav">
            <button onClick={() => navigate('/home')} className="nav-link">Inicio</button>
            <button onClick={() => navigate('/perfil')} className="nav-link">Perfil</button>
            <button onClick={() => navigate('/aulas')} className="nav-link">Aulas</button>
            <button onClick={() => navigate('/')} className="nav-link">Cerrar sesión</button>
          </nav>
        </div>
      </header>

      <div className="main-content">
        <div className="modal-content">
          <div className="modal-header">
            <h3>
              <BookOpen size={24} style={{ marginRight: '8px' }} />
              Crear Nueva Aula Virtual
            </h3>
            <button onClick={() => navigate('/aulas')} className="close-button">
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
                      placeholder="Ej: Algoritmos y Estructuras de Datos"
                      value={formData.nombre}
                      onChange={handleChange}
                      className="input"
                      required
                    />
                  </div>
                </div>

                <div className="input-container">
                  <label className="label">Título (opcional)</label>
                  <div className="input-wrapper">
                    <Book size={18} className="input-icon" />
                    <input
                      type="text"
                      name="titulo"
                      placeholder="Ej: AED-2024-I"
                      value={formData.titulo}
                      onChange={handleChange}
                      className="input"
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

            {/* Filtros en Cascada */}
            <div className="form-section">
              <h4 className="form-section-title">Clasificación Académica</h4>
              
              <div className="form-row">
                <div className="input-container">
                  <label className="label">Departamento *</label>
                  <div className="input-wrapper">
                    <Building size={18} className="input-icon" />
                    <select
                      name="departamentoId"
                      value={formData.departamentoId}
                      onChange={handleChange}
                      className="input"
                      required
                    >
                      <option value="">Seleccionar departamento</option>
                      {departamentos.map(dept => (
                        <option key={dept.id} value={dept.id}>
                          {dept.nombre}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="input-container">
                  <label className="label">Carrera *</label>
                  <div className="input-wrapper">
                    <GraduationCap size={18} className="input-icon" />
                    <select
                      name="carreraId"
                      value={formData.carreraId}
                      onChange={handleChange}
                      className="input"
                      disabled={!formData.departamentoId}
                      required
                    >
                      <option value="">
                        {formData.departamentoId ? 'Seleccionar carrera' : 'Primero selecciona departamento'}
                      </option>
                      {carreras.map(carrera => (
                        <option key={carrera.id} value={carrera.id}>
                          {carrera.nombre}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
              </div>

              <div className="form-row">
                <div className="input-container">
                  <label className="label">Ciclo *</label>
                  <div className="input-wrapper">
                    <BookOpen size={18} className="input-icon" />
                    <select
                      name="ciclo"
                      value={formData.ciclo}
                      onChange={handleChange}
                      className="input"
                      disabled={!formData.carreraId}
                      required
                    >
                      <option value="">
                        {formData.carreraId ? 'Seleccionar ciclo' : 'Primero selecciona carrera'}
                      </option>
                      {ciclos.map(ciclo => (
                        <option key={ciclo} value={ciclo}>
                          Ciclo {ciclo}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <div className="input-container">
                  <label className="label">Sección</label>
                  <div className="input-wrapper">
                    <Users size={18} className="input-icon" />
                    <select
                      name="seccionId"
                      value={formData.seccionId}
                      onChange={handleChange}
                      className="input"
                      disabled={!formData.ciclo}
                    >
                      <option value="">
                        {formData.ciclo ? 'Seleccionar sección (opcional)' : 'Primero selecciona ciclo'}
                      </option>
                      {secciones.map(seccion => (
                        <option key={seccion.id} value={seccion.id}>
                          Sección {seccion.nombre}
                        </option>
                      ))}
                    </select>
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
                onClick={() => navigate('/aulas')} 
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

            {/* Mensajes */}
            {error && (
              <div className="error-message">
                {error}
              </div>
            )}
            
            {success && (
              <div className="success-message-container">
                <div className="success-message">
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
                    onClick={() => navigate('/aulas')}
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