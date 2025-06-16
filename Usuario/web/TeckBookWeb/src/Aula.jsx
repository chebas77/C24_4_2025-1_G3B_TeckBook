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
  MapPin
} from 'lucide-react';
import './Aula.css';

function Aulas() {
  const [aulas, setAulas] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterCarrera, setFilterCarrera] = useState('');
  const [userData, setUserData] = useState(null);
  const navigate = useNavigate();

  // Mock data para aulas (esto debería venir del backend)
  const mockAulas = [
    {
      id: 1,
      nombre: "Algoritmos y Estructuras de Datos",
      carrera: "Desarrollo de Software",
      ciclo: "IV",
      seccion: "A",
      profesor: "Dr. Carlos Mendoza",
      estudiantes: 28,
      horario: "Lunes y Miércoles 10:00-12:00",
      aula: "Lab 301",
      codigo: "AED-2024-I",
      descripcion: "Curso fundamental sobre algoritmos y estructuras de datos",
      color: "#3B82F6"
    },
    {
      id: 2,
      nombre: "Base de Datos",
      carrera: "Desarrollo de Software",
      ciclo: "V",
      seccion: "B",
      profesor: "Ing. María González",
      estudiantes: 25,
      horario: "Martes y Jueves 14:00-16:00",
      aula: "Aula 205",
      codigo: "BD-2024-I",
      descripcion: "Diseño y administración de bases de datos relacionales",
      color: "#10B981"
    },
    {
      id: 3,
      nombre: "Redes y Comunicaciones",
      carrera: "Administración de Redes",
      ciclo: "VI",
      seccion: "A",
      profesor: "Ing. Pedro Sánchez",
      estudiantes: 22,
      horario: "Viernes 08:00-12:00",
      aula: "Lab 102",
      codigo: "RC-2024-I",
      descripcion: "Fundamentos de redes y protocolos de comunicación",
      color: "#F59E0B"
    },
    {
      id: 4,
      nombre: "Electrónica Digital",
      carrera: "Electrónica y Automatización",
      ciclo: "III",
      seccion: "A",
      profesor: "Ing. Ana Rivera",
      estudiantes: 30,
      horario: "Lunes a Viernes 16:00-18:00",
      aula: "Lab Electrónica",
      codigo: "ED-2024-I",
      descripcion: "Principios de electrónica digital y circuitos lógicos",
      color: "#8B5CF6"
    }
  ];

  useEffect(() => {
    // Verificar autenticación
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/');
      return;
    }

    // Obtener datos del usuario
    const fetchUserData = async () => {
      try {
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

    // Simular carga de aulas (aquí iría la llamada al backend)
    const fetchAulas = async () => {
      try {
        setIsLoading(true);
        // Simular delay de API
        await new Promise(resolve => setTimeout(resolve, 1000));
        setAulas(mockAulas);
      } catch (error) {
        setError("Error al cargar las aulas");
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserData();
    fetchAulas();
  }, [navigate]);

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

  // Filtrar aulas según búsqueda y filtros
  const filteredAulas = aulas.filter(aula => {
    const matchesSearch = aula.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         aula.profesor.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         aula.codigo.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesCarrera = filterCarrera === '' || aula.carrera === filterCarrera;
    
    return matchesSearch && matchesCarrera;
  });

  // Obtener carreras únicas para el filtro
  const carreras = [...new Set(aulas.map(aula => aula.carrera))];

  const getUserInitials = () => {
    if (userData?.nombre && userData?.apellidos) {
      return `${userData.nombre.charAt(0)}${userData.apellidos.charAt(0)}`.toUpperCase();
    }
    return 'GS';
  };

  if (isLoading) {
    return (
      <div className="aulas-wrapper">
        <div className="aulas-loading">
          <div className="loading-spinner"></div>
          <p>Cargando aulas...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="aulas-wrapper">
      {/* HEADER */}
      <header className="aulas-header">
        <h1 className="aulas-logo">TecBook</h1>
        <nav className="aulas-nav">
          <button onClick={() => navigate('/home')} className="aulas-nav-link">
            Inicio
          </button>
          <button onClick={() => navigate('/perfil')} className="aulas-nav-link">
            Perfil
          </button>
          <button className="aulas-nav-link aulas-active">
            Aulas
          </button>
          <button onClick={() => navigate('/crear-aula')} className="aulas-nav-link aulas-create-btn">
            <Plus size={16} style={{marginRight: '4px'}} />
            Crear Aula
          </button>
          <button onClick={handleLogout} className="aulas-logout">
            Cerrar sesión
          </button>
        </nav>
      </header>

      {/* CONTENIDO PRINCIPAL */}
      <div className="aulas-main">
        <div className="aulas-container">
          {/* TÍTULO Y CONTROLES */}
          <div className="aulas-top">
            <div className="aulas-title-section">
              <h2 className="aulas-title">Mis Aulas</h2>
              <p className="aulas-subtitle">
                Aquí puedes ver todas las aulas a las que estás asignado como {userData?.rol?.toLowerCase() || 'estudiante'}
              </p>
            </div>
            
            <div className="aulas-controls">
              <div className="aulas-search">
                <Search size={20} className="search-icon" />
                <input
                  type="text"
                  placeholder="Buscar aulas, profesores o códigos..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="search-input"
                />
              </div>
              
              <div className="aulas-filter">
                <Filter size={20} className="filter-icon" />
                <select
                  value={filterCarrera}
                  onChange={(e) => setFilterCarrera(e.target.value)}
                  className="filter-select"
                >
                  <option value="">Todas las carreras</option>
                  {carreras.map(carrera => (
                    <option key={carrera} value={carrera}>{carrera}</option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {/* GRID DE AULAS */}
          {error ? (
            <div className="aulas-error">
              <p>Error: {error}</p>
            </div>
          ) : (
            <div className="aulas-grid">
              {filteredAulas.length === 0 ? (
                <div className="aulas-empty">
                  <BookOpen size={48} color="#94a3b8" />
                  <h3>No se encontraron aulas</h3>
                  <p>No hay aulas que coincidan con tu búsqueda</p>
                </div>
              ) : (
                filteredAulas.map(aula => (
                  <div key={aula.id} className="aula-card" style={{'--aula-color': aula.color}}>
                    <div className="aula-header">
                      <div className="aula-color-bar"></div>
                      <div className="aula-title-section">
                        <h3 className="aula-name">{aula.nombre}</h3>
                        <span className="aula-code">{aula.codigo}</span>
                      </div>
                      <button className="aula-menu">
                        <MoreVertical size={20} />
                      </button>
                    </div>
                    
                    <div className="aula-content">
                      <p className="aula-description">{aula.descripcion}</p>
                      
                      <div className="aula-info">
                        <div className="info-item">
                          <GraduationCap size={16} className="info-icon" />
                          <span>{aula.carrera}</span>
                        </div>
                        
                        <div className="info-item">
                          <BookOpen size={16} className="info-icon" />
                          <span>Ciclo {aula.ciclo} - Sección {aula.seccion}</span>
                        </div>
                        
                        <div className="info-item">
                          <Users size={16} className="info-icon" />
                          <span>{aula.estudiantes} estudiantes</span>
                        </div>
                        
                        <div className="info-item">
                          <Clock size={16} className="info-icon" />
                          <span>{aula.horario}</span>
                        </div>
                        
                        <div className="info-item">
                          <MapPin size={16} className="info-icon" />
                          <span>{aula.aula}</span>
                        </div>
                      </div>
                      
                      <div className="aula-professor">
                        <div className="professor-avatar">
                          {aula.profesor.split(' ').map(n => n.charAt(0)).join('').toUpperCase()}
                        </div>
                        <span className="professor-name">{aula.profesor}</span>
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
    </div>
  );
}

export default Aulas;