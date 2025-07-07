import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNavigate, useLocation } from 'react-router-dom';

const AdminLayout = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const navigation = [
    {
      name: 'Dashboard',
      href: '/dashboard',
      icon: '📊',
      current: location.pathname === '/dashboard'
    },
    {
      name: 'Gestión de Usuarios',
      href: '/usuarios',
      icon: '👥',
      current: location.pathname === '/usuarios'
    },
    {
      name: 'Moderación',
      href: '/moderacion',
      icon: '🛡️',
      current: location.pathname === '/moderacion'
    }
  ];

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f9fafb' }}>
      {/* Sidebar para móvil */}
      {sidebarOpen && (
        <div style={{
          position: 'fixed',
          inset: 0,
          zIndex: 50,
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
        }} onClick={() => setSidebarOpen(false)}>
          <div style={{
            position: 'fixed',
            left: 0,
            top: 0,
            height: '100%',
            width: '256px',
            backgroundColor: 'white',
            boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)'
          }} onClick={(e) => e.stopPropagation()}>
            <SidebarContent navigation={navigation} />
          </div>
        </div>
      )}

      {/* Sidebar para desktop */}
      <div style={{
        position: 'fixed',
        left: 0,
        top: 0,
        height: '100%',
        width: '256px',
        backgroundColor: 'white',
        borderRight: '1px solid #e5e7eb',
        display: window.innerWidth >= 768 ? 'block' : 'none'
      }}>
        <SidebarContent navigation={navigation} />
      </div>

      {/* Contenido principal */}
      <div style={{ marginLeft: window.innerWidth >= 768 ? '256px' : '0' }}>
        {/* Header */}
        <header style={{
          backgroundColor: 'white',
          borderBottom: '1px solid #e5e7eb',
          padding: '1rem 1.5rem'
        }}>
          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
          }}>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <button
                onClick={() => setSidebarOpen(true)}
                style={{
                  display: window.innerWidth >= 768 ? 'none' : 'block',
                  padding: '0.5rem',
                  marginRight: '1rem',
                  backgroundColor: 'transparent',
                  border: 'none',
                  cursor: 'pointer',
                  fontSize: '1.5rem'
                }}
              >
                ☰
              </button>
              <h1 style={{
                fontSize: '1.5rem',
                fontWeight: 'bold',
                color: '#111827'
              }}>
                Panel de Administración
              </h1>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>
                {user?.nombre} {user?.apellidos}
              </span>
              <button
                onClick={handleLogout}
                style={{
                  padding: '0.5rem 1rem',
                  backgroundColor: '#dc2626',
                  color: 'white',
                  border: 'none',
                  borderRadius: '0.375rem',
                  fontSize: '0.875rem',
                  cursor: 'pointer'
                }}
              >
                Cerrar sesión
              </button>
            </div>
          </div>
        </header>

        {/* Contenido de la página */}
        <main style={{ padding: '1.5rem' }}>
          {children}
        </main>
      </div>
    </div>
  );
};

const SidebarContent = ({ navigation }) => {
  const navigate = useNavigate();

  return (
    <div style={{ padding: '1.5rem' }}>
      {/* Logo */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        marginBottom: '2rem'
      }}>
        <div style={{
          width: '32px',
          height: '32px',
          backgroundColor: '#3b82f6',
          borderRadius: '8px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          marginRight: '0.75rem'
        }}>
          <span style={{ color: 'white', fontSize: '1.25rem' }}>🏫</span>
        </div>
        <span style={{
          fontSize: '1.125rem',
          fontWeight: 'bold',
          color: '#111827'
        }}>
          TecBook Admin
        </span>
      </div>

      {/* Navegación */}
      <nav>
        <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
          {navigation.map((item) => (
            <li key={item.name} style={{ marginBottom: '0.5rem' }}>
              <button
                onClick={() => navigate(item.href)}
                style={{
                  width: '100%',
                  textAlign: 'left',
                  padding: '0.75rem',
                  borderRadius: '0.375rem',
                  border: 'none',
                  backgroundColor: item.current ? '#dbeafe' : 'transparent',
                  color: item.current ? '#1d4ed8' : '#374151',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  fontSize: '0.875rem',
                  fontWeight: item.current ? '500' : '400'
                }}
              >
                <span style={{ marginRight: '0.75rem', fontSize: '1rem' }}>
                  {item.icon}
                </span>
                {item.name}
              </button>
            </li>
          ))}
        </ul>
      </nav>
    </div>
  );
};

export default AdminLayout;