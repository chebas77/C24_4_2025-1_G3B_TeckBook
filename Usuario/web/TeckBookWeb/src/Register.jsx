import React, { useState } from 'react';
import portalImage from "./assets/portal.png";
import { Eye, EyeOff, User, Mail, Lock, Book, BookOpen, School, Users, Database } from 'lucide-react';

import { useNavigate } from "react-router-dom";

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

  const navigate = useNavigate();  // <---- aquí

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
        navigate('/home');  // <---- redirige a /home
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
    <div style={styles.wrapper}>
      {/* LADO IZQUIERDO: Formulario */}
      <div style={styles.left}>
        <div style={styles.formBox}>
          <h1 style={styles.logo}>TecBook</h1>
          <h2 style={styles.title}>Registro de Usuario</h2>
          
          {activeStep === 1 && (
            <div style={styles.form}>
              <div style={styles.inputContainer}>
                <User size={20} color="#005DAB" style={styles.inputIcon} />
                <input 
                  type="text" 
                  placeholder="Nombre" 
                  value={nombre} 
                  onChange={e => setNombre(e.target.value)} 
                  style={styles.input} 
                  required 
                />
              </div>
              
              <div style={styles.inputContainer}>
                <User size={20} color="#005DAB" style={styles.inputIcon} />
                <input 
                  type="text" 
                  placeholder="Apellidos" 
                  value={apellidos} 
                  onChange={e => setApellidos(e.target.value)} 
                  style={styles.input} 
                  required 
                />
              </div>
              
              <div style={styles.inputContainer}>
                <Database size={20} color="#005DAB" style={styles.inputIcon} />
                <input 
                  type="text" 
                  placeholder="Código" 
                  value={codigo} 
                  onChange={e => setCodigo(e.target.value)} 
                  style={styles.input} 
                  required 
                />
              </div>
              
              <div style={styles.inputContainer}>
                <Mail size={20} color="#005DAB" style={styles.inputIcon} />
                <input 
                  type="email" 
                  placeholder="Correo Institucional" 
                  value={correoInstitucional} 
                  onChange={e => setCorreoInstitucional(e.target.value)} 
                  style={styles.input} 
                  required 
                />
              </div>
              
              <div style={styles.inputContainer}>
                <Lock size={20} color="#005DAB" style={styles.inputIcon} />
                <input 
                  type={showPassword ? "text" : "password"} 
                  placeholder="Contraseña" 
                  value={contrasena} 
                  onChange={e => setContrasena(e.target.value)} 
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
              
              <button onClick={nextStep} style={styles.button}>
                Siguiente
              </button>
            </div>
          )}
          
          {activeStep === 2 && (
            <div style={styles.form}>
              <div style={styles.inputContainer}>
                <BookOpen size={20} color="#005DAB" style={styles.inputIcon} />
                <input 
                  type="number" 
                  placeholder="Ciclo" 
                  value={ciclo} 
                  onChange={e => setCiclo(e.target.value)} 
                  style={styles.input} 
                />
              </div>
              
              <div style={styles.inputContainer}>
                <Users size={20} color="#005DAB" style={styles.inputIcon} />
                <input 
                  type="text" 
                  placeholder="Rol" 
                  value={rol} 
                  onChange={e => setRol(e.target.value)} 
                  style={styles.input} 
                />
              </div>
              
              <div style={styles.inputContainer}>
                <School size={20} color="#005DAB" style={styles.inputIcon} />
                <input 
                  type="number" 
                  placeholder="Departamento ID" 
                  value={departamentoId} 
                  onChange={e => setDepartamentoId(e.target.value)} 
                  style={styles.input} 
                />
              </div>
              
              <div style={styles.inputContainer}>
                <Book size={20} color="#005DAB" style={styles.inputIcon} />
                <input 
                  type="number" 
                  placeholder="Carrera ID" 
                  value={carreraId} 
                  onChange={e => setCarreraId(e.target.value)} 
                  style={styles.input} 
                />
              </div>
              
              <div style={styles.inputContainer}>
                <Users size={20} color="#005DAB" style={styles.inputIcon} />
                <input 
                  type="number" 
                  placeholder="Sección ID" 
                  value={seccionId} 
                  onChange={e => setSeccionId(e.target.value)} 
                  style={styles.input} 
                />
              </div>
              
              <div style={styles.buttonGroup}>
                <button onClick={prevStep} style={styles.secondaryButton}>
                  Atrás
                </button>
                <button onClick={nextStep} style={styles.button}>
                  Siguiente
                </button>
              </div>
            </div>
          )}
          
          {activeStep === 3 && (
            <div style={styles.form}>
              <div style={styles.inputContainer}>
                <Mail size={20} color="#005DAB" style={styles.inputIcon} />
                <input 
                  type="email" 
                  placeholder="Email (opcional)" 
                  value={email} 
                  onChange={e => setEmail(e.target.value)} 
                  style={styles.input} 
                />
              </div>
              
              <div style={styles.inputContainer}>
                <Lock size={20} color="#005DAB" style={styles.inputIcon} />
                <input 
                  type="password" 
                  placeholder="Password (opcional)" 
                  value={password} 
                  onChange={e => setPassword(e.target.value)} 
                  style={styles.input} 
                />
              </div>
              
              <div style={styles.buttonGroup}>
      <button onClick={prevStep} style={styles.secondaryButton}>
        Atrás
      </button>
      <button onClick={handleRegister} style={styles.button}>
        Completar Registro
      </button>
    </div>
            </div>
          )}
          
          <div style={styles.stepIndicator}>
            <div style={activeStep === 1 ? styles.activeStep : styles.step}></div>
            <div style={activeStep === 2 ? styles.activeStep : styles.step}></div>
            <div style={activeStep === 3 ? styles.activeStep : styles.step}></div>
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
    textAlign: "center"
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
    marginBottom: "30px",
    color: "#333",
    fontWeight: "600",
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
    marginRight: "10px",
   

    
  },
  secondaryButton: {
    backgroundColor: "#e0e6ed",
    color: "#4a5568",
    padding: "15px",
    fontSize: "16px",
    border: "none",
    borderRadius: "10px",
    cursor: "pointer",
    fontWeight: "500",
    transition: "background-color 0.2s ease",
    boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
    width: "100%",
    marginTop: "5px",
    marginRight: "10px",
    
  },
  buttonGroup: {
    display: "flex",
    justifyContent: "space-between",
    marginTop: "5px",
  },
  stepIndicator: {
    display: "flex",
    justifyContent: "center",
    gap: "8px",
    marginTop: "25px",
  },
  step: {
    width: "8px",
    height: "8px",
    borderRadius: "50%",
    backgroundColor: "#e0e6ed",
  },
  activeStep: {
    width: "8px",
    height: "8px",
    borderRadius: "50%",
    backgroundColor: "#005DAB",
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
  },
};

export default Register;