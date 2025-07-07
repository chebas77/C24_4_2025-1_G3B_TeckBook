import React, { useState, useEffect } from 'react';
import AdminLayout from '../components/layout/AdminLayout';
import dashboardService from '../services/dashboardService';

const DashboardPage = () => {
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const data = await dashboardService.getDashboardData();
      setDashboardData(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar los datos del dashboard');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <AdminLayout>
        <div style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '400px'
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
            <p style={{ color: '#6b7280' }}>Cargando datos del dashboard...</p>
          </div>
        </div>
      </AdminLayout>
    );
  }

  if (error) {
    return (
      <AdminLayout>
        <div style={{
          backgroundColor: '#fef2f2',
          border: '1px solid #fecaca',
          borderRadius: '0.5rem',
          padding: '1rem',
          margin: '1rem 0'
        }}>
          <p style={{ color: '#dc2626' }}>{error}</p>
          <button
            onClick={loadDashboardData}
            style={{
              marginTop: '0.5rem',
              padding: '0.5rem 1rem',
              backgroundColor: '#dc2626',
              color: 'white',
              border: 'none',
              borderRadius: '0.375rem',
              cursor: 'pointer'
            }}
          >
            Reintentar
          </button>
        </div>
      </AdminLayout>
    );
  }

  const stats = dashboardData?.estadisticas_basicas || {};

  return (
    <AdminLayout>
      <div>
        <h2 style={{
          fontSize: '1.875rem',
          fontWeight: 'bold',
          color: '#111827',
          marginBottom: '1.5rem'
        }}>
          Dashboard Principal
        </h2>

        {/* Estadísticas principales */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
          gap: '1.5rem',
          marginBottom: '2rem'
        }}>
          <StatCard
            title="Total Usuarios"
            value={stats.totalUsuarios || 0}
            icon="👥"
            color="#3b82f6"
          />
          <StatCard
            title="Usuarios Activos"
            value={stats.usuariosActivos || 0}
            icon="✅"
            color="#10b981"
          />
          <StatCard
            title="Publicaciones Hoy"
            value={stats.publicacionesHoy || 0}
            icon="📝"
            color="#f59e0b"
          />
          <StatCard
            title="Contenido Pendiente"
            value={stats.contenidoPendiente || 0}
            icon="⏳"
            color="#ef4444"
          />
          <StatCard
            title="Total Aulas"
            value={stats.totalAulas || 0}
            icon="🏫"
            color="#8b5cf6"
          />
          <StatCard
            title="Aulas Activas"
            value={stats.aulasActivas || 0}
            icon="🟢"
            color="#06b6d4"
          />
        </div>

        {/* Alertas recientes */}
        {dashboardData?.alertasRecientes && dashboardData.alertasRecientes.length > 0 && (
          <div style={{
            backgroundColor: 'white',
            borderRadius: '0.5rem',
            padding: '1.5rem',
            boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)',
            marginBottom: '2rem'
          }}>
            <h3 style={{
              fontSize: '1.125rem',
              fontWeight: '600',
              color: '#111827',
              marginBottom: '1rem'
            }}>
              Alertas Recientes
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {dashboardData.alertasRecientes.map((alerta) => (
                <div
                  key={alerta.id}
                  style={{
                    padding: '0.75rem',
                    backgroundColor: '#fef3c7',
                    borderLeft: '4px solid #f59e0b',
                    borderRadius: '0.375rem'
                  }}
                >
                  <p style={{ color: '#92400e', fontSize: '0.875rem' }}>
                    {alerta.mensaje} • {alerta.tiempo}
                  </p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Aulas más pobladas */}
        {dashboardData?.aulasPobladas && dashboardData.aulasPobladas.length > 0 && (
          <div style={{
            backgroundColor: 'white',
            borderRadius: '0.5rem',
            padding: '1.5rem',
            boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)'
          }}>
            <h3 style={{
              fontSize: '1.125rem',
              fontWeight: '600',
              color: '#111827',
              marginBottom: '1rem'
            }}>
              Aulas Más Pobladas
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {dashboardData.aulasPobladas.map((aula, index) => (
                <div
                  key={index}
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    padding: '0.75rem',
                    backgroundColor: '#f9fafb',
                    borderRadius: '0.375rem'
                  }}
                >
                  <div>
                    <p style={{ fontWeight: '500', color: '#111827' }}>
                      {aula.nombre}
                    </p>
                    <p style={{ fontSize: '0.875rem', color: '#6b7280' }}>
                      Código: {aula.codigo}
                    </p>
                  </div>
                  <span style={{
                    padding: '0.25rem 0.75rem',
                    backgroundColor: '#dbeafe',
                    color: '#1d4ed8',
                    borderRadius: '9999px',
                    fontSize: '0.875rem',
                    fontWeight: '500'
                  }}>
                    {aula.estudiantes} estudiantes
                  </span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </AdminLayout>
  );
};

const StatCard = ({ title, value, icon, color }) => {
  return (
    <div style={{
      backgroundColor: 'white',
      borderRadius: '0.5rem',
      padding: '1.5rem',
      boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <p style={{
            fontSize: '0.875rem',
            color: '#6b7280',
            marginBottom: '0.25rem'
          }}>
            {title}
          </p>
          <p style={{
            fontSize: '2rem',
            fontWeight: 'bold',
            color: '#111827'
          }}>
            {value}
          </p>
        </div>
        <div style={{
          width: '48px',
          height: '48px',
          backgroundColor: color,
          borderRadius: '0.5rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          fontSize: '1.5rem'
        }}>
          {icon}
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;