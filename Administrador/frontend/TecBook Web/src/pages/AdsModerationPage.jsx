import React, { useEffect, useState } from 'react';
import AdminLayout from '../components/layout/AdminLayout';
import adService from '../services/adService';

const AdsModerationPage = () => {
  const [ads, setAds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filtros, setFiltros] = useState({ estado: '', buscar: '' });
  const [pagination, setPagination] = useState({ count: 0, next: null, previous: null, current_page: 1 });
  const [selectedAd, setSelectedAd] = useState(null);
  const [comentario, setComentario] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState('');
  const [historial, setHistorial] = useState([]);
  const [loadingHistorial, setLoadingHistorial] = useState(false);

  useEffect(() => {
    loadAds();
  }, [filtros]);

  const loadAds = async (page = 1) => {
    try {
      setLoading(true);
      const data = await adService.getAds({ ...filtros, page, page_size: 20 });
      setAds(data.results || []);
      setPagination({
        count: data.count || 0,
        next: data.next,
        previous: data.previous,
        current_page: page
      });
      setError(null);
    } catch (err) {
      setError('Error al cargar anuncios');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (key, value) => {
    setFiltros(prev => ({ ...prev, [key]: value }));
  };

  const handleCensurar = (ad) => {
    setSelectedAd(ad);
    setComentario('');
    setModalType('censurar');
    setShowModal(true);
  };

  const handleDescensurar = async (ad) => {
    if (window.confirm('¿Seguro que quieres descensurar este anuncio?')) {
      try {
        await adService.descensurarAd(ad.id);
        loadAds(pagination.current_page);
      } catch (err) {
        alert('Error al descensurar anuncio');
      }
    }
  };

  const handleVerHistorial = async (ad) => {
    setSelectedAd(ad);
    setLoadingHistorial(true);
    setModalType('historial');
    setShowModal(true);
    try {
      const data = await adService.getAdModerationHistory(ad.id);
      setHistorial(data.historial || []);
    } catch (err) {
      setHistorial([]);
    } finally {
      setLoadingHistorial(false);
    }
  };

  const handleSubmitCensura = async () => {
    if (!comentario.trim()) return;
    try {
      await adService.censurarAd(selectedAd.id, comentario);
      setShowModal(false);
      loadAds(pagination.current_page);
    } catch (err) {
      alert('Error al censurar anuncio');
    }
  };

  return (
    <AdminLayout>
      <div>
        <h2 style={{ fontSize: '1.875rem', fontWeight: 'bold', color: '#111827', marginBottom: '1.5rem' }}>
          Moderación de Anuncios
        </h2>
        {/* Filtros */}
        <div style={{ backgroundColor: 'white', borderRadius: '0.5rem', padding: '1.5rem', boxShadow: '0 1px 3px 0 rgba(0,0,0,0.1)', marginBottom: '1.5rem' }}>
          <div style={{ display: 'flex', gap: '1rem', alignItems: 'end' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '0.5rem' }}>Estado</label>
              <select value={filtros.estado} onChange={e => handleFilterChange('estado', e.target.value)} style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem', fontSize: '0.875rem' }}>
                <option value="">Todos</option>
                <option value="visible">Visibles</option>
                <option value="oculto">Ocultos</option>
              </select>
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', color: '#374151', marginBottom: '0.5rem' }}>Buscar</label>
              <input type="text" placeholder="Título o contenido..." value={filtros.buscar} onChange={e => handleFilterChange('buscar', e.target.value)} style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem', fontSize: '0.875rem' }} />
            </div>
          </div>
        </div>
        {/* Tabla de anuncios */}
        {loading ? (
          <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px' }}>
            <p style={{ color: '#6b7280' }}>Cargando anuncios...</p>
          </div>
        ) : error ? (
          <div style={{ backgroundColor: '#fef2f2', border: '1px solid #fecaca', borderRadius: '0.5rem', padding: '1rem' }}>
            <p style={{ color: '#dc2626' }}>{error}</p>
          </div>
        ) : (
          <div style={{ backgroundColor: 'white', borderRadius: '0.5rem', overflow: 'hidden', boxShadow: '0 1px 3px 0 rgba(0,0,0,0.1)' }}>
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead style={{ backgroundColor: '#f9fafb' }}>
                  <tr>
                    <th style={{ padding: '0.75rem', textAlign: 'left', fontSize: '0.875rem', fontWeight: '500', color: '#374151' }}>Título</th>
                    <th style={{ padding: '0.75rem', textAlign: 'left', fontSize: '0.875rem', fontWeight: '500', color: '#374151' }}>Estado</th>
                    <th style={{ padding: '0.75rem', textAlign: 'left', fontSize: '0.875rem', fontWeight: '500', color: '#374151' }}>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {ads.map(ad => (
                    <tr key={ad.id} style={{ borderTop: '1px solid #e5e7eb' }}>
                      <td style={{ padding: '0.75rem' }}>
                        <div>
                          <p style={{ fontWeight: '500', color: '#111827' }}>{ad.titulo}</p>
                          <p style={{ fontSize: '0.875rem', color: '#6b7280' }}>{ad.contenido}</p>
                        </div>
                      </td>
                      <td style={{ padding: '0.75rem' }}>
                        <span style={{ padding: '0.25rem 0.75rem', backgroundColor: ad.estado === 'oculto' ? '#fef2f2' : '#ecfdf5', color: ad.estado === 'oculto' ? '#dc2626' : '#059669', borderRadius: '9999px', fontSize: '0.75rem', fontWeight: '500' }}>{ad.estado === 'oculto' ? 'Oculto' : 'Visible'}</span>
                      </td>
                      <td style={{ padding: '0.75rem' }}>
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                          {ad.estado === 'visible' ? (
                            <button onClick={() => handleCensurar(ad)} style={{ padding: '0.25rem 0.5rem', backgroundColor: '#dc2626', color: 'white', border: 'none', borderRadius: '0.25rem', fontSize: '0.75rem', cursor: 'pointer' }}>Censurar</button>
                          ) : (
                            <button onClick={() => handleDescensurar(ad)} style={{ padding: '0.25rem 0.5rem', backgroundColor: '#059669', color: 'white', border: 'none', borderRadius: '0.25rem', fontSize: '0.75rem', cursor: 'pointer' }}>Descensurar</button>
                          )}
                          <button onClick={() => handleVerHistorial(ad)} style={{ padding: '0.25rem 0.5rem', backgroundColor: '#3b82f6', color: 'white', border: 'none', borderRadius: '0.25rem', fontSize: '0.75rem', cursor: 'pointer' }}>Historial</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {/* Paginación */}
            {pagination.count > 20 && (
              <div style={{ padding: '1rem', borderTop: '1px solid #e5e7eb', display: 'flex', justifyContent: 'between', alignItems: 'center' }}>
                <p style={{ fontSize: '0.875rem', color: '#6b7280' }}>Mostrando {ads.length} de {pagination.count} anuncios</p>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <button onClick={() => loadAds(pagination.current_page - 1)} disabled={!pagination.previous} style={{ padding: '0.5rem 1rem', backgroundColor: pagination.previous ? '#3b82f6' : '#e5e7eb', color: pagination.previous ? 'white' : '#9ca3af', border: 'none', borderRadius: '0.375rem', cursor: pagination.previous ? 'pointer' : 'not-allowed' }}>Anterior</button>
                  <button onClick={() => loadAds(pagination.current_page + 1)} disabled={!pagination.next} style={{ padding: '0.5rem 1rem', backgroundColor: pagination.next ? '#3b82f6' : '#e5e7eb', color: pagination.next ? 'white' : '#9ca3af', border: 'none', borderRadius: '0.375rem', cursor: pagination.next ? 'pointer' : 'not-allowed' }}>Siguiente</button>
                </div>
              </div>
            )}
          </div>
        )}
      </div>
      {/* Modal de censura y de historial */}
      {showModal && (
        <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 50 }}>
          <div style={{ backgroundColor: 'white', borderRadius: '0.5rem', padding: '1.5rem', maxWidth: '400px', width: '100%', margin: '1rem' }}>
            {modalType === 'censurar' ? (
              <>
                <h3 style={{ fontSize: '1.125rem', fontWeight: '600', marginBottom: '1rem' }}>Censurar Anuncio</h3>
                <p style={{ fontSize: '0.875rem', color: '#6b7280', marginBottom: '1rem' }}>Título: {selectedAd?.titulo}</p>
                <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: '500', marginBottom: '0.5rem' }}>Comentario de moderación:</label>
                <textarea value={comentario} onChange={e => setComentario(e.target.value)} placeholder="Ingresa el motivo..." style={{ width: '100%', padding: '0.5rem', border: '1px solid #d1d5db', borderRadius: '0.375rem', resize: 'vertical', minHeight: '80px', marginBottom: '1rem' }} />
                <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                  <button onClick={() => setShowModal(false)} style={{ padding: '0.5rem 1rem', backgroundColor: '#e5e7eb', color: '#374151', border: 'none', borderRadius: '0.375rem', cursor: 'pointer' }}>Cancelar</button>
                  <button onClick={handleSubmitCensura} disabled={!comentario.trim()} style={{ padding: '0.5rem 1rem', backgroundColor: comentario.trim() ? '#dc2626' : '#e5e7eb', color: comentario.trim() ? 'white' : '#9ca3af', border: 'none', borderRadius: '0.375rem', cursor: comentario.trim() ? 'pointer' : 'not-allowed' }}>Confirmar</button>
                </div>
              </>
            ) : (
              <>
                <h3 style={{ fontSize: '1.125rem', fontWeight: '600', marginBottom: '1rem' }}>Historial de Moderación</h3>
                <p style={{ fontSize: '0.875rem', color: '#6b7280', marginBottom: '1rem' }}>Título: {selectedAd?.titulo}</p>
                {loadingHistorial ? (
                  <p>Cargando historial...</p>
                ) : historial.length === 0 ? (
                  <p>No hay historial de moderación para este anuncio.</p>
                ) : (
                  <ul style={{ maxHeight: '200px', overflowY: 'auto', paddingLeft: 0 }}>
                    {historial.map((h, idx) => (
                      <li key={idx} style={{ marginBottom: '0.75rem', borderBottom: '1px solid #f3f4f6', paddingBottom: '0.5rem' }}>
                        <div style={{ fontSize: '0.875rem', color: '#374151' }}><b>Acción:</b> {h.accion}</div>
                        <div style={{ fontSize: '0.875rem', color: '#374151' }}><b>Moderador:</b> {h.moderador}</div>
                        <div style={{ fontSize: '0.875rem', color: '#374151' }}><b>Comentario:</b> {h.comentario}</div>
                        <div style={{ fontSize: '0.75rem', color: '#6b7280' }}>{h.fecha}</div>
                      </li>
                    ))}
                  </ul>
                )}
                <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end', marginTop: '1rem' }}>
                  <button onClick={() => setShowModal(false)} style={{ padding: '0.5rem 1rem', backgroundColor: '#e5e7eb', color: '#374151', border: 'none', borderRadius: '0.375rem', cursor: 'pointer' }}>Cerrar</button>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </AdminLayout>
  );
};

export default AdsModerationPage;
