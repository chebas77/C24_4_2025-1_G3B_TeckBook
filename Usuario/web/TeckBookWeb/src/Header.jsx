import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './Header.css';

function Header() {
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = async () => {
    try {
      const token = localStorage.getItem('token');
      
      if (token) {
        console.log("Cerrando sesión en el backend...");
        
        // Llamada al backend para invalidar token
        const response = await fetch('http://localhost:8080/api/auth/logout', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
        
        if (response.ok) {
          const data = await response.json();
          console.log("✅ Sesión cerrada en el backend:", data);
        } else {
          console.warn("⚠️ Error al cerrar sesión en backend, pero continuando logout");
        }
      }
      
      // Limpiar frontend siempre
      localStorage.removeItem('token');
      console.log("✅ Token eliminado del localStorage");
      
      // Redireccionar al login
      navigate('/');
      
    } catch (error) {
      console.error("❌ Error durante logout:", error);
      
      // Limpiar frontend aunque falle el backend
      localStorage.removeItem('token');
      navigate('/');
    }
  };

  // Función para determinar si un link está activo
  const isActive = (path) => location.pathname === path;

  return (
    <header className="app-header">
      <nav className="navbar navbar-expand-lg">
        <div className="container-fluid px-4">
          {/* Logo */}
          <div className="navbar-brand">
            <h1 className="header-logo">TecBook</h1>
          </div>

          {/* Toggle button para móvil */}
          <button 
            className="navbar-toggler" 
            type="button" 
            data-bs-toggle="collapse" 
            data-bs-target="#navbarNav"
            aria-controls="navbarNav" 
            aria-expanded="false" 
            aria-label="Toggle navigation"
          >
            <span className="navbar-toggler-icon"></span>
          </button>

          {/* Navigation */}
          <div className="collapse navbar-collapse" id="navbarNav">
            <ul className="navbar-nav ms-auto">
              <li className="nav-item">
                <button 
                  onClick={() => navigate('/home')} 
                  className={`nav-link-custom ${isActive('/home') ? 'active' : ''}`}
                >
                  <i className="bi bi-house-door me-2"></i>
                  Inicio
                </button>
              </li>
              
              <li className="nav-item">
                <button 
                  onClick={() => navigate('/perfil')} 
                  className={`nav-link-custom ${isActive('/perfil') ? 'active' : ''}`}
                >
                  <i className="bi bi-person me-2"></i>
                  Perfil
                </button>
              </li>
              
              <li className="nav-item">
                <button 
                  onClick={() => navigate('/cursos')} 
                  className={`nav-link-custom ${isActive('/cursos') ? 'active' : ''}`}
                >
                  <i className="bi bi-book me-2"></i>
                  Cursos
                </button>
              </li>
              
              <li className="nav-item">
                <button 
                  onClick={handleLogout} 
                  className="nav-link-custom logout-btn"
                >
                  <i className="bi bi-box-arrow-right me-2"></i>
                  Cerrar sesión
                </button>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    </header>
  );
}

export default Header;