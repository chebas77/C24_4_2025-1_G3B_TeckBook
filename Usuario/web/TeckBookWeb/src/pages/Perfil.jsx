import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  User,
  Mail,
  BookOpen,
  Building,
  Book,
  Users,
  Save,
  ArrowLeft,
  Edit3,
  Upload,
  Camera,
  Phone,
  Calendar,
  GraduationCap
} from 'lucide-react';
import "../css/Perfil.css";
import Header from '../components/Header';
// ✅ Usar servicios centralizados
import { API_CONFIG, ROUTES } from '../config/apiConfig';
import apiService from '../services/apiService'; // Ajusta la ruta según tu estructura
import { ENDPOINTS } from '../config/apiConfig';

function Perfil() {
  const [userData, setUserData] = useState({
    id: "",
    nombre: "",
    apellidos: "",
    correoInstitucional: "",
    telefono: "",
    cicloActual: "",
    rol: "",
    departamentoId: "",
    carreraId: "",
    seccionId: "",
    profileImageUrl: "",
    // Datos adicionales para mostrar nombres en lugar de IDs
    departamentoNombre: "",
    carreraNombre: "",
    seccionNombre: ""
  });

  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null); // Nuevo estado para el archivo seleccionado
  const [previewUrl, setPreviewUrl] = useState(null); // Nuevo estado para la preview
  const fileInputRef = useRef(null);

  // Estados para los selects del formulario
  const [departamentos, setDepartamentos] = useState([]);
  const [carreras, setCarreras] = useState([]);
  const [secciones, setSecciones] = useState([]);

  const navigate = useNavigate();

  // Obtener datos del usuario al cargar
useEffect(() => {
  let isMounted = true;
  const token = localStorage.getItem('token');

  if (!token) {
    navigate(ROUTES.PUBLIC.LOGIN);
    return;
  }

  const fetchUserData = async () => {
  try {
    const data = await apiService.get(ENDPOINTS.AUTH.GET_USER);

    if (!isMounted) return;

    setUserData({
      ...data,
      profileImageUrl: data.profileImageUrl || '',
      cicloActual: data.cicloActual || data.ciclo || '',
      telefono: data.telefono || '',
      departamentoNombre: data.departamentoNombre || '',
      carreraNombre: data.carreraNombre || '',
      seccionNombre: data.seccionNombre || ''
    });

  } catch (error) {
    if (isMounted) {
      console.error('🔴 Error al obtener datos del usuario:', error.message);
      setError(error.message);
      setTimeout(() => {
        localStorage.removeItem('token');
        navigate(ROUTES.PUBLIC.LOGIN);
      }, 3000);
    }
  } finally {
    if (isMounted) setIsLoading(false);
  }
};

fetchUserData();

return () => {
  isMounted = false;
};
}, [navigate]);



  // Cargar datos para los selects cuando se abre el modal de edición
  useEffect(() => {
    if (isEditing) {
      fetchFormData();
      // Si el usuario ya tiene una imagen, establecerla como preview inicial
      if (userData.profileImageUrl) {
        setPreviewUrl(userData.profileImageUrl);
      }
    } else {
      // Limpiar preview y selectedFile al cerrar el modal
      setSelectedFile(null);
      setPreviewUrl(null);
    }
  }, [isEditing, userData.profileImageUrl]); // Añadido userData.profileImageUrl para reaccionar a cambios iniciales

 const fetchFormData = async () => {
  try {
    // ✅ Cargar departamentos activos
    const deptData = await apiService.get(ENDPOINTS.DEPARTAMENTOS.ACTIVOS);
    setDepartamentos(deptData.departamentos || deptData || []);

    // ✅ Cargar carreras activas
    const carrerasData = await apiService.get(ENDPOINTS.CARRERAS.ACTIVAS);
    setCarreras(carrerasData.carreras || carrerasData || []);

    // ✅ Cargar secciones si hay carrera y ciclo seleccionado
    if (userData?.carreraId && userData?.cicloId) {
      const seccionesData = await apiService.get(
        ENDPOINTS.SECCIONES.BY_CARRERA_CICLO(userData.carreraId, userData.cicloId)
      );
      setSecciones(seccionesData.secciones || seccionesData || []);
    }

  } catch (error) {
    console.error('❌ Error al cargar datos del formulario:', error);
  }
};



  const handleChange = (e) => {
    const { name, value } = e.target;
    setUserData(prev => ({
      ...prev,
      [name]: value
    }));

    // Si cambia el departamento, cargar nuevas secciones
    if (name === 'departamentoId' && value) {
      loadSecciones(value);
      // Limpiar seccion actual si cambia el departamento
      setUserData(prev => ({ ...prev, seccionId: '' }));
    }
  };

  const loadSecciones = async (departamentoId) => {
  try {
    const endpoint = ENDPOINTS.SECCIONES.BY_CARRERA(departamentoId); // o ajusta según tu backend si es BY_DEPARTAMENTO
    const data = await apiService.get(endpoint);
    setSecciones(data.secciones || data || []);
  } catch (error) {
    console.error('❌ Error al cargar secciones:', error);
  }
};

  const handleImageSelect = (e) => {
  const file = e.target.files[0];

  if (!file) {
    setSelectedFile(null);
    setPreviewUrl(userData.profileImageUrl || null);
    return;
  }

  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  if (!validTypes.includes(file.type)) {
    setError('Formato de imagen no válido. Usa JPG, PNG, GIF o WebP.');
    setSelectedFile(null);
    setPreviewUrl(null);
    return;
  }

  if (file.size > 5 * 1024 * 1024) {
    setError('La imagen es demasiado grande. Máximo 5MB.');
    setSelectedFile(null);
    setPreviewUrl(null);
    return;
  }

  setError(null);
  setSuccess(null);
  setSelectedFile(file);

  const reader = new FileReader();
  reader.onload = (e) => {
    setPreviewUrl(e.target.result);
  };
  reader.readAsDataURL(file);

  console.log(`📷 Imagen seleccionada: ${file.name} | Tamaño: ${(file.size / 1024).toFixed(2)} KB`);
};


  // Función para subir la imagen al servidor
  
  
