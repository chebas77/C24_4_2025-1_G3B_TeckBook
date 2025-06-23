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
      navigate('/');
      return;
    }
    fetchUserData();
    fetchAulas();
  }, [navigate]);

  const fetchUserData = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/auth/user', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('No se pudo obtener la información del usuario');
      }

      const data = await response.json();
      setUserData(data);
    } catch (error) {
      console.error("Error al obtener datos del usuario:", error);
      setError(error.message);
    }
  };

  const fetchAulas = async () => {
    try {
      setIsLoading(true);
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/aulas', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (!response.ok) {
        throw new Error('Error al obtener las aulas');
      }
      const data = await response.json();
      setAulas(data.aulas || []);
    } catch (error) {
      console.error("Error al cargar las aulas:", error);
      setError("Error al cargar las aulas: " + error.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      const token = localStorage.getItem('token');
      if (token) {
        const response = await fetch('http://localhost:8080/api/auth/logout', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
        if (response.ok) {
          console.log("✅ Sesión cerrada en el backend");
        }
      }
      localStorage.removeItem('token');
      navigate('/');
    } catch (error) {
      console.error("❌ Error durante logout:", error);
      localStorage.removeItem('token');
      navigate('/');
    }
  };

  const handleInviteStudents = (aula) => {
    setSelectedAula(aula);
    setShowInviteModal(true);
  };

  const handleCreateAula = () => {
    navigate('/crear-aula');
  };

  const filteredAulas = aulas.filter(aula => {
    const matchesSearch = (aula.nombre || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
      (aula.titulo || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
      (aula.descripcion || '').toLowerCase().includes(searchTerm.toLowerCase());
    return matchesSearch;
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
        <Header active="Aulas" />
        <div className="aulas-loading">
          <div className="loading-spinner"></div>
          <p>Cargando aulas...</p>
        </div>
      </div>
    );
  }
  return (
    <div className="aulas-wrapper">
      <Header active="Aulas" />
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
                          {isProfesor() ? getUserInitials() : 'PR'}
                        </div>
                        <span className="professor-name">
                          {isProfesor() ? 'Mi aula' : 'Profesor asignado'}
                        </span>
                      </div>
                    </div>

                    <div className="aula-actions">
                      <button className="aula-btn primary">
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