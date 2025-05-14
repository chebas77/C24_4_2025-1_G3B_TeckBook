import { useState } from 'react';
import axios from 'axios';

function Login() {
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [mensaje, setMensaje] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      // Este endpoint se conectará a Spring Boot más adelante
      const response = await axios.post('http://localhost:8080/api/login', {
        correo: correo,
        contrasena: contrasena
      });

      if (response.data.success) {
        setMensaje('Inicio de sesión exitoso ✅');
      } else {
        setMensaje('Credenciales incorrectas ❌');
      }
    } catch (error) {
      console.error(error);
      setMensaje('Error al conectar con el servidor');
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.formBox}>
        <h2>Iniciar Sesión</h2>
        <form onSubmit={handleLogin} style={styles.form}>
          <input
            type="email"
            placeholder="Correo institucional"
            value={correo}
            onChange={(e) => setCorreo(e.target.value)}
            required
            style={styles.input}
          />
          <input
            type="password"
            placeholder="Contraseña"
            value={contrasena}
            onChange={(e) => setContrasena(e.target.value)}
            required
            style={styles.input}
          />
          <button type="submit" style={styles.button}>Ingresar</button>
        </form>
        {mensaje && <p style={styles.message}>{mensaje}</p>}
      </div>
    </div>
  );
}

const styles = {
  container: {
    height: '100vh',
    width: '100vw',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#1e1e1e', // para que combine con el fondo oscuro
    fontFamily: 'Arial'
  },
  formBox: {
    width: '300px',
    padding: '20px',
    border: '1px solid #ccc',
    borderRadius: '10px',
    textAlign: 'center',
    backgroundColor: '#2c2c2c',
    color: '#fff'
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '10px'
  },
  input: {
    padding: '10px',
    fontSize: '16px',
    backgroundColor: '#333',
    color: '#fff',
    border: '1px solid #555'
  },
  button: {
    padding: '10px',
    backgroundColor: '#4CAF50',
    color: 'white',
    fontSize: '16px',
    cursor: 'pointer',
    border: 'none'
  },
  message: {
    marginTop: '10px',
    color: '#d9534f'
  }
};

export default Login;
