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
      navigate('/');
      return;
    }

    const fetchUserData = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/auth/user', {
          headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error('No se pudo obtener la información del usuario');

        const data = await response.json();
        if (!isMounted) return;

        setUserData({
          ...data,
          profileImageUrl: data.profileImageUrl || "",
          cicloActual: data.cicloActual || data.ciclo || "",
          telefono: data.telefono || "",
          departamentoNombre: data.departamentoNombre || "",
          carreraNombre: data.carreraNombre || "",
          seccionNombre: data.seccionNombre || ""
        });

      } catch (error) {
        if (isMounted) {
          setError(error.message);
          setTimeout(() => {
            localStorage.removeItem('token');
            navigate('/');
          }, 3000);
        }
      } finally {
        if (isMounted) setIsLoading(false);
      }
    };

    fetchUserData();
    return () => { isMounted = false; };
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
      const token = localStorage.getItem('token');

      // Cargar departamentos
      const deptResponse = await fetch('http://localhost:8080/api/departamentos', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (deptResponse.ok) {
        const deptData = await deptResponse.json();
        setDepartamentos(deptData.departamentos || deptData || []);
      }

      // Cargar carreras
      const carrerasResponse = await fetch('http://localhost:8080/api/carreras/activas', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (carrerasResponse.ok) {
        const carrerasData = await carrerasResponse.json();
        setCarreras(carrerasData.carreras || carrerasData || []);
      }

      // Cargar secciones si hay departamento seleccionado
      if (userData.departamentoId) {
        const seccionesResponse = await fetch(`http://localhost:8080/api/departamentos/${userData.departamentoId}/secciones`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (seccionesResponse.ok) {
          const seccionesData = await seccionesResponse.json();
          setSecciones(seccionesData.secciones || seccionesData || []);
        }
      }
    } catch (error) {
      console.error('Error al cargar datos del formulario:', error);
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
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/departamentos/${departamentoId}/secciones`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        setSecciones(data.secciones || data || []);
      }
    } catch (error) {
      console.error('Error al cargar secciones:', error);
    }
  };

  // Función para manejar la selección de imágenes (sin subir todavía)
  const handleImageSelect = (e) => { // Removed async since no API call here
    const file = e.target.files[0];
    if (!file) {
      setSelectedFile(null);
      setPreviewUrl(userData.profileImageUrl || null); // Revert to original or null
      return;
    }

    // Validar tipo y tamaño
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
    if (!validTypes.includes(file.type)) {
      setError('Formato de imagen no válido. Usa JPG, PNG, GIF o WebP.');
      setSelectedFile(null); // Clear selected file
      setPreviewUrl(null); // Clear preview
      return;
    }

    if (file.size > 5 * 1024 * 1024) { // 5MB
      setError('La imagen es demasiado grande. Máximo 5MB.');
      setSelectedFile(null); // Clear selected file
      setPreviewUrl(null); // Clear preview
      return;
    }

    // Clear previous errors/success related to image
    setError(null);
    setSuccess(null);

    // Guardar el archivo seleccionado
    setSelectedFile(file);

    // Crear preview de la imagen
    const reader = new FileReader();
    reader.onload = (e) => {
      setPreviewUrl(e.target.result);
    };
    reader.readAsDataURL(file);

    console.log('Imagen seleccionada:', file.name, 'Tamaño:', file.size);
  };

  // Función para subir la imagen al servidor
  const uploadImageToServer = async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    const token = localStorage.getItem('token');
    console.log('Subiendo imagen al servidor:', file.name);

    const response = await fetch('http://localhost:8080/api/upload/profile-image', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      },
      body: formData
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({ error: 'Error desconocido' }));
      throw new Error(errorData.error || `Error ${response.status}: ${response.statusText}`);
    }

    const data = await response.json();
    console.log('Imagen subida exitosamente:', data);
    return data.imageUrl || data.url || data.profileImageUrl;
  };

  // Corrección: Encapsular el contenido en la función
  const handleRemovePhoto = async () => {
    if (!confirm("¿Estás seguro de que deseas eliminar tu foto de perfil?")) {
      return;
    }

    try {
      setIsUploading(true);
      setError(null);
      setSuccess(null);

      const token = localStorage.getItem('token');
      console.log('Eliminando imagen con token:', token ? 'Token presente' : 'Sin token');

      const response = await fetch('http://localhost:8080/api/upload/profile-image', {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      console.log('Respuesta del servidor (eliminar):', response.status, response.statusText);

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ error: 'Error desconocido' }));
        console.error('Error del servidor:', errorData);
        throw new Error(errorData.error || `Error ${response.status}: ${response.statusText}`);
      }

      console.log('Imagen eliminada exitosamente');

      // Actualizar el estado local para reflejar la eliminación
      setUserData(prev => ({
        ...prev,
        profileImageUrl: null
      }));
      setSelectedFile(null); // Clear selected file
      setPreviewUrl(null); // Clear preview

      setSuccess('Foto de perfil eliminada correctamente');

    } catch (error) {
      console.error('Error completo:', error);
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
      setIsUploading(true); // Activar carga al iniciar el submit
      const token = localStorage.getItem('token');
      let finalProfileImageUrl = userData.profileImageUrl; // Imagen actual como base

      // 1. Si hay una nueva imagen seleccionada, subirla
      if (selectedFile) {
        console.log('Subiendo nueva imagen antes de guardar perfil...');
        finalProfileImageUrl = await uploadImageToServer(selectedFile);
        console.log('Nueva imagen subida y URL:', finalProfileImageUrl);
      } else if (previewUrl === null && userData.profileImageUrl !== null) {
        // 2. Si el previewUrl es null pero userData.profileImageUrl NO es null (es decir, se eliminó la foto existente)
        console.log('Se ha marcado para eliminar la foto de perfil existente.');
        // No necesitamos llamar a la API DELETE aquí, ya que el backend debería manejar
        // la eliminación si profileImageUrl se envía como null en la actualización del usuario.
        finalProfileImageUrl = null;
      }
      // Si previewUrl es igual a userData.profileImageUrl y no hay selectedFile,
      // significa que no se cambió la foto, entonces finalProfileImageUrl ya es correcta.

      // 3. Preparar datos para enviar (incluir la URL final de la imagen)
      const dataToSend = {
        nombre: userData.nombre,
        apellidos: userData.apellidos,
        telefono: userData.telefono,
        cicloActual: userData.cicloActual,
        departamentoId: userData.departamentoId ? parseInt(userData.departamentoId) : null,
        carreraId: userData.carreraId ? parseInt(userData.carreraId) : null,
        seccionId: userData.seccionId ? parseInt(userData.seccionId) : null,
        profileImageUrl: finalProfileImageUrl // Enviar la URL final (nueva, existente, o null si se eliminó)
      };

      console.log('Enviando datos del perfil:', dataToSend);

      // 4. Actualizar el perfil en el backend
      const response = await fetch(`http://localhost:8080/api/usuarios/${userData.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(dataToSend)
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || 'Error al actualizar el perfil');
      }

      const updatedUserData = await response.json();

      // 5. Actualizar estado local con datos actualizados
      setUserData(prev => ({
        ...prev,
        ...updatedUserData,
        profileImageUrl: finalProfileImageUrl // Asegurar que la imagen se actualice con la URL final
      }));

      // 6. Limpiar estados temporales de imagen
      setSelectedFile(null);
      setPreviewUrl(finalProfileImageUrl); // Establecer la preview a la imagen final

      setSuccess('Perfil actualizado correctamente');
      setIsEditing(false);

      // Limpiar el input file si no es necesario (se limpia al cerrar el modal)
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }

    } catch (err) {
      console.error('Error al guardar perfil:', err);
      setError(`Error al actualizar perfil: ${err.message}`);
    } finally {
      setIsUploading(false); // Desactivar carga al finalizar
    }
  };

  const handleCancel = () => {
    // Limpiar estados temporales de imagen y errores/éxitos
    setSelectedFile(null);
    setPreviewUrl(null); // Importante limpiar para no mostrar la preview de una imagen no guardada
    setError(null);
    setSuccess(null);
    // Limpiar el input file si no se ha guardado
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }

    // Recargar datos originales del usuario
    const fetchUserData = async () => {
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
        setUserData({
          ...data,
          profileImageUrl: data.profileImageUrl || "",
          cicloActual: data.cicloActual || data.ciclo || "",
          telefono: data.telefono || ""
        });
        setIsEditing(false); // Cerrar el modal de edición
      } catch (error) {
        setError(error.message);
      }
    };

    fetchUserData();
  };

  // Funciones para obtener nombres de IDs
  const getDepartamentoNombre = () => {
    if (userData.departamentoNombre) return userData.departamentoNombre;
    const dept = departamentos.find(d => d.id === parseInt(userData.departamentoId));
    return dept ? dept.nombre : userData.departamentoId || "No especificado";
  };

  const getCarreraNombre = () => {
    if (userData.carreraNombre) return userData.carreraNombre;
    const carrera = carreras.find(c => c.id === parseInt(userData.carreraId));
    return carrera ? carrera.nombre : userData.carreraId || "No especificado";
  };

  const getSeccionNombre = () => {
    if (userData.seccionNombre) return userData.seccionNombre;
    const seccion = secciones.find(s => s.id === parseInt(userData.seccionId));
    return seccion ? seccion.nombre : userData.seccionId || "No especificado";
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