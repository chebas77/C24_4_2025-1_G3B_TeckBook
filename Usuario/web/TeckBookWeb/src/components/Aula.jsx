import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  BookOpen,
  Users,
  Calendar,
  GraduationCap,
  Plus,
  Search,
  Filter,
  MoreVertical,
  Clock,
  MapPin,
  UserPlus,
  Settings
} from 'lucide-react';
import InvitarEstudiantesModal from './InvitarEstudiantesModal';
import Header from '../components/Header';
import { ENDPOINTS, ROUTES } from '../config/apiConfig';
import apiService from '../services/apiService';
import './Aula.css';

function Aulas() {
  const [aulas, setAulas] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterCarrera, setFilterCarrera] = useState('');
  const [userData, setUserData] = useState(null);
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [selectedAula, setSelectedAula] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate(ROUTES.PUBLIC.LOGIN);
      return;
    }
    fetchUserData();
    fetchAulas();
  }, [navigate]);

 const fetchUserData = async () => {
  try {
    const data = await apiService.get(ENDPOINTS.AUTH.GET_USER);
    setUserData(data);
  } catch (error) {
    console.error('❌ Error al obtener datos del usuario:', error);
    setError(error.message || 'Error desconocido al obtener datos del usuario');
  }
};


  const fetchAulas = async () => {
  try {
    setIsLoading(true);
    const data = await apiService.get(ENDPOINTS.AULAS.BASE);
    setAulas(data.aulas || data || []);
  } catch (error) {
    console.error("❌ Error al cargar las aulas:", error);
    setError("Error al cargar las aulas: " + (error.message || "Error desconocido"));
  } finally {
    setIsLoading(false);
  }
};

  // 👉 Cuando se hace clic en "Invitar", se selecciona el aula y se muestra el modal
const handleInviteStudents = (aula) => {
  setSelectedAula(aula);
  setShowInviteModal(true);
};

// 👉 Redirige al formulario para crear un aula
const handleCreateAula = () => {
  navigate(ROUTES.PROTECTED.CREATE_AULA);
};

// 👉 Se llama cuando se acepta/crea una aula (por ejemplo, después de guardar)
const handleAulaAceptada = () => {
  fetchAulas(); // Refresca la lista
};