const uploadImageToServer = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  const token = localStorage.getItem('token'); // 🔧 aseguramos que el token esté definido

  try {
    const response = await fetch(`${API_CONFIG.API_BASE_URL}/api/upload/profile-image`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`
      },
      body: formData
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error || 'Error al subir imagen');
    }

    const data = await response.json();
    return data.imageUrl;

  } catch (err) {
    console.error('❌ Error al subir imagen:', err);
    throw err;
  }
};


const handleRemovePhoto = async () => {
  if (!confirm("¿Estás seguro de que deseas eliminar tu foto de perfil?")) {
    return;
  }

  try {
    setIsUploading(true);
    setError(null);
    setSuccess(null);

    console.log('🗑️ Eliminando imagen de perfil...');

    await apiService.delete(ENDPOINTS.UPLOAD.IMAGE);

    console.log('✅ Imagen eliminada exitosamente');

    setUserData(prev => ({
      ...prev,
      profileImageUrl: null
    }));
    setSelectedFile(null);
    setPreviewUrl(null);
    setSuccess('Foto de perfil eliminada correctamente');
  } catch (error) {
    console.error('❌ Error al eliminar imagen:', error);
    setError(`Error al eliminar imagen: ${error.message}`);
  } finally {
    setIsUploading(false);
  }
};


  // Función para obtener iniciales del usuario
  const getUserInitials = () => {
    return userData?.nombre && userData?.apellidos
      ? `${userData.nombre.charAt(0)}${userData.apellidos.charAt(0)}`.toUpperCase()
      : 'GS';
  };

  // Función para renderizar el avatar
  const renderAvatar = () => {
    // Determinar qué imagen mostrar: preview, actual, o iniciales
    const imageSource = previewUrl || userData.profileImageUrl;

    return (
      <div className="avatar-container">
        {imageSource ? (
          <img
            src={imageSource}
            alt="Foto de perfil"
            className="avatar-image"
          />
        ) : (
          <div className="avatar">
            {getUserInitials()}
          </div>
        )}

        {isEditing && (
          <button
            onClick={() => fileInputRef.current.click()}
            className="change-photo-button"
            disabled={isUploading}
          >
            {isUploading ? '...' : (imageSource ? <Camera size={16} /> : <Upload size={16} />)}
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
    setIsUploading(true);

    let finalProfileImageUrl = userData.profileImageUrl;

    // 1. Subir nueva imagen si se seleccionó
    if (selectedFile) {
      console.log('📤 Subiendo nueva imagen antes de guardar perfil...');
      finalProfileImageUrl = await uploadImageToServer(selectedFile);
      console.log('✅ Nueva imagen subida:', finalProfileImageUrl);
    } else if (!previewUrl && userData.profileImageUrl) {
      // Si se eliminó la imagen (previewUrl === null)
      console.log('🗑️ Se eliminará la imagen existente');
      finalProfileImageUrl = null;
    }

    // 2. Preparar los datos a enviar
    const dataToSend = {
      nombre: userData.nombre,
      apellidos: userData.apellidos,
      telefono: userData.telefono,
      cicloActual: userData.cicloActual,
      departamentoId: userData.departamentoId ? parseInt(userData.departamentoId) : null,
      carreraId: userData.carreraId ? parseInt(userData.carreraId) : null,
      seccionId: userData.seccionId ? parseInt(userData.seccionId) : null,
      profileImageUrl: finalProfileImageUrl
    };

    console.log('📦 Enviando datos del perfil:', dataToSend);

    // 3. Enviar datos al backend
    const updatedUser = await apiService.put(
      ENDPOINTS.USERS.UPDATE(userData.id),
      dataToSend
    );

    // 4. Actualizar estado local
    setUserData(prev => ({
      ...prev,
      ...updatedUser,
      profileImageUrl: finalProfileImageUrl
    }));

    // 5. Limpiar imagen
    setSelectedFile(null);
    setPreviewUrl(finalProfileImageUrl);

    setSuccess('Perfil actualizado correctamente');
    setIsEditing(false);

    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }

  } catch (err) {
    console.error('❌ Error al guardar perfil:', err);
    setError(`Error al actualizar perfil: ${err.message}`);
  } finally {
    setIsUploading(false);
  }
};


  
const handleCancel = () => {
  // Limpiar estados temporales de imagen y errores/éxitos
  setSelectedFile(null);
  setPreviewUrl(null);
  setError(null);
  setSuccess(null);

  if (fileInputRef.current) {
    fileInputRef.current.value = '';
  }

  // Recargar datos originales del usuario
  const fetchUserData = async () => {
    try {
      const data = await apiService.get(ENDPOINTS.AUTH.GET_USER);

      setUserData({
        ...data,
        profileImageUrl: data.profileImageUrl || "",
        cicloActual: data.cicloActual || data.ciclo || "",
        telefono: data.telefono || ""
      });

      setIsEditing(false); // Cerrar edición
    } catch (error) {
      setError(error.message);
    }
  };

  fetchUserData();
};

  // Fu// Funciones para obtener nombres desde IDs
const getDepartamentoNombre = () => {
  if (userData?.departamentoNombre) return userData.departamentoNombre;

  const dept = departamentos?.find(d => d.id === parseInt(userData?.departamentoId));
  return dept ? dept.nombre : userData?.departamentoId || "No especificado";
};

const getCarreraNombre = () => {
  if (userData?.carreraNombre) return userData.carreraNombre;

  const carrera = carreras?.find(c => c.id === parseInt(userData?.carreraId));
  return carrera ? carrera.nombre : userData?.carreraId || "No especificado";
};

const getSeccionNombre = () => {
  if (userData?.seccionNombre) return userData.seccionNombre;

  const seccion = secciones?.find(s => s.id === parseInt(userData?.seccionId));
  return seccion ? seccion.nombre : userData?.seccionId || "No especificado";
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
                onChange={handleImageSelect}
                accept="image/jpeg,image/png,image/gif,image/webp"
                style={{ display: 'none' }}
              />

              <div className="profile-info">
                <h2 className="profile-name">{`${userData.nombre} ${userData.apellidos}`}</h2>
                <p className="profile-role">{userData.rol}</p>
                <p className="profile-email">{userData.correoInstitucional}</p>
              </div>
              <div className="edit-button-container">
                <button
                  onClick={() => setIsEditing(true)}
                  className="edit-button"
                >
                  <Edit3 size={16} />
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
              <h3 className="section-title">
                <User className="section-icon" />
                Información Personal
              </h3>
              <div className="info-grid">
                <div className="info-item">
                  <p className="info-label">Nombre Completo</p>
                  <div className="info-field">
                    <User size={16} className="field-icon" />
                    <span className="field-value">{`${userData.nombre} ${userData.apellidos}`}</span>
                  </div>
                </div>
                <div className="info-item">
                  <p className="info-label">Correo Institucional</p>
                  <div className="info-field">
                    <Mail size={16} className="field-icon" />
                    <span className="field-value">{userData.correoInstitucional}</span>
                  </div>
                </div>
                <div className="info-item">
                  <p className="info-label">Teléfono</p>
                  <div className="info-field">
                    <Phone size={16} className="field-icon" />
                    <span className="field-value">{userData.telefono || "No especificado"}</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="info-section">
              <h3 className="section-title">
                <GraduationCap className="section-icon" />
                Información Académica
              </h3>
              <div className="info-grid">
                <div className="info-item">
                  <p className="info-label">Ciclo Actual</p>
                  <div className="info-field">
                    <Calendar size={16} className="field-icon" />
                    <span className="field-value">{userData.cicloActual || "No especificado"}</span>
                  </div>
                </div>
                <div className="info-item">
                  <p className="info-label">Rol</p>
                  <div className="info-field">
                    <Users size={16} className="field-icon" />
                    <span className="field-value">{userData.rol}</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="info-section">
              <h3 className="section-title">
                <Building className="section-icon" />
                Información Institucional
              </h3>
              <div className="info-grid">
                <div className="info-item">
                  <p className="info-label">Departamento</p>
                  <div className="info-field">
                    <Building size={16} className="field-icon" />
                    <span className="field-value">{getDepartamentoNombre()}</span>
                  </div>
                </div>
                <div className="info-item">
                  <p className="info-label">Carrera</p>
                  <div className="info-field">
                    <Book size={16} className="field-icon" />
                    <span className="field-value">{getCarreraNombre()}</span>
                  </div>
                </div>
                <div className="info-item">
                  <p className="info-label">Sección</p>
                  <div className="info-field">
                    <Users size={16} className="field-icon" />
                    <span className="field-value">{getSeccionNombre()}</span>
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
                        {previewUrl || userData.profileImageUrl ? (
                          <img
                            src={previewUrl || userData.profileImageUrl}
                            alt="Foto de perfil"
                            className="current-photo"
                          />
                        ) : (
                          <div className="photo-placeholder">
                            {getUserInitials()}
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
                              <Upload size={16} />
                              {previewUrl || userData.profileImageUrl ? 'Cambiar foto' : 'Subir foto'}
                            </>
                          )}
                        </button>
                        {(previewUrl || userData.profileImageUrl) && ( // Solo muestra el botón si hay una imagen que eliminar/cambiar
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
                          <User size={18} className="input-icon" />
                          <input
                            type="text"
                            name="nombre"
                            value={userData.nombre || ''}
                            onChange={handleChange}
                            className="input"
                            required
                          />
                        </div>
                      </div>
                      <div className="input-container">
                        <label className="label">Apellidos</label>
                        <div className="input-wrapper">
                          <User size={18} className="input-icon" />
                          <input
                            type="text"
                            name="apellidos"
                            value={userData.apellidos || ''}
                            onChange={handleChange}
                            className="input"
                            required
                          />
                        </div>
                      </div>
                    </div>
                    <div className="form-row">
                      <div className="input-container">
                        <label className="label">Teléfono</label>
                        <div className="input-wrapper">
                          <Phone size={18} className="input-icon" />
                          <input
                            type="tel"
                            name="telefono"
                            value={userData.telefono || ''}
                            onChange={handleChange}
                            placeholder="Ej: +51 999 999 999"
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
                        <label className="label">Correo Institucional</label>
                        <div className="input-wrapper">
                          <Mail size={18} className="input-icon" />
                          <input
                            type="email"
                            name="correoInstitucional"
                            value={userData.correoInstitucional || ''}
                            disabled={true}
                            className="input"
                            style={{ backgroundColor: '#f1f5f9', cursor: 'not-allowed' }}
                          />
                        </div>
                      </div>
                      <div className="input-container">
                        <label className="label">Ciclo Actual</label>
                        <div className="input-wrapper">
                          <Calendar size={18} className="input-icon" />
                          <input
                            type="text"
                            name="cicloActual"
                            value={userData.cicloActual || ''}
                            onChange={handleChange}
                            placeholder="Ej: 2024-1"
                            className="input"
                          />
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="form-section">
                    <h4 className="form-section-title">Información Institucional</h4>
                    <div className="form-row">
                      <div className="input-container">
                        <label className="label">Departamento</label>
                        <div className="input-wrapper">
                          <Building size={18} className="input-icon" />
                          <select
                            name="departamentoId"
                            value={userData.departamentoId || ''}
                            onChange={handleChange}
                            className="input"
                          >
                            <option value="">Selecciona un departamento</option>
                            {departamentos.map(dept => (
                              <option key={dept.id} value={dept.id}>
                                {dept.nombre}
                              </option>
                            ))}
                          </select>
                        </div>
                      </div>
                      <div className="input-container">
                        <label className="label">Carrera</label>
                        <div className="input-wrapper">
                          <Book size={18} className="input-icon" />
                          <select
                            name="carreraId"
                            value={userData.carreraId || ''}
                            onChange={handleChange}
                            className="input"
                          >
                            <option value="">Selecciona una carrera</option>
                            {carreras.map(carrera => (
                              <option key={carrera.id} value={carrera.id}>
                                {carrera.nombre}
                              </option>
                            ))}
                          </select>
                        </div>
                      </div>
                    </div>

                    <div className="form-row">
                      <div className="input-container">
                        <label className="label">Sección</label>
                        <div className="input-wrapper">
                          <Users size={18} className="input-icon" />
                          <select
                            name="seccionId"
                            value={userData.seccionId || ''}
                            onChange={handleChange}
                            disabled={!userData.departamentoId}
                            className="input"
                          >
                            <option value="">Selecciona una sección</option>
                            {secciones.map(seccion => (
                              <option key={seccion.id} value={seccion.id}>
                                {seccion.nombre}
                              </option>
                            ))}
                          </select>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="form-actions">
                    <button type="button" onClick={handleCancel} className="cancel-button">
                      Cancelar
                    </button>
                    <button type="submit" className="save-button" disabled={isUploading}>
                      <Save size={18} />
                      {isUploading ? 'Guardando...' : 'Guardar Cambios'}
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