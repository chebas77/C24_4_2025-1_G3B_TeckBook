import { useState } from "react";
import portalImage from "./assets/portal.png"; // usa la misma imagen del login

function RecuperarCuenta() {
  const [correo, setCorreo] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    alert("Enlace de recuperación enviado al correo institucional");
  };

  return (
    <div style={styles.wrapper}>
      {/* LADO IZQUIERDO */}
      <div style={styles.left}>
        <div style={styles.formBox}>
          <h1 style={styles.logo}>TecBook</h1>
          <p style={styles.instruction}>
            Ingresa tu correo institucional y te enviaremos un enlace para recuperar tu acceso.
          </p>
          <form onSubmit={handleSubmit} style={styles.form}>
            <input
              type="email"
              placeholder="Correo institucional"
              value={correo}
              onChange={(e) => setCorreo(e.target.value)}
              required
              style={styles.input}
            />
            <button type="submit" style={styles.button}>
              Enviar enlace
            </button>
          </form>
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
          <h3 style={styles.infoTitle}>¿Olvidaste tu acceso?</h3>
          <p style={styles.infoText}>
            Te ayudaremos a recuperar tu cuenta para que sigas aprendiendo con TecBook.
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
  formBox: {
    width: "100%",
    maxWidth: "400px",
    textAlign: "center",
  },
  logo: {
    fontSize: "36px",
    color: "#005DAB",
    fontWeight: "bold",
    marginBottom: "20px",
  },
  instruction: {
    color: "#666",
    marginBottom: "25px",
    fontSize: "15px",
  },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "15px",
  },
  input: {
    padding: "14px",
    fontSize: "15px",
    border: "1px solid #ccc",
    borderRadius: "8px",
    outline: "none",
  },
  button: {
    padding: "14px",
    fontSize: "15px",
    backgroundColor: "#005DAB",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontWeight: "bold",
    boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
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
    background: "linear-gradient(135deg, rgba(0, 93, 171, 0.3) 0%, rgba(0, 93, 171, 0) 70%)",
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
    fontSize: "14px",
  },
};

export default RecuperarCuenta;
