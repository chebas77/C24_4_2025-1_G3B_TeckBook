import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  User, 
  Mail, 
  BookOpen, 
  Database, 
  Building, 
  Book, 
  Users, 
  Save, 
  ArrowLeft,
  Edit3,
  Upload,
  Camera
} from 'lucide-react';
import "../css/Perfil.css";
import Header from '../components/Header';


function Perfil() {
  const [usuario, setUsuario] = useState({
    id: "",
    nombre: "",
    apellidos: "",
    codigo: "",
    correoInstitucional: "",
    ciclo: "",
    rol: "",
    departamentoId: "",
    carreraId: "",
    seccionId: "",
    profileImageUrl: ""
  });
  
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const fileInputRef = useRef(null);
  
  const handleRemovePhoto = async () => {
    if (!confirm("¿Estás seguro de que deseas eliminar tu foto de perfil?")) {
      return;
    }
    
    try {
      setIsUploading(true);
      setError(null);
      setSuccess(null);
      
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/upload/profile-image', {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Error al eliminar la imagen');
      }
      
      // Actualizar el estado local
      setUsuario(prev => ({
        ...prev,
        profileImageUrl: null
      }));
      
      setSuccess('Foto de perfil eliminada correctamente');
    } catch (error) {
      setError(error.message);
    } finally {
      setIsUploading(false);
    }
  };
  
  const navigate = useNavigate();

  // Obtener datos del usuario al cargar
  useEffect(() => {
    const token = localStorage.getItem('token');
    
    if (!token) {
      navigate('/');
      return;
    }

    const fetchUsuario = async () => {
      try {
        setIsLoading(true);
        const response = await fetch('http://localhost:8080/api/auth/user', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (!response.ok) {
          throw new Error('No se pudo obtener la información del usuario');
        }

        const data = await response.json();
        setUsuario(data);
        setIsLoading(false);
      } catch (error) {
        console.error("Error al obtener datos del usuario:", error);
        setError("Error al cargar los datos del perfil. Por favor, inicie sesión nuevamente.");
        setIsLoading(false);
        setTimeout(() => {
          localStorage.removeItem('token');
          navigate('/');
        }, 3000);
      }
    };

    fetchUsuario();
  }, [navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUsuario(prev => ({
      ...prev,
      [name]: value
    }));
  };
  
  // Función para manejar la subida de imágenes
  const handleImageUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    
    // Validar tipo y tamaño
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
    if (!validTypes.includes(file.type)) {
      setError('Formato de imagen no válido. Usa JPG, PNG, GIF o WebP.');
      return;
    }
    
    if (file.size > 5 * 1024 * 1024) { // 5MB
      setError('La imagen es demasiado grande. Máximo 5MB.');
      return;
    }
    
    try {
      setIsUploading(true);
      setError(null);
      setSuccess(null);
      
      const formData = new FormData();
      formData.append('file', file);
      
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/upload/profile-image', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Error al subir la imagen');
      }
      
      const data = await response.json();
      
      // Actualizar el estado local
      setUsuario(prev => ({
        ...prev,
        profileImageUrl: data.imageUrl
      }));
      
      setSuccess('Imagen de perfil actualizada correctamente');
    } catch (error) {
      setError(error.message);
    } finally {
      setIsUploading(false);
    }
  };

  // Función para renderizar el avatar
  const renderAvatar = () => {
    return (
      <div className="avatar-container">
        {usuario.profileImageUrl ? (
          <img 
            src={usuario.profileImageUrl} 
            alt="Foto de perfil" 
            className="avatar-image" 
          />
        ) : (
          <div className="avatar">
            {usuario.nombre && usuario.apellidos ? 
              `${usuario.nombre.charAt(0)}${usuario.apellidos.charAt(0)}`.toUpperCase() : 'GS'}
          </div>
        )}
        
        {isEditing && (
          <button 
            onClick={() => fileInputRef.current.click()} 
            className="change-photo-button"
            disabled={isUploading}
          >
            {isUploading ? '...' : (usuario.profileImageUrl ? <Camera size={16} /> : <Upload size={16} />)}
          </button>
        )}
      </div>
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/usuarios/${usuario.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(usuario)
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || 'Error al actualizar el perfil');
      }

      const updatedUsuario = await response.json();
      setUsuario(updatedUsuario);
      setSuccess('Perfil actualizado correctamente');
      setIsEditing(false);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleLogout = async () => {
  try {
    const token = localStorage.getItem('token');
    
    if (token) {
      console.log("Cerrando sesión en el backend...");
      
      // 🎯 LLAMADA AL BACKEND PARA INVALIDAR TOKEN
      const response = await fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        console.log("✅ Sesión cerrada en el backend:", data);
        
        if (data.tokenInvalidated) {
          console.log("✅ Token invalidado correctamente");
        }
      } else {
        console.warn("⚠️ Error al cerrar sesión en backend, pero continuando logout");
      }
    }
    
    // 🔧 LIMPIAR FRONTEND SIEMPRE (incluso si falla el backend)
    localStorage.removeItem('token');
    console.log("✅ Token eliminado del localStorage");
    
    // Redireccionar al login
    navigate('/');
    
  } catch (error) {
    console.error("❌ Error durante logout:", error);
    
    // 🔧 LIMPIAR FRONTEND AUNQUE FALLE EL BACKEND
    localStorage.removeItem('token');
    navigate('/');
  }
};

  if (isLoading) {
    return (
      <div className="full-page">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Cargando información del perfil...</p>
        </div>
      </div>
    );
  }
  const handleCancel = () => {
  const fetchUsuario = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/auth/user', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('No se pudo obtener la información del usuario');
      }

      const data = await response.json();
      setUsuario(data);
      setIsEditing(false);
    } catch (error) {
      setError(error.message);
    }
  };

  fetchUsuario();
};

