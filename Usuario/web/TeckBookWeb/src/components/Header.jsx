import { NavLink, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { Bell } from 'lucide-react';
import InvitacionesPendientes from './InvitacionesPendientes';
import './Header.css';
import { ENDPOINTS, ROUTES } from '../config/apiConfig';
import apiService from '../services/apiService';

function Header() {
  const navigate = useNavigate();
  const [userData, setUserData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [showInvitaciones, setShowInvitaciones] = useState(false);

  useEffect(() => {
    fetchUserData();
  }, []);

const fetchUserData = async () => {
  try {
    const data = await apiService.get(ENDPOINTS.AUTH.GET_USER);
    setUserData(data);
  } catch (error) {
    console.error('Error al obtener datos del usuario:', error);
  } finally {
    setIsLoading(false);
  }
};

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate(ROUTES.PUBLIC.LOGIN);
  };

  const handleAulaAceptada = () => {
    // Refrescar la página o actualizar el estado según sea necesario
    window.location.reload();
  };

  const isProfesor = () => {
    return userData?.rol === 'PROFESOR' || userData?.rol === 'profesor';
  };

  if (isLoading) {
    return (
      <header className="header-wrapper">
        <div className="header-left">
          <h1 className="header-logo">TecBook</h1>
        </div>
        <nav className="header-nav">
          <div className="header-loading">Cargando...</div>
        </nav>
      </header>
    );
  }

  return (
    <>
      <header className="header-wrapper">
        <div className="header-left">
          <h1 className="header-logo">TecBook</h1>
        </div>
        <nav className="header-nav">
          <NavLink to={ROUTES.PROTECTED.DASHBOARD} className={({ isActive }) => isActive ? 'header-link active' : 'header-link'}>
            Inicio
          </NavLink>
          <NavLink to={ROUTES.PROTECTED.PROFILE} className={({ isActive }) => isActive ? 'header-link active' : 'header-link'}>
            Perfil
          </NavLink>
          <NavLink to={ROUTES.PROTECTED.AULAS} className={({ isActive }) => isActive ? 'header-link active' : 'header-link'}>
            Aulas
          </NavLink>
          {/* MOSTRAR "CREAR AULA" SOLO PARA PROFESORES */}
          {isProfesor() && (
            <NavLink to={ROUTES.PROTECTED.CREATE_AULA} className={({ isActive }) => isActive ? 'header-link active' : 'header-link'}>
              Crear Aula
            </NavLink>
          )}
          {/* ENLACE A ANUNCIOS GENERALES */}
          <NavLink to={ROUTES.PROTECTED.ANNOUNCEMENTS} className={({ isActive }) => isActive ? 'header-link active' : 'header-link'}>
            Anuncios Generales
          </NavLink>
          {/* BOTÓN DE INVITACIONES GLOBAL */}
          <button 
            onClick={() => setShowInvitaciones(true)} 
            className="header-invitations-btn"
            title="Ver invitaciones pendientes"
          >
            <Bell size={16} />
            Invitaciones
          </button>
          <button onClick={handleLogout} className="header-logout">
            Cerrar sesión
          </button>
        </nav>
      </header>

      {/* MODAL DE INVITACIONES GLOBAL */}
      <InvitacionesPendientes
        isOpen={showInvitaciones}
        onClose={() => setShowInvitaciones(false)}
        onAulaAceptada={handleAulaAceptada}
      />
    </>
  );
}

export default Header;