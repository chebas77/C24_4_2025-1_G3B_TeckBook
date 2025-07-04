import { NavLink, useNavigate } from 'react-router-dom';
import './Header.css';

function Header() {
  const navigate = useNavigate();

 const handleLogout = () => {
  localStorage.removeItem('token');
  navigate('/', { replace: true }); // 👈 usa '/'
};

  return (
    <header className="header-wrapper">
      <div className="header-left">
        <h1 className="header-logo">TecBook</h1>
      </div>
      <nav className="header-nav">
        <NavLink to="/home" className={({ isActive }) => isActive ? 'header-link active' : 'header-link'}>Inicio</NavLink>
        <NavLink to="/perfil" className={({ isActive }) => isActive ? 'header-link active' : 'header-link'}>Perfil</NavLink>
        <NavLink to="/aulas" className={({ isActive }) => isActive ? 'header-link active' : 'header-link'}>Aulas</NavLink>
        <NavLink to="/crear-aula" className={({ isActive }) => isActive ? 'header-link active' : 'header-link'}>Crear Aula</NavLink>
        <button onClick={handleLogout} className="header-logout">Cerrar sesión</button>
      </nav>
    </header>
  );
}

export default Header;
