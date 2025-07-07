import React, { useEffect, useState } from 'react';
import { Users, XCircle, ChevronDown, ChevronUp } from 'lucide-react';
import { API_CONFIG, ENDPOINTS } from '../config/apiConfig'; // ajusta la ruta si es distinta

function ListaIntegrantes({ aulaId }) {
  const [integrantes, setIntegrantes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [removingId, setRemovingId] = useState(null);
  const [removeError, setRemoveError] = useState(null);
  const [userData, setUserData] = useState(null);
  const [showList, setShowList] = useState(true);

  useEffect(() => {
    fetchUserData();
  }, []);

  useEffect(() => {
    if (aulaId) {
      setLoading(true);
      setError(null);
      fetchIntegrantes();
    }
  }, [aulaId]);

  useEffect(() => {
    if (userData) {
      setShowList(isProfesor());
    }
  }, [userData]);

  const fetchUserData = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API_CONFIG.API_BASE_URL}${ENDPOINTS.AUTH.GET_USER}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!response.ok) throw new Error('No se pudo obtener el usuario');
      const data = await response.json();
      setUserData(data);
    } catch (error) {
      setUserData(null);
    }
  };

  const isProfesor = () => {
    return userData?.rol?.toLowerCase() === 'profesor';
  };

  const fetchIntegrantes = async () => {
    try {
      const token = localStorage.getItem('token');
      const res = await fetch(`${API_CONFIG.API_BASE_URL}/api/aulas/${aulaId}/participantes`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('No se pudo obtener la lista de integrantes');
      const data = await res.json();
      setIntegrantes(data.participantes || data || []);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const eliminarIntegrante = async (id) => {
    if (!window.confirm('¿Seguro que deseas eliminar este integrante?')) return;
    setRemovingId(id);
    setRemoveError(null);
    try {
      const token = localStorage.getItem('token');
      const res = await fetch(`${API_CONFIG.API_BASE_URL}/api/aulas/${aulaId}/participantes/${id}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('No se pudo eliminar el integrante');
      setIntegrantes(integrantes.filter((p) => p.id !== id));
    } catch (e) {
      setRemoveError(e.message);
    } finally {
      setRemovingId(null);
    }
  };

  const handleToggle = () => setShowList((v) => !v);

  return (
    <div className="integrantes-section">
      <h3 className="integrantes-title">
        <Users size={18} /> Integrantes
        {!isProfesor() && (
          <button
            className="toggle-integrantes-btn"
            onClick={handleToggle}
            style={{
              marginLeft: 10,
              background: 'none',
              border: 'none',
              cursor: 'pointer',
              color: '#2563eb'
            }}
          >
            {showList ? (
              <>
                <ChevronUp size={16} /> Ocultar
              </>
            ) : (
              <>
                <ChevronDown size={16} /> Ver integrantes
              </>
            )}
          </button>
        )}
      </h3>
      {(isProfesor() || showList) &&
        (loading ? (
          <div className="integrantes-loading">Cargando integrantes...</div>
        ) : error ? (
          <div className="integrantes-error">{error}</div>
        ) : integrantes.length === 0 ? (
          <div className="integrantes-empty">No hay integrantes registrados.</div>
        ) : (
          <ul className="integrantes-list">
            {integrantes.map((p) => (
              <li key={p.id} className="integrante-item">
                <Users size={14} style={{ color: '#3B82F6', marginRight: 6 }} />
                <span>{p.nombre || p.username || p.email || 'Sin nombre'}</span>
                {isProfesor() && (
                  <button
                    className="eliminar-btn"
                    title="Eliminar integrante"
                    onClick={() => eliminarIntegrante(p.estudianteId)}
                    disabled={removingId === p.estudianteId}
                    style={{
                      marginLeft: 8,
                      color: '#ef4444',
                      background: 'none',
                      border: 'none',
                      cursor: 'pointer'
                    }}
                  >
                    {removingId === p.estudianteId ? 'Eliminando...' : <XCircle size={16} />}
                  </button>
                )}
              </li>
            ))}
          </ul>
        ))}
      {removeError && <div className="integrantes-error">{removeError}</div>}
    </div>
  );
}

export default ListaIntegrantes;
