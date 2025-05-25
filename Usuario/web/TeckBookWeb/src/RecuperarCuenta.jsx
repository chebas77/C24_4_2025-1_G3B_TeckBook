import { useState } from "react";
import portalImage from "./assets/portal.png";
import './RecuperarCuenta.css';

function RecuperarCuenta() {
  const [correo, setCorreo] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    alert("Enlace de recuperación enviado al correo institucional");
  };

  return (
    <div className="wrapper">
      {/* LADO IZQUIERDO */}
      <div className="left">
        <div className="form-box">
          <h1 className="logo">TecBook</h1>
          <p className="instruction">
            Ingresa tu correo institucional y te enviaremos un enlace para recuperar tu acceso.
          </p>
          <form onSubmit={handleSubmit} className="form">
            <input
              type="email"
              placeholder="Correo institucional"
              value={correo}
              onChange={(e) => setCorreo(e.target.value)}
              required
              className="input"
            />
            <button type="submit" className="button">
              Enviar enlace
            </button>
          </form>
        </div>
      </div>

      {/* LADO DERECHO */}
      <div className="right">
        <div className="right-background" style={{backgroundImage: `url(${portalImage})`}}></div>
        <div className="overlay"></div>
        <div className="info-card">
          <h3 className="info-title">¿Olvidaste tu acceso?</h3>
          <p className="info-text">
            Te ayudaremos a recuperar tu cuenta para que sigas aprendiendo con TecBook.
          </p>
        </div>
      </div>
    </div>
  );
}

export default RecuperarCuenta;