import React, { useEffect, useState } from 'react';
import { FileText, Upload, Filter, Search, Calendar, MessageCircle, Heart, Eye, X, Plus, File, HelpCircle, User, BookOpen, ThumbsUp, Share2, MoreHorizontal, Check, Copy } from 'lucide-react';
import Header from '../components/Header';
import '../css/AnunciosGeneral.css';
import { API_CONFIG, ENDPOINTS } from '../config/apiConfig';

export default function AnunciosGeneral() {
  const [anuncios, setAnuncios] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newAnuncio, setNewAnuncio] = useState({ 
    titulo: '', 
    contenido: '', 
    tipo: 'anuncio', 
    categoria: '',
    etiquetas: '',
    archivo: null 
  });
  const [filtro, setFiltro] = useState('generales'); // Cambiar default
  const [busqueda, setBusqueda] = useState('');
  const [ordenTiempo, setOrdenTiempo] = useState('recientes'); // Nuevo estado para orden temporal
  const [userData, setUserData] = useState(null);
  const [likedPosts, setLikedPosts] = useState(new Set());
  const [shareStatus, setShareStatus] = useState({});

  useEffect(() => {
    fetchAnuncios();
    fetchUserData();
  }, []);

  const fetchUserData = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API_CONFIG.API_BASE_URL}${ENDPOINTS.AUTH.GET_USER}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        setUserData(data);
      }
    } catch (error) {
      console.error('Error al obtener datos del usuario:', error);
    }
  };
  const fetchAnuncios = async () => {
  setIsLoading(true);
  setError(null);
  try {
    const token = localStorage.getItem('token');
    const res = await fetch(`${API_CONFIG.API_BASE_URL}/api/anuncios/general`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('No se pudieron cargar los anuncios generales');
    setAnuncios(await res.json());
  } catch (e) {
    setError(e.message);
    setAnuncios([]);
  } finally {
    setIsLoading(false);
  }
};


  const handleCreateAnuncio = async (e) => {
  e.preventDefault();
  try {
    const token = localStorage.getItem('token');
    const formData = new FormData();
    formData.append('titulo', newAnuncio.titulo);
    formData.append('contenido', newAnuncio.contenido);
    formData.append('tipo', newAnuncio.tipo);
    formData.append('categoria', newAnuncio.categoria);
    formData.append('etiquetas', newAnuncio.etiquetas);
    if (newAnuncio.archivo) formData.append('archivo', newAnuncio.archivo);

    const res = await fetch(`${API_CONFIG.API_BASE_URL}/api/anuncios/general`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
        // ⚠️ No agregues Content-Type aquí, fetch lo hace solo con FormData
      },
      body: formData
    });

    if (res.ok) {
      setShowCreateModal(false);
      setNewAnuncio({
        titulo: '',
        contenido: '',
        tipo: 'anuncio',
        categoria: '',
        etiquetas: '',
        archivo: null
      });
      fetchAnuncios(); // recarga lista
    } else {
      alert('❌ Error al crear el anuncio general');
    }
  } catch (e) {
    console.error('❌ Error en handleCreateAnuncio:', e);
    alert('❌ Error inesperado al crear el anuncio general');
  }
};

  const anunciosFiltrados = anuncios.filter(a => {
    const coincideBusqueda = a.titulo?.toLowerCase().includes(busqueda.toLowerCase()) || 
                             a.contenido?.toLowerCase().includes(busqueda.toLowerCase());
    
    // Filtros específicos (sin "todos los anuncios")
    if (filtro === 'generales') return a.esGeneral === true && coincideBusqueda;
    if (filtro === 'preguntas') return a.tipo === 'pregunta' && coincideBusqueda;
    if (filtro === 'materiales') return a.tipo === 'material' && coincideBusqueda;
    if (filtro === 'archivos') return a.tipo === 'archivo' && coincideBusqueda;
    return coincideBusqueda;
  });

  // Ordenar por tiempo
  const anunciosOrdenados = [...anunciosFiltrados].sort((a, b) => {
    if (ordenTiempo === 'recientes') {
      return new Date(b.fechaPublicacion) - new Date(a.fechaPublicacion);
    } else {
      return new Date(a.fechaPublicacion) - new Date(b.fechaPublicacion);
    }
  });

  const getTipoIcon = (tipo) => {
    switch(tipo) {
      case 'pregunta': return <HelpCircle size={16} />;
      case 'material': return <FileText size={16} />;
      case 'archivo': return <File size={16} />;
      default: return <FileText size={16} />;
    }
  };

  const getTipoColor = (tipo) => {
    switch(tipo) {
      case 'pregunta': return 'var(--anuncios-generales-tipo-pregunta)';
      case 'material': return 'var(--anuncios-generales-tipo-material)';
      case 'archivo': return 'var(--anuncios-generales-tipo-archivo)';
      default: return 'var(--anuncios-generales-primary-blue)';
    }
  };

  const getUserInitials = () => {
    return userData?.nombre && userData?.apellidos
      ? `${userData.nombre.charAt(0)}${userData.apellidos.charAt(0)}`.toUpperCase()
      : 'GS';
  };

  const renderAvatar = (className = "anuncios-generales-user-avatar") => {
    return userData?.profileImageUrl?.trim()
      ? <img src={userData.profileImageUrl} alt="avatar" className={className} />
      : <div className={className}>{getUserInitials()}</div>;
  };

  const handleLike = (postId) => {
    setLikedPosts(prev => {
      const newLiked = new Set(prev);
      if (newLiked.has(postId)) {
        newLiked.delete(postId);
      } else {
        newLiked.add(postId);
      }
      return newLiked;
    });
  };

  const handleShare = async (post) => {
  const shareUrl = `${window.location.origin}/anuncios-generales/${post.id}`;
  const shareText = `📢 ${post.titulo}\n\n${post.contenido}\n\nVía ${API_CONFIG.APP_NAME} - ${shareUrl}`;

  try {
    if (navigator.share) {
      await navigator.share({
        title: post.titulo,
        text: post.contenido,
        url: shareUrl
      });
      setShareStatus({ [post.id]: 'shared' });
    } else {
      await navigator.clipboard.writeText(shareText);
      setShareStatus({ [post.id]: 'copied' });
    }
  } catch (error) {
    console.error('Error al compartir:', error);
    setShareStatus({ [post.id]: 'error' });
  } finally {
    // Limpiar estado después de 2 segundos
    setTimeout(() => {
      setShareStatus(prev => ({ ...prev, [post.id]: null }));
    }, 2000);
  }
};


  const formatFileSize = (bytes) => {
    if (!bytes) return '';
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i];
  };

  return (
    <>
      <Header />
      <div className="anuncios-generales-page-wrapper">
        <div className="anuncios-generales-main-layout">
          {/* Sidebar izquierdo */}
          <aside className="anuncios-generales-left-sidebar">
            <div className="anuncios-generales-welcome-card">
              <div className="anuncios-generales-user-info">
                {renderAvatar("anuncios-generales-user-avatar")}
                <div className="anuncios-generales-user-details">
                  <h3>{userData ? `${userData.nombre} ${userData.apellidos}` : 'Anuncios Generales'}</h3>
                  <p>Comunicados oficiales e información institucional</p>
                </div>
              </div>
            </div>

            <div className="anuncios-generales-quick-actions">
              <h3 className="anuncios-generales-section-title">Tipos de Anuncios</h3>
              <div className="anuncios-generales-action-buttons">
                <div 
                  className={`anuncios-generales-action-button ${filtro === 'generales' ? 'active' : ''}`}
                  onClick={() => setFiltro('generales')}
                >
                  <FileText className="anuncios-generales-action-icon" />
                  <div className="anuncios-generales-action-text">
                    <span className="anuncios-generales-action-title">Anuncios generales</span>
                    <span className="anuncios-generales-action-subtitle">Información institucional</span>
                  </div>
                </div>
                <div 
                  className={`anuncios-generales-action-button ${filtro === 'preguntas' ? 'active' : ''}`}
                  onClick={() => setFiltro('preguntas')}
                >
                  <HelpCircle className="anuncios-generales-action-icon" />
                  <div className="anuncios-generales-action-text">
                    <span className="anuncios-generales-action-title">Preguntas</span>
                    <span className="anuncios-generales-action-subtitle">Consultas de la comunidad</span>
                  </div>
                </div>
                <div 
                  className={`anuncios-generales-action-button ${filtro === 'materiales' ? 'active' : ''}`}
                  onClick={() => setFiltro('materiales')}
                >
                  <FileText className="anuncios-generales-action-icon" />
                  <div className="anuncios-generales-action-text">
                    <span className="anuncios-generales-action-title">Materiales</span>
                    <span className="anuncios-generales-action-subtitle">Recursos compartidos</span>
                  </div>
                </div>
                <div 
                  className={`anuncios-generales-action-button ${filtro === 'archivos' ? 'active' : ''}`}
                  onClick={() => setFiltro('archivos')}
                >
                  <File className="anuncios-generales-action-icon" />
                  <div className="anuncios-generales-action-text">
                    <span className="anuncios-generales-action-title">Archivos</span>
                    <span className="anuncios-generales-action-subtitle">Documentos importantes</span>
                  </div>
                </div>
              </div>
            </div>
          </aside>

          {/* Contenido principal */}
          <main className="anuncios-generales-main-content">
            {/* Barra de crear anuncio (estilo Home) */}
            <div className="anuncios-generales-create-post">
              <div className="anuncios-generales-create-post-header">
                {renderAvatar("anuncios-generales-create-post-avatar")}
                <input 
                  type="text" 
                  placeholder="¿Qué anuncio general quieres compartir?" 
                  className="anuncios-generales-create-post-input" 
                  readOnly 
                  onClick={() => setShowCreateModal(true)}
                />
              </div>
              <div className="anuncios-generales-create-post-actions">
                <button 
                  className="anuncios-generales-post-action academic-action"
                  onClick={() => setShowCreateModal(true)}
                >
                  <FileText size={20} />
                  Anuncio General
                </button>
                <button 
                  className="anuncios-generales-post-action academic-action"
                  onClick={() => setShowCreateModal(true)}
                >
                  <HelpCircle size={20} />
                  Comunicado
                </button>
                <button 
                  className="anuncios-generales-post-action academic-action"
                  onClick={() => setShowCreateModal(true)}
                >
                  <Upload size={20} />
                  Documento
                </button>
              </div>
            </div>

            {/* Barra de búsqueda y filtro temporal */}
            <div className="anuncios-generales-search-container">
              <div className="anuncios-generales-search-wrapper">
                <Search className="anuncios-generales-search-icon" size={18} />
                <input 
                  type="text"
                  placeholder="Buscar anuncios..."
                  value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}
                  className="anuncios-generales-search-input"
                />
              </div>
              
              {/* Filtro de tiempo elegante */}
              <div className="anuncios-generales-time-filter">
                <span className="anuncios-generales-time-filter-label">Ordenar por:</span>
                <div className="anuncios-generales-time-filter-buttons">
                  <button 
                    className={`anuncios-generales-time-btn ${ordenTiempo === 'recientes' ? 'active' : ''}`}
                    onClick={() => setOrdenTiempo('recientes')}
                  >
                    <Calendar size={16} />
                    Más recientes
                  </button>
                  <button 
                    className={`anuncios-generales-time-btn ${ordenTiempo === 'antiguos' ? 'active' : ''}`}
                    onClick={() => setOrdenTiempo('antiguos')}
                  >
                    <Calendar size={16} />
                    Más antiguos
                  </button>
                </div>
              </div>
            </div>

            {/* Lista de anuncios (estilo Home) */}
            <div className="anuncios-generales-posts-container">
              {isLoading ? (
                <div className="anuncios-generales-loading-container">
                  <div className="anuncios-generales-loading-spinner"></div>
                  <p>Cargando anuncios...</p>
                </div>
              ) : error ? (
                <div className="anuncios-generales-error-container">
                  <FileText size={48} />
                  <h3>Error al cargar</h3>
                  <p>{error}</p>
                  <button onClick={fetchAnuncios} className="anuncios-generales-retry-btn">Reintentar</button>
                </div>
              ) : anunciosOrdenados.length === 0 ? (
                <div className="anuncios-generales-empty-state">
                  <FileText size={48} color="#94a3b8" />
                  <h3>No hay anuncios</h3>
                  <p>No se encontraron anuncios que coincidan con tu búsqueda.</p>
                </div>
              ) : (
                anunciosOrdenados.map(anuncio => (
                  <article key={anuncio.id} className="anuncios-generales-post">
                    <div className="anuncios-generales-post-header">
                      <div className="anuncios-generales-post-avatar">
                        {anuncio.autorNombre
                          ? anuncio.autorNombre.split(' ').map(n => n[0]).join('').toUpperCase()
                          : 'AD'}
                      </div>
                      <div className="anuncios-generales-post-info">
                        <h4 className="anuncios-generales-post-title">{anuncio.titulo}</h4>
                        <div className="anuncios-generales-post-meta">
                          <span 
                            className="anuncios-generales-post-tipo"
                            style={{ backgroundColor: getTipoColor(anuncio.tipo) }}
                          >
                            {getTipoIcon(anuncio.tipo)}
                            {anuncio.tipo}
                          </span>
                          {anuncio.categoria && (
                            <span className="anuncios-generales-post-categoria">
                              📋 {anuncio.categoria}
                            </span>
                          )}
                        </div>
                        <p className="anuncios-generales-post-date">
                          {anuncio.fechaPublicacion ? 
                            new Date(anuncio.fechaPublicacion).toLocaleDateString('es-ES', {
                              day: 'numeric',
                              month: 'long',
                              hour: '2-digit',
                              minute: '2-digit'
                            }) : ''}
                        </p>
                      </div>
                      <button className="anuncios-generales-chat-options">
                        <MoreHorizontal size={20} />
                      </button>
                    </div>
                    
                    <div className="anuncios-generales-post-content">{anuncio.contenido}</div>

                    {anuncio.archivoNombre && (
                      <div className="anuncios-generales-post-archivo">
                        <File size={20} />
                        <div className="anuncios-generales-archivo-info">
                          <a 
                            href={anuncio.archivoUrl} 
                            target="_blank" 
                            rel="noopener noreferrer"
                            className="anuncios-generales-archivo-link"
                          >
                            {anuncio.archivoNombre}
                          </a>
                          {anuncio.archivoTamaño && (
                            <span className="anuncios-generales-archivo-size">
                              {formatFileSize(anuncio.archivoTamaño)}
                            </span>
                          )}
                        </div>
                      </div>
                    )}

                    {anuncio.etiquetas && (
                      <div className="anuncios-generales-post-etiquetas">
                        {JSON.parse(anuncio.etiquetas || '[]').map((etiqueta, index) => (
                          <span key={index} className="anuncios-generales-etiqueta">
                            #{etiqueta}
                          </span>
                        ))}
                      </div>
                    )}
                    
                    <div className="anuncios-generales-post-actions">
                      <button 
                        className={`anuncios-generales-post-action-btn ${likedPosts.has(anuncio.id) ? 'liked' : ''}`}
                        onClick={() => handleLike(anuncio.id)}
                      >
                        <ThumbsUp size={18} />
                        {likedPosts.has(anuncio.id) ? 'Te gusta' : 'Me gusta'}
                      </button>
                      
                      <button className="anuncios-generales-post-action-btn">
                        <MessageCircle size={18} />
                        Comentar
                      </button>
                      
                      <button 
                        className={`anuncios-generales-post-action-btn ${shareStatus[anuncio.id] ? 'sharing' : ''}`}
                        onClick={() => handleShare(anuncio)}
                      >
                        {shareStatus[anuncio.id] === 'copied' ? (
                          <>
                            <Check size={18} />
                            Copiado
                          </>
                        ) : shareStatus[anuncio.id] === 'shared' ? (
                          <>
                            <Check size={18} />
                            Compartido
                          </>
                        ) : (
                          <>
                            <Share2 size={18} />
                            Compartir
                          </>
                        )}
                      </button>
                    </div>
                  </article>
                ))
              )}
            </div>
          </main>

          {/* Sidebar derecho */}
          <aside className="anuncios-generales-right-sidebar">
            <div className="anuncios-generales-chat-section">
              <div className="anuncios-generales-chat-header">
                <h3 className="anuncios-generales-chat-title">📊 Estadísticas</h3>
              </div>
              <div className="anuncios-generales-chat-list">
                <div className="anuncios-generales-stats-grid">
                  <div className="anuncios-generales-stat-item">
                    <span className="anuncios-generales-stat-number">{anuncios.length}</span>
                    <span className="anuncios-generales-stat-label">Total anuncios</span>
                  </div>
                  <div className="anuncios-generales-stat-item">
                    <span className="anuncios-generales-stat-number">{anuncios.filter(a => a.tipo === 'pregunta').length}</span>
                    <span className="anuncios-generales-stat-label">Preguntas</span>
                  </div>
                  <div className="anuncios-generales-stat-item">
                    <span className="anuncios-generales-stat-number">{anuncios.filter(a => a.tipo === 'material').length}</span>
                    <span className="anuncios-generales-stat-label">Materiales</span>
                  </div>
                  <div className="anuncios-generales-stat-item">
                    <span className="anuncios-generales-stat-number">{anuncios.filter(a => a.archivoNombre).length}</span>
                    <span className="anuncios-generales-stat-label">Con archivos</span>
                  </div>
                </div>
              </div>
            </div>
          </aside>
        </div>

        {/* Modal de creación */}
        {showCreateModal && (
          <div className="anuncios-generales-modal-overlay" onClick={() => setShowCreateModal(false)}>
            <div className="anuncios-generales-modal" onClick={(e) => e.stopPropagation()}>
              <div className="anuncios-generales-modal-header">
                <h3 className="anuncios-generales-modal-title">Crear Nuevo Anuncio General</h3>
                <button 
                  className="anuncios-generales-modal-close"
                  onClick={() => setShowCreateModal(false)}
                >
                  <X size={24} />
                </button>
              </div>

              <form className="anuncios-generales-modal-form" onSubmit={handleCreateAnuncio}>
                <div className="anuncios-generales-form-group">
                  <label className="anuncios-generales-form-label">Título *</label>
                  <input 
                    type="text" 
                    placeholder="Título del anuncio" 
                    value={newAnuncio.titulo} 
                    onChange={(e) => setNewAnuncio({ ...newAnuncio, titulo: e.target.value })} 
                    required 
                    className="anuncios-generales-form-input"
                  />
                </div>

                <div className="anuncios-generales-form-group">
                  <label className="anuncios-generales-form-label">Contenido *</label>
                  <textarea 
                    placeholder="Describe tu anuncio..." 
                    value={newAnuncio.contenido} 
                    onChange={(e) => setNewAnuncio({ ...newAnuncio, contenido: e.target.value })} 
                    required 
                    className="anuncios-generales-form-textarea"
                  />
                </div>

                <div className="anuncios-generales-form-row">
                  <div className="anuncios-generales-form-group">
                    <label className="anuncios-generales-form-label">Tipo</label>
                    <select 
                      value={newAnuncio.tipo} 
                      onChange={(e) => setNewAnuncio({ ...newAnuncio, tipo: e.target.value })}
                      className="anuncios-generales-form-select"
                    >
                      <option value="anuncio">Anuncio</option>
                      <option value="pregunta">Pregunta</option>
                      <option value="material">Material</option>
                      <option value="archivo">Archivo</option>
                    </select>
                  </div>

                  <div className="anuncios-generales-form-group">
                    <label className="anuncios-generales-form-label">Categoría</label>
                    <input 
                      type="text" 
                      placeholder="Ej: Importante, Evento, etc." 
                      value={newAnuncio.categoria} 
                      onChange={(e) => setNewAnuncio({ ...newAnuncio, categoria: e.target.value })} 
                      className="anuncios-generales-form-input"
                    />
                  </div>
                </div>

                <div className="anuncios-generales-form-group">
                  <label className="anuncios-generales-form-label">Etiquetas</label>
                  <input 
                    type="text" 
                    placeholder="Separa las etiquetas con comas" 
                    value={newAnuncio.etiquetas} 
                    onChange={(e) => setNewAnuncio({ ...newAnuncio, etiquetas: e.target.value })} 
                    className="anuncios-generales-form-input"
                  />
                </div>

                <div className="anuncios-generales-form-group">
                  <label className="anuncios-generales-form-label">Archivo adjunto</label>
                  <div className="anuncios-generales-file-upload">
                    <input 
                      type="file" 
                      onChange={(e) => setNewAnuncio({ ...newAnuncio, archivo: e.target.files[0] })} 
                      className="anuncios-generales-file-input"
                    />
                    <div className="anuncios-generales-file-upload-content">
                      <Upload size={32} />
                      <p>
                        {newAnuncio.archivo ? 
                          `Archivo seleccionado: ${newAnuncio.archivo.name}` :
                          'Haz clic o arrastra un archivo aquí'
                        }
                      </p>
                      <span>Tamaño máximo: 10MB</span>
                    </div>
                  </div>
                </div>

                <div className="anuncios-generales-modal-actions">
                  <button 
                    type="submit" 
                    className="anuncios-generales-btn anuncios-generales-btn-primary"
                  >
                    Publicar Anuncio
                  </button>
                  <button 
                    type="button" 
                    className="anuncios-generales-btn anuncios-generales-btn-secondary"
                    onClick={() => setShowCreateModal(false)}
                  >
                    Cancelar
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </>
  );
}