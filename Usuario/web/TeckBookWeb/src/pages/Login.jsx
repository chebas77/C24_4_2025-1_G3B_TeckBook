import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Eye, EyeOff, Mail, Lock, ArrowRight, CheckCircle, HelpCircle } from 'lucide-react';
import { ROUTES } from '../config/apiConfig';
import portalImage from "../assets/portal.png";
import '../css/Login.css';

// URL del backend desplegado en Koyeb
const API_BASE_URL = 'https://rival-terra-chebas77-e06d6aa9.koyeb.app';

function Login() {
  const [correoInstitucional, setCorreoInstitucional] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleGoogleLogin = () => {
    // Redirigir a la URL de autenticación de Google en Koyeb
    window.location.href = `${API_BASE_URL}/oauth2/authorize/google`;
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);
    
    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          // Agregar headers para CORS si es necesario
          "Accept": "application/json"
        },
        body: JSON.stringify({ 
          correoInstitucional: correoInstitucional, 
          password: password 
        }),
      });
      
      if (!response.ok) {
        const errorData = await response.text();
        throw new Error('Credenciales incorrectas');
      }

      const result = await response.json();
      
      if (result && result.token) {
        // Guardar el token en localStorage
        localStorage.setItem('token', result.token);
        
        // Mensaje de éxito
        alert("Inicio de sesión exitoso");
        navigate(ROUTES.PROTECTED.DASHBOARD);
      } else {
        setError('Respuesta del servidor inválida');
      }
    } catch (error) {
      console.error('Error de login:', error);
      setError(error.message || 'Error de conexión con el backend');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="wrapper">
      {/* LADO IZQUIERDO: Formulario */}
      <div className="left">
        <div className="form-box">
          <h1 className="logo">TecBook</h1>
          <h2 className="title">Bienvenido</h2>
          <p className="subtitle">
            Inicia sesión con tus credenciales institucionales
          </p>

          {error && (
            <div className="error-message">
              <p>{error}</p>
            </div>
          )}

          <form onSubmit={handleLogin} className="form">
            <div className="input-container">
              <Mail size={20} color="#005DAB" className="input-icon" />
              <input
                type="email"
                placeholder="Correo institucional"
                value={correoInstitucional}
                onChange={(e) => setCorreoInstitucional(e.target.value)}
                className="input"
                required
              />
            </div>

            <div className="input-container">
              <Lock size={20} color="#005DAB" className="input-icon" />
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Contraseña"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="input"
                style={{paddingRight: '40px'}}
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="password-toggle"
              >
                {showPassword ? 
                  <EyeOff size={20} color="#005DAB" /> : 
                  <Eye size={20} color="#005DAB" />
                }
              </button>
            </div>

            <div className="forgot-password-container">
              <Link to={ROUTES.PUBLIC.RECOVER_PASSWORD} className="forgot-password-link">
                ¿Olvidaste tu contraseña?
              </Link>
            </div>

            <button 
              type="submit" 
              className="button"
              disabled={isLoading}
            >
              <span className="button-content">
                {isLoading ? 'Ingresando...' : 'Ingresar'}
                <ArrowRight size={18} />
              </span>
            </button>
          </form>

          <Link to={ROUTES.PUBLIC.REGISTER} className="register-button">
            ¿No tienes cuenta? <span className="register-highlight">Regístrate aquí</span>
          </Link>

          <div className="divider-container">
            <div className="divider"></div>
            <span className="divider-text">o continúa con</span>
            <div className="divider"></div>
          </div>

          <button 
            onClick={handleGoogleLogin}
            className="google-button"
            type="button"
          >
            <div className="google-icon-wrapper">
              <svg className="google-icon" viewBox="0 0 24 24">
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
            <span className="google-button-text">
              Ingresa con tu correo de Tecsup
            </span>
          </button>

          <div className="help-links">
            <span className="help-text">¿Necesitas ayuda?</span>
            <a href="#" className="link">
              <HelpCircle size={16} />
              Contactar soporte
            </a>
          </div>
        </div>
      </div>

      {/* LADO DERECHO: Imagen */}
      <div className="right">
        <div className="right-background" style={{backgroundImage: `url(${portalImage})`}}></div>
        <div className="overlay"></div>
        <div className="info-card">
          <h3 className="info-title">Portal Académico</h3>
          <p className="info-text">
            Accede a todos tus recursos educativos, calificaciones y material de
            estudio en un solo lugar.
          </p>
          <div className="info-features">
            <div className="info-feature">
              <CheckCircle size={20} color="#005DAB" />
              <span>Material de estudio actualizado</span>
            </div>
            <div className="info-feature">
              <CheckCircle size={20} color="#005DAB" />
              <span>Seguimiento de calificaciones</span>
            </div>
            <div className="info-feature">
              <CheckCircle size={20} color="#005DAB" />
              <span>Comunicación con docentes</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;