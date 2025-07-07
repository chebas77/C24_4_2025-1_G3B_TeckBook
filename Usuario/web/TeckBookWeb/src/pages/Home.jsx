import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import '../css/Home.css';
import CompletarPerfil from '../components/CompletarPerfil';
import Header from '../components/Header';
import Modal from '../components/Modal';
import { 
  FileText,
  Upload,
  HelpCircle,
  MessageCircle,
  ThumbsUp,
  Share2,
  MoreHorizontal,
  User,
  BookOpen,
  Copy,
  ExternalLink,
  Check
} from 'lucide-react';
import { ENDPOINTS, ROUTES } from '../config/apiConfig';
import apiService from '../services/apiService';
import aulasService from '../services/aulasService';

function Home() {
  const [userData, setUserData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCompletarPerfil, setShowCompletarPerfil] = useState(false);
  const [anuncios, setAnuncios] = useState([]);
  const [anunciosFijados, setAnunciosFijados] = useState([]);
  const [likedPosts, setLikedPosts] = useState(new Set());
  const [shareStatus, setShareStatus] = useState({});
  const [sortOrder, setSortOrder] = useState('recientes');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [createType, setCreateType] = useState(null);
  const [formData, setFormData] = useState({ aulaId: '', titulo: '', contenido: '', archivo: null });
  const [aulasDisponibles, setAulasDisponibles] = useState([]);
  const [formError, setFormError] = useState("");
  const firstInputRef = useRef(null);

  const navigate = useNavigate();

  useEffect(() => {
    let isMounted = true;

    const queryParams = new URLSearchParams(window.location.search);
    const tokenFromUrl = queryParams.get('token');
    const isNewUserParam = queryParams.get('new') === 'true';
    const isIncompleteParam = queryParams.get('incomplete') === 'true';

    // 1️⃣ Guardar token si viene por URL
    if (tokenFromUrl) {
      localStorage.setItem('token', tokenFromUrl);
      window.history.replaceState({}, document.title, '/home');

      // Esperar un poco para que esté disponible el token
      setTimeout(() => {
        fetchUserData();
        fetchAnuncios();
      }, 100);

      return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
      navigate(ROUTES.PUBLIC.LOGIN);
      return;
    }

    fetchUserData();
    fetchAnuncios();

   async function fetchUserData() {
  try {
    const data = await apiService.get(ENDPOINTS.AUTH.GET_USER);

    if (!isMounted) return;

    setUserData({
      ...data,
      profileImageUrl: data.profileImageUrl || ''
    });

    const datosIncompletos = 
      !data.carreraId ||
      !data.cicloActual ||
      !data.departamentoId ||
      !data.telefono ||
      !data.correoInstitucional;

    const needsCompletion =
      data.requiresCompletion ||
      isNewUserParam ||
      isIncompleteParam ||
      datosIncompletos;

    // Pequeña espera para cargar correctamente el modal
    if (needsCompletion) {
      setTimeout(() => setShowCompletarPerfil(true), 300);
    }

  } catch (error) {
    if (isMounted) {
      console.error("❌ Error al obtener datos del usuario:", error);
      setError(error.message || 'Error al obtener datos del usuario');
    }
  } finally {
    if (isMounted) setIsLoading(false);
  }
}

// 🔥 NUEVA FUNCIÓN: Cargar anuncios de todas las aulas del usuario
async function fetchAnuncios() {
  try {
    console.log('🔍 Cargando anuncios de las aulas...');
    
    // Primero obtener las aulas del usuario
    const aulas = await aulasService.getAll();
    console.log('📚 Aulas obtenidas:', aulas.length);
    
    if (aulas.length === 0) {
      console.log('ℹ️ El usuario no pertenece a ninguna aula');
      return;
    }

    // Obtener anuncios de todas las aulas
    const todosLosAnuncios = [];
    const anunciosFijadosTemp = [];

    for (const aula of aulas) {
      try {
        const anunciosAula = await aulasService.getAnuncios(aula.id);
        console.log(`📋 Anuncios del aula ${aula.nombre}:`, anunciosAula.length);
        
        // Agregar nombre del aula a cada anuncio
        const anunciosConAula = anunciosAula.map(anuncio => ({
          ...anuncio,
          aulaNombre: aula.nombre || aula.titulo,
          aulaId: aula.id
        }));

        todosLosAnuncios.push(...anunciosConAula);

        // Separar anuncios fijados
        const fijados = anunciosConAula.filter(a => a.fijado);
        anunciosFijadosTemp.push(...fijados);

      } catch (error) {
        console.error(`❌ Error al cargar anuncios del aula ${aula.id}:`, error);
      }
    }

    console.log('📊 Total anuncios cargados:', todosLosAnuncios.length);
    console.log('📌 Anuncios fijados:', anunciosFijadosTemp.length);

    if (isMounted) {
      setAnuncios(todosLosAnuncios);
      setAnunciosFijados(anunciosFijadosTemp);
    }

  } catch (error) {
    console.error('❌ Error al cargar anuncios:', error);
    if (isMounted) {
      setError('Error al cargar los anuncios');
    }
  }
}

  

    return () => {
      isMounted = false;
    };
  }, []);


  const handleCompletarPerfil = (result) => {
    setUserData(prev => ({ ...prev, ...result }));
    setShowCompletarPerfil(false);
  };

  const getUserInitials = () => {
    return userData?.nombre && userData?.apellidos
      ? `${userData.nombre.charAt(0)}${userData.apellidos.charAt(0)}`.toUpperCase()
      : 'GS';
  };

  const renderPostAvatar = (className = "home-create-post-avatar") => {
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
    try {
      const shareUrl = `${window.location.origin}/aulas/${post.aulaId}?anuncio=${post.id}`;
      const shareText = `📚 ${post.titulo}\n\n${post.contenido}\n\nVía TecBook - ${shareUrl}`;

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

      setTimeout(() => {
        setShareStatus(prev => ({ ...prev, [post.id]: null }));
      }, 2000);

    } catch (error) {
      console.error('Error al compartir:', error);
      setShareStatus({ [post.id]: 'error' });
      setTimeout(() => {
        setShareStatus(prev => ({ ...prev, [post.id]: null }));
      }, 2000);
    }
  };

  // ✅ Usar aulasService
  const fetchAulasForForm = async () => {
    try {
      const aulas = await aulasService.getAll();
      setAulasDisponibles(aulas);
    } catch (error) {
      console.error('Error obteniendo aulas:', error);
    }
  };

  const handleCreateContent = (type) => {
    setCreateType(type);
    setShowCreateModal(true);
    fetchAulasForForm();
    setFormData({ aulaId: '', titulo: '', contenido: '', archivo: null });
  };

  useEffect(() => {
    if (showCreateModal && firstInputRef.current) {
      setTimeout(() => firstInputRef.current.focus(), 100);
    }
  }, [showCreateModal]);

  const handleFormChange = e => {
    const { name, value, files } = e.target;
    setFormError("");
    setFormData(prev => ({
      ...prev,
      [name]: files ? files[0] : value
    }));
  };

  const validateForm = () => {
    if (!formData.aulaId) return "Selecciona un aula.";
    if (!formData.titulo.trim()) return "El título es obligatorio.";
    if (formData.titulo.length > 255) return "El título es muy largo.";
    if (!formData.contenido.trim()) return "El contenido es obligatorio.";
    if (createType === 'upload' && !formData.archivo) return "Debes adjuntar un archivo.";
    if (createType === 'upload' && formData.archivo && formData.archivo.size > 10 * 1024 * 1024) return "El archivo no debe superar 10MB.";
    return "";
  };

  // ✅ Usar aulasService
  const handleCreateSubmit = async (e) => {
    e.preventDefault();
    const errorMsg = validateForm();
    if (errorMsg) {
      setFormError(errorMsg);
      if (firstInputRef.current) firstInputRef.current.focus();
      return;
    }

    try {
      const form = new FormData();
      form.append('titulo', formData.titulo);
      form.append('contenido', formData.contenido);
      form.append('tipo', createType === 'question' ? 'PREGUNTA' : createType === 'material' ? 'MATERIAL' : 'ARCHIVO');
      if (formData.archivo) form.append('archivo', formData.archivo);

      await aulasService.createAnuncio(formData.aulaId, form);
      
      setShowCreateModal(false);
      setCreateType(null);
      setFormData({ aulaId: '', titulo: '', contenido: '', archivo: null });
      
      // Refrescar página
      window.location.reload();
    } catch (error) {
      console.error('Error creando anuncio:', error);
      alert('Error al crear el anuncio');
    }
  };

  // Ordenar anuncios según filtro
  const sortedAnuncios = [...anuncios].sort((a, b) => {
    if (sortOrder === 'recientes') {
      return new Date(b.fechaPublicacion) - new Date(a.fechaPublicacion);
    } else {
      return new Date(a.fechaPublicacion) - new Date(b.fechaPublicacion);
    }
  });

  if (isLoading) return <div className="home-loading-container"><div className="home-loading-spinner"></div><p>Cargando información del usuario...</p></div>;
  if (error) return <div className="home-error-container"><h2>Error al cargar datos</h2><p>{error}</p><button onClick={() => navigate(ROUTES.PUBLIC.LOGIN)} className="home-button">Volver al inicio</button></div>;

  return (
    <div className="home-wrapper">
      <Header />
      <div className="home-main-layout">
        <aside className="home-left-sidebar">
          <div className="home-welcome-card">
            <div className="home-user-info">
              {renderPostAvatar("home-user-avatar")}
              <div className="home-user-details">
                <h3>{userData ? `${userData.nombre} ${userData.apellidos}` : 'Bienvenido a TecBook'}</h3>
                <p>{userData?.rol || 'Estudiante'}</p>
              </div>
            </div>
            <p className="home-welcome-text">Conecta con tu comunidad académica...</p>
          </div>

          <div className="home-quick-actions">
            <h3 className="home-section-title">Acciones Rápidas</h3>
            <div className="home-action-buttons">
              <div className="home-action-button" onClick={() => navigate(ROUTES.PROTECTED.PROFILE)}>
                <User className="home-action-icon" />
                <div className="home-action-text">
                  <span className="home-action-title">Mi Perfil</span>
                  <span className="home-action-subtitle">Actualiza tus datos</span>
                </div>
              </div>
              <div className="home-action-button" onClick={() => navigate(ROUTES.PROTECTED.AULAS)}>
                <BookOpen className="home-action-icon" />
                <div className="home-action-text">
                  <span className="home-action-title">Mis Aulas</span>
                  <span className="home-action-subtitle">Accede a tus aulas</span>
                </div>
              </div>
              <div className="home-action-button" onClick={() => setShowCompletarPerfil(true)}>
                <User className="home-action-icon" />
                <div className="home-action-text">
                  <span className="home-action-title">Completar Perfil</span>
                  <span className="home-action-subtitle">Información académica</span>
                </div>
              </div>
            </div>
          </div>
        </aside>
        
        <main className="home-main-content">
          <div className="home-create-post">
            <div className="home-create-post-header">
              {renderPostAvatar()}
              <input 
                type="text" 
                placeholder="¿Tienes una duda o quieres ayudar a tus compañeros?" 
                className="home-create-post-input" 
                readOnly 
              />
            </div>
            <div className="home-create-post-actions">
              <button 
                className="home-post-action academic-action"
                onClick={() => handleCreateContent('material')}
              >
                <FileText size={20} />
                Compartir Material
              </button>
              <button 
                className="home-post-action academic-action"
                onClick={() => handleCreateContent('question')}
              >
                <HelpCircle size={20} />
                Hacer Pregunta
              </button>
              <button 
                className="home-post-action academic-action"
                onClick={() => handleCreateContent('upload')}
              >
                <Upload size={20} />
                Subir Archivo
              </button>
            </div>
          </div>

          <div className="home-anuncios-sort-filter">
            <label htmlFor="anuncios-sort-select">Ordenar anuncios:</label>
            <select
              id="anuncios-sort-select"
              value={sortOrder}
              onChange={e => setSortOrder(e.target.value)}
              className="home-anuncios-sort-select"
            >
              <option value="recientes">Más recientes primero</option>
              <option value="antiguos">Más antiguos primero</option>
            </select>
          </div>

          <div className="home-posts-container">
            {sortedAnuncios.length === 0 && !isLoading ? (
              <div className="home-empty-state">
                <FileText size={48} color="#94a3b8" />
                <h3>No hay anuncios recientes</h3>
                <p>Los anuncios de tus aulas aparecerán aquí cuando se publiquen.</p>
              </div>
            ) : (
              sortedAnuncios.map(post => (
                <article key={post.id} className="home-post">
                  <div className="home-post-header">
                    {post.profesorImagenUrl && post.profesorImagenUrl.trim() ? (
                      <img src={post.profesorImagenUrl} alt="avatar" className="home-post-avatar" />
                    ) : (
                      <div className="home-post-avatar">
                        {post.autorNombre
                          ? post.autorNombre.split(' ').map(n => n[0]).join('').toUpperCase()
                          : 'PR'}
                      </div>
                    )}
                    <div className="home-post-info">
                      <h4>
                        <a
                          href={`/aulas/${post.aulaId}?anuncio=${post.id}`}
                          className="home-post-title-link"
                          style={{ color: 'inherit', textDecoration: 'underline', cursor: 'pointer' }}
                        >
                          {post.titulo}
                        </a>
                      </h4>
                      <p className="home-post-aula">{post.aulaNombre ? `📚 ${post.aulaNombre}` : ''}</p>
                      <p className="home-post-date">
                        {post.fechaPublicacion ? 
                          new Date(post.fechaPublicacion).toLocaleDateString('es-ES', {
                            day: 'numeric',
                            month: 'long',
                            hour: '2-digit',
                            minute: '2-digit'
                          }) : ''}
                      </p>
                    </div>
                    <button className="home-chat-options">
                      <MoreHorizontal size={20} />
                    </button>
                  </div>
                  
                  <div className="home-post-content">{post.contenido}</div>
                  
                  <div className="home-post-actions">
                    <button 
                      className={`home-post-action-btn ${likedPosts.has(post.id) ? 'liked' : ''}`}
                      onClick={() => handleLike(post.id)}
                    >
                      <ThumbsUp size={18} />
                      {likedPosts.has(post.id) ? 'Te gusta' : 'Me gusta'}
                    </button>
                    
                    <button className="home-post-action-btn">
                      <MessageCircle size={18} />
                      Comentar
                    </button>
                    
                    <button 
                      className={`home-post-action-btn ${shareStatus[post.id] ? 'sharing' : ''}`}
                      onClick={() => handleShare(post)}
                      disabled={shareStatus[post.id] === 'sharing'}
                    >
                      {shareStatus[post.id] === 'copied' ? (
                        <>
                          <Check size={18} />
                          Copiado
                        </>
                      ) : shareStatus[post.id] === 'shared' ? (
                        <>
                          <Check size={18} />
                          Compartido
                        </>
                      ) : shareStatus[post.id] === 'error' ? (
                        <>
                          <Copy size={18} />
                          Error
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

        <aside className="home-right-sidebar">
          <div className="home-chat-section">
            <div className="home-chat-header">
              <h3 className="home-chat-title">📌 Anuncios Fijados</h3>
            </div>
            <div className="home-chat-list">
              {anunciosFijados.length === 0 && !isLoading ? (
                <div className="home-empty-announcements">
                  <p>No hay anuncios fijados.</p>
                </div>
              ) : (
                anunciosFijados.map(announcement => (
                  <div key={announcement.id} className="home-chat-item">
                    <div className="home-chat-info">
                      <h4 className="home-chat-name">{announcement.titulo}</h4>
                      <p className="home-chat-aula">📚 {announcement.aulaNombre}</p>
                      <p className="home-chat-message">{announcement.contenido}</p>
                    </div>
                    <span className="home-chat-time">
                      {announcement.fechaPublicacion ? 
                        new Date(announcement.fechaPublicacion).toLocaleDateString('es-ES', {
                          day: 'numeric',
                          month: 'short'
                        }) : ''}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>
        </aside>
      </div>

      <CompletarPerfil
        isOpen={showCompletarPerfil}
        onClose={() => setShowCompletarPerfil(false)}
        token={localStorage.getItem('token')}
        userData={userData}
        onComplete={handleCompletarPerfil}
        isNewUser={false}
      />

      <Modal isOpen={showCreateModal} onClose={() => setShowCreateModal(false)}>
        <form className="home-create-modal-form" onSubmit={handleCreateSubmit}>
          <h2 style={{textAlign:'center',marginBottom:18}}>
            {createType === 'material' ? '📚 Compartir Material' : createType === 'question' ? '❓ Hacer Pregunta' : '📎 Subir Archivo'}
          </h2>
          {formError && <div className="modal-error">{formError}</div>}
          <label>Aula:
            <select name="aulaId" value={formData.aulaId} onChange={handleFormChange} required ref={firstInputRef}>
              <option value="">Selecciona un aula</option>
              {aulasDisponibles.map(a => <option key={a.id} value={a.id}>{a.nombre || a.titulo}</option>)}
            </select>
          </label>
          <label>Título:
            <input name="titulo" value={formData.titulo} onChange={handleFormChange} required maxLength={255} />
          </label>
          <label>Contenido:
            <textarea name="contenido" value={formData.contenido} onChange={handleFormChange} required rows={4} />
          </label>
          {createType === 'upload' && (
            <label>Archivo:
              <input type="file" name="archivo" onChange={handleFormChange} accept="*" required />
              <span className="modal-file-info">{formData.archivo ? formData.archivo.name : ''}</span>
            </label>
          )}
          <button className="home-button" type="submit" style={{marginTop:16}}>Publicar</button>
        </form>
      </Modal>
    </div>
  );
}

export default Home;