// 👉 Filtro de búsqueda en base al nombre, título o descripción del aula
const filteredAulas = aulas.filter((aula) => {
  const search = searchTerm.toLowerCase();
  return (
    (aula.nombre || '').toLowerCase().includes(search) ||
    (aula.titulo || '').toLowerCase().includes(search) ||
    (aula.descripcion || '').toLowerCase().includes(search)
  );
});
  

  const getUserInitials = () => {
    if (userData?.nombre && userData?.apellidos) {
      return `${userData.nombre.charAt(0)}${userData.apellidos.charAt(0)}`.toUpperCase();
    }
    return 'GS';
  };

  const getAulaColor = (index) => {
    const colors = ['#3B82F6', '#10B981', '#F59E0B', '#8B5CF6', '#EF4444', '#06B6D4'];
    return colors[index % colors.length];
  };

  const isProfesor = () => {
    return userData?.rol === 'PROFESOR' || userData?.rol === 'profesor';
  };

  if (isLoading) {
    return (
      <div className="aulas-wrapper">
        <Header />
        <div className="aulas-loading">
          <div className="loading-spinner"></div>
          <p>Cargando aulas...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="aulas-wrapper">
      {/* USAR EL HEADER UNIFICADO */}
      <Header />

      {/* CONTENIDO PRINCIPAL */}
      <div className="aulas-main">
        <div className="aulas-container">
          {/* TÍTULO Y CONTROLES */}
          <div className="aulas-top">
            <div className="aulas-title-section">
              <h2 className="aulas-title">Mis Aulas</h2>
              <p className="aulas-subtitle">
                {isProfesor()
                  ? `Gestiona tus aulas como ${userData?.rol?.toLowerCase() || 'profesor'}`
                  : `Aulas en las que estás inscrito como ${userData?.rol?.toLowerCase() || 'estudiante'}`
                }
              </p>
            </div>

            <div className="aulas-controls">
              <div className="aulas-search">
                <Search size={20} className="search-icon" />
                <input
                  type="text"
                  placeholder="Buscar aulas..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="search-input"
                />
              </div>
              
              {/* BOTÓN DE CREAR AULA SOLO PARA PROFESORES */}
              {isProfesor() && (
                <button onClick={handleCreateAula} className="aulas-create-main-btn" type="button">
                  <Plus size={20} />
                  Crear Nueva Aula
                </button>
              )}
            </div>
          </div>

          {/* GRID DE AULAS */}
          {error ? (
            <div className="aulas-error">
              <p>Error: {error}</p>
              <button onClick={fetchAulas} className="retry-button">
                Intentar de nuevo
              </button>
            </div>
          ) : (
            <div className="aulas-grid">
              {filteredAulas.length === 0 ? (
                <div className="aulas-empty">
                  <BookOpen size={48} color="#94a3b8" />
                  <h3>
                    {aulas.length === 0
                      ? (isProfesor() ? 'No has creado aulas aún' : 'No estás inscrito en ningún aula')
                      : 'No se encontraron aulas'
                    }
                  </h3>
                  <p>
                    {aulas.length === 0
                      ? (isProfesor() ? 'Crea tu primera aula para comenzar' : 'Espera a que un profesor te invite a un aula')
                      : 'No hay aulas que coincidan con tu búsqueda'
                    }
                  </p>
                  {isProfesor() && aulas.length === 0 && (
                    <button onClick={handleCreateAula} className="create-first-aula-btn">
                      <Plus size={20} style={{ marginRight: '8px' }} />
                      Crear mi primera aula
                    </button>
                  )}
                </div>
              ) : (
                filteredAulas.map((aula, index) => (
                  <div key={aula.id} className="aula-card" style={{ '--aula-color': getAulaColor(index) }}>
                    <div className="aula-header">
                      <div className="aula-color-bar"></div>
                      <div className="aula-title-section">
                        <h3 className="aula-name">{aula.nombre || aula.titulo || 'Aula sin nombre'}</h3>
                        {aula.codigoAcceso && (
                          <span className="aula-code">{aula.codigoAcceso}</span>
                        )}
                      </div>
                      <div className="aula-menu-container">
                        {isProfesor() && (
                          <button
                            onClick={() => handleInviteStudents(aula)}
                            className="aula-invite-btn"
                            title="Invitar estudiantes"
                          >
                            <UserPlus size={16} />
                          </button>
                        )}
                        <button className="aula-menu">
                          <MoreVertical size={20} />
                        </button>
                      </div>
                    </div>

                    <div className="aula-content">
                      <p className="aula-description">
                        {aula.descripcion || 'Sin descripción disponible'}
                      </p>

                      <div className="aula-info">
                        <div className="info-item">
                          <BookOpen size={16} className="info-icon" />
                          <span>Estado: {aula.estado || 'Activa'}</span>
                        </div>

                        {aula.fechaInicio && (
                          <div className="info-item">
                            <Calendar size={16} className="info-icon" />
                            <span>Inicio: {new Date(aula.fechaInicio).toLocaleDateString()}</span>
                          </div>
                        )}

                        {aula.fechaFin && (
                          <div className="info-item">
                            <Calendar size={16} className="info-icon" />
                            <span>Fin: {new Date(aula.fechaFin).toLocaleDateString()}</span>
                          </div>
                        )}
                      </div>

                      <div className="aula-professor">
                        <div className="professor-avatar">
                          {isProfesor() ? getUserInitials() : (aula.profesorNombreCompleto ? aula.profesorNombreCompleto.charAt(0) : 'PR')}
                        </div>
                        <span className="professor-name">
                          {isProfesor() ? 'Mi aula' : (aula.profesorNombreCompleto || 'Profesor asignado')}
                        </span>
                      </div>
                    </div>

                    <div className="aula-actions">
                      <button className="aula-btn primary" onClick={() => navigate(ROUTES.PROTECTED.AULA_DETAIL(aula.id))}>
                        Ingresar al Aula
                      </button>
                      <button className="aula-btn secondary">
                        Ver Detalles
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>
          )}
        </div>
      </div>

      {/* Modal de Invitar Estudiantes */}
      {showInviteModal && selectedAula && (
        <InvitarEstudiantesModal
          isOpen={showInviteModal}
          onClose={() => {
            setShowInviteModal(false);
            setSelectedAula(null);
          }}
          aulaId={selectedAula.id}
          aulaNombre={selectedAula.nombre || selectedAula.titulo}
        />
      )}
    </div>
  );
}

export default Aulas;