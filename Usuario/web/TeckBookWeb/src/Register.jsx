import React, { useState } from 'react';
import portalImage from "./assets/portal.png";

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
      } else {
        const errorText = await response.text();
        alert('Error al registrar usuario: ' + errorText);
      }
    } catch (err) {
      alert('Error de conexión: ' + err.message);
    }
  };

  return (
    <div style={styles.wrapper}>
      {/* LADO IZQUIERDO: Formulario */}
      <div style={styles.left}>
        <div style={styles.formBox}>
          <h1 style={styles.logo}>TecBook</h1>
          <h2 style={styles.title}>Registro de Usuario</h2>
          <form onSubmit={handleRegister} style={styles.form}>
            <input type="text" placeholder="Nombre" value={nombre} onChange={e => setNombre(e.target.value)} style={styles.input} required />
            <input type="text" placeholder="Apellidos" value={apellidos} onChange={e => setApellidos(e.target.value)} style={styles.input} required />
            <input type="text" placeholder="Código" value={codigo} onChange={e => setCodigo(e.target.value)} style={styles.input} required />
            <input type="email" placeholder="Correo Institucional" value={correoInstitucional} onChange={e => setCorreoInstitucional(e.target.value)} style={styles.input} required />
            <input type="password" placeholder="Contraseña" value={contrasena} onChange={e => setContrasena(e.target.value)} style={styles.input} required />
            <input type="number" placeholder="Ciclo" value={ciclo} onChange={e => setCiclo(e.target.value)} style={styles.input} />
            <input type="text" placeholder="Rol" value={rol} onChange={e => setRol(e.target.value)} style={styles.input} />
            <input type="number" placeholder="Departamento ID" value={departamentoId} onChange={e => setDepartamentoId(e.target.value)} style={styles.input} />
            <input type="number" placeholder="Carrera ID" value={carreraId} onChange={e => setCarreraId(e.target.value)} style={styles.input} />
            <input type="number" placeholder="Sección ID" value={seccionId} onChange={e => setSeccionId(e.target.value)} style={styles.input} />
            <input type="password" placeholder="Password (opcional)" value={password} onChange={e => setPassword(e.target.value)} style={styles.input} />
            <input type="email" placeholder="Email (opcional)" value={email} onChange={e => setEmail(e.target.value)} style={styles.input} />
            <button type="submit" style={styles.button}>Registrarse</button>
          </form>
        </div>
      </div>
      {/* LADO DERECHO: Imagen */}
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
    background: "#fff",
    borderRadius: "12px",
    boxShadow: "0 4px 20px rgba(0,0,0,0.10)",
    padding: "40px 30px",
    textAlign: "center"
  },
  logo: {
    fontSize: "40px",
    color: "#005DAB",
    fontWeight: "bold",
    marginBottom: "5px",
    letterSpacing: "-0.5px",
  },
  title: {
    fontSize: "22px",
    marginBottom: "20px",
    color: "#333",
    fontWeight: "600",
  },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "12px",
  },
  input: {
    width: "100%",
    padding: "12px 15px",
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
    marginTop: "10px"
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
};

export default Register;
