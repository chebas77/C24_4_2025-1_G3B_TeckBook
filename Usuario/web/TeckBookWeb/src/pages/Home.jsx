import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import '../css/Home.css';
import CompletarPerfil from '../components/CompletarPerfil';
import Header from '../components/Header';
import InvitacionesPendientes from '../components/InvitacionesPendientes';
import { 
  Video,
  Image,
  Smile,
  MessageCircle,
  ThumbsUp,
  Share2,
  MoreHorizontal,
  Search,
  User,
  BookOpen,
  Plus
} from 'lucide-react';
import "../css/Home.css";

function Home() {
  const [userData, setUserData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCompletarPerfil, setShowCompletarPerfil] = useState(false);
    const [showInvitaciones, setShowInvitaciones] = useState(false);

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

    fetchUserData();
    return () => { isMounted = false; };
  }, [navigate]);

  const handleLogout = async () => {
    try {
      const token = localStorage.getItem('token');
      if (token) {
        await fetch('http://localhost:8080/api/auth/logout', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
      }
      localStorage.removeItem('token');
      navigate('/');
    } catch {
      localStorage.removeItem('token');
      navigate('/');
    }
  };

  const handleCompletarPerfil = (result) => {
    setUserData(prev => ({ ...prev, ...result }));
    setShowCompletarPerfil(false);
  };

  // Handler para cuando se acepta un aula (puedes personalizar la lógica)
  const handleAulaAceptada = () => {
    // Por ejemplo, podrías recargar aulas o mostrar un mensaje
    // window.location.reload();
    // O simplemente cerrar el modal
    setShowInvitaciones(false);
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

  const mockPosts = [
    { id: 1, author: "Dr. Carlos Mendoza", initials: "CM", time: "Hace 2 horas", content: "Recordatorio: El examen parcial de Algoritmos..." },
    { id: 2, author: "María González", initials: "MG", time: "Hace 4 horas", content: "¡Acabamos de publicar las notas del proyecto..." },
    { id: 3, author: "Admin TecBook", initials: "AT", time: "Hace 6 horas", content: "Nueva actualización del sistema: Ahora pueden..." }
  ];

  const mockPinnedAnnouncements = [
    { id: 1, title: "Bienvenida al nuevo ciclo", content: "Les damos la bienvenida a todos los estudiantes al nuevo ciclo académico. ¡Éxitos!", time: "Hace 1 día" },
    { id: 2, title: "Feria de proyectos", content: "No olviden participar en la feria de proyectos este viernes. Habrá premios.", time: "Hace 3 días" }
  ];

  if (isLoading) return <div className="home-loading-container"><div className="home-loading-spinner"></div><p>Cargando información del usuario...</p></div>;
  if (error) return <div className="home-error-container"><h2>Error al cargar datos</h2><p>{error}</p><button onClick={() => navigate('/')} className="home-button">Volver al inicio</button></div>;

  return (
    <div className="home-wrapper">
      {/* ENCABEZADO */}
<header className="home-header">
  <h1 className="home-logo">TecBook</h1>
  <nav className="home-nav">
    <button className="home-nav-link" style={{ color: '#ffc107' }}>
      Inicio
    </button>
    <button onClick={() => navigate('/perfil')} className="home-nav-link">
      Perfil
    </button>
    <button onClick={() => navigate('/aulas')} className="home-nav-link">
      Aulas
    </button>
    <button onClick={() => navigate('/crear-aula')} className="home-nav-link home-create-btn">
      <Plus size={16} style={{ marginRight: '4px' }} />
      Crear Aula
    </button>
    <button onClick={() => setShowInvitaciones(true)} className="home-nav-link">
      Ver Invitaciones
    </button>
    <button onClick={handleLogout} className="home-logout">
      Cerrar sesión
    </button>
  </nav>
</header>

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
              <div className="home-action-button" onClick={() => navigate('/perfil')}><User className="home-action-icon" /><div className="home-action-text"><span className="home-action-title">Mi Perfil</span><span className="home-action-subtitle">Actualiza tus datos</span></div></div>
              <div className="home-action-button" onClick={() => navigate('/aulas')}><BookOpen className="home-action-icon" /><div className="home-action-text"><span className="home-action-title">Mis Aulas</span><span className="home-action-subtitle">Accede a tus aulas</span></div></div>
              <div className="home-action-button" onClick={() => setShowCompletarPerfil(true)}><User className="home-action-icon" /><div className="home-action-text"><span className="home-action-title">Completar Perfil</span><span className="home-action-subtitle">Información académica</span></div></div>
            </div>
          </div>
        </aside>

        <main className="home-main-content">
          <div className="home-create-post">
            <div className="home-create-post-header">
              {renderPostAvatar()}
              <input type="text" placeholder={`¿Qué estás pensando, ${userData?.nombre || 'Usuario'}?`} className="home-create-post-input" readOnly />
            </div>
            <div className="home-create-post-actions">
              <button className="home-post-action"><Video size={20} />Video en vivo</button>
              <button className="home-post-action"><Image size={20} />Foto/Video</button>
              <button className="home-post-action"><Smile size={20} />Actividad</button>
            </div>
          </div>

          <div className="home-posts-container">
            {mockPosts.map(post => (
              <article key={post.id} className="home-post">
                <div className="home-post-header">
                  <div className="home-post-avatar">{post.initials}</div>
                  <div className="home-post-info">
                    <h4>{post.author}</h4><p>{post.time}</p>
                  </div>
                  <button className="home-chat-options"><MoreHorizontal size={20} /></button>
                </div>
                <div className="home-post-content">{post.content}</div>
                <div className="home-post-actions">
                  <button className="home-post-action-btn"><ThumbsUp size={18} />Me gusta</button>
                  <button className="home-post-action-btn"><MessageCircle size={18} />Comentar</button>
                  <button className="home-post-action-btn"><Share2 size={18} />Compartir</button>
                </div>
              </article>
            ))}
          </div>
        </main>

        <aside className="home-right-sidebar">
          <div className="home-chat-section">
            <div className="home-chat-header">
              <h3 className="home-chat-title">Anuncios Fijados</h3>
            </div>
            <div className="home-chat-list">
              {mockPinnedAnnouncements.map(announcement => (
                <div key={announcement.id} className="home-chat-item">
                  <div className="home-chat-info">
                    <h4 className="home-chat-name">{announcement.title}</h4>
                    <p className="home-chat-message">{announcement.content}</p>
                  </div>
                  <span className="home-chat-time">{announcement.time}</span>
                </div>
              ))}
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

      {/* MODAL DE INVITACIONES PENDIENTES */}
      <InvitacionesPendientes
        isOpen={showInvitaciones}
        onClose={() => setShowInvitaciones(false)}
        onAulaAceptada={handleAulaAceptada}
      />
    </div>
  );
}

export default Home;
