// Usuario/web/TeckBookWeb/src/Register.jsx - CON IMAGEN LATERAL
import React, { useState, useEffect } from 'react';
import { Eye, EyeOff, User, Mail, Lock, BookOpen, Database, GraduationCap, ArrowRight, AlertCircle, CheckCircle } from 'lucide-react';
import { useNavigate } from "react-router-dom";
import portalImage from "../assets/portal.png";
import "../css/Register.css";

function Register() {
  const [formData, setFormData] = useState({
    nombre: '',
    apellidos: '',
    codigo: '',
    correoInstitucional: '',
    password: '',
    ciclo: '',
    carreraId: ''
  });
  
  const [carreras, setCarreras] = useState([]);
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingCarreras, setIsLoadingCarreras] = useState(true);
  const navigate = useNavigate();

  // Cargar carreras al montar el componente
  useEffect(() => {
    fetchCarreras();
  }, []);

  const fetchCarreras = async () => {
    try {
      setIsLoadingCarreras(true);
      console.log('Obteniendo carreras desde el backend...');
      
      const response = await fetch('http://localhost:8080/api/carreras/activas');
      
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Carreras obtenidas:', data);
      
      if (data.carreras && Array.isArray(data.carreras)) {
        setCarreras(data.carreras);
        console.log(`Se cargaron ${data.carreras.length} carreras`);
      } else {
        console.warn('Respuesta de carreras no tiene el formato esperado:', data);
        setCarreras([]);
      }
      
    } catch (error) {
      console.error('Error al obtener carreras:', error);
      setError('Error al cargar las carreras. Por favor, recarga la página.');
      
      // Fallback con carreras predeterminadas
      setCarreras([
        { id: 1, nombre: 'Desarrollo de Software' },
        { id: 2, nombre: 'Administración de Redes y Comunicaciones' },
        { id: 3, nombre: 'Electrónica y Automatización Industrial' }
      ]);
    } finally {
      setIsLoadingCarreras(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Limpiar error cuando el usuario empiece a escribir
    if (error) {
      setError('');
    }
  };

  const validateForm = () => {
    const { nombre, apellidos, codigo, correoInstitucional, password, ciclo, carreraId } = formData;
    
    if (!nombre.trim()) return 'El nombre es requerido';
    if (!apellidos.trim()) return 'Los apellidos son requeridos';
    if (!codigo.trim()) return 'El código de estudiante es requerido';
    if (!correoInstitucional.trim()) return 'El correo institucional es requerido';
    if (!correoInstitucional.endsWith('@tecsup.edu.pe')) {
      return 'Debe usar un correo institucional (@tecsup.edu.pe)';
    }
    if (!password.trim()) return 'La contraseña es requerida';
    if (password.length < 6) return 'La contraseña debe tener al menos 6 caracteres';
    if (!ciclo.trim()) return 'El ciclo es requerido';
    const cicloNum = parseInt(ciclo);
    if (isNaN(cicloNum) || cicloNum < 1 || cicloNum > 6) {
      return 'El ciclo debe ser un número entre 1 y 6';
    }
    if (!carreraId) return 'Debe seleccionar una carrera';
    
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      return;
    }
    
    setIsLoading(true);

    try {
      // Preparar datos para envío
      const usuarioData = {
        nombre: formData.nombre.trim(),
        apellidos: formData.apellidos.trim(),
        codigo: formData.codigo.trim(),
        correoInstitucional: formData.correoInstitucional.trim().toLowerCase(),
        password: formData.password,
        ciclo: formData.ciclo.trim(),
        rol: 'ESTUDIANTE',
        carreraId: parseInt(formData.carreraId),
        departamentoId: 1,
        seccionId: null,
        telefono: null,
        direccion: null,
        fechaNacimiento: null,
        profileImageUrl: null
      };

      console.log('Enviando datos de registro:', usuarioData);

      const response = await fetch('http://localhost:8080/api/usuarios/register', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json' 
        },
        body: JSON.stringify(usuarioData)
      });

      if (response.ok) {
        const result = await response.json();
        console.log('Usuario registrado exitosamente:', result);
        
        // Encontrar el nombre de la carrera seleccionada
        const carreraSeleccionada = carreras.find(c => c.id === parseInt(formData.carreraId));
        const nombreCarrera = carreraSeleccionada ? carreraSeleccionada.nombre : 'la carrera seleccionada';
        
        // Mostrar mensaje de éxito
        alert(`¡Registro exitoso! Bienvenido ${formData.nombre}.\nCarrera: ${nombreCarrera}\nYa puedes iniciar sesión.`);
        
        // Redireccionar al login
        navigate('/');
      } else {
        const errorText = await response.text();
        console.error('Error en el registro:', errorText);
        setError('Error al registrar usuario: ' + errorText);
      }
    } catch (err) {
      console.error('Error de conexión:', err);
      setError('Error de conexión con el servidor. Por favor, intenta nuevamente.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="wrapper">
      {/* LADO IZQUIERDO: Formulario */}
      <div className="left">
        <div className="form-box">
          <div className="header-section">
            <div className="logo-container">
              <GraduationCap size={40} color="#005DAB" />
              <h1 className="logo">TecBook</h1>
            </div>
            <h2 className="title">Crear Cuenta</h2>
            <p className="subtitle">Únete a la comunidad académica de Tecsup</p>
          </div>

          {error && (
            <div className="error-message">
              <AlertCircle size={16} style={{ marginRight: '8px', flexShrink: 0 }} />
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="form">
            {/* NOMBRE Y APELLIDOS */}
            <div className="form-row">
              <div className="input-container">
                <User size={18} color="#005DAB" className="input-icon" />
                <input
                  type="text"
                  name="nombre"
                  placeholder="Nombre"
                  value={formData.nombre}
                  onChange={handleChange}
                  required
                  className="input"
                />
              </div>
              <div className="input-container">
                <User size={18} color="#005DAB" className="input-icon" />
                <input
                  type="text"
                  name="apellidos"
                  placeholder="Apellidos"
                  value={formData.apellidos}
                  onChange={handleChange}
                  required
                  className="input"
                />
              </div>
            </div>

            {/* CÓDIGO Y CICLO */}
            <div className="form-row">
              <div className="input-container">
                <Database size={18} color="#005DAB" className="input-icon" />
                <input
                  type="text"
                  name="codigo"
                  placeholder="Código estudiante"
                  value={formData.codigo}
                  onChange={handleChange}
                  required
                  className="input"
                />
              </div>
              <div className="input-container">
                <BookOpen size={18} color="#005DAB" className="input-icon" />
                <input
                  type="number"
                  name="ciclo"
                  placeholder="Ciclo"
                  value={formData.ciclo}
                  onChange={handleChange}
                  min="1"
                  max="6"
                  required
                  className="input"
                />
              </div>
            </div>

            {/* CORREO INSTITUCIONAL */}
            <div className="input-container">
              <Mail size={18} color="#005DAB" className="input-icon" />
              <input
                type="email"
                name="correoInstitucional"
                placeholder="Correo institucional (@tecsup.edu.pe)"
                value={formData.correoInstitucional}
                onChange={handleChange}
                required
                className="input"
              />
            </div>

            {/* CARRERA */}
            <div className="input-container">
              <GraduationCap size={18} color="#005DAB" className="input-icon" />
              <select
                name="carreraId"
                value={formData.carreraId}
                onChange={handleChange}
                required
                className="input select"
                disabled={isLoadingCarreras}
              >
                <option value="">
                  {isLoadingCarreras ? 'Cargando carreras...' : 'Selecciona tu carrera'}
                </option>
                {carreras.map(carrera => (
                  <option key={carrera.id} value={carrera.id}>
                    {carrera.nombre}
                  </option>
                ))}
              </select>
              {isLoadingCarreras && (
                <div className="loading-spinner-small"></div>
              )}
            </div>

            {/* CONTRASEÑA */}
            <div className="input-container">
              <Lock size={18} color="#005DAB" className="input-icon" />
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                placeholder="Contraseña (mínimo 6 caracteres)"
                value={formData.password}
                onChange={handleChange}
                required
                className="input password-input"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="password-toggle"
              >
                {showPassword ? 
                  <EyeOff size={18} color="#005DAB" /> : 
                  <Eye size={18} color="#005DAB" />
                }
              </button>
            </div>

            {/* BOTÓN DE REGISTRO */}
            <button
              type="submit"
              disabled={isLoading || isLoadingCarreras}
              className="button"
            >
              <span className="button-content">
                {isLoading ? (
                  <>
                    <div className="loading-spinner" />
                    Registrando...
                  </>
                ) : (
                  <>
                    Crear cuenta
                    <ArrowRight size={16} />
                  </>
                )}
              </span>
            </button>

            {/* LINK A LOGIN */}
            <div className="login-link">
              <p>
                ¿Ya tienes cuenta?{' '}
                <button
                  type="button"
                  onClick={() => navigate('/')}
                  className="link-button"
                >
                  Inicia sesión aquí
                </button>
              </p>
            </div>
          </form>
        </div>
      </div>

      {/* LADO DERECHO: Imagen - RESTAURADO */}
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
              <span>Acceso a materiales de estudio</span>
            </div>
            <div className="info-feature">
              <CheckCircle size={20} color="#005DAB" />
              <span>Comunicación con docentes</span>
            </div>
            <div className="info-feature">
              <CheckCircle size={20} color="#005DAB" />
              <span>Seguimiento de calificaciones</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Register;