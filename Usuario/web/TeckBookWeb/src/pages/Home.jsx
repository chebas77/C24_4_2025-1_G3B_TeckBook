import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import '../css/Home.css';
import CompletarPerfil from '../components/CompletarPerfil';
import Header from '../components/Header';
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

function Home() {
  const [userData, setUserData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCompletarPerfil, setShowCompletarPerfil] = useState(false);
  const [anuncios, setAnuncios] = useState([]);
  const [anunciosFijados, setAnunciosFijados] = useState([]);
  const [likedPosts, setLikedPosts] = useState(new Set());
  const [shareStatus, setShareStatus] = useState({});

  const navigate = useNavigate();

  useEffect(() => {
    let isMounted = true;

    const queryParams = new URLSearchParams(window.location.search);
    const tokenFromUrl = queryParams.get('token');
    const isNewUserParam = queryParams.get('new') === 'true';
    const isIncompleteParam = queryParams.get('incomplete') === 'true';

    if (tokenFromUrl) {
      localStorage.setItem('token', tokenFromUrl);
      window.history.replaceState({}, document.title, '/home');
    }

    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/');
      return;
    }

    const fetchUserData = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/auth/user', {
          headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error('No se pudo obtener la información del usuario');

        const data = await response.json();
        if (!isMounted) return;

        setUserData({ ...data, profileImageUrl: data.profileImageUrl || "" });

        const needsCompletion = isNewUserParam || 
          isIncompleteParam || 
          !data.carreraId || 
          !data.cicloActual || 
          !data.departamentoId;

        if (needsCompletion) setShowCompletarPerfil(true);

      } catch (error) {
        if (isMounted) {
          setError(error.message);
          localStorage.removeItem('token');
          setTimeout(() => navigate('/'), 2000);
        }
      } finally {
        if (isMounted) setIsLoading(false);
      }
    };

    // Obtener anuncios de todas las aulas del usuario
    const fetchAnunciosDeUsuario = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const token = localStorage.getItem('token');
        // 1. Obtener aulas del usuario
        const aulasRes = await fetch('http://localhost:8080/api/aulas', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!aulasRes.ok) throw new Error('No se pudieron obtener las aulas del usuario');
        const aulasData = await aulasRes.json();
        const aulas = aulasData.aulas || aulasData;
        // 2. Obtener anuncios de cada aula
        let allAnuncios = [];
        for (const aula of aulas) {
          const res = await fetch(`http://localhost:8080/api/aulas/${aula.id}/anuncios`, {
            headers: { 'Authorization': `Bearer ${token}` }
          });
          if (res.ok) {
            const anunciosAula = await res.json();
            // Enriquecer con datos del aula
            (anunciosAula || []).forEach(anuncio => {
              allAnuncios.push({ ...anuncio, aulaNombre: aula.nombre, aulaTitulo: aula.titulo });
            });
          }
        }
        // Separar fijados y normales
        setAnunciosFijados(allAnuncios.filter(a => a.fijado));
        setAnuncios(allAnuncios.filter(a => !a.fijado));
      } catch (e) {
        setError(e.message);
        setAnuncios([]);
        setAnunciosFijados([]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserData();
    fetchAnunciosDeUsuario();
    return () => { isMounted = false; };
  }, [navigate]);

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

  // Funcionalidad del Like
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

  // Funcionalidad de Compartir
  const handleShare = async (post) => {
    try {
      const shareUrl = `${window.location.origin}/aulas/${post.aulaId}?anuncio=${post.id}`;
      const shareText = `📚 ${post.titulo}\n\n${post.contenido}\n\nVía TecBook - ${shareUrl}`;

      // Intentar usar Web Share API si está disponible
      if (navigator.share) {
        await navigator.share({
          title: post.titulo,
          text: post.contenido,
          url: shareUrl
        });
        setShareStatus({ [post.id]: 'shared' });
      } else {
        // Fallback: copiar al portapapeles
        await navigator.clipboard.writeText(shareText);
        setShareStatus({ [post.id]: 'copied' });
      }

      // Resetear estado después de 2 segundos
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

  // Navegación a crear contenido
  const handleCreateContent = (type) => {
    switch (type) {
      case 'material':
        // Redirigir a página de subir material o abrir modal
        console.log('Compartir material de estudio');
        break;
      case 'question':
        // Redirigir a página de hacer pregunta o abrir modal
        console.log('Hacer pregunta académica');
        break;
      case 'resource':
        // Redirigir a página de compartir recurso
        console.log('Compartir recurso útil');
        break;
      case 'upload':
        // Abrir selector de archivos
        console.log('Subir archivo');
        break;
      default:
        break;
    }
  };

  if (isLoading) return <div className="home-loading-container"><div className="home-loading-spinner"></div><p>Cargando información del usuario...</p></div>;
  if (error) return <div className="home-error-container"><h2>Error al cargar datos</h2><p>{error}</p><button onClick={() => navigate('/')} className="home-button">Volver al inicio</button></div>;

  return (
    <div className="home-wrapper">
      {/* USAR EL HEADER UNIFICADO */}
      <Header />
      
      {/* LAYOUT DE 3 COLUMNAS */}
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
              <div className="home-action-button" onClick={() => navigate('/perfil')}>
                <User className="home-action-icon" />
                <div className="home-action-text">
                  <span className="home-action-title">Mi Perfil</span>
                  <span className="home-action-subtitle">Actualiza tus datos</span>
                </div>
              </div>
              <div className="home-action-button" onClick={() => navigate('/aulas')}>
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
          {/* SECCIÓN DE CREAR CONTENIDO ACADÉMICO */}
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

          <div className="home-posts-container">
            {anuncios.length === 0 && !isLoading ? (
              <div className="home-empty-state">
                <FileText size={48} color="#94a3b8" />
                <h3>No hay anuncios recientes</h3>
                <p>Los anuncios de tus aulas aparecerán aquí cuando se publiquen.</p>
              </div>
            ) : (
              anuncios.map(post => (
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
    </div>
  );
}

export default Home;