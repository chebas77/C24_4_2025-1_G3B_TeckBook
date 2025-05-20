import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import portalImage from "./assets/portal.png";

function Home() {
  const [userData, setUserData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    // Verificar si hay un token en la URL (redirección desde OAuth2)
    const queryParams = new URLSearchParams(window.location.search);
    const tokenFromUrl = queryParams.get('token');
    
    if (tokenFromUrl) {
      console.log("Token encontrado en URL:", tokenFromUrl.substring(0, 20) + "...");
      
      // Guardar el token de la URL en localStorage
      localStorage.setItem('token', tokenFromUrl);
      
      // Limpiar la URL para evitar problemas si se recarga la página
      window.history.replaceState({}, document.title, '/home');
    } else {
      console.log("No se encontró token en la URL");
    }
    
    // Verificar si el usuario está autenticado
    const token = localStorage.getItem('token');
    
    if (!token) {
      console.log("No se encontró token en localStorage, redirigiendo a login");
      navigate('/');
      return;
    }

    console.log("Token encontrado en localStorage, obteniendo datos de usuario");

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
        setUserData(data);
      } catch (error) {
        console.error("Error al obtener datos del usuario:", error);
        setError(error.message);
        
        // Si hay un error de autenticación, redirigir al login
        localStorage.removeItem('token');
        setTimeout(() => {
          navigate('/');
        }, 2000);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserData();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/');
  };

  if (isLoading) {
    return (
      <div style={styles.loadingContainer}>
        <div style={styles.loadingSpinner}></div>
        <p>Cargando información del usuario...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div style={styles.errorContainer}>
        <h2>Error al cargar datos</h2>
        <p>{error}</p>
        <button 
          onClick={() => navigate('/')} 
          style={styles.button}
        >
          Volver al inicio de sesión
        </button>
      </div>
    );
  }

  return (
    <div style={styles.wrapper}>
      {/* ENCABEZADO */}
      <header style={styles.header}>
        <h1 style={styles.logo}>TecBook</h1>
        <nav style={styles.nav}>
          <button style={{...styles.navLink, color: '#ffc107'}}>
            Inicio
          </button>
          <button onClick={() => navigate('/perfil')} style={styles.navLink}>
            Perfil
          </button>
          <button onClick={() => navigate('/cursos')} style={styles.navLink}>
            Cursos
          </button>
          <button onClick={handleLogout} style={styles.logout}>
            Cerrar sesión
          </button>
        </nav>
      </header>

      {/* CUERPO PRINCIPAL */}
      <main style={styles.main}>
        <div style={styles.leftContent}>
          <h2 style={styles.welcomeTitle}>
            {userData ? `¡Bienvenido, ${userData.nombre}!` : 'Bienvenido a TecBook'}
          </h2>
          <p style={styles.description}>
            Conecta con tu comunidad académica. Explora cursos, comparte recursos y mantente informado con las últimas novedades del campus.
          </p>
          <button onClick={() => navigate('/cursos')} style={styles.button}>
            Ir a mis cursos
          </button>
        </div>

        <div style={styles.rightImage}>
          <div style={styles.imageCard}>
            <img src={portalImage} alt="portal institucional" style={styles.image} />
          </div>
        </div>
      </main>

      {/* TARJETAS DE ACCIÓN RÁPIDA */}
      <section style={styles.quickActions}>
        <h3 style={styles.sectionTitle}>Acciones Rápidas</h3>
        <div style={styles.cardsContainer}>
          <div style={styles.card} onClick={() => navigate('/perfil')}>
            <div style={styles.cardIcon}>👤</div>
            <h4 style={styles.cardTitle}>Mi Perfil</h4>
            <p style={styles.cardDescription}>Actualiza tus datos personales y académicos</p>
          </div>
          
          <div style={styles.card}>
            <div style={styles.cardIcon}>📚</div>
            <h4 style={styles.cardTitle}>Mis Cursos</h4>
            <p style={styles.cardDescription}>Accede a tus materiales de estudio</p>
          </div>
          
          <div style={styles.card}>
            <div style={styles.cardIcon}>📅</div>
            <h4 style={styles.cardTitle}>Calendario</h4>
            <p style={styles.cardDescription}>Revisa tus próximas entregas y evaluaciones</p>
          </div>
          
          <div style={styles.card}>
            <div style={styles.cardIcon}>📊</div>
            <h4 style={styles.cardTitle}>Notas</h4>
            <p style={styles.cardDescription}>Consulta tus calificaciones actuales</p>
          </div>
        </div>
      </section>
    </div>
  );
}

