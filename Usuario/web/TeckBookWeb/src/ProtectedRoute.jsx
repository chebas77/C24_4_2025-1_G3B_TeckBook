import { useEffect, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";

function ProtectedRoute({ children }) {
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const location = useLocation();

  useEffect(() => {
    const validateToken = async () => {
      // Verificar si hay un token en la URL (redirección desde OAuth2)
      const queryParams = new URLSearchParams(window.location.search);
      const tokenFromUrl = queryParams.get('token');
      
      if (tokenFromUrl) {
        // Guardar el token de la URL en localStorage
        localStorage.setItem('token', tokenFromUrl);
        
        // No limpiamos la URL aquí para que el componente Home pueda procesarla
      }
      
      const token = localStorage.getItem('token');
      
      if (!token) {
        setIsAuthenticated(false);
        setIsLoading(false);
        return;
      }

      try {
        // Verificar la validez del token
        const response = await fetch('http://localhost:8080/api/auth/user', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (response.ok) {
          setIsAuthenticated(true);
        } else {
          // Token inválido, eliminarlo
          localStorage.removeItem('token');
          setIsAuthenticated(false);
        }
      } catch (error) {
        console.error("Error validando token:", error);
        localStorage.removeItem('token');
        setIsAuthenticated(false);
      } finally {
        setIsLoading(false);
      }
    };

    validateToken();
  }, []);

  if (isLoading) {
    return (
      <div style={styles.loadingContainer}>
        <div style={styles.loadingSpinner}></div>
        <p>Verificando autenticación...</p>
      </div>
    );
  }

  if (!isAuthenticated) {
    // Redireccionar al login y guardar la ubicación actual
    return <Navigate to="/" state={{ from: location }} replace />;
  }

  return children;
}

const styles = {
  loadingContainer: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    height: "100vh",
    gap: "20px",
  },
  loadingSpinner: {
    width: "40px",
    height: "40px",
    border: "4px solid #f3f3f3",
    borderTop: "4px solid #005DAB",
    borderRadius: "50%",
    animation: "spin 1s linear infinite",
  },
};

export default ProtectedRoute;