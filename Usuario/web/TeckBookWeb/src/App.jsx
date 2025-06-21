import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./Login";
import RecuperarCuenta from "./RecuperarCuenta";
import Home from "./Home";
import Register from "./Register";
import Perfil from "./Perfil"; 
import Aulas from "./Aula"; // 🆕 IMPORTAR
import CrearAula from "./CrearAula"; // 🆕 IMPORTAR
import ProtectedRoute from "./ProtectedRoute";

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
      </Routes>
    </BrowserRouter>
  );
}

export default App;