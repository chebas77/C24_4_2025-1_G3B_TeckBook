import React, { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import { User, Bell, Book, Calendar } from 'lucide-react';

function Home() {
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const location = useLocation();

  useEffect(() => {
    console.log("Home component mounted or updated");
    
    // Verificar si hay un token en la URL
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');
    
    if (token) {
      console.log("Token encontrado en URL");
      
      // Guardar el token
      localStorage.setItem('auth_token', token);
      
      // Decodificar el token JWT manualmente
      try {
        // Dividir el token en sus partes (header, payload, signature)
        const parts = token.split('.');
        if (parts.length === 3) {
          // Decodificar el payload (segunda parte)
          const base64Url = parts[1];
          const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
          const payload = JSON.parse(atob(base64));
          
          console.log("Payload del token decodificado:", payload);
          
          // Extraer el email del payload (normalmente está en el campo 'sub')
          const email = payload.sub;
          
          // Crear un nombre formateado a partir del email
          let formattedName = email.split('@')[0]; // Quitar el dominio
          formattedName = formattedName
            .replace(/\./g, ' ') // Reemplazar puntos por espacios
            .split(' ')
            .map(part => part.charAt(0).toUpperCase() + part.slice(1)) // Capitalizar
            .join(' ');
          
          // Crear objeto con datos del usuario
          const userData = {
            correoInstitucional: email,
            nombre: formattedName
          };
          
          console.log("Datos de usuario extraídos:", userData);
          
          // Guardar en localStorage y actualizar estado
          localStorage.setItem('user_data', JSON.stringify(userData));
          setUserData(userData);
        }
      } catch (error) {
        console.error("Error al decodificar token:", error);
      }
      
      // Limpiar la URL (opcional)
      window.history.replaceState({}, document.title, "/home");
    } else {
      console.log("No hay token en URL, buscando en localStorage");
      
      // Intentar cargar datos del localStorage
      const storedUserData = localStorage.getItem('user_data');
      if (storedUserData) {
        try {
          const parsed = JSON.parse(storedUserData);
          console.log("Datos cargados desde localStorage:", parsed);
          setUserData(parsed);
        } catch (error) {
          console.error("Error al parsear datos:", error);
        }
      }
    }
    
    setLoading(false);
  }, [location.search]); // Dependencia: location.search para detectar cambios en la URL

  const handleLogout = () => {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_data');
    window.location.href = '/';
  };

  if (loading) {
    return (
      <div style={styles.loadingContainer}>
        <div style={styles.spinner}></div>
        <p style={styles.loadingText}>Cargando...</p>
      </div>
    );
  }

  return (
    <div style={styles.wrapper}>
      {/* ENCABEZADO */}
      <header style={styles.header}>
        <div style={styles.headerContent}>
          <h1 style={styles.logo}>TecBook</h1>
          <nav style={styles.nav}>
            <Link to="/home" style={styles.navLink}>Inicio</Link>
            <Link to="/perfil" style={styles.navLink}>Perfil</Link>
            <Link to="/cursos" style={styles.navLink}>Cursos</Link>
            <button onClick={handleLogout} style={styles.logout}>Cerrar sesión</button>
          </nav>
        </div>
      </header>

      {/* CUERPO PRINCIPAL */}
      <main style={styles.main}>
        <div style={styles.welcomeCard}>
          <div style={styles.welcomeHeader}>
            <div style={styles.userIconContainer}>
              <User size={40} color="#fff" />
            </div>
            <div style={styles.welcomeText}>
              <h2 style={styles.welcomeTitle}>
                ¡Hola, {userData?.nombre || "Usuario"}!
              </h2>
              <p style={styles.welcomeSubtitle}>
                Bienvenido a TecBook
              </p>
            </div>
          </div>
          <p style={styles.welcomeMessage}>
            Tu plataforma educativa para conectar con tu comunidad académica, acceder a recursos de aprendizaje y mantenerte al día con tus cursos.
          </p>
        </div>

        {/* Resto del componente sin cambios */}
        <div style={styles.cardsContainer}>
          <div style={styles.featureCard}>
            <Book size={32} color="#005DAB" />
            <h3 style={styles.featureTitle}>Recursos Educativos</h3>
            <p style={styles.featureText}>
              Accede a materiales de estudio, libros digitales y recursos académicos para tus cursos.
            </p>
          </div>
          
          <div style={styles.featureCard}>
            <Calendar size={32} color="#005DAB" />
            <h3 style={styles.featureTitle}>Calendario Académico</h3>
            <p style={styles.featureText}>
              Mantente al día con fechas importantes, exámenes y entregas de trabajos.
            </p>
          </div>
          
          <div style={styles.featureCard}>
            <Bell size={32} color="#005DAB" />
            <h3 style={styles.featureTitle}>Notificaciones</h3>
            <p style={styles.featureText}>
              Recibe alertas sobre actividades, mensajes de profesores y eventos próximos.
            </p>
          </div>
        </div>
      </main>

      {/* PIE DE PÁGINA */}
      <footer style={styles.footer}>
        <p style={styles.footerText}>
          © {new Date().getFullYear()} TecBook - Plataforma Educativa
        </p>
      </footer>
    </div>
  );
}

const styles = {
  wrapper: {
    display: "flex",
    flexDirection: "column",
    minHeight: "100vh",
    width: "100vw",
    margin: 0,
    padding: 0,
    fontFamily: "'Segoe UI', sans-serif",
    backgroundColor: "#f7f9fc",
    overflow: "hidden"
  },
  header: {
    width: "100%",
    backgroundColor: "#005DAB",
    color: "white",
    boxShadow: "0 4px 10px rgba(0,0,0,0.1)",
    padding: "0",
    margin: "0"
  },
  headerContent: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "20px 40px",
    maxWidth: "1400px",
    margin: "0 auto",
    width: "100%",
    boxSizing: "border-box"
  },
  logo: {
    fontSize: "28px",
    fontWeight: "bold",
    margin: 0,
  },
  nav: {
    display: "flex",
    gap: "20px",
    alignItems: "center"
  },
  navLink: {
    color: "white",
    textDecoration: "none",
    fontSize: "16px",
    fontWeight: "500",
    transition: "color 0.3s ease",
    padding: "8px 12px",
    borderRadius: "6px"
  },
  logout: {
    backgroundColor: "#ff9800",
    border: "none",
    color: "white",
    padding: "8px 16px",
    borderRadius: "6px",
    cursor: "pointer",
    fontWeight: "500",
    fontSize: "16px"
  },
  main: {
    flex: "1",
    width: "100%",
    maxWidth: "1400px",
    margin: "0 auto",
    padding: "40px",
    boxSizing: "border-box"
  },
  welcomeCard: {
    background: "linear-gradient(135deg, #005DAB, #0088cc)",
    color: "white",
    borderRadius: "16px",
    padding: "30px",
    boxShadow: "0 10px 30px rgba(0,93,171,0.2)",
    marginBottom: "40px",
    width: "100%",
    boxSizing: "border-box"
  },
  welcomeHeader: {
    display: "flex",
    alignItems: "center",
    marginBottom: "20px"
  },
  userIconContainer: {
    backgroundColor: "rgba(255,255,255,0.2)",
    width: "60px",
    height: "60px",
    borderRadius: "50%",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    marginRight: "20px"
  },
  welcomeTitle: {
    fontSize: "32px",
    margin: 0,
    fontWeight: "600"
  },
  welcomeSubtitle: {
    fontSize: "18px",
    margin: "6px 0 0 0",
    opacity: 0.9
  },
  welcomeMessage: {
    fontSize: "17px",
    lineHeight: 1.6,
    marginTop: "10px",
    marginBottom: 0
  },
  cardsContainer: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(300px, 1fr))",
    gap: "30px",
    marginTop: "30px",
    width: "100%"
  },
  featureCard: {
    backgroundColor: "white",
    padding: "30px",
    borderRadius: "12px",
    boxShadow: "0 8px 20px rgba(0,0,0,0.05)",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    textAlign: "center"
  },
  featureTitle: {
    fontSize: "20px",
    color: "#333",
    margin: "20px 0 10px"
  },
  featureText: {
    color: "#666",
    lineHeight: 1.6,
    margin: 0
  },
  footer: {
    backgroundColor: "#f0f4f8",
    padding: "20px",
    textAlign: "center",
    borderTop: "1px solid #e0e6ed",
    width: "100%"
  },
  footerText: {
    margin: 0,
    color: "#666",
    fontSize: "14px"
  },
  loadingContainer: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    height: "100vh",
    width: "100vw"
  },
  spinner: {
    border: "4px solid rgba(0,0,0,0.1)",
    borderTop: "4px solid #005DAB",
    borderRadius: "50%",
    width: "40px",
    height: "40px",
    animation: "spin 1s linear infinite"
  },
  loadingText: {
    marginTop: "20px",
    color: "#005DAB",
    fontSize: "18px"
  }
};

export default Home;