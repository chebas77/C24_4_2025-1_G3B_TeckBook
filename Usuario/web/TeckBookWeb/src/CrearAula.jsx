import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BookOpen, Users, Book, Save, ArrowLeft } from 'lucide-react';
import './CrearAula.css';

function CrearAula() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    nombre: '',
    carrera: '',
    ciclo: '',
    seccion: ''
  });
  const [success, setSuccess] = useState(null);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/aulas', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(formData)
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Error al crear aula');
      }

      setSuccess('Aula creada correctamente');
      setFormData({ nombre: '', carrera: '', ciclo: '', seccion: '' });
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="full-page">
      <header className="header">
        <div className="header-content">
          <h1 className="logo">TecBook</h1>
          <nav className="nav">
            <a href="/home" className="nav-link">Inicio</a>
            <a href="/perfil" className="nav-link">Perfil</a>
            <a href="/aulas" className="nav-link">Aulas</a>
            <a onClick={() => navigate('/')} className="nav-link">Cerrar sesión</a>
          </nav>
        </div>
      </header>

      <div className="main-content">
        <div className="modal-content">
          <div className="modal-header">
            <h3>Crear Nueva Aula</h3>
            <button onClick={() => navigate('/home')} className="close-button">
              <ArrowLeft size={20} />
            </button>
          </div>
          <form onSubmit={handleSubmit} className="form">
            <div className="form-section">
              <div className="form-row">
                <div className="input-container">
                  <label className="label">Nombre del Aula</label>
                  <div className="input-wrapper">
                    <BookOpen className="input-icon" />
                    <input
                      type="text"
                      name="nombre"
                      value={formData.nombre}
                      onChange={handleChange}
                      className="input"
                      required
                    />
                  </div>
                </div>
                <div className="input-container">
                  <label className="label">Carrera</label>
                  <div className="input-wrapper">
                    <Book className="input-icon" />
                    <input
                      type="text"
                      name="carrera"
                      value={formData.carrera}
                      onChange={handleChange}
                      className="input"
                      required
                    />
                  </div>
                </div>
              </div>
              <div className="form-row">
                <div className="input-container">
                  <label className="label">Ciclo</label>
                  <div className="input-wrapper">
                    <BookOpen className="input-icon" />
                    <input
                      type="text"
                      name="ciclo"
                      value={formData.ciclo}
                      onChange={handleChange}
                      className="input"
                      required
                    />
                  </div>
                </div>
                <div className="input-container">
                  <label className="label">Sección</label>
                  <div className="input-wrapper">
                    <Users className="input-icon" />
                    <input
                      type="text"
                      name="seccion"
                      value={formData.seccion}
                      onChange={handleChange}
                      className="input"
                      required
                    />
                  </div>
                </div>
              </div>
            </div>
            <div className="form-actions">
              <button type="submit" className="save-button">
                <Save size={18} style={{ marginRight: '8px' }} />
                Crear Aula
              </button>
            </div>
            {error && <div className="error-message">{error}</div>}
            {success && <div className="success-message">{success}</div>}
          </form>
        </div>
      </div>
    </div>
  );
}

export default CrearAula;
