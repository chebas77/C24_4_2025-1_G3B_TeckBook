import React, { useState } from 'react';
import portalImage from "./assets/portal.png";
import { Eye, EyeOff, User, Mail, Lock, Book, BookOpen, School, Users, Database } from 'lucide-react';
import { useNavigate } from "react-router-dom";
import './Register.css';

function Register() {
  const [nombre, setNombre] = useState('');
  const [apellidos, setApellidos] = useState('');
  const [codigo, setCodigo] = useState('');
  const [correoInstitucional, setCorreoInstitucional] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [ciclo, setCiclo] = useState('');
  const [rol, setRol] = useState('');
  const [departamentoId, setDepartamentoId] = useState('');
  const [carreraId, setCarreraId] = useState('');
  const [seccionId, setSeccionId] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [activeStep, setActiveStep] = useState(1);

  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    const usuario = {
      nombre,
      apellidos,
      codigo,
      correoInstitucional,
      contrasena,
      ciclo: ciclo ? parseInt(ciclo) : null,
      rol,
      departamentoId: departamentoId ? parseInt(departamentoId) : null,
      carreraId: carreraId ? parseInt(carreraId) : null,
      seccionId: seccionId ? parseInt(seccionId) : null,
      password,
      email
    };
    try {
      const response = await fetch('http://localhost:8080/api/usuarios/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(usuario)
      });
      if (response.ok) {
        alert('Usuario registrado correctamente');
        navigate('/home');
      } else {
        const errorText = await response.text();
        alert('Error al registrar usuario: ' + errorText);
      }
    } catch (err) {
      alert('Error de conexión: ' + err.message);
    }
  };

  const nextStep = () => {
    setActiveStep(activeStep + 1);
  };

  const prevStep = () => {
    setActiveStep(activeStep - 1);
  };

  return (
    <div className="wrapper">
      {/* LADO IZQUIERDO: Formulario */}
      <div className="left">
        <div className="form-box">
          <h1 className="logo">TecBook</h1>
          <h2 className="title">Registro de Usuario</h2>
          
          {activeStep === 1 && (
            <div className="form">
              <div className="input-container">
                <User size={20} color="#005DAB" className="input-icon" />
                <input 
                  type="text" 
                  placeholder="Nombre" 
                  value={nombre} 
                  onChange={e => setNombre(e.target.value)} 
                  className="input" 
                  required 
                />
              </div>
              
              <div className="input-container">
                <User size={20} color="#005DAB" className="input-icon" />
                <input 
                  type="text" 
                  placeholder="Apellidos" 
                  value={apellidos} 
                  onChange={e => setApellidos(e.target.value)} 
                  className="input" 
                  required 
                />
              </div>
              
              <div className="input-container">
                <Database size={20} color="#005DAB" className="input-icon" />
                <input 
                  type="text" 
                  placeholder="Código" 
                  value={codigo} 
                  onChange={e => setCodigo(e.target.value)} 
                  className="input" 
                  required 
                />
              </div>
              
              <div className="input-container">
                <Mail size={20} color="#005DAB" className="input-icon" />
                <input 
                  type="email" 
                  placeholder="Correo Institucional" 
                  value={correoInstitucional} 
                  onChange={e => setCorreoInstitucional(e.target.value)} 
                  className="input" 
                  required 
                />
              </div>
              
              <div className="input-container">
                <Lock size={20} color="#005DAB" className="input-icon" />
                <input 
                  type={showPassword ? "text" : "password"} 
                  placeholder="Contraseña" 
                  value={contrasena} 
                  onChange={e => setContrasena(e.target.value)} 
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
              
              <button onClick={nextStep} className="button">
                Siguiente
              </button>
            </div>
          )}
          
          {activeStep === 2 && (
            <div className="form">
              <div className="input-container">
                <BookOpen size={20} color="#005DAB" className="input-icon" />
                <input 
                  type="number" 
                  placeholder="Ciclo" 
                  value={ciclo} 
                  onChange={e => setCiclo(e.target.value)} 
                  className="input" 
                />
              </div>
              
              <div className="input-container">
                <Users size={20} color="#005DAB" className="input-icon" />
                <input 
                  type="text" 
                  placeholder="Rol" 
                  value={rol} 
                  onChange={e => setRol(e.target.value)} 
                  className="input" 
                />
              </div>
              
              <div className="input-container">
                <School size={20} color="#005DAB" className="input-icon" />
                <input 
                  type="number" 
                  placeholder="Departamento ID" 
                  value={departamentoId} 
                  onChange={e => setDepartamentoId(e.target.value)} 
                  className="input" 
                />
              </div>
              
              <div className="input-container">
                <Book size={20} color="#005DAB" className="input-icon" />
                <input 
                  type="number" 
                  placeholder="Carrera ID" 
                  value={carreraId} 
                  onChange={e => setCarreraId(e.target.value)} 
                  className="input" 
                />
              </div>
              
              <div className="input-container">
                <Users size={20} color="#005DAB" className="input-icon" />
                <input 
                  type="number" 
                  placeholder="Sección ID" 
                  value={seccionId} 
                  onChange={e => setSeccionId(e.target.value)} 
                  className="input" 
                />
              </div>
              
              <div className="button-group">
                <button onClick={prevStep} className="secondary-button">
                  Atrás
                </button>
                <button onClick={nextStep} className="button">
                  Siguiente
                </button>
              </div>
            </div>
          )}
          
          {activeStep === 3 && (
            <div className="form">
              <div className="input-container">
                <Mail size={20} color="#005DAB" className="input-icon" />
                <input 
                  type="email" 
                  placeholder="Email (opcional)" 
                  value={email} 
                  onChange={e => setEmail(e.target.value)} 
                  className="input" 
                />
              </div>
              
              <div className="input-container">
                <Lock size={20} color="#005DAB" className="input-icon" />
                <input 
                  type="password" 
                  placeholder="Password (opcional)" 
                  value={password} 
                  onChange={e => setPassword(e.target.value)} 
                  className="input" 
                />
              </div>
              
              <div className="button-group">
                <button onClick={prevStep} className="secondary-button">
                  Atrás
                </button>
                <button onClick={handleRegister} className="button">
                  Completar Registro
                </button>
              </div>
            </div>
          )}
          
          <div className="step-indicator">
            <div className={activeStep === 1 ? 'active-step' : 'step'}></div>
            <div className={activeStep === 2 ? 'active-step' : 'step'}></div>
            <div className={activeStep === 3 ? 'active-step' : 'step'}></div>
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
        </div>
      </div>
    </div>
  );
}

export default Register;