return (
  <>
    <Header />
    <div className="full-page">
      <div className="main-content">
        <div className="profile-card">
          <div className="profile-header">
            {renderAvatar()}

            <input 
              type="file" 
              ref={fileInputRef}
              onChange={handleImageUpload}
              accept="image/jpeg,image/png,image/gif,image/webp"
              style={{ display: 'none' }} 
            />

            <div className="profile-info">
              <h2 className="profile-name">{`${usuario.nombre} ${usuario.apellidos}`}</h2>
              <p className="profile-role">{usuario.rol}</p>
              <p className="profile-email">{usuario.correoInstitucional}</p>
            </div>
            <div className="edit-button-container">
              <button 
                onClick={() => setIsEditing(true)} 
                className="edit-button"
              >
                <Edit3 size={16} style={{marginRight: '8px'}} />
                Editar Perfil
              </button>
            </div>
          </div>
        </div>

        {error && (
          <div className="error-message">
            <p>{error}</p>
          </div>
        )}

        {success && (
          <div className="success-message">
            <p>{success}</p>
          </div>
        )}

        <div className="info-sections">
          <div className="info-section">
            <h3 className="section-title">Información Personal</h3>
            <div className="info-grid">
              <div className="info-item">
                <p className="info-label">Nombre</p>
                <div className="info-field">
                  <User size={16} className="field-icon" />
                  <span className="field-value">{usuario.nombre}</span>
                </div>
              </div>
              <div className="info-item">
                <p className="info-label">Apellidos</p>
                <div className="info-field">
                  <User size={16} className="field-icon" />
                  <span className="field-value">{usuario.apellidos}</span>
                </div>
              </div>
            </div>
          </div>

          <div className="info-section">
            <h3 className="section-title">Información Académica</h3>
            <div className="info-grid">
              <div className="info-item">
                <p className="info-label">Código de Estudiante</p>
                <div className="info-field">
                  <Database size={16} className="field-icon" />
                  <span className="field-value">{usuario.codigo}</span>
                </div>
              </div>
              <div className="info-item">
                <p className="info-label">Ciclo</p>
                <div className="info-field">
                  <BookOpen size={16} className="field-icon" />
                  <span className="field-value">{usuario.ciclo}</span>
                </div>
              </div>
              <div className="info-item">
                <p className="info-label">Rol</p>
                <div className="info-field">
                  <Users size={16} className="field-icon" />
                  <span className="field-value">{usuario.rol}</span>
                </div>
              </div>
              <div className="info-item">
                <p className="info-label">Departamento ID</p>
                <div className="info-field">
                  <Building size={16} className="field-icon" />
                  <span className="field-value">{usuario.departamentoId}</span>
                </div>
              </div>
              <div className="info-item">
                <p className="info-label">Carrera ID</p>
                <div className="info-field">
                  <Book size={16} className="field-icon" />
                  <span className="field-value">{usuario.carreraId || "No asignado"}</span>
                </div>
              </div>
              <div className="info-item">
                <p className="info-label">Sección ID</p>
                <div className="info-field">
                  <Users size={16} className="field-icon" />
                  <span className="field-value">{usuario.seccionId || "No asignado"}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {isEditing && (
          <div className="modal-overlay">
            <div className="modal-content">
              <div className="modal-header">
                <h3>Editar Perfil</h3>
                <button onClick={handleCancel} className="close-button">
                  <ArrowLeft size={20} />
                </button>
              </div>
              <form onSubmit={handleSubmit} className="form">
                <div className="form-section">
                  <h4 className="form-section-title">Foto de Perfil</h4>
                  <div className="photo-upload-container">
                    <div className="current-photo-container">
                      {usuario.profileImageUrl ? (
                        <img 
                          src={usuario.profileImageUrl} 
                          alt="Foto de perfil" 
                          className="current-photo" 
                        />
                      ) : (
                        <div className="photo-placeholder">
                          {usuario.nombre && usuario.apellidos ? 
                            `${usuario.nombre.charAt(0)}${usuario.apellidos.charAt(0)}`.toUpperCase() : 'GS'}
                        </div>
                      )}
                    </div>
                    <div className="photo-upload-actions">
                      <button 
                        type="button" 
                        onClick={() => fileInputRef.current.click()} 
                        className="upload-photo-button"
                        disabled={isUploading}
                      >
                        {isUploading ? 'Subiendo...' : (
                          <>
                            <Upload size={16} style={{marginRight: '8px'}} />
                            {usuario.profileImageUrl ? 'Cambiar foto' : 'Subir foto'}
                          </>
                        )}
                      </button>
                      {usuario.profileImageUrl && (
                        <button 
                          type="button" 
                          onClick={handleRemovePhoto} 
                          className="remove-photo-button"
                          disabled={isUploading}
                        >
                          Eliminar foto
                        </button>
                      )}
                      <div className="photo-help-text">
                        Formatos aceptados: JPG, PNG, GIF (máx. 5MB)
                      </div>
                    </div>
                  </div>
                </div>

                <div className="form-section">
                  <h4 className="form-section-title">Información Personal</h4>
                  <div className="form-row">
                    <div className="input-container">
                      <label className="label">Nombre</label>
                      <div className="input-wrapper">
                        <User size={18} color="#005DAB" className="input-icon" />
                        <input
                          type="text"
                          name="nombre"
                          value={usuario.nombre || ''}
                          onChange={handleChange}
                          className="input"
                        />
                      </div>
                    </div>
                    <div className="input-container">
                      <label className="label">Apellidos</label>
                      <div className="input-wrapper">
                        <User size={18} color="#005DAB" className="input-icon" />
                        <input
                          type="text"
                          name="apellidos"
                          value={usuario.apellidos || ''}
                          onChange={handleChange}
                          className="input"
                        />
                      </div>
                    </div>
                  </div>
                </div>

                <div className="form-section">
                  <h4 className="form-section-title">Información Académica</h4>
                  <div className="form-row">
                    <div className="input-container">
                      <label className="label">Código de Estudiante</label>
                      <div className="input-wrapper">
                        <Database size={18} color="#005DAB" className="input-icon" />
                        <input
                          type="text"
                          name="codigo"
                          value={usuario.codigo || ''}
                          onChange={handleChange}
                          disabled={true}
                          className="input"
                          style={{backgroundColor: '#e9ecef', cursor: 'default'}}
                        />
                      </div>
                    </div>
                    <div className="input-container">
                      <label className="label">Correo Institucional</label>
                      <div className="input-wrapper">
                        <Mail size={18} color="#005DAB" className="input-icon" />
                        <input
                          type="email"
                          name="correoInstitucional"
                          value={usuario.correoInstitucional || ''}
                          onChange={handleChange}
                          disabled={true}
                          className="input"
                          style={{backgroundColor: '#e9ecef', cursor: 'default'}}
                        />
                      </div>
                    </div>
                  </div>

                  <div className="form-row">
                    <div className="input-container">
                      <label className="label">Ciclo</label>
                      <div className="input-wrapper">
                        <BookOpen size={18} color="#005DAB" className="input-icon" />
                        <input
                          type="text"
                          name="ciclo"
                          value={usuario.ciclo || ''}
                          onChange={handleChange}
                          className="input"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="form-row">
                    <div className="input-container">
                      <label className="label">Departamento ID</label>
                      <div className="input-wrapper">
                        <Building size={18} color="#005DAB" className="input-icon" />
                        <input
                          type="number"
                          name="departamentoId"
                          value={usuario.departamentoId || ''}
                          onChange={handleChange}
                          className="input"
                        />
                      </div>
                    </div>
                    <div className="input-container">
                      <label className="label">Carrera ID</label>
                      <div className="input-wrapper">
                        <Book size={18} color="#005DAB" className="input-icon" />
                        <input
                          type="number"
                          name="carreraId"
                          value={usuario.carreraId || ''}
                          onChange={handleChange}
                          className="input"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="form-row">
                    <div className="input-container">
                      <label className="label">Sección ID</label>
                      <div className="input-wrapper">
                        <Users size={18} color="#005DAB" className="input-icon" />
                        <input
                          type="number"
                          name="seccionId"
                          value={usuario.seccionId || ''}
                          onChange={handleChange}
                          className="input"
                        />
                      </div>
                    </div>
                  </div>
                </div>

                <div className="form-actions">
                  <button type="button" onClick={handleCancel} className="cancel-button">
                    Cancelar
                  </button>
                  <button type="submit" className="save-button">
                    <Save size={18} style={{marginRight: '8px'}} />
                    Guardar Cambios
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  </>
);
}

export default Perfil;