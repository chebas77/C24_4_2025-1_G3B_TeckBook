import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { 
  User, 
  BookOpen, 
  Image, 
  Video, 
  Smile, 
  MessageCircle, 
  ThumbsUp, 
  Share2,
  MoreHorizontal,
  Search,
  Plus,
  Calendar,
  BarChart3
} from 'lucide-react';
import "../css/Home.css";
import CompletarPerfil from '../components/CompletarPerfil'; // ✅ IMPORTAR EL MODAL
import InvitacionesPendientes from '../components/InvitacionesPendientes';

function Home() {
  const [userData, setUserData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // ✅ ESTADO PARA EL MODAL DE COMPLETAR PERFIL
  const [showCompletarPerfil, setShowCompletarPerfil] = useState(false);
  const [showInvitaciones, setShowInvitaciones] = useState(false);
  
  const navigate = useNavigate();

  useEffect(() => {
    let isMounted = true; // Flag para evitar actualizaciones si el componente se desmonta
    
    // Verificar si hay un token en la URL (redirección desde OAuth2)
    const queryParams = new URLSearchParams(window.location.search);
    const tokenFromUrl = queryParams.get('token');
    const isNewUserParam = queryParams.get('new') === 'true';
    const isIncompleteParam = queryParams.get('incomplete') === 'true';
    
    if (tokenFromUrl) {
      console.log("Token encontrado en URL:", tokenFromUrl.substring(0, 20) + "...");
      console.log("Es nuevo usuario:", isNewUserParam);
      console.log("Perfil incompleto:", isIncompleteParam);
      
      // Guardar el token de la URL en localStorage
      localStorage.setItem('token', tokenFromUrl);
      
      // Limpiar la URL para evitar problemas si se recarga la página
      window.history.replaceState({}, document.title, '/home');
    }
    
    // Verificar si el usuario está autenticado
    const token = localStorage.getItem('token');
    
    if (!token) {
      console.log("No se encontró token en localStorage, redirigiendo a login");
      navigate('/');
      return;
    }

    console.log("Token encontrado, obteniendo datos de usuario");

    // Obtener datos del usuario
    const fetchUserData = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/auth/user', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (!response.ok) {
          const errorText = await response.text();
          console.error("Error en la respuesta:", errorText);
          throw new Error('No se pudo obtener la información del usuario');
        }

        const data = await response.json();
        console.log("Datos del usuario obtenidos:", data);
        
        // Solo actualizar el estado si el componente sigue montado
        if (!isMounted) return;
        
        setUserData({
          ...data,
          profileImageUrl: data.profileImageUrl || ""
        });

        // ✅ VERIFICAR SI EL PERFIL ESTÁ INCOMPLETO
        // Verificar por parámetros de URL o por datos faltantes CRÍTICOS
        // NO verificar seccionId porque se asigna después por admin
        const needsCompletion = isNewUserParam || 
                               isIncompleteParam || 
                               !data.carreraId || 
                               !data.cicloActual || // Ajusta a 'ciclo' si tu campo se llama así
                               !data.departamentoId;
                               
        if (needsCompletion) {
          console.log("Perfil incompleto detectado, mostrando modal");
          console.log("Razones:", {
            isNewUser: isNewUserParam,
            isIncomplete: isIncompleteParam,
            noCarrera: !data.carreraId,
            noCicloActual: !data.cicloActual, // Ajusta según tu campo
            noDepartamento: !data.departamentoId,
            // ❌ CAMPOS QUE NO AFECTAN LA DECISIÓN (solo para debug):
            seccionId: data.seccionId, // Se asigna por admin después
            telefono: data.telefono, // Es opcional
            createdAt: data.createdAt, // Se genera automáticamente
            updatedAt: data.updatedAt, // Se actualiza automáticamente
            profileImageUrl: !!data.profileImageUrl // Es opcional
          });
          if (isMounted) {
            setShowCompletarPerfil(true);
          }
        }
        
      } catch (error) {
        console.error("Error al obtener datos del usuario:", error);
        if (isMounted) {
          setError(error.message);
          localStorage.removeItem('token');
          setTimeout(() => {
            navigate('/');
          }, 2000);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    };

    fetchUserData();
    
    // Cleanup function para evitar actualizaciones en componente desmontado
    return () => {
      isMounted = false;
    };
  }, [navigate]); // Solo depende de navigate, no de otros estados

  const handleLogout = async () => {
    try {
      const token = localStorage.getItem('token');
      
      if (token) {
        console.log("Cerrando sesión en el backend...");
        
        const response = await fetch('http://localhost:8080/api/auth/logout', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
        
        if (response.ok) {
          const data = await response.json();
          console.log("✅ Sesión cerrada en el backend:", data);
          
          if (data.tokenInvalidated) {
            console.log("✅ Token invalidado correctamente");
          }
        } else {
          console.warn("⚠️ Error al cerrar sesión en backend, pero continuando logout");
        }
      }
      
      localStorage.removeItem('token');
      console.log("✅ Token eliminado del localStorage");
      
      navigate('/');
      
    } catch (error) {
      console.error("❌ Error durante logout:", error);
      localStorage.removeItem('token');
      navigate('/');
    }
  };

  // ✅ FUNCIÓN PARA MANEJAR COMPLETAR PERFIL
  const handleCompletarPerfil = (result) => {
    console.log('Perfil completado:', result);
    // Actualizar userData con los nuevos datos
    setUserData(prev => ({
      ...prev,
      ...result
    }));
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
    if (userData?.nombre && userData?.apellidos) {
      return `${userData.nombre.charAt(0)}${userData.apellidos.charAt(0)}`.toUpperCase();
    }
    return 'GS';
  };

  const renderUserAvatar = () => {
    if (userData?.profileImageUrl && userData.profileImageUrl.trim() !== "") {
      return (
        <img 
          src={userData.profileImageUrl} 
          alt="Foto de perfil" 
          className="home-user-avatar"
          style={{
            objectFit: 'cover',
            backgroundColor: '#005DAB'
          }}
          onLoad={() => console.log("✅ Imagen de perfil cargada en Home")}
          onError={(e) => {
            console.warn("❌ Error cargando imagen de perfil en Home:", userData.profileImageUrl);
            e.target.style.display = 'none';
            const fallback = document.createElement('div');
            fallback.className = 'home-user-avatar';
            fallback.textContent = getUserInitials();
            e.target.parentNode.appendChild(fallback);
          }}
        />
      );
    }
    
    return (
      <div className="home-user-avatar">
        {getUserInitials()}
      </div>
    );
  };

  const renderPostAvatar = (className = "home-create-post-avatar") => {
    if (userData?.profileImageUrl && userData.profileImageUrl.trim() !== "") {
      return (
        <img 
          src={userData.profileImageUrl} 
          alt="Foto de perfil" 
          className={className}
          style={{
            objectFit: 'cover',
            backgroundColor: '#005DAB'
          }}
          onError={(e) => {
            e.target.style.display = 'none';
            const fallback = document.createElement('div');
            fallback.className = className;
            fallback.textContent = getUserInitials();
            e.target.parentNode.appendChild(fallback);
          }}
        />
      );
    }
    
    return (
      <div className={className}>
        {getUserInitials()}
      </div>
    );
  };

  const mockPosts = [
    {
      id: 1,
      author: "Dr. Carlos Mendoza",
      initials: "CM",
      time: "Hace 2 horas",
      content: "Recordatorio: El examen parcial de Algoritmos y Estructuras de Datos será este viernes a las 10:00 AM. Por favor, revisen los capítulos 4 y 5 del libro principal."
    },
    {
      id: 2,
      author: "María González",
      initials: "MG",
      time: "Hace 4 horas",
      content: "¡Acabamos de publicar las notas del proyecto de base de datos! Pueden revisarlas en el portal académico. ¡Excelente trabajo equipo!"
    },
    {
      id: 3,
      author: "Admin TecBook",
      initials: "AT",
      time: "Hace 6 horas",
      content: "Nueva actualización del sistema: Ahora pueden descargar certificados digitales directamente desde sus perfiles. ¡Prueben esta nueva funcionalidad!"
    }
  ];

  const mockChats = [
    { id: 1, name: "Ana Rivera", initials: "AR", message: "¿Tienes las notas de la clase?", time: "10:30", online: true },
    { id: 2, name: "Carlos Ruiz", initials: "CR", message: "Nos vemos en el laboratorio", time: "09:45", online: true },
    { id: 3, name: "María López", initials: "ML", message: "El proyecto está listo", time: "Ayer", online: false },
    { id: 4, name: "Grupo Desarrollo", initials: "GD", message: "Nueva tarea asignada", time: "Ayer", online: false },
    { id: 5, name: "Pedro Sánchez", initials: "PS", message: "¿Cuando es la entrega?", time: "Lun", online: false }
  ];

  if (isLoading) {
    return (
      <div className="home-loading-container">
        <div className="home-loading-spinner"></div>
        <p>Cargando información del usuario...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="home-error-container">
        <h2>Error al cargar datos</h2>
        <p>{error}</p>
        <button 
          onClick={() => navigate('/')} 
          className="home-button"
        >
          Volver al inicio de sesión
        </button>
      </div>
    );
  }

  return (
    <div className="home-wrapper">
      {/* ENCABEZADO */}
      <header className="home-header">
        <h1 className="home-logo">TecBook</h1>
        <nav className="home-nav">
          <button className="home-nav-link" style={{color: '#ffc107'}}>
            Inicio
          </button>
          <button onClick={() => navigate('/perfil')} className="home-nav-link">
            Perfil
          </button>
          <button onClick={() => navigate('/aulas')} className="home-nav-link">
            Aulas
          </button>
          <button onClick={() => navigate('/crear-aula')} className="home-nav-link home-create-btn">
            <Plus size={16} style={{marginRight: '4px'}} />
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
        
        {/* COLUMNA IZQUIERDA */}
        <aside className="home-left-sidebar">
          <div className="home-welcome-card">
            <div className="home-user-info">
              {renderUserAvatar()}
              <div className="home-user-details">
                <h3>{userData ? `${userData.nombre} ${userData.apellidos}` : 'Bienvenido a TecBook'}</h3>
                <p>{userData?.rol || 'Estudiante'}</p>
              </div>
            </div>
            <p className="home-welcome-text">
              Conecta con tu comunidad académica. Explora aulas, comparte recursos y mantente informado.
            </p>
          </div>

          <div className="home-quick-actions">
            <h3 className="home-section-title">Acciones Rápidas</h3>
            <div className="home-action-buttons">
              <div className="home-action-button" onClick={() => navigate('/perfil')}>
                <User className="home-action-icon" size={20} />
                <div className="home-action-text">
                  <span className="home-action-title">Mi Perfil</span>
                  <span className="home-action-subtitle">Actualiza tus datos personales</span>
                </div>
              </div>
              
              <div className="home-action-button" onClick={() => navigate('/aulas')}>
                <BookOpen className="home-action-icon" size={20} />
                <div className="home-action-text">
                  <span className="home-action-title">Mis Aulas</span>
                  <span className="home-action-subtitle">Accede a tus aulas asignadas</span>
                </div>
              </div>
              
              <div className="home-action-button">
                <Calendar className="home-action-icon" size={20} />
                <div className="home-action-text">
                  <span className="home-action-title">Calendario</span>
                  <span className="home-action-subtitle">Revisa tus próximas entregas</span>
                </div>
              </div>
              
              <div className="home-action-button">
                <BarChart3 className="home-action-icon" size={20} />
                <div className="home-action-text">
                  <span className="home-action-title">Notas</span>
                  <span className="home-action-subtitle">Consulta tus calificaciones</span>
                </div>
              </div>

              {/* ✅ BOTÓN PARA ABRIR MODAL MANUALMENTE */}
              <div 
                className="home-action-button" 
                onClick={() => setShowCompletarPerfil(true)}
              >
                <User className="home-action-icon" size={20} />
                <div className="home-action-text">
                  <span className="home-action-title">Completar Perfil</span>
                  <span className="home-action-subtitle">Actualiza tu información académica</span>
                </div>
              </div>
            </div>
          </div>
        </aside>

        {/* COLUMNA CENTRAL - Feed */}
        <main className="home-main-content">
          <div className="home-create-post">
            <div className="home-create-post-header">
              {renderPostAvatar("home-create-post-avatar")}
              <input 
                type="text" 
                placeholder={`¿Qué estás pensando, ${userData?.nombre || 'Usuario'}?`}
                className="home-create-post-input"
                readOnly
              />
            </div>
            <div className="home-create-post-actions">
              <button className="home-post-action">
                <Video size={20} style={{color: '#ef4444'}} />
                Video en vivo
              </button>
              <button className="home-post-action">
                <Image size={20} style={{color: '#10b981'}} />
                Foto/Video
              </button>
              <button className="home-post-action">
                <Smile size={20} style={{color: '#f59e0b'}} />
                Actividad
              </button>
            </div>
          </div>

          <div className="home-posts-container">
            {mockPosts.map(post => (
              <article key={post.id} className="home-post">
                <div className="home-post-header">
                  <div className="home-post-avatar">
                    {post.initials}
                  </div>
                  <div className="home-post-info">
                    <h4>{post.author}</h4>
                    <p>{post.time}</p>
                  </div>
                  <button className="home-chat-options">
                    <MoreHorizontal size={20} />
                  </button>
                </div>
                <div className="home-post-content">
                  {post.content}
                </div>
                <div className="home-post-actions">
                  <button className="home-post-action-btn">
                    <ThumbsUp size={18} />
                    Me gusta
                  </button>
                  <button className="home-post-action-btn">
                    <MessageCircle size={18} />
                    Comentar
                  </button>
                  <button className="home-post-action-btn">
                    <Share2 size={18} />
                    Compartir
                  </button>
                </div>
              </article>
            ))}
          </div>
        </main>

        {/* COLUMNA DERECHA - Chat */}
        <aside className="home-right-sidebar">
          <div className="home-chat-section">
            <div className="home-chat-header">
              <h3 className="home-chat-title">Chats Recientes</h3>
              <button className="home-chat-options">
                <Search size={20} />
              </button>
            </div>
            
            <div className="home-chat-list">
              {mockChats.map(chat => (
                <div key={chat.id} className="home-chat-item">
                  <div className="home-chat-avatar">
                    {chat.initials}
                    <div className={`home-chat-status ${chat.online ? 'online' : 'offline'}`}></div>
                  </div>
                  <div className="home-chat-info">
                    <h4 className="home-chat-name">{chat.name}</h4>
                    <p className="home-chat-message">{chat.message}</p>
                  </div>
                  <span className="home-chat-time">{chat.time}</span>
                </div>
              ))}
            </div>
          </div>
        </aside>
      </div>

      {/* ✅ MODAL DE COMPLETAR PERFIL */}
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