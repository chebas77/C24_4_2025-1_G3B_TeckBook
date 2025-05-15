import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Eye, EyeOff, Mail, Lock, ArrowRight, CheckCircle, HelpCircle } from 'lucide-react';
import portalImage from "./assets/portal.png";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
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
      {/* LADO IZQUIERDO: Formulario */}
      <div style={styles.left}>
        <div style={styles.formBox}>
          <h1 style={styles.logo}>TecBook</h1>
          <h2 style={styles.title}>Bienvenido</h2>
          <p style={styles.subtitle}>
            Inicia sesión con tus credenciales institucionales
          </p>

          <form onSubmit={handleLogin} style={styles.form}>
            <div style={styles.inputContainer}>
              <Mail size={20} color="#005DAB" style={styles.inputIcon} />
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
              <Lock size={20} color="#005DAB" style={styles.inputIcon} />
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Contraseña"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                style={{...styles.input, paddingRight: '40px'}}
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                style={styles.passwordToggle}
              >
                {showPassword ? 
                  <EyeOff size={20} color="#005DAB" /> : 
                  <Eye size={20} color="#005DAB" />
                }
              </button>
            </div>

            <div style={styles.forgotPasswordContainer}>
              <Link to="/recuperar" style={styles.forgotPasswordLink}>
                ¿Olvidaste tu contraseña?
              </Link>
            </div>

            <button type="submit" style={styles.button}>
              <span style={styles.buttonContent}>
                Ingresar
                <ArrowRight size={18} />
              </span>
            </button>
          </form>

          <Link to="/register" style={styles.registerButton}>
            ¿No tienes cuenta? <span style={styles.registerHighlight}>Regístrate aquí</span>
          </Link>

          <div style={styles.dividerContainer}>
            <div style={styles.divider}></div>
            <span style={styles.dividerText}>o continúa con</span>
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

          <div style={styles.helpLinks}>
            <span style={styles.helpText}>¿Necesitas ayuda?</span>
            <a href="#" style={styles.link}>
              <HelpCircle size={16} />
              Contactar soporte
            </a>
          </div>
        </div>
      </div>

      {/* LADO DERECHO: Imagen */}
      <div style={styles.right}>
        <div style={{
          ...styles.rightBackground,
          backgroundImage: `url(${portalImage})`,
        }}></div>
        <div style={styles.overlay}></div>
        <div style={styles.infoCard}>
          <h3 style={styles.infoTitle}>Portal Académico</h3>
          <p style={styles.infoText}>
            Accede a todos tus recursos educativos, calificaciones y material de
            estudio en un solo lugar.
          </p>
          <div style={styles.infoFeatures}>
            <div style={styles.infoFeature}>
              <CheckCircle size={20} color="#005DAB" />
              <span>Material de estudio actualizado</span>
            </div>
            <div style={styles.infoFeature}>
              <CheckCircle size={20} color="#005DAB" />
              <span>Seguimiento de calificaciones</span>
            </div>
            <div style={styles.infoFeature}>
              <CheckCircle size={20} color="#005DAB" />
              <span>Comunicación con docentes</span>
            </div>
          </div>
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
    background: "#f5f7fa",
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
      "linear-gradient(135deg, rgba(0, 93, 171, 0.5) 0%, rgba(0, 93, 171, 0.2) 70%)",
  },
  formBox: {
    width: "100%",
    maxWidth: "450px",
    background: "#fff",
    borderRadius: "16px",
    boxShadow: "0 8px 30px rgba(0,0,0,0.12)",
    padding: "40px 35px",
    textAlign: "center",
  },
  logo: {
    fontSize: "42px",
    color: "#005DAB",
    fontWeight: "bold",
    marginBottom: "8px",
    letterSpacing: "-0.5px",
  },
  title: {
    fontSize: "24px",
    marginBottom: "8px",
    color: "#333",
    fontWeight: "600",
  },
  subtitle: {
    color: "#666",
    marginBottom: "30px",
    fontSize: "16px",
  },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "15px",
  },
  inputContainer: {
    position: "relative",
    width: "100%",
  },
  inputIcon: {
    position: "absolute",
    left: "15px",
    top: "50%",
    transform: "translateY(-50%)",
  },
  input: {
    width: "100%",
    padding: "15px 15px 15px 45px",
    fontSize: "15px",
    border: "1px solid #e0e6ed",
    borderRadius: "10px",
    outline: "none",
    transition: "all 0.2s ease",
    boxSizing: "border-box",
    backgroundColor: "#f9fafc",
    color: "#333",
  },
  passwordToggle: {
    position: "absolute",
    right: "15px",
    top: "50%",
    transform: "translateY(-50%)",
    background: "none",
    border: "none",
    cursor: "pointer",
    padding: 0,
  },
  forgotPasswordContainer: {
    display: "flex",
    justifyContent: "flex-end",
    width: "100%",
    marginTop: "5px",
  },
  forgotPasswordLink: {
    color: "#666",
    textDecoration: "none",
    fontSize: "14px",
    fontWeight: "500",
    transition: "color 0.2s ease",
    "&:hover": {
      color: "#005DAB",
    },
  },
  button: {
    backgroundColor: "#005DAB",
    color: "white",
    padding: "15px",
    fontSize: "16px",
    border: "none",
    borderRadius: "10px",
    cursor: "pointer",
    fontWeight: "500",
    transition: "background-color 0.2s ease",
    boxShadow: "0 4px 6px rgba(0,93,171,0.15)",
    marginTop: "5px",
    width: "100%",
  },
  buttonContent: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: "8px",
  },
  registerButton: {
    display: "block",
    marginTop: "20px",
    color: "#555",
    textDecoration: "none",
    fontWeight: "500",
    fontSize: "15px",
    transition: "color 0.2s",
    textAlign: "center",
    cursor: "pointer",
  },
  registerHighlight: {
    color: "#005DAB",
    fontWeight: "600",
  },
  dividerContainer: {
    display: "flex",
    alignItems: "center",
    margin: "25px 0 20px",
    width: "100%",
  },
  divider: {
    flex: 1,
    height: "1px",
    backgroundColor: "#e0e6ed",
  },
  dividerText: {
    padding: "0 15px",
    color: "#666",
    fontSize: "14px",
    fontWeight: "500",
  },
  googleButton: {
    display: "flex",
    alignItems: "center",
    backgroundColor: "white",
    border: "1px solid #e0e6ed",
    borderRadius: "10px",
    boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
    color: "#3c4043",
    cursor: "pointer",
    fontFamily: "'Segoe UI', Arial, sans-serif",
    fontSize: "15px",
    fontWeight: "500",
    height: "50px",
    justifyContent: "center",
    padding: "0 15px",
    position: "relative",
    transition: "all 0.2s ease",
    width: "100%",
  },
  googleButtonText: {
    paddingLeft: "35px",
    textAlign: "center",
    color: "#444",
    fontSize: "15px",
    fontWeight: "500",
  },
  googleIconWrapper: {
    position: "absolute",
    left: "15px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  googleIcon: {
    width: "20px",
    height: "20px",
  },
  helpLinks: {
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    gap: "8px",
    marginTop: "25px",
  },
  helpText: {
    color: "#666",
    fontSize: "14px",
  },
  link: {
    color: "#005DAB",
    textDecoration: "none",
    fontSize: "15px",
    fontWeight: "500",
    cursor: "pointer",
    transition: "color 0.2s ease",
    display: "flex",
    alignItems: "center",
    gap: "5px",
  },
  infoCard: {
    position: "absolute",
    bottom: "50px",
    left: "50px",
    backgroundColor: "rgba(255, 255, 255, 0.9)",
    padding: "25px",
    borderRadius: "14px",
    backdropFilter: "blur(10px)",
    maxWidth: "320px",
    boxShadow: "0 10px 25px rgba(0, 0, 0, 0.1)",
  },
  infoTitle: {
    fontSize: "22px",
    color: "#005DAB",
    fontWeight: "bold",
    marginBottom: "10px",
  },
  infoText: {
    color: "#2d3748",
    fontSize: "15px",
    lineHeight: "1.6",
    marginBottom: "20px",
  },
  infoFeatures: {
    display: "flex",
    flexDirection: "column",
    gap: "12px",
  },
  infoFeature: {
    display: "flex",
    alignItems: "center",
    gap: "10px",
    fontSize: "15px",
    color: "#555",
  },
};

export default Login;