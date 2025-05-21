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
        // Intentamos obtener información del usuario mediante el endpoint de auth
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
        // Si hay un error de autenticación, redirigir al login
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
      <div style={styles.avatarContainer}>
        {usuario.profileImageUrl ? (
          <img 
            src={usuario.profileImageUrl} 
            alt="Foto de perfil" 
            style={styles.avatarImage} 
          />
        ) : (
          <div style={styles.avatar}>
            {usuario.nombre && usuario.apellidos ? 
              `${usuario.nombre.charAt(0)}${usuario.apellidos.charAt(0)}`.toUpperCase() : 'GS'}
          </div>
        )}
        
        {isEditing && (
          <button 
            onClick={() => fileInputRef.current.click()} 
            style={styles.changePhotoButton}
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

  const handleCancel = () => {
    // Recargar datos originales
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

  if (isLoading) {
    return (
      <div style={styles.fullPage}>
        <div style={styles.loadingContainer}>
          <div style={styles.loadingSpinner}></div>
          <p>Cargando información del perfil...</p>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.fullPage}>
      {/* HEADER */}
      <header style={styles.header}>
        <div style={styles.headerContent}>
          <h1 style={styles.logo}>TecBook</h1>
          <nav style={styles.nav}>
            <a href="/home" style={styles.navLink}>Inicio</a>
            <a href="/perfil" style={{...styles.navLink, color: '#FFA500', fontWeight: 'bold'}}>Perfil</a>
            <a href="/cursos" style={styles.navLink}>Cursos</a>
            <a 
              href="/"
              onClick={(e) => {
                e.preventDefault();
                localStorage.removeItem('token');
                navigate('/');
              }} 
              style={styles.navLink}
            >
              Cerrar sesión
            </a>
          </nav>
        </div>
      </header>

      <div style={styles.mainContent}>
        <div style={styles.profileCard}>
          <div style={styles.profileHeader}>
            {/* Aquí implementamos el avatar con foto o iniciales */}
            {renderAvatar()}
            
            {/* Input file oculto para subir imágenes */}
            <input 
              type="file" 
              ref={fileInputRef}
              onChange={handleImageUpload}
              accept="image/jpeg,image/png,image/gif,image/webp"
              style={{ display: 'none' }} 
            />
            
            <div style={styles.profileInfo}>
              <h2 style={styles.profileName}>{`${usuario.nombre} ${usuario.apellidos}`}</h2>
              <p style={styles.profileRole}>{usuario.rol}</p>
              <p style={styles.profileEmail}>{usuario.correoInstitucional}</p>
            </div>
            <div style={styles.editButtonContainer}>
              <button 
                onClick={() => setIsEditing(true)} 
                style={styles.editButton}
              >
                <Edit3 size={16} style={{marginRight: '8px'}} />
                Editar Perfil
              </button>
            </div>
          </div>
        </div>

        {error && (
          <div style={styles.errorMessage}>
            <p>{error}</p>
          </div>
        )}

        {success && (
          <div style={styles.successMessage}>
            <p>{success}</p>
          </div>
        )}

        <div style={styles.infoSections}>
          <div style={styles.infoSection}>
            <h3 style={styles.sectionTitle}>Información Personal</h3>
            <div style={styles.infoGrid}>
              <div style={styles.infoItem}>
                <p style={styles.infoLabel}>Nombre</p>
                <div style={styles.infoField}>
                  <User size={16} style={styles.fieldIcon} />
                  <span style={styles.fieldValue}>{usuario.nombre}</span>
                </div>
              </div>
              <div style={styles.infoItem}>
                <p style={styles.infoLabel}>Apellidos</p>
                <div style={styles.infoField}>
                  <User size={16} style={styles.fieldIcon} />
                  <span style={styles.fieldValue}>{usuario.apellidos}</span>
                </div>
              </div>
            </div>
          </div>

          <div style={styles.infoSection}>
            <h3 style={styles.sectionTitle}>Información Académica</h3>
            <div style={styles.infoGrid}>
              <div style={styles.infoItem}>
                <p style={styles.infoLabel}>Código de Estudiante</p>
                <div style={styles.infoField}>
                  <Database size={16} style={styles.fieldIcon} />
                  <span style={styles.fieldValue}>{usuario.codigo}</span>
                </div>
              </div>
              <div style={styles.infoItem}>
                <p style={styles.infoLabel}>Correo Institucional</p>
                <div style={styles.infoField}>
                  <Mail size={16} style={styles.fieldIcon} />
                  <span style={styles.fieldValue}>{usuario.correoInstitucional}</span>
                </div>
              </div>
              <div style={styles.infoItem}>
                <p style={styles.infoLabel}>Ciclo</p>
                <div style={styles.infoField}>
                  <BookOpen size={16} style={styles.fieldIcon} />
                  <span style={styles.fieldValue}>{usuario.ciclo}</span>
                </div>
              </div>
              <div style={styles.infoItem}>
                <p style={styles.infoLabel}>Rol</p>
                <div style={styles.infoField}>
                  <Users size={16} style={styles.fieldIcon} />
                  <span style={styles.fieldValue}>{usuario.rol}</span>
                </div>
              </div>
              <div style={styles.infoItem}>
                <p style={styles.infoLabel}>Departamento ID</p>
                <div style={styles.infoField}>
                  <Building size={16} style={styles.fieldIcon} />
                  <span style={styles.fieldValue}>{usuario.departamentoId}</span>
                </div>
              </div>
              <div style={styles.infoItem}>
                <p style={styles.infoLabel}>Carrera ID</p>
                <div style={styles.infoField}>
                  <Book size={16} style={styles.fieldIcon} />
                  <span style={styles.fieldValue}>{usuario.carreraId || "No asignado"}</span>
                </div>
              </div>
              <div style={styles.infoItem}>
                <p style={styles.infoLabel}>Sección ID</p>
                <div style={styles.infoField}>
                  <Users size={16} style={styles.fieldIcon} />
                  <span style={styles.fieldValue}>{usuario.seccionId || "No asignado"}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Modal de edición */}
{isEditing && (
  <div style={styles.modalOverlay}>
    <div style={styles.modalContent}>
      <div style={styles.modalHeader}>
        <h3>Editar Perfil</h3>
        <button onClick={handleCancel} style={styles.closeButton}>
          <ArrowLeft size={20} />
        </button>
      </div>
      <form onSubmit={handleSubmit} style={styles.form}>
        {/* Sección de foto de perfil */}
        <div style={styles.formSection}>
          <h4 style={styles.formSectionTitle}>Foto de Perfil</h4>
          <div style={styles.photoUploadContainer}>
            <div style={styles.currentPhotoContainer}>
              {usuario.profileImageUrl ? (
                <img 
                  src={usuario.profileImageUrl} 
                  alt="Foto de perfil" 
                  style={styles.currentPhoto} 
                />
              ) : (
                <div style={styles.photoPlaceholder}>
                  {usuario.nombre && usuario.apellidos ? 
                    `${usuario.nombre.charAt(0)}${usuario.apellidos.charAt(0)}`.toUpperCase() : 'GS'}
                </div>
              )}
            </div>
            <div style={styles.photoUploadActions}>
              <button 
                type="button" 
                onClick={() => fileInputRef.current.click()} 
                style={styles.uploadPhotoButton}
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
                  style={styles.removePhotoButton}
                  disabled={isUploading}
                >
                  Eliminar foto
                </button>
              )}
              <div style={styles.photoHelpText}>
                Formatos aceptados: JPG, PNG, GIF (máx. 5MB)
              </div>
            </div>
          </div>
        </div>

        <div style={styles.formSection}>
          <h4 style={styles.formSectionTitle}>Información Personal</h4>
          <div style={styles.formRow}>
            <div style={styles.inputContainer}>
              <label style={styles.label}>Nombre</label>
              <div style={styles.inputWrapper}>
                <User size={18} color="#005DAB" style={styles.inputIcon} />
                <input
                  type="text"
                  name="nombre"
                  value={usuario.nombre || ''}
                  onChange={handleChange}
                  style={styles.input}
                />
              </div>
            </div>
            <div style={styles.inputContainer}>
              <label style={styles.label}>Apellidos</label>
              <div style={styles.inputWrapper}>
                <User size={18} color="#005DAB" style={styles.inputIcon} />
                <input
                  type="text"
                  name="apellidos"
                  value={usuario.apellidos || ''}
                  onChange={handleChange}
                  style={styles.input}
                />
              </div>
            </div>
          </div>
        </div>

        <div style={styles.formSection}>
          <h4 style={styles.formSectionTitle}>Información Académica</h4>
          <div style={styles.formRow}>
            <div style={styles.inputContainer}>
              <label style={styles.label}>Código de Estudiante</label>
              <div style={styles.inputWrapper}>
                <Database size={18} color="#005DAB" style={styles.inputIcon} />
                <input
                  type="text"
                  name="codigo"
                  value={usuario.codigo || ''}
                  onChange={handleChange}
                  disabled={true}
                  style={{...styles.input, backgroundColor: '#e9ecef', cursor: 'default'}}
                />
              </div>
            </div>
            <div style={styles.inputContainer}>
              <label style={styles.label}>Correo Institucional</label>
              <div style={styles.inputWrapper}>
                <Mail size={18} color="#005DAB" style={styles.inputIcon} />
                <input
                  type="email"
                  name="correoInstitucional"
                  value={usuario.correoInstitucional || ''}
                  onChange={handleChange}
                  disabled={true}
                  style={{...styles.input, backgroundColor: '#e9ecef', cursor: 'default'}}
                />
              </div>
            </div>
          </div>

          <div style={styles.formRow}>
            <div style={styles.inputContainer}>
              <label style={styles.label}>Ciclo</label>
              <div style={styles.inputWrapper}>
                <BookOpen size={18} color="#005DAB" style={styles.inputIcon} />
                <input
                  type="text"
                  name="ciclo"
                  value={usuario.ciclo || ''}
                  onChange={handleChange}
                  style={styles.input}
                />
              </div>
            </div>
            {/* Se elimina el campo del rol ya que no debe mostrarse */}
          </div>

          <div style={styles.formRow}>
            <div style={styles.inputContainer}>
              <label style={styles.label}>Departamento ID</label>
              <div style={styles.inputWrapper}>
                <Building size={18} color="#005DAB" style={styles.inputIcon} />
                <input
                  type="number"
                  name="departamentoId"
                  value={usuario.departamentoId || ''}
                  onChange={handleChange}
                  style={styles.input}
                />
              </div>
            </div>
            <div style={styles.inputContainer}>
              <label style={styles.label}>Carrera ID</label>
              <div style={styles.inputWrapper}>
                <Book size={18} color="#005DAB" style={styles.inputIcon} />
                <input
                  type="number"
                  name="carreraId"
                  value={usuario.carreraId || ''}
                  onChange={handleChange}
                  style={styles.input}
                />
              </div>
            </div>
          </div>

          <div style={styles.formRow}>
            <div style={styles.inputContainer}>
              <label style={styles.label}>Sección ID</label>
              <div style={styles.inputWrapper}>
                <Users size={18} color="#005DAB" style={styles.inputIcon} />
                <input
                  type="number"
                  name="seccionId"
                  value={usuario.seccionId || ''}
                  onChange={handleChange}
                  style={styles.input}
                />
              </div>
            </div>
          </div>
        </div>

        <div style={styles.formActions}>
          <button type="button" onClick={handleCancel} style={styles.cancelButton}>
            Cancelar
          </button>
          <button type="submit" style={styles.saveButton}>
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
  );
}

const styles = {
  fullPage: {
    margin: 0,
    padding: 0,
    width: '100vw',
    minHeight: '100vh',
    fontFamily: "'Segoe UI', Arial, sans-serif",
    backgroundColor: "#f7f9fc",
    display: 'flex',
    flexDirection: 'column',
  },
  // Añadir estos estilos al objeto styles
photoUploadContainer: {
  display: "flex",
  alignItems: "center",
  gap: "20px",
  flexWrap: "wrap",
},
currentPhotoContainer: {
  width: "100px",
  height: "100px",
  position: "relative",
},
currentPhoto: {
  width: "100px",
  height: "100px",
  borderRadius: "50%",
  objectFit: "cover",
  border: "2px solid #e0e6ed",
},
photoPlaceholder: {
  width: "100px",
  height: "100px",
  borderRadius: "50%",
  backgroundColor: "#005DAB",
  color: "white",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  fontSize: "36px",
  fontWeight: "bold",
},
photoUploadActions: {
  flex: 1,
  display: "flex",
  flexDirection: "column",
  gap: "10px",
},
uploadPhotoButton: {
  backgroundColor: "#005DAB",
  color: "white",
  border: "none",
  padding: "8px 15px",
  borderRadius: "4px",
  fontSize: "14px",
  fontWeight: "500",
  cursor: "pointer",
  display: "flex",
  alignItems: "center",
  maxWidth: "fit-content",
},
removePhotoButton: {
  backgroundColor: "transparent",
  color: "#dc3545",
  border: "1px solid #dc3545",
  padding: "6px 12px",
  borderRadius: "4px",
  fontSize: "14px",
  fontWeight: "500",
  cursor: "pointer",
  maxWidth: "fit-content",
},
photoHelpText: {
  fontSize: "12px",
  color: "#666",
  marginTop: "5px",
},
  header: {
    width: '100%',
    backgroundColor: "#005DAB",
    color: "white",
    boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
    padding: "0",
  },
  headerContent: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "15px 40px",
    maxWidth: "1400px",
    margin: "0 auto",
    width: "100%",
  },
  logo: {
    fontSize: "24px",
    fontWeight: "bold",
    margin: 0,
  },
  nav: {
    display: "flex",
    gap: "20px"
  },
  navLink: {
    color: "white",
    textDecoration: "none",
    fontSize: "16px",
    fontWeight: "400",
    padding: "5px 10px",
  },
  mainContent: {
    flex: 1,
    width: "100%",
    maxWidth: "1200px",
    margin: "0 auto",
    padding: "20px",
  },
  profileCard: {
    backgroundColor: "white",
    borderRadius: "8px",
    boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
    marginBottom: "20px",
  },
  profileHeader: {
    display: "flex",
    alignItems: "center",
    padding: "20px",
  },
  avatarContainer: {
    position: "relative",
    marginRight: "20px",
  },
  avatar: {
    width: "60px",
    height: "60px",
    borderRadius: "50%",
    backgroundColor: "#005DAB",
    color: "white",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    fontSize: "24px",
    fontWeight: "bold",
  },
  avatarImage: {
    width: '60px',
    height: '60px',
    borderRadius: '50%',
    objectFit: 'cover',
    border: '2px solid #f0f4f8',
  },
  changePhotoButton: {
    position: 'absolute',
    bottom: '0',
    right: '0',
    backgroundColor: '#005DAB',
    color: 'white',
    border: 'none',
    borderRadius: '50%',
    width: '24px',
    height: '24px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    cursor: 'pointer',
    boxShadow: '0 2px 5px rgba(0,0,0,0.2)',
    padding: '0',
  },
  profileInfo: {
    flex: 1,
  },
  profileName: {
    fontSize: "20px",
    fontWeight: "600",
    color: "#333",
    margin: "0 0 5px 0",
  },
  profileRole: {
    fontSize: "14px",
    color: "#666",
    margin: "0 0 5px 0",
  },
  profileEmail: {
    fontSize: "14px",
    color: "#888",
    margin: 0,
  },
  editButtonContainer: {
    marginLeft: "auto",
  },
  editButton: {
    backgroundColor: "#005DAB",
    color: "white",
    border: "none",
    padding: "8px 15px",
    borderRadius: "4px",
    fontSize: "14px",
    fontWeight: "500",
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
  },
  infoSections: {
    display: "flex",
    flexDirection: "column",
    gap: "20px",
  },
  infoSection: {
    backgroundColor: "white",
    borderRadius: "8px",
    boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
    padding: "20px",
  },
  sectionTitle: {
    fontSize: "16px",
    color: "#005DAB",
    fontWeight: "600",
    marginTop: 0,
    marginBottom: "15px",
    paddingBottom: "10px",
    borderBottom: "1px solid #e0e6ed",
  },
  infoGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(300px, 1fr))",
    gap: "15px",
  },
  infoItem: {
    marginBottom: "10px",
  },
  infoLabel: {
    fontSize: "14px",
    color: "#666",
    margin: "0 0 5px 0",
  },
  infoField: {
    display: "flex",
    alignItems: "center",
    backgroundColor: "#f8f9fa",
    borderRadius: "4px",
    padding: "8px 12px",
  },
  fieldIcon: {
    color: "#005DAB",
    marginRight: "10px",
  },
  fieldValue: {
    fontSize: "14px",
    color: "#333",
  },
  modalOverlay: {
    position: "fixed",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: "rgba(0, 0, 0, 0.5)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 1000,
  },
  modalContent: {
    backgroundColor: "white",
    borderRadius: "8px",
    boxShadow: "0 5px 15px rgba(0,0,0,0.2)",
    width: "90%",
    maxWidth: "800px",
    maxHeight: "80vh",
    overflow: "auto",
  },
  modalHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "15px 20px",
    borderBottom: "1px solid #e0e6ed",
  },
  closeButton: {
    background: "none",
    border: "none",
    cursor: "pointer",
    color: "#666",
    padding: "5px",
  },
  form: {
    padding: "20px",
  },
  formSection: {
    marginBottom: "20px",
  },
  formSectionTitle: {
    fontSize: "16px",
    color: "#005DAB",
    fontWeight: "600",
    marginTop: 0,
    marginBottom: "15px",
  },
  formRow: {
    display: "flex",
    flexWrap: "wrap",
    gap: "15px",
    marginBottom: "15px",
  },
  inputContainer: {
    flex: "1 1 calc(50% - 10px)",
    minWidth: "250px",
  },
  label: {
    display: "block",
    marginBottom: "5px",
    color: "#555",
    fontSize: "14px",
    fontWeight: "500",
  },
  inputWrapper: {
    position: "relative",
  },
  inputIcon: {
    position: "absolute",
    left: "10px",
    top: "50%",
    transform: "translateY(-50%)",
  },
  input: {
    width: "100%",
    padding: "10px 10px 10px 35px",
    fontSize: "14px",
    border: "1px solid #e0e6ed",
    borderRadius: "4px",
    outline: "none",
    transition: "all 0.2s ease",
    boxSizing: "border-box",
  },
  formActions: {
    display: "flex",
    justifyContent: "flex-end",
    gap: "10px",
    marginTop: "20px",
  },
  cancelButton: {
    backgroundColor: "#e0e6ed",
    color: "#4a5568",
    border: "none",
    padding: "10px 15px",
    borderRadius: "4px",
    fontSize: "14px",
    fontWeight: "500",
    cursor: "pointer",
  },
  saveButton: {
    backgroundColor: "#005DAB",
    color: "white",
    border: "none",
    padding: "10px 15px",
    borderRadius: "4px",
    fontSize: "14px",
    fontWeight: "500",
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
  },
  errorMessage: {
    backgroundColor: "#FEE2E2",
    color: "#B91C1C",
    padding: "10px 15px",
    borderRadius: "4px",
    marginBottom: "15px",
    fontSize: "14px",
  },
  successMessage: {
    backgroundColor: "#D1FAE5",
    color: "#065F46",
    padding: "10px 15px",
    borderRadius: "4px",
    marginBottom: "15px",
    fontSize: "14px",
  },
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
  }
};

export default Perfil;