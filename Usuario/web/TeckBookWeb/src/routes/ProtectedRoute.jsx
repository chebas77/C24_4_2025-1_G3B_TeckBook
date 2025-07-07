// src/routes/ProtectedRoute.jsx
import { Navigate, useLocation } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { ROUTES } from '../config/apiConfig';
import authService from '../services/authService';

const ProtectedRoute = ({ children, requireRole = null }) => {
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userData, setUserData] = useState(null);
  const location = useLocation();

  useEffect(() => {
    const checkAuth = async () => {
      try {
        // Verificar si hay token
        if (!authService.isAuthenticated()) {
          setIsLoading(false);
          return;
        }

        // ✅ Usar authService en lugar de fetch directo
        const user = await authService.getUser();
        setUserData(user);
        setIsAuthenticated(true);
        
      } catch (error) {
        console.error('Error verificando autenticación:', error);
        // Si hay error, limpiar token y redirigir
        authService.logout();
        setIsAuthenticated(false);
      } finally {
        setIsLoading(false);
      }
    };

    checkAuth();
  }, []);

  if (isLoading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        flexDirection: 'column'
      }}>
        <div style={{ 
          width: '40px', 
          height: '40px', 
          border: '4px solid #f3f3f3',
          borderTop: '4px solid #005DAB',
          borderRadius: '50%',
          animation: 'spin 1s linear infinite'
        }}></div>
        <p style={{ marginTop: '16px', color: '#666' }}>
          Verificando autenticación...
        </p>
        <style>
          {`
            @keyframes spin {
              0% { transform: rotate(0deg); }
              100% { transform: rotate(360deg); }
            }
          `}
        </style>
      </div>
    );
  }

  // No autenticado - redirigir a login
  if (!isAuthenticated) {
    return <Navigate to={ROUTES.PUBLIC.LOGIN} state={{ from: location }} replace />;
  }

  // Verificar rol si es requerido
  if (requireRole && userData?.rol !== requireRole) {
    return <Navigate to={ROUTES.PROTECTED.DASHBOARD} replace />;
  }

  return children;
};

export default ProtectedRoute;