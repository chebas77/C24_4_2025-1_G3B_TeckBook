// ===============================================
// App.js - Migrado con configuración centralizada
// ===============================================
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { API_CONFIG, ROUTES } from "./config/apiConfig";
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
  
// Páginas públicas
import Login from "./pages/Login";
import RecuperarCuenta from "./pages/RecuperarCuenta";
import Register from "./pages/Register";

// Páginas protegidas
import Home from "./pages/Home";
import Perfil from "./pages/Perfil"; 
import Aulas from "./components/Aula";
import CrearAula from "./pages/CrearAula";
import AulaDetalle from "./pages/AulaDetalle";
import AnunciosGeneral from "./pages/AnunciosGeneral";

// Componentes
import ProtectedRoute from "./routes/ProtectedRoute";
import OAuth2Redirect from './pages/OAuth2Redirect';

function App() {
  // Mostrar info en desarrollo
  if (API_CONFIG.ENVIRONMENT === 'development') {
    console.log(`🚀 ${API_CONFIG.APP_NAME} v${API_CONFIG.APP_VERSION}`);
    console.log(`🌐 API: ${API_CONFIG.API_BASE_URL}`);
  }

  return (
  <BrowserRouter>
    {/* Contenedor de notificaciones fuera de <Routes> */}
    <ToastContainer position="top-center" autoClose={false} />

    <Routes>
      {/* Rutas públicas */}
      <Route path={ROUTES.PUBLIC.LOGIN} element={<Login />} />
      <Route path="/oauth2/redirect" element={<OAuth2Redirect />} />
      <Route path={ROUTES.PUBLIC.RECOVER_PASSWORD} element={<RecuperarCuenta />} />
      <Route path={ROUTES.PUBLIC.REGISTER} element={<Register />} />

      {/* Rutas protegidas */}
      <Route path={ROUTES.PROTECTED.DASHBOARD} element={
        <ProtectedRoute>
          <Home />
        </ProtectedRoute>
      } />

      <Route path={ROUTES.PROTECTED.PROFILE} element={
        <ProtectedRoute>
          <Perfil />
        </ProtectedRoute>
      } />

      <Route path={ROUTES.PROTECTED.AULAS} element={
        <ProtectedRoute>
          <Aulas />
        </ProtectedRoute>
      } />

      <Route path={ROUTES.PROTECTED.CREATE_AULA} element={
        <ProtectedRoute>
          <CrearAula />
        </ProtectedRoute>
      } />

      <Route path="/aulas/:aulaId" element={
        <ProtectedRoute>
          <AulaDetalle />
        </ProtectedRoute>
      } />

      <Route path={ROUTES.PROTECTED.ANNOUNCEMENTS} element={
        <ProtectedRoute>
          <AnunciosGeneral />
        </ProtectedRoute>
      } />
    </Routes>
  </BrowserRouter>
);

}

export default App;