const styles = {
  wrapper: {
    display: "flex",
    flexDirection: "column",
    minHeight: "100vh",
    width: "100vw",
    fontFamily: "'Segoe UI', sans-serif",
    backgroundColor: "#f7f9fc"
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "20px 40px",
    backgroundColor: "#005DAB",
    color: "white",
    boxShadow: "0 4px 10px rgba(0,0,0,0.1)"
  },
  logo: {
    fontSize: "28px",
    fontWeight: "bold",
    margin: 0,
  },
  nav: {
    display: "flex",
    gap: "20px"
  },
  navLink: {
    color: "white",
    backgroundColor: "transparent",
    border: "none",
    fontSize: "16px",
    fontWeight: "500",
    cursor: "pointer",
    padding: "5px 10px",
    transition: "color 0.3s ease"
  },
  logout: {
    color: "#ffc107",
    backgroundColor: "transparent",
    border: "none",
    fontSize: "16px",
    fontWeight: "500",
    cursor: "pointer",
    padding: "5px 10px",
  },
  main: {
    display: "flex",
    padding: "60px 80px",
    alignItems: "center",
    justifyContent: "space-between",
    width: "100%",
    boxSizing: "border-box"
  },
  leftContent: {
    maxWidth: "550px",
  },
  welcomeTitle: {
    fontSize: "36px",
    color: "#003c71",
    marginBottom: "20px",
  },
  description: {
    fontSize: "18px",
    color: "#444",
    marginBottom: "30px",
    lineHeight: "1.6"
  },
  button: {
    display: "inline-block",
    backgroundColor: "#005DAB",
    color: "white",
    padding: "14px 28px",
    borderRadius: "10px",
    border: "none",
    textDecoration: "none",
    fontSize: "16px",
    fontWeight: "500",
    boxShadow: "0 6px 12px rgba(0,0,0,0.15)",
    cursor: "pointer",
  },
  rightImage: {
    flex: 1,
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    maxWidth: "50%"
  },
  imageCard: {
    backgroundColor: "#ffffff",
    padding: "30px",
    borderRadius: "20px",
    boxShadow: "0 10px 40px rgba(0,0,0,0.1)",
  },
  image: {
    width: "100%",
    maxWidth: "480px",
    borderRadius: "12px",
  },
  quickActions: {
    padding: "0 80px 60px 80px",
  },
  sectionTitle: {
    fontSize: "24px",
    color: "#003c71",
    marginBottom: "25px",
  },
  cardsContainer: {
    display: "flex",
    gap: "20px",
    flexWrap: "wrap",
  },
  card: {
    backgroundColor: "#ffffff",
    borderRadius: "15px",
    padding: "25px",
    flex: "1 1 200px",
    minWidth: "200px",
    boxShadow: "0 5px 20px rgba(0,0,0,0.05)",
    transition: "transform 0.2s, box-shadow 0.2s",
    cursor: "pointer",
  },
  cardIcon: {
    fontSize: "32px",
    marginBottom: "15px",
  },
  cardTitle: {
    fontSize: "18px",
    color: "#003c71",
    marginBottom: "10px",
    fontWeight: "600",
  },
  cardDescription: {
    fontSize: "14px",
    color: "#666",
    lineHeight: "1.5",
  },
  loadingContainer: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    height: "100vh",
    gap: "20px",
  },
  loadingSpinner: {
    width: "40px",
    height: "40px",
    border: "4px solid #f3f3f3",
    borderTop: "4px solid #005DAB",
    borderRadius: "50%",
    animation: "spin 1s linear infinite",
  },
  errorContainer: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    height: "100vh",
    gap: "20px",
    textAlign: "center",
    padding: "0 20px",
  },
};

export default Home;