import React, { useState, useEffect } from 'react';
import AdminLayout from '../components/layout/AdminLayout';
import userService from '../services/userService';

const UsersPage = () => {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filtros, setFiltros] = useState({
    rol: '',
    activo: '',
    strikes: '',
    buscar: ''
  });
  const [pagination, setPagination] = useState({
    count: 0,
    next: null,
    previous: null,
    current_page: 1
  });

  useEffect(() => {
    loadUsuarios();
  }, [filtros]);

  const loadUsuarios = async (page = 1) => {
    try {
      setLoading(true);
      const data = await userService.getUsuarios({
        ...filtros,
        page,
        page_size: 20
      });
      
      setUsuarios(data.results || []);
      setPagination({
        count: data.count || 0,
        next: data.next,
        previous: data.previous,
        current_page: page
      });
      setError(null);
    } catch (err) {
      setError('Error al cargar usuarios');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (key, value) => {
    setFiltros(prev => ({
      ...prev,
      [key]: value
    }));
  };

  const handleAction = async (action, userId, motivo = '') => {
    try {
      let result;
      switch (action) {
        case 'strike':
          result = await userService.aplicarStrike(userId, motivo);
          break;
        case 'suspender':
          result = await userService.suspenderUsuario(userId, motivo);
          break;
        case 'reactivar':
          result = await userService.reactivarUsuario(userId);
          break;
        default:
          return;
      }

      alert(result.mensaje || 'Acción completada exitosamente');
      loadUsuarios(pagination.current_page);
    } catch (error) {
      alert(error.response?.data?.error || 'Error al realizar la acción');
    }
  };

  const getUserStatusBadge = (usuario) => {
    if (!usuario.activo) {
      return { text: 'Suspendido', color: '#dc2626', bg: '#fef2f2' };
    }
    if (!usuario.is_active) {
      return { text: 'Inactivo', color: '#6b7280', bg: '#f9fafb' };
    }
    return { text: 'Activo', color: '#059669', bg: '#ecfdf5' };
  };

  const getStrikesBadge = (strikes) => {
    if (strikes === 0) {
      return { text: '0', color: '#059669', bg: '#ecfdf5' };
    }
    if (strikes < 3) {
      return { text: strikes.toString(), color: '#d97706', bg: '#fef3c7' };
    }
    return { text: strikes.toString(), color: '#dc2626', bg: '#fef2f2' };
  };

  return (
    <AdminLayout>
      <div>
        <h2 style={{
          fontSize: '1.875rem',
          fontWeight: 'bold',
          color: '#111827',
          marginBottom: '1.5rem'
        }}>
          Gestión de Usuarios
        </h2>

        {/* Filtros */}
        <div style={{
          backgroundColor: 'white',
          borderRadius: '0.5rem',
          padding: '1.5rem',
          boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)',
          marginBottom: '1.5rem'
        }}>
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '1rem',
            alignItems: 'end'
          }}>
            <div>
              <label style={{ 
                display: 'block', 
                fontSize: '0.875rem', 
                fontWeight: '500', 
                color: '#374151',
                marginBottom: '0.5rem'
              }}>
                Rol
              </label>
              <select
                value={filtros.rol}
                onChange={(e) => handleFilterChange('rol', e.target.value)}
                style={{
                  width: '100%',
                  padding: '0.5rem',
                  border: '1px solid #d1d5db',
                  borderRadius: '0.375rem',
                  fontSize: '0.875rem'
                }}
              >
                <option value="">Todos los roles</option>
                <option value="ESTUDIANTE">Estudiante</option>
                <option value="PROFESOR">Profesor</option>
                <option value="ADMINISTRADOR">Administrador</option>
              </select>
            </div>

            <div>
              <label style={{ 
                display: 'block', 
                fontSize: '0.875rem', 
                fontWeight: '500', 
                color: '#374151',
                marginBottom: '0.5rem'
              }}>
                Estado
              </label>
              <select
                value={filtros.activo}
                onChange={(e) => handleFilterChange('activo', e.target.value)}
                style={{
                  width: '100%',
                  padding: '0.5rem',
                  border: '1px solid #d1d5db',
                  borderRadius: '0.375rem',
                  fontSize: '0.875rem'
                }}
              >
                <option value="">Todos los estados</option>
                <option value="true">Activos</option>
                <option value="false">Inactivos</option>
              </select>
            </div>

            <div>
              <label style={{ 
                display: 'block', 
                fontSize: '0.875rem', 
                fontWeight: '500', 
                color: '#374151',
                marginBottom: '0.5rem'
              }}>
                Strikes
              </label>
              <select
                value={filtros.strikes}
                onChange={(e) => handleFilterChange('strikes', e.target.value)}
                style={{
                  width: '100%',
                  padding: '0.5rem',
                  border: '1px solid #d1d5db',
                  borderRadius: '0.375rem',
                  fontSize: '0.875rem'
                }}
              >
                <option value="">Cualquier número</option>
                <option value="0">Sin strikes</option>
                <option value="1">1 strike</option>
                <option value="2">2 strikes</option>
                <option value="3">3+ strikes</option>
              </select>
            </div>

            <div>
              <label style={{ 
                display: 'block', 
                fontSize: '0.875rem', 
                fontWeight: '500', 
                color: '#374151',
                marginBottom: '0.5rem'
              }}>
                Buscar
              </label>
              <input
                type="text"
                placeholder="Nombre, apellido o correo..."
                value={filtros.buscar}
                onChange={(e) => handleFilterChange('buscar', e.target.value)}
                style={{
                  width: '100%',
                  padding: '0.5rem',
                  border: '1px solid #d1d5db',
                  borderRadius: '0.375rem',
                  fontSize: '0.875rem'
                }}
              />
            </div>
          </div>
        </div>

        {/* Tabla de usuarios */}
        {loading ? (
          <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: '300px'
          }}>
            <div style={{ textAlign: 'center' }}>
              <div style={{
                width: '40px',
                height: '40px',
                border: '4px solid #f3f4f6',
                borderTop: '4px solid #3b82f6',
                borderRadius: '50%',
                animation: 'spin 1s linear infinite',
                margin: '0 auto 1rem'
              }}></div>
              <p style={{ color: '#6b7280' }}>Cargando usuarios...</p>
            </div>
          </div>
        ) : error ? (
          <div style={{
            backgroundColor: '#fef2f2',
            border: '1px solid #fecaca',
            borderRadius: '0.5rem',
            padding: '1rem'
          }}>
            <p style={{ color: '#dc2626' }}>{error}</p>
          </div>
        ) : (
          <div style={{
            backgroundColor: 'white',
            borderRadius: '0.5rem',
            overflow: 'hidden',
            boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)'
          }}>
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead style={{ backgroundColor: '#f9fafb' }}>
                  <tr>
                    <th style={{ padding: '0.75rem', textAlign: 'left', fontSize: '0.875rem', fontWeight: '500', color: '#374151' }}>
                      Usuario
                    </th>
                    <th style={{ padding: '0.75rem', textAlign: 'left', fontSize: '0.875rem', fontWeight: '500', color: '#374151' }}>
                      Rol
                    </th>
                    <th style={{ padding: '0.75rem', textAlign: 'left', fontSize: '0.875rem', fontWeight: '500', color: '#374151' }}>
                      Estado
                    </th>
                    <th style={{ padding: '0.75rem', textAlign: 'left', fontSize: '0.875rem', fontWeight: '500', color: '#374151' }}>
                      Strikes
                    </th>
                    <th style={{ padding: '0.75rem', textAlign: 'left', fontSize: '0.875rem', fontWeight: '500', color: '#374151' }}>
                      Carrera
                    </th>
                    <th style={{ padding: '0.75rem', textAlign: 'left', fontSize: '0.875rem', fontWeight: '500', color: '#374151' }}>
                      Acciones
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {usuarios.map((usuario) => {
                    const statusBadge = getUserStatusBadge(usuario);
                    const strikesBadge = getStrikesBadge(usuario.strikes);
                    
                    return (
                      <tr key={usuario.id} style={{ borderTop: '1px solid #e5e7eb' }}>
                        <td style={{ padding: '0.75rem' }}>
                          <div>
                            <p style={{ fontWeight: '500', color: '#111827' }}>
                              {usuario.nombre} {usuario.apellidos}
                            </p>
                            <p style={{ fontSize: '0.875rem', color: '#6b7280' }}>
                              {usuario.correo_institucional}
                            </p>
                          </div>
                        </td>
                        <td style={{ padding: '0.75rem', fontSize: '0.875rem', color: '#374151' }}>
                          {usuario.rol}
                        </td>
                        <td style={{ padding: '0.75rem' }}>
                          <span style={{
                            padding: '0.25rem 0.75rem',
                            backgroundColor: statusBadge.bg,
                            color: statusBadge.color,
                            borderRadius: '9999px',
                            fontSize: '0.75rem',
                            fontWeight: '500'
                          }}>
                            {statusBadge.text}
                          </span>
                        </td>
                        <td style={{ padding: '0.75rem' }}>
                          <span style={{
                            padding: '0.25rem 0.75rem',
                            backgroundColor: strikesBadge.bg,
                            color: strikesBadge.color,
                            borderRadius: '9999px',
                            fontSize: '0.75rem',
                            fontWeight: '500'
                          }}>
                            {strikesBadge.text}
                          </span>
                        </td>
                        <td style={{ padding: '0.75rem', fontSize: '0.875rem', color: '#374151' }}>
                          {usuario.carrera_nombre || 'N/A'}
                        </td>
                        <td style={{ padding: '0.75rem' }}>
                          <UserActions 
                            usuario={usuario} 
                            onAction={handleAction}
                          />
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>

            {/* Paginación */}
            {pagination.count > 20 && (
              <div style={{
                padding: '1rem',
                borderTop: '1px solid #e5e7eb',
                display: 'flex',
                justifyContent: 'between',
                alignItems: 'center'
              }}>
                <p style={{ fontSize: '0.875rem', color: '#6b7280' }}>
                  Mostrando {usuarios.length} de {pagination.count} usuarios
                </p>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <button
                    onClick={() => loadUsuarios(pagination.current_page - 1)}
                    disabled={!pagination.previous}
                    style={{
                      padding: '0.5rem 1rem',
                      backgroundColor: pagination.previous ? '#3b82f6' : '#e5e7eb',
                      color: pagination.previous ? 'white' : '#9ca3af',
                      border: 'none',
                      borderRadius: '0.375rem',
                      cursor: pagination.previous ? 'pointer' : 'not-allowed'
                    }}
                  >
                    Anterior
                  </button>
                  <button
                    onClick={() => loadUsuarios(pagination.current_page + 1)}
                    disabled={!pagination.next}
                    style={{
                      padding: '0.5rem 1rem',
                      backgroundColor: pagination.next ? '#3b82f6' : '#e5e7eb',
                      color: pagination.next ? 'white' : '#9ca3af',
                      border: 'none',
                      borderRadius: '0.375rem',
                      cursor: pagination.next ? 'pointer' : 'not-allowed'
                    }}
                  >
                    Siguiente
                  </button>
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </AdminLayout>
  );
};

const UserActions = ({ usuario, onAction }) => {
  const [showModal, setShowModal] = useState(false);
  const [actionType, setActionType] = useState('');
  const [motivo, setMotivo] = useState('');

  const openModal = (type) => {
    setActionType(type);
    setMotivo('');
    setShowModal(true);
  };

  const handleSubmit = () => {
    if (actionType === 'reactivar' || motivo.trim()) {
      onAction(actionType, usuario.id, motivo);
      setShowModal(false);
    }
  };

  if (usuario.rol === 'ADMINISTRADOR') {
    return (
      <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>
        Sin acciones
      </span>
    );
  }

  return (
    <>
      <div style={{ display: 'flex', gap: '0.5rem' }}>
        {usuario.activo ? (
          <>
            <button
              onClick={() => openModal('strike')}
              style={{
                padding: '0.25rem 0.5rem',
                backgroundColor: '#f59e0b',
                color: 'white',
                border: 'none',
                borderRadius: '0.25rem',
                fontSize: '0.75rem',
                cursor: 'pointer'
              }}
            >
              Strike
            </button>
            <button
              onClick={() => openModal('suspender')}
              style={{
                padding: '0.25rem 0.5rem',
                backgroundColor: '#dc2626',
                color: 'white',
                border: 'none',
                borderRadius: '0.25rem',
                fontSize: '0.75rem',
                cursor: 'pointer'
              }}
            >
              Suspender
            </button>
          </>
        ) : (
          <button
            onClick={() => onAction('reactivar', usuario.id)}
            style={{
              padding: '0.25rem 0.5rem',
              backgroundColor: '#059669',
              color: 'white',
              border: 'none',
              borderRadius: '0.25rem',
              fontSize: '0.75rem',
              cursor: 'pointer'
            }}
          >
            Reactivar
          </button>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div style={{
          position: 'fixed',
          inset: 0,
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 50
        }}>
          <div style={{
            backgroundColor: 'white',
            borderRadius: '0.5rem',
            padding: '1.5rem',
            maxWidth: '400px',
            width: '100%',
            margin: '1rem'
          }}>
            <h3 style={{ fontSize: '1.125rem', fontWeight: '600', marginBottom: '1rem' }}>
              {actionType === 'strike' ? 'Aplicar Strike' : 'Suspender Usuario'}
            </h3>
            <p style={{ fontSize: '0.875rem', color: '#6b7280', marginBottom: '1rem' }}>
              Usuario: {usuario.nombre} {usuario.apellidos}
            </p>
            
            <label style={{ 
              display: 'block', 
              fontSize: '0.875rem', 
              fontWeight: '500', 
              marginBottom: '0.5rem'
            }}>
              Motivo:
            </label>
            <textarea
              value={motivo}
              onChange={(e) => setMotivo(e.target.value)}
              placeholder="Ingresa el motivo..."
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #d1d5db',
                borderRadius: '0.375rem',
                resize: 'vertical',
                minHeight: '80px',
                marginBottom: '1rem'
              }}
            />
            
            <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
              <button
                onClick={() => setShowModal(false)}
                style={{
                  padding: '0.5rem 1rem',
                  backgroundColor: '#e5e7eb',
                  color: '#374151',
                  border: 'none',
                  borderRadius: '0.375rem',
                  cursor: 'pointer'
                }}
              >
                Cancelar
              </button>
              <button
                onClick={handleSubmit}
                disabled={!motivo.trim()}
                style={{
                  padding: '0.5rem 1rem',
                  backgroundColor: motivo.trim() ? '#dc2626' : '#e5e7eb',
                  color: motivo.trim() ? 'white' : '#9ca3af',
                  border: 'none',
                  borderRadius: '0.375rem',
                  cursor: motivo.trim() ? 'pointer' : 'not-allowed'
                }}
              >
                Confirmar
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default UsersPage;