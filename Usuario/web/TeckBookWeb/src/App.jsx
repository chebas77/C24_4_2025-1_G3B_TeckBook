import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./Login";
import RecuperarCuenta from "./RecuperarCuenta"; // asegúrate que esté en src/
import Home from "./Home";
import Register from "./Register"; // nuevo import


function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/recuperar" element={<RecuperarCuenta />} />
        <Route path="/home" element={<Home />} />
        <Route path="/register" element={<Register />} /> {/* nueva ruta */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;
