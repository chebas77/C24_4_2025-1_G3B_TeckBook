import { useState } from "react";
import portalImage from "./assets/portal.png";
import { Link, useNavigate } from "react-router-dom";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch("http://localhost:8080/api/usuarios/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });
      const result = await response.json();
      if (result) {
        alert("Inicio de sesión exitoso");
        navigate("/home");
      } else {
        alert("Credenciales incorrectas");
      }
    } catch (error) {
      alert("Error de conexión con el backend");
    }
  };

  return (
    <div style={styles.wrapper}>
      {/* LADO IZQUIERDO */}
      <div style={styles.left}>
        <div style={styles.formBox}>
          <h1 style={styles.logo}>TecBook</h1>
          <h2 style={styles.title}>Bienvenido</h2>
          <p style={styles.subtitle}>
            Inicia sesión con tus credenciales institucionales
          </p>

          <div style={styles.form}>
            <form onSubmit={handleLogin}>
              <div style={styles.inputContainer}>
                <div style={styles.inputIcon}>
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
                    <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
                    <circle cx="12" cy="7" r="4" />
                  </svg>
                </div>
                <input
                  type="email"
                  placeholder="Correo institucional"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  style={styles.input}
                  required
                />
              </div>

              <div style={styles.inputContainer}>
                <div style={styles.inputIcon}>
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
                    <rect width="18" height="11" x="3" y="11" rx="2" ry="2" />
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                  </svg>
                </div>
                <input
                  type="password"
                  placeholder="Contraseña"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  style={styles.input}
                  required
                />
              </div>

              <button
                type="submit"
                style={styles.button}
                onMouseOver={(e) => (e.currentTarget.style.backgroundColor = "#004c8e")}
                onMouseOut={(e) => (e.currentTarget.style.backgroundColor = "#005DAB")}
              >
                <span style={styles.buttonContent}>
                  Ingresar
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="white"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    style={styles.arrowIcon}
                  >
                    <path d="M5 12h14" />
                    <path d="m12 5 7 7-7 7" />
                  </svg>
                </span>
              </button>
            </form>
            <Link to="/register" style={styles.registerButton}>
              ¿No tienes cuenta? Regístrate aquí
            </Link>
          </div>

          <div style={styles.dividerContainer}>
            <div style={styles.divider}></div>
            <span style={styles.dividerText}>o</span>
            <div style={styles.divider}></div>
          </div>

          <button style={styles.googleButton}>
            <div style={styles.googleIconWrapper}>
              <svg style={styles.googleIcon} viewBox="0 0 24 24">
                <path
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                  fill="#4285F4"
                />
                <path
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                  fill="#34A853"
                />
                <path
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                  fill="#FBBC05"
                />
                <path
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                  fill="#EA4335"
                />
              </svg>
            </div>
            <span style={styles.googleButtonText}>
              Ingresa con tu correo de Tecsup
            </span>
          </button>

          <div style={{ ...styles.dividerContainer, marginTop: "20px" }}>
            <div style={styles.divider}></div>
            <span style={styles.dividerText}>¿Problemas para acceder?</span>
            <div style={styles.divider}></div>
          </div>

          <div style={styles.helpLinks}>
            <Link
              to="/recuperar"
              style={styles.link}
              onMouseOver={(e) => (e.currentTarget.style.color = "#004c8e")}
              onMouseOut={(e) => (e.currentTarget.style.color = "#005DAB")}
            >
              Recuperar contraseña
            </Link>
            <span style={styles.dot}>•</span>
            <a
              style={styles.link}
              onMouseOver={(e) => (e.currentTarget.style.color = "#004c8e")}
              onMouseOut={(e) => (e.currentTarget.style.color = "#005DAB")}
            >
              Contactar soporte
            </a>
          </div>
        </div>
      </div>

      {/* LADO DERECHO */}
      <div style={styles.right}>
        <div
          style={{
            ...styles.rightBackground,
            backgroundImage: `url(${portalImage})`,
          }}
        ></div>
        <div style={styles.overlay}></div>
        <div style={styles.infoCard}>
          <h3 style={styles.infoTitle}>Portal Académico</h3>
          <p style={styles.infoText}>
            Accede a todos tus recursos educativos, calificaciones y material de
            estudio en un solo lugar.
          </p>
        </div>
      </div>
    </div>
  );
}

const styles = {
  wrapper: {
    display: "flex",
    height: "100vh",
    width: "100vw",
    overflow: "hidden",
    fontFamily: "'Segoe UI', Arial, sans-serif",
    background: "#ffffff",
  },
  left: {
    flex: 1,
    backgroundColor: "#fff",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    padding: "0 20px",
  },
  right: {
    position: "relative",
    flex: 1,
    backgroundColor: "#f0f4f8",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    overflow: "hidden",
  },
  rightBackground: {
    position: "absolute",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundSize: "cover",
    backgroundPosition: "center",
    backgroundRepeat: "no-repeat",
  },
  overlay: {
    position: "absolute",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background:
      "linear-gradient(135deg, rgba(0, 93, 171, 0.3) 0%, rgba(0, 93, 171, 0) 70%)",
  },
  formBox: {
    width: "100%",
    maxWidth: "400px",
    textAlign: "center",
  },
  logo: {
    fontSize: "50px",
    color: "#005DAB",
    fontWeight: "bold",
    marginBottom: "5px",
    letterSpacing: "-0.5px",
  },
  title: {
    fontSize: "26px",
    marginBottom: "5px",
    color: "#333",
    fontWeight: "600",
  },
  subtitle: {
    color: "#666",
    marginBottom: "30px",
    fontSize: "15px",
  },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "15px",
    marginBottom: "25px",
  },
  inputContainer: {
    position: "relative",
    width: "100%",
  },
  inputIcon: {
    position: "absolute",
    left: "12px",
    top: "50%",
    transform: "translateY(-50%)",
    color: "#6b7280",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  input: {
    width: "100%",
    padding: "14px 15px 14px 40px",
    fontSize: "15px",
    border: "1px solid #ddd",
    borderRadius: "8px",
    outline: "none",
    transition: "all 0.2s ease",
    boxSizing: "border-box",
  },
  button: {
    backgroundColor: "#005DAB",
    color: "white",
    padding: "14px",
    fontSize: "15px",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontWeight: "500",
    transition: "background-color 0.2s ease",
    boxShadow: "0 2px 5px rgba(0,0,0,0.15)",
  },
  buttonContent: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: "8px",
  },
  arrowIcon: {
    marginLeft: "5px",
    transition: "transform 0.3s ease",
  },
  dividerContainer: {
    display: "flex",
    alignItems: "center",
    margin: "25px 0 15px",
  },
  divider: {
    flex: 1,
    height: "1px",
    backgroundColor: "#e0e0e0",
  },
  dividerText: {
    padding: "0 15px",
    color: "#666",
    fontSize: "14px",
  },
  helpLinks: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    gap: "10px",
    marginTop: "15px",
  },
  link: {
    color: "#005DAB",
    textDecoration: "none",
    fontSize: "14px",
    fontWeight: "500",
    cursor: "pointer",
    transition: "color 0.2s ease",
  },
  dot: {
    color: "#999",
  },
  infoCard: {
    position: "absolute",
    bottom: "40px",
    left: "40px",
    backgroundColor: "rgba(255, 255, 255, 0.85)",
    padding: "20px",
    borderRadius: "12px",
    backdropFilter: "blur(5px)",
    maxWidth: "300px",
    boxShadow: "0 4px 20px rgba(0, 0, 0, 0.1)",
  },
  infoTitle: {
    fontSize: "20px",
    color: "#005DAB",
    fontWeight: "bold",
    marginBottom: "8px",
  },
  infoText: {
    color: "#000",
  },
  googleButton: {
    display: "flex",
    alignItems: "center",
    backgroundColor: "white",
    border: "1px solid #dadce0",
    borderRadius: "8px",
    boxShadow: "0 1px 3px rgba(0,0,0,0.08)",
    color: "#3c4043",
    cursor: "pointer",
    fontFamily: "'Segoe UI', Arial, sans-serif",
    fontSize: "14px",
    fontWeight: "500",
    height: "48px",
    justifyContent: "center",
    letterSpacing: "0.25px",
    padding: "0 12px",
    position: "relative",
    textAlign: "center",
    transition: "all 0.2s ease",
    width: "100%",
    marginTop: "5px",
  },
  googleButtonText: {
    paddingLeft: "35px",
    textAlign: "center",
    color: "#444",
    fontSize: "14px",
    fontWeight: "500",
  },
  googleIconWrapper: {
    position: "absolute",
    left: "12px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  googleIcon: {
    width: "18px",
    height: "18px",
  },
  registerButton: {
    display: "block",
    marginTop: "18px",
    color: "#005DAB",
    textDecoration: "none",
    fontWeight: "500",
    fontSize: "15px",
    transition: "color 0.2s",
    textAlign: "center",
    cursor: "pointer",
  },
};

export default Login;