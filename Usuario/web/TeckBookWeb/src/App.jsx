import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import RecuperarCuenta from "./pages/RecuperarCuenta";
import Home from "./pages/Home";
import Register from "./pages/Register";
import Perfil from "./pages/Perfil"; 
import Aulas from "./components/Aula"; // 🆕 IMPORTAR
import CrearAula from "./pages/CrearAula"; // 🆕 IMPORTAR
import AulaDetalle from "./pages/AulaDetalle";
import ProtectedRoute from "./routes/ProtectedRoute";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Rutas públicas */}
        <Route path="/" element={<Login />} />
        <Route path="/recuperar" element={<RecuperarCuenta />} />
        <Route path="/register" element={<Register />} />
        
        {/* Rutas protegidas que requieren autenticación */}
        <Route path="/home" element={
          <ProtectedRoute>
            <Home />
          </ProtectedRoute>
        } />
        
        <Route path="/perfil" element={
          <ProtectedRoute>
            <Perfil />
          </ProtectedRoute>
        } />
        
        {/* 🆕 NUEVAS RUTAS DE AULAS */}
        <Route path="/aulas" element={
          <ProtectedRoute>
            <Aulas />
          </ProtectedRoute>
        } />
        
        <Route path="/crear-aula" element={
          <ProtectedRoute>
            <CrearAula />
          </ProtectedRoute>
        } />
        
        <Route path="/aulas/:aulaId" element={
          <ProtectedRoute>
            <AulaDetalle />
          </ProtectedRoute>
        } />
      </Routes>
    </BrowserRouter>
  );
}

export default App;