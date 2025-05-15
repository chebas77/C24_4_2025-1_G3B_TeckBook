// src/Home.jsx
import React from "react";
import { Link } from "react-router-dom";
import portalImage from "./assets/portal.png"; // Puedes usar otra imagen institucional

function Home() {
  return (
    <div style={styles.wrapper}>
      {/* ENCABEZADO */}
      <header style={styles.header}>
        <h1 style={styles.logo}>TecBook</h1>
        <nav style={styles.nav}>
          <Link to="/" style={styles.navLink}>Inicio</Link>
          <Link to="/perfil" style={styles.navLink}>Perfil</Link>
          <Link to="/cursos" style={styles.navLink}>Cursos</Link>
          <Link to="/logout" style={styles.logout}>Cerrar sesión</Link>
        </nav>
      </header>

      {/* CUERPO PRINCIPAL */}
      <main style={styles.main}>
        <div style={styles.leftContent}>
          <h2 style={styles.welcomeTitle}>Bienvenido a TecBook</h2>
          <p style={styles.description}>
            Conecta con tu comunidad académica. Explora cursos, comparte recursos y mantente informado con las últimas novedades del campus.
          </p>
          <Link to="/cursos" style={styles.button}>Ir a mis cursos</Link>
        </div>

        <div style={styles.rightImage}>
          <div style={styles.imageCard}>
            <img src={portalImage} alt="portal institucional" style={styles.image} />
          </div>
        </div>
      </main>
    </div>
  );
}

const styles = {
  wrapper: {
    display: "flex",
    flexDirection: "column",
    height: "100vh",
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
    textDecoration: "none",
    fontSize: "16px",
    fontWeight: "500",
    transition: "color 0.3s ease"
  },
  logout: {
    color: "#ffc107",
    textDecoration: "none",
    fontSize: "16px",
    fontWeight: "500",
  },
  main: {
    display: "flex",
    flex: 1,
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
    textDecoration: "none",
    fontSize: "16px",
    fontWeight: "500",
    boxShadow: "0 6px 12px rgba(0,0,0,0.15)",
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
};

export default Home;
