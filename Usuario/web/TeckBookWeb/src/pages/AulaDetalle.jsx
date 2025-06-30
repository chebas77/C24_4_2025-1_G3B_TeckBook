import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeft, 
  Users, 
  Calendar, 
  BookOpen, 
  Plus, 
  MoreVertical,
  Eye,
  MessageCircle,
  Pin,
  UserPlus,
  Settings,
  Share2,
  Bell,
  Search,
  Filter,
  AlertCircle
} from 'lucide-react';
import './AulaDetalle.css';
import ListaIntegrantes from '../components/ListaIntegrantes';

function AulaDetalle() {
  const { aulaId } = useParams();
  const navigate = useNavigate();
  const [anuncios, setAnuncios] = useState([]);
  const [aula, setAula] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newAnuncio, setNewAnuncio] = useState({
    titulo: '',
    contenido: '',
    fijado: false
  });

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }
    setError(null);
    fetchAula();
    fetchAnuncios();
  }, [aulaId, navigate]);

  const fetchAula = async () => {
    try {
      const token = localStorage.getItem('token');
      const res = await fetch(`http://localhost:8080/api/aulas/${aulaId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('No se pudo cargar el aula');
      const data = await res.json();
      // Si la respuesta tiene un objeto 'aula', úsalo, si no, usa el objeto raíz
      if (data.aula) {
        setAula(data.aula);
      } else {
        setAula(data);
      }
    } catch (e) {
      setError(e.message);
    }
  };

  const fetchAnuncios = async () => {
    try {
      setLoading(true);
      setError(null);
      const token = localStorage.getItem('token');
      const res = await fetch(`http://localhost:8080/api/aulas/${aulaId}/anuncios`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('No tienes acceso o no hay anuncios');
      setAnuncios(await res.json());
    } catch (e) {
      setAnuncios([]);
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateAnuncio = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const res = await fetch(`http://localhost:8080/api/aulas/${aulaId}/anuncios`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newAnuncio)
      });
      if (res.ok) {
        setShowCreateModal(false);
        setNewAnuncio({ titulo: '', contenido: '', fijado: false });
        fetchAnuncios();
      }
    } catch (e) {
      console.error('Error creating announcement:', e);
    }
  };

  const filteredAnuncios = anuncios.filter(anuncio =>
    anuncio.titulo?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    anuncio.contenido?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Utilidad para obtener la fecha de creación más probable
  const getFechaCreacion = () => {
    if (!aula) return null;
    // Intenta varios nombres de campo posibles
    return (
      aula.fechaCreacion ||
      aula.fecha_creacion ||
      aula.fechaInicio ||
      aula.fecha_inicio ||
      null
    );
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Cargando aula...</p>
      </div>
    );
  }

  if (error && !aula) {
    return (
      <div className="error-container">
        <AlertCircle size={48} />
        <h2>Error al cargar el aula</h2>
        <p>{error}</p>
        <div className="error-actions">
          <button className="btn-secondary" onClick={() => navigate(-1)}>
            Volver
          </button>
          <button className="btn-primary" onClick={() => window.location.reload()}>
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="aula-interna">
      {/* Header */}
      <header className="aula-header" style={{ background: 'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)', zIndex: 10, position: 'relative' }}>
        <div className="header-content" style={{ position: 'relative', zIndex: 11 }}>
          <div className="header-left">
            <button className="back-btn" onClick={() => navigate(-1)}>
              <ArrowLeft size={20} />
            </button>
            <div className="aula-info">
              <h1>{aula?.nombre || aula?.titulo || 'Aula'}</h1>
              {aula?.descripcion && (
                <div className="aula-descripcion-header">
                  {aula.descripcion}
                </div>
              )}
              <div className="aula-meta">
                <span><BookOpen size={14} /> Código: {aula?.codigo || 'N/A'}</span>
                {/* Se elimina la fecha de creación del header para mostrarla solo en la sidebar */}
              </div>
            </div>
          </div>
          <div className="header-actions">
            <button className="header-btn" onClick={() => setShowCreateModal(true)}>
              <Plus size={16} />
              <span>Nuevo Anuncio</span>
            </button>
            <button className="header-btn">
              <Settings size={16} />
              <span>Configuración</span>
            </button>
          </div>
        </div>
      </header>

      {/* Contenido Principal */}
      <div className="main-content" style={{ zIndex: 1, position: 'relative' }}>
        {/* Información del Aula visible arriba */}
        <div className="info-card" style={{ marginBottom: 24 }}>
          <h3>Información del Aula</h3>
          <div className="info-items">
            <div className="info-item">
              <BookOpen size={16} color="#64748b" />
              <div>
                <div className="label">Código</div>
                <div className="value">{aula?.codigo || 'N/A'}</div>
              </div>
            </div>
            <div className="info-item">
              <Calendar size={16} color="#64748b" />
              <div>
                <div className="label">Fecha de Creación</div>
                <div className="value">
                  {getFechaCreacion() ? 
                    new Date(getFechaCreacion()).toLocaleDateString('es-ES') : 
                    'No disponible'
                  }
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="anuncios-section">
          {/* Barra de Acciones */}
          <div className="actions-bar">
            <div className="search-container">
              <Search className="search-icon" size={16} />
              <input
                type="text"
                className="search-input"
                placeholder="Buscar anuncios..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              <button className="filter-btn">
                <Filter size={16} />
              </button>
            </div>
            <button className="create-btn" onClick={() => setShowCreateModal(true)}>
              <Plus size={16} />
              Crear Anuncio
            </button>
          </div>

          {/* Lista de Anuncios */}
          <div className="anuncios-list">
            {error && anuncios.length === 0 ? (
              <div className="empty-state">
                <AlertCircle size={48} color="#64748b" />
                <h3>No se pudieron cargar los anuncios</h3>
                <p>{error}</p>
              </div>
            ) : filteredAnuncios.length === 0 ? (
              <div className="empty-state">
                <MessageCircle size={48} color="#64748b" />
                <h3>No hay anuncios</h3>
                <p>Cuando se publiquen anuncios en esta aula, aparecerán aquí.</p>
              </div>
            ) : (
              filteredAnuncios.map(anuncio => (
                <div key={anuncio.id} className={`anuncio-card ${anuncio.fijado ? 'fijado' : ''}`}>
                  {anuncio.fijado && (
                    <div className="pin-badge">
                      <Pin size={12} />
                      Fijado
                    </div>
                  )}
                  
                  <div className="anuncio-header">
                    <div className="anuncio-title-section">
                      <h3>{anuncio.titulo}</h3>
                      <div className="anuncio-meta">
                        <span><Calendar size={14} /> {anuncio.fechaPublicacion ? 
                          new Date(anuncio.fechaPublicacion).toLocaleDateString('es-ES', {
                            day: 'numeric',
                            month: 'long',
                            year: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit'
                          }) : 'Fecha no disponible'}</span>
                      </div>
                    </div>
                    <div className="anuncio-actions">
                      <button className="anuncio-btn" title="Ver">
                        <Eye size={16} />
                      </button>
                      <button className="anuncio-btn" title="Comentarios">
                        <MessageCircle size={16} />
                      </button>
                      <button className="anuncio-btn" title="Fijar">
                        <Pin size={16} />
                      </button>
                      <button className="anuncio-btn" title="Invitar">
                        <UserPlus size={16} />
                      </button>
                      <button className="anuncio-btn" title="Más opciones">
                        <MoreVertical size={16} />
                      </button>
                    </div>
                  </div>

                  <div className="anuncio-content">
                    <p>{anuncio.contenido}</p>
                  </div>

                  <div className="anuncio-footer">
                    <span className="anuncio-fecha">
                      {anuncio.fechaPublicacion ? new Date(anuncio.fechaPublicacion).toLocaleString() : ''}
                    </span>
                    <button className="anuncio-share">
                      <Share2 size={14} />
                      Compartir
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Sidebar: primero info del aula, luego integrantes */}
        <div className="sidebar">
          <div className="info-card" style={{ marginBottom: 24 }}>
            <h3>Información del Aula</h3>
            <div className="info-items">
              <div className="info-item">
                <BookOpen size={16} color="#64748b" />
                <div>
                  <div className="label">Código</div>
                  <div className="value">{aula?.codigo || 'N/A'}</div>
                </div>
              </div>
              <div className="info-item">
                <Calendar size={16} color="#64748b" />
                <div>
                  <div className="label">Fecha de Creación</div>
                  <div className="value">
                    {getFechaCreacion() ? 
                      new Date(getFechaCreacion()).toLocaleDateString('es-ES') : 
                      'No disponible'
                    }
                  </div>
                </div>
              </div>
            </div>
          </div>
          <ListaIntegrantes aulaId={aulaId} />
        </div>
      </div>

      {/* Footer con controles */}
      <div className="aula-footer">
        <div className="footer-control">
          <Bell size={20} />
        </div>
        <div className="footer-search">
          <input 
            type="text" 
            placeholder="Buscar..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <button>
            <Search size={16} />
          </button>
        </div>
        <div className="footer-control">
          <Filter size={20} />
        </div>
      </div>

      {/* Modal Crear Anuncio */}
      {showCreateModal && (
        <div className="modal-overlay" onClick={() => setShowCreateModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Crear Nuevo Anuncio</h3>
              <button className="close-btn" onClick={() => setShowCreateModal(false)}>
                ×
              </button>
            </div>
            <form className="anuncio-form" onSubmit={handleCreateAnuncio}>
              <div className="form-group">
                <label htmlFor="titulo">Título</label>
                <input
                  id="titulo"
                  type="text"
                  value={newAnuncio.titulo}
                  onChange={(e) => setNewAnuncio({...newAnuncio, titulo: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="contenido">Contenido</label>
                <textarea
                  id="contenido"
                  rows="6"
                  value={newAnuncio.contenido}
                  onChange={(e) => setNewAnuncio({...newAnuncio, contenido: e.target.value})}
                  required
                />
              </div>
              <div className="checkbox-group">
                <input
                  id="fijado"
                  type="checkbox"
                  checked={newAnuncio.fijado}
                  onChange={(e) => setNewAnuncio({...newAnuncio, fijado: e.target.checked})}
                />
                <label htmlFor="fijado">Fijar anuncio</label>
              </div>
              <div className="form-actions">
                <button type="button" className="btn-cancel" onClick={() => setShowCreateModal(false)}>
                  Cancelar
                </button>
                <button type="submit" className="btn-submit">
                  Publicar Anuncio
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default AulaDetalle;