import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./Login";
import RecuperarCuenta from "./RecuperarCuenta";
import Home from "./Home";
import Register from "./Register";
import Perfil from "./Perfil";
import Aulas from "./Aula";
import ProtectedRoute from "./ProtectedRoute";
import CrearAula from './CrearAula';
import CompletarPerfil from './CompletarPerfil'; // 🆕 AGREGAR ESTA IMPORTACIÓN

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Rutas públicas */}
        <Route path="/" element={<Login />} />
        <Route path="/recuperar" element={<RecuperarCuenta />} />
        <Route path="/register" element={<Register />} />
        
        {/* 🆕 RUTA PARA COMPLETAR PERFIL DESPUÉS DE OAUTH2 */}
        <Route path="/completar-perfil" element={<CompletarPerfil />} />

        {/* Rutas protegidas */}
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