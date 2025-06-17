import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

function CompletarPerfil() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    carreraId: '',
    cicloActual: '',
    departamentoId: '',
    telefono: ''
  });
  
  const [usuario, setUsuario] = useState(null);
  const [carreras, setCarreras] = useState([]);
  const [departamentos, setDepartamentos] = useState([]);
  
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
  
  const token = searchParams.get('token');
  const isNewUser = searchParams.get('new') === 'true';

  useEffect(() => {
    if (!token) {
      navigate('/');
      return;
    }
    
    loadData();
  }, [token]);

  const loadData = async () => {
    try {
      // Cargar usuario
      const userResponse = await fetch('http://localhost:8080/api/auth/user', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      
      if (userResponse.ok) {
        const userData = await userResponse.json();
        setUsuario(userData);
        
        setFormData({
          carreraId: userData.carreraId || '',
          cicloActual: userData.cicloActual || '',
          departamentoId: userData.departamentoId || '',
          telefono: userData.telefono || ''
        });
      }

      // Cargar carreras desde BD
      const carrerasResponse = await fetch('http://localhost:8080/api/carreras/activas');
      if (carrerasResponse.ok) {
        const carrerasData = await carrerasResponse.json();
        setCarreras(carrerasData.carreras || []);
      }

      // Cargar departamentos desde BD
      const departamentosResponse = await fetch('http://localhost:8080/api/departamentos/activos');
      if (departamentosResponse.ok) {
        const departamentosData = await departamentosResponse.json();
        setDepartamentos(departamentosData.departamentos || []);
      }

    } catch (error) {
      console.error('Error cargando datos:', error);
      setError('Error al cargar la información');
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    if (error) setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validaciones
    if (!formData.carreraId) {
      setError('Debe seleccionar una carrera');
      return;
    }
    
    if (!formData.cicloActual) {
      setError('Debe seleccionar un ciclo');
      return;
    }
    
    if (!formData.departamentoId) {
      setError('Debe seleccionar un departamento');
      return;
    }
    
    setIsSubmitting(true);
    setError('');

    try {
      const response = await fetch(`http://localhost:8080/api/usuarios/${usuario.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          ...usuario,
          carreraId: parseInt(formData.carreraId),
          cicloActual: parseInt(formData.cicloActual),
          departamentoId: parseInt(formData.departamentoId),
          telefono: formData.telefono || null
        })
      });

      if (response.ok) {
        localStorage.setItem('token', token);
        navigate('/home');
      } else {
        const errorData = await response.text();
        setError('Error al guardar: ' + errorData);
      }
    } catch (error) {
      setError('Error de conexión. Intenta nuevamente.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        fontFamily: 'Arial, sans-serif'
      }}>
        <div style={{ textAlign: 'center' }}>
          <div style={{
            width: '40px',
            height: '40px',
            border: '4px solid #f3f3f3',
            borderTop: '4px solid #005DAB',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite',
            margin: '0 auto 20px'
          }}></div>
          <p>Cargando información...</p>
          <style>{`
            @keyframes spin {
              0% { transform: rotate(0deg); }
              100% { transform: rotate(360deg); }
            }
          `}</style>
        </div>
      </div>
    );
  }

  return (
    <div style={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '20px',
      fontFamily: 'Arial, sans-serif'
    }}>
      <div style={{
        background: 'white',
        borderRadius: '20px',
        boxShadow: '0 20px 40px rgba(0,0,0,0.1)',
        maxWidth: '600px',
        width: '100%',
        padding: '40px'
      }}>
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: '30px' }}>
          <h1 style={{ fontSize: '28px', color: '#333', margin: '0 0 10px 0' }}>
            {isNewUser ? '¡Bienvenido a TecBook!' : 'Completar Perfil'}
          </h1>
          <p style={{ color: '#666', margin: 0 }}>
            {isNewUser 
              ? `Hola ${usuario?.nombre}! Completa tu información académica.`
              : 'Completa la información faltante.'
            }
          </p>
        </div>

        {/* Usuario Info */}
        {usuario && (
          <div style={{
            background: '#f8f9fa',
            borderRadius: '12px',
            padding: '20px',
            marginBottom: '30px',
            display: 'flex',
            alignItems: 'center',
            gap: '15px'
          }}>
            {usuario.profileImageUrl ? (
              <img 
                src={usuario.profileImageUrl} 
                alt="Perfil" 
                style={{ width: '50px', height: '50px', borderRadius: '50%' }}
              />
            ) : (
              <div style={{
                width: '50px',
                height: '50px',
                background: '#005DAB',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: 'white',
                fontWeight: 'bold'
              }}>
                {usuario.nombre?.charAt(0)}{usuario.apellidos?.charAt(0)}
              </div>
            )}
            <div>
              <h3 style={{ margin: '0 0 5px 0', fontSize: '18px' }}>
                {usuario.nombre} {usuario.apellidos}
              </h3>
              <p style={{ margin: 0, fontSize: '14px', color: '#666' }}>
                {usuario.correoInstitucional}
              </p>
            </div>
          </div>
        )}

        {/* Error */}
        {error && (
          <div style={{
            background: '#fee2e2',
            color: '#dc2626',
            padding: '15px',
            borderRadius: '8px',
            marginBottom: '20px'
          }}>
            {error}
          </div>
        )}

        {/* Formulario */}
        <div onSubmit={handleSubmit}>
          {/* Carrera */}
          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold' }}>
              Carrera *
            </label>
            <select
              name="carreraId"
              value={formData.carreraId}
              onChange={handleChange}
              required
              style={{
                width: '100%',
                padding: '12px',
                border: '2px solid #e2e8f0',
                borderRadius: '8px',
                fontSize: '16px'
              }}
            >
              <option value="">Selecciona tu carrera</option>
              {carreras.map(carrera => (
                <option key={carrera.id} value={carrera.id}>
                  {carrera.nombre}
                </option>
              ))}
            </select>
          </div>

          {/* Ciclo y Departamento */}
          <div style={{
            display: 'grid',
            gridTemplateColumns: '1fr 1fr',
            gap: '20px',
            marginBottom: '20px'
          }}>
            <div>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold' }}>
                Ciclo Actual *
              </label>
              <select
                name="cicloActual"
                value={formData.cicloActual}
                onChange={handleChange}
                required
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '2px solid #e2e8f0',
                  borderRadius: '8px',
                  fontSize: '16px'
                }}
              >
                <option value="">Ciclo</option>
                {[1,2,3,4,5,6].map(ciclo => (
                  <option key={ciclo} value={ciclo}>
                    {ciclo}° Ciclo
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold' }}>
                Departamento *
              </label>
              <select
                name="departamentoId"
                value={formData.departamentoId}
                onChange={handleChange}
                required
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '2px solid #e2e8f0',
                  borderRadius: '8px',
                  fontSize: '16px'
                }}
              >
                <option value="">Selecciona departamento</option>
                {departamentos.map(depto => (
                  <option key={depto.id} value={depto.id}>
                    {depto.nombre}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Teléfono */}
          <div style={{ marginBottom: '30px' }}>
            <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold' }}>
              Teléfono (Opcional)
            </label>
            <input
              type="tel"
              name="telefono"
              value={formData.telefono}
              onChange={handleChange}
              placeholder="Ej: +51 999 888 777"
              style={{
                width: '100%',
                padding: '12px',
                border: '2px solid #e2e8f0',
                borderRadius: '8px',
                fontSize: '16px'
              }}
            />
          </div>

          {/* Botón */}
          <button
            onClick={handleSubmit}
            disabled={isSubmitting}
            style={{
              width: '100%',
              background: isSubmitting ? '#94a3b8' : '#005DAB',
              color: 'white',
              padding: '15px',
              border: 'none',
              borderRadius: '8px',
              fontSize: '18px',
              fontWeight: 'bold',
              cursor: isSubmitting ? 'not-allowed' : 'pointer'
            }}
          >
            {isSubmitting ? 'Guardando...' : 'Completar Perfil'}
          </button>
        </div>

        <div style={{ textAlign: 'center', marginTop: '20px', fontSize: '14px', color: '#666' }}>
          Los campos marcados con * son obligatorios
        </div>
      </div>
    </div>
  );
}

export default CompletarPerfil;