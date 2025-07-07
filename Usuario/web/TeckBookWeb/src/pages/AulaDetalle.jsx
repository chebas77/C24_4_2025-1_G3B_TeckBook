import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeft, 
  Users, 
  Calendar, 
  BookOpen, 
  Plus, 
  MoreVertical,
  Eye,
  MessageCircle,
  Pin,
  UserPlus,
  Settings,
  Share2,
  Bell,
  Search,
  Filter,
  AlertCircle,
  Calculator,
  Trash2,
  X
} from 'lucide-react';
import './AulaDetalle.css';
import ListaIntegrantes from '../components/ListaIntegrantes';
import AnuncioInteractions from '../components/AnuncioInteractions';
import { API_CONFIG, ENDPOINTS, ROUTES } from '../config/apiConfig'

function AulaDetalle() {
  const { aulaId } = useParams();
  const navigate = useNavigate();
  const [anuncios, setAnuncios] = useState([]);
  const [aula, setAula] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showCalculadora, setShowCalculadora] = useState(false);
  const [newAnuncio, setNewAnuncio] = useState({
    titulo: '',
    contenido: '',
    fijado: false
  });

  // Estados para la calculadora de notas
  const [porcentajeTeoria, setPorcentajeTeoria] = useState(70);
  const [notasTeoria, setNotasTeoria] = useState([]);
  const [notasLaboratorio, setNotasLaboratorio] = useState([]);
  const [nuevaNotaTeoria, setNuevaNotaTeoria] = useState('');
  const [nuevaNotaLaboratorio, setNuevaNotaLaboratorio] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate(ROUTES.PUBLIC.LOGIN);
      return;
    }
    setError(null);
    fetchAula(token);
    fetchAnuncios(token);
  }, [aulaId]);

  const fetchAula = async (token) => {
    try {
      const res = await fetch(`${API_CONFIG.API_BASE_URL}${ENDPOINTS.AULAS.BY_ID(aulaId)}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('No se pudo cargar el aula');
      const data = await res.json();
      setAula(data?.aula || data);
    } catch (e) {
      setError(e.message);
    }
  };

  const fetchAnuncios = async (token) => {
    try {
      setLoading(true);
      setError(null);
      const res = await fetch(`${API_CONFIG.API_BASE_URL}${ENDPOINTS.AULAS.ANUNCIOS.BY_AULA(aulaId)}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('No tienes acceso o no hay anuncios');
      setAnuncios(await res.json());
    } catch (e) {
      setAnuncios([]);
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateAnuncio = async (e) => {
    e.preventDefault();

    if (!aulaId) return console.error('aulaId no definido');
    if (!newAnuncio.titulo.trim() || !newAnuncio.contenido.trim()) return console.error('Título o contenido vacío');

    try {
      const token = localStorage.getItem('token');
      const formData = new FormData();
      formData.append('titulo', newAnuncio.titulo);
      formData.append('contenido', newAnuncio.contenido);
      formData.append('fijado', newAnuncio.fijado || false);
      formData.append('tipo', newAnuncio.tipo || 'anuncio');
      formData.append('categoria', newAnuncio.categoria || '');
      formData.append('etiquetas', newAnuncio.etiquetas || '');
      if (newAnuncio.archivo) {
        formData.append('archivo', newAnuncio.archivo);
      }

      const res = await fetch(`${API_CONFIG.API_BASE_URL}${ENDPOINTS.AULAS.ANUNCIOS.CREATE(aulaId)}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });

      if (res.ok) {
        setShowCreateModal(false);
        setNewAnuncio({ titulo: '', contenido: '', fijado: false });
        fetchAnuncios(token);
      } else {
        const errorData = await res.json();
        console.error('Error en el POST:', errorData);
      }
    } catch (e) {
      console.error('Error creando anuncio:', e);
    }
  };

  // Funciones para la calculadora de notas
  const agregarNotaTeoria = () => {
    const nota = parseFloat(nuevaNotaTeoria);
    if (!isNaN(nota) && nota >= 0 && nota <= 20) {
      setNotasTeoria([...notasTeoria, nota]);
      setNuevaNotaTeoria('');
    }
  };

  const agregarNotaLaboratorio = () => {
    const nota = parseFloat(nuevaNotaLaboratorio);
    if (!isNaN(nota) && nota >= 0 && nota <= 20) {
      setNotasLaboratorio([...notasLaboratorio, nota]);
      setNuevaNotaLaboratorio('');
    }
  };

  const eliminarNotaTeoria = (index) => {
    const nuevasNotas = notasTeoria.filter((_, i) => i !== index);
    setNotasTeoria(nuevasNotas);
  };

  const eliminarNotaLaboratorio = (index) => {
    const nuevasNotas = notasLaboratorio.filter((_, i) => i !== index);
    setNotasLaboratorio(nuevasNotas);
  };

  const calcularPromedioTeoria = () => {
    if (notasTeoria.length === 0) return 0;
    if (notasTeoria.length === 1) return notasTeoria[0];
    
    const notasOrdenadas = [...notasTeoria].sort((a, b) => a - b);
    const notasSinLaMasBaja = notasOrdenadas.slice(1);
    const suma = notasSinLaMasBaja.reduce((acc, nota) => acc + nota, 0);
    return suma / notasSinLaMasBaja.length;
  };

  const calcularPromedioLaboratorio = () => {
    if (notasLaboratorio.length === 0) return 0;
    const suma = notasLaboratorio.reduce((acc, nota) => acc + nota, 0);
    return suma / notasLaboratorio.length;
  };

  const calcularNotaFinal = () => {
    const promedioTeoria = calcularPromedioTeoria();
    const promedioLaboratorio = calcularPromedioLaboratorio();
    const porcentajeLaboratorio = 100 - porcentajeTeoria;

    const notaTeoriaPonderada = (promedioTeoria * porcentajeTeoria) / 100;
    const notaLaboratorioPonderada = (promedioLaboratorio * porcentajeLaboratorio) / 100;
    
    let notaFinal = notaTeoriaPonderada + notaLaboratorioPonderada;
    
    if (notaFinal >= 12.49) {
      notaFinal = Math.max(13, Math.round(notaFinal));
    } else {
      notaFinal = Math.round(notaFinal * 100) / 100;
    }
    
    return notaFinal;
  };

  const limpiarCalculadora = () => {
    setNotasTeoria([]);
    setNotasLaboratorio([]);
    setPorcentajeTeoria(70);
  };

  const filteredAnuncios = anuncios.filter(anuncio =>
    anuncio.titulo?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    anuncio.contenido?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Utilidad para obtener la fecha de creación más probable
  const getFechaCreacion = () => {
    if (!aula) return null;
    return (
      aula.fechaCreacion ||
      aula.fecha_creacion ||
      aula.fechaInicio ||
      aula.fecha_inicio ||
      null
    );
  };

  if (loading) {
    return (
      <div className="aula-loading-container">
        <div className="aula-loading-spinner"></div>
        <p>Cargando aula...</p>
      </div>
    );
  }

  if (error && !aula) {
    return (
      <div className="aula-error-container">
        <AlertCircle size={48} />
        <h2>Error al cargar el aula</h2>
        <p>{error}</p>
        <div className="aula-error-actions">
          <button className="aula-btn-secondary" onClick={() => navigate(-1)}>
            Volver
          </button>
          <button className="aula-btn-primary" onClick={() => window.location.reload()}>
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  const notaFinal = calcularNotaFinal();
  const esAprobado = notaFinal >= 13;

  return (
    <div className="aula-detalle-container">
      {/* Header */}
      <header className="aula-detalle-header" style={{ background: 'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)', zIndex: 10, position: 'relative' }}>
        <div className="aula-header-content" style={{ position: 'relative', zIndex: 11 }}>
          <div className="aula-header-left">
            <button className="aula-back-btn" onClick={() => navigate(-1)}>
              <ArrowLeft size={20} />
            </button>
            <div className="aula-info-section">
              <h1>{aula?.nombre || aula?.titulo || 'Aula'}</h1>
              {aula?.descripcion && (
                <div className="aula-descripcion-header">
                  {aula.descripcion}
                </div>
              )}
              <div className="aula-meta-info">
                <span><BookOpen size={14} /> Código: {aula?.codigo || 'N/A'}</span>
              </div>
            </div>
          </div>
          <div className="aula-header-actions">
            <button className="aula-header-btn" onClick={() => setShowCreateModal(true)}>
              <Plus size={16} />
              <span>Nuevo Anuncio</span>
            </button>
            <button className="aula-header-btn">
              <Settings size={16} />
              <span>Configuración</span>
            </button>
          </div>
        </div>
      </header>

      {/* Contenido Principal */}
      <div className="aula-main-content" style={{ zIndex: 1, position: 'relative' }}>
        <div className="aula-anuncios-section">
          {/* Barra de Acciones */}
          <div className="aula-actions-bar">
            <div className="aula-search-container">
              <Search className="aula-search-icon" size={16} />
              <input
                type="text"
                className="aula-search-input"
                placeholder="Buscar anuncios..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              <button className="aula-filter-btn">
                <Filter size={16} />
              </button>
            </div>
            <button className="aula-create-btn" onClick={() => setShowCreateModal(true)}>
              <Plus size={16} />
              Crear Anuncio
            </button>
          </div>

          {/* Lista de Anuncios */}
          <div className="aula-anuncios-list">
            {error && anuncios.length === 0 ? (
              <div className="aula-empty-state">
                <AlertCircle size={48} color="#64748b" />
                <h3>No se pudieron cargar los anuncios</h3>
                <p>{error}</p>
              </div>
            ) : filteredAnuncios.length === 0 ? (
              <div className="aula-empty-state">
                <MessageCircle size={48} color="#64748b" />
                <h3>No hay anuncios</h3>
                <p>Cuando se publiquen anuncios en esta aula, aparecerán aquí.</p>
              </div>
            ) : (
              filteredAnuncios.map(anuncio => (
                <div key={anuncio.id} className={`aula-anuncio-card ${anuncio.fijado ? 'fijado' : ''}`}>
                  {anuncio.fijado && (
                    <div className="aula-pin-badge">
                      <Pin size={12} />
                      Fijado
                    </div>
                  )}
                  
                  <div className="aula-anuncio-header">
                    <div className="aula-anuncio-title-section">
                      <h3>{anuncio.titulo}</h3>
                      <div className="aula-anuncio-meta">
                        <span><Calendar size={14} /> {anuncio.fechaPublicacion ? 
                          new Date(anuncio.fechaPublicacion).toLocaleDateString('es-ES', {
                            day: 'numeric',
                            month: 'long',
                            year: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit'
                          }) : 'Fecha no disponible'}</span>
                      </div>
                    </div>
                    <div className="aula-anuncio-actions">
                      <button className="aula-anuncio-btn" title="Ver">
                        <Eye size={16} />
                      </button>
                      <button className="aula-anuncio-btn" title="Fijar">
                        <Pin size={16} />
                      </button>
                      <button className="aula-anuncio-btn" title="Invitar">
                        <UserPlus size={16} />
                      </button>
                      <button className="aula-anuncio-btn" title="Más opciones">
                        <MoreVertical size={16} />
                      </button>
                    </div>
                  </div>

                  <div className="aula-anuncio-content">
                    <p>{anuncio.contenido}</p>
                  </div>

                  <div className="aula-anuncio-footer">
                    <span className="aula-anuncio-fecha">
                      {anuncio.fechaPublicacion ? new Date(anuncio.fechaPublicacion).toLocaleString() : ''}
                    </span>
                    <button className="aula-anuncio-share">
                      <Share2 size={14} />
                      Compartir
                    </button>
                  </div>

                  {/* 🔥 NUEVO: Componente de interacciones */}
                  <AnuncioInteractions 
                    anuncioId={anuncio.id}
                    onStatsChange={(stats) => {
                      console.log(`Stats de anuncio aula ${anuncio.id}:`, stats);
                    }}
                  />
                </div>
              ))
            )}
          </div>
        </div>

        {/* Sidebar: información del aula compacta e integrantes con borde */}
        <div className="aula-sidebar">
          {/* Información del Aula - Versión Compacta */}
          <div className="aula-info-card-compact">
            <h4>Información del Aula</h4>
            <div className="aula-info-items-compact">
              <div className="aula-info-item-compact">
                <BookOpen size={14} color="#64748b" />
                <span className="label-compact">Código:</span>
                <span className="value-compact">{aula?.codigo || 'N/A'}</span>
              </div>
              <div className="aula-info-item-compact">
                <Calendar size={14} color="#64748b" />
                <span className="label-compact">Creado:</span>
                <span className="value-compact">
                  {getFechaCreacion() ? 
                    new Date(getFechaCreacion()).toLocaleDateString('es-ES') : 
                    'No disponible'
                  }
                </span>
              </div>
            </div>
          </div>

          {/* Integrantes con borde especial */}
          <div className="aula-integrantes-card">
            <h4>
              <Users size={16} color="#3b82f6" />
              Integrantes
            </h4>
            <ListaIntegrantes aulaId={aulaId} />
            
            {/* Botón de Calculadora de Notas */}
            <button 
              className="aula-calculadora-btn" 
              onClick={() => setShowCalculadora(true)}
            >
              <Calculator size={16} />
              Calculadora de Notas
            </button>
          </div>
        </div>
      </div>

      {/* Footer con controles */}
      <div className="aula-footer">
        <div className="aula-footer-control">
          <Bell size={20} />
        </div>
        <div className="aula-footer-search">
          <input 
            type="text" 
            placeholder="Buscar..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <button>
            <Search size={16} />
          </button>
        </div>
        <div className="aula-footer-control">
          <Filter size={20} />
        </div>
      </div>

      {/* Modal Crear Anuncio */}
      {showCreateModal && (
        <div className="aula-modal-overlay" onClick={() => setShowCreateModal(false)}>
          <div className="aula-modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="aula-modal-header">
              <h3>Crear Nuevo Anuncio</h3>
              <button className="aula-close-btn" onClick={() => setShowCreateModal(false)}>
                ×
              </button>
            </div>
            <form className="aula-anuncio-form" onSubmit={handleCreateAnuncio}>
              <div className="aula-form-group">
                <label htmlFor="titulo">Título</label>
                <input
                  id="titulo"
                  type="text"
                  value={newAnuncio.titulo}
                  onChange={(e) => setNewAnuncio({...newAnuncio, titulo: e.target.value})}
                  required
                />
              </div>
              <div className="aula-form-group">
                <label htmlFor="contenido">Contenido</label>
                <textarea
                  id="contenido"
                  rows="6"
                  value={newAnuncio.contenido}
                  onChange={(e) => setNewAnuncio({...newAnuncio, contenido: e.target.value})}
                  required
                />
              </div>
              <div className="aula-checkbox-group">
                <input
                  id="fijado"
                  type="checkbox"
                  checked={newAnuncio.fijado}
                  onChange={(e) => setNewAnuncio({...newAnuncio, fijado: e.target.checked})}
                />
                <label htmlFor="fijado">Fijar anuncio</label>
              </div>
              <div className="aula-form-actions">
                <button type="button" className="aula-btn-cancel" onClick={() => setShowCreateModal(false)}>
                  Cancelar
                </button>
                <button type="submit" className="aula-btn-submit">
                  Publicar Anuncio
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Calculadora de Notas */}
      {showCalculadora && (
        <div className="aula-modal-overlay" onClick={() => setShowCalculadora(false)}>
          <div className="calculadora-modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="calculadora-modal-header">
              <h3>
                <Calculator size={20} />
                Calculadora de Notas
              </h3>
              <button className="aula-close-btn" onClick={() => setShowCalculadora(false)}>
                <X size={20} />
              </button>
            </div>

            <div className="calculadora-body">
              {/* Configuración de porcentajes */}
              <div className="porcentaje-section">
                <label className="porcentaje-label">
                  Porcentaje de Teoría: {porcentajeTeoria}%
                </label>
                <input
                  type="range"
                  min="0"
                  max="100"
                  value={porcentajeTeoria}
                  onChange={(e) => setPorcentajeTeoria(parseInt(e.target.value))}
                  className="porcentaje-slider"
                />
                <div className="porcentaje-info">
                  <span>Teoría: {porcentajeTeoria}%</span>
                  <span>Laboratorio/Taller: {100 - porcentajeTeoria}%</span>
                </div>
              </div>

              <div className="notas-sections">
                {/* Sección de Teoría */}
                <div className="notas-section">
                  <h4>Notas de Teoría</h4>
                  <p className="notas-info">Se elimina la nota más baja automáticamente</p>
                  
                  <div className="agregar-nota">
                    <input
                      type="number"
                      min="0"
                      max="20"
                      step="0.1"
                      value={nuevaNotaTeoria}
                      onChange={(e) => setNuevaNotaTeoria(e.target.value)}
                      placeholder="Ingresa nota (0-20)"
                      className="nota-input"
                      onKeyPress={(e) => e.key === 'Enter' && agregarNotaTeoria()}
                    />
                    <button onClick={agregarNotaTeoria} className="agregar-btn">
                      <Plus size={16} />
                    </button>
                  </div>

                  <div className="notas-lista">
                    {notasTeoria.map((nota, index) => (
                      <div key={index} className="nota-item">
                        <span>{nota}</span>
                        <button onClick={() => eliminarNotaTeoria(index)} className="eliminar-btn">
                          <Trash2 size={14} />
                        </button>
                      </div>
                    ))}
                  </div>
                  
                  {notasTeoria.length > 0 && (
                    <div className="promedio-info">
                      Promedio: {calcularPromedioTeoria().toFixed(2)}
                      {notasTeoria.length > 1 && (
                        <span className="nota-eliminada"> (eliminando nota más baja: {Math.min(...notasTeoria)})</span>
                      )}
                    </div>
                  )}
                </div>

                {/* Sección de Laboratorio */}
                <div className="notas-section">
                  <h4>Notas de Laboratorio/Taller</h4>
                  <p className="notas-info">Se consideran todas las notas</p>
                  
                  <div className="agregar-nota">
                    <input
                      type="number"
                      min="0"
                      max="20"
                      step="0.1"
                      value={nuevaNotaLaboratorio}
                      onChange={(e) => setNuevaNotaLaboratorio(e.target.value)}
                      placeholder="Ingresa nota (0-20)"
                      className="nota-input"
                      onKeyPress={(e) => e.key === 'Enter' && agregarNotaLaboratorio()}
                    />
                    <button onClick={agregarNotaLaboratorio} className="agregar-btn">
                      <Plus size={16} />
                    </button>
                  </div>

                  <div className="notas-lista">
                    {notasLaboratorio.map((nota, index) => (
                      <div key={index} className="nota-item">
                        <span>{nota}</span>
                        <button onClick={() => eliminarNotaLaboratorio(index)} className="eliminar-btn">
                          <Trash2 size={14} />
                        </button>
                      </div>
                    ))}
                  </div>
                  
                  {notasLaboratorio.length > 0 && (
                    <div className="promedio-info">
                      Promedio: {calcularPromedioLaboratorio().toFixed(2)}
                    </div>
                  )}
                </div>
              </div>

              {/* Resultado Final */}
              <div className="resultado-final">
                <div className="calculo-detalle">
                  <div className="calculo-item">
                    <span>Teoría ({porcentajeTeoria}%): </span>
                    <span>{((calcularPromedioTeoria() * porcentajeTeoria) / 100).toFixed(2)}</span>
                  </div>
                  <div className="calculo-item">
                    <span>Laboratorio ({100 - porcentajeTeoria}%): </span>
                    <span>{((calcularPromedioLaboratorio() * (100 - porcentajeTeoria)) / 100).toFixed(2)}</span>
                  </div>
                </div>
                
                <div className={`nota-final ${esAprobado ? 'aprobado' : 'desaprobado'}`}>
                  <h3>Nota Final: {notaFinal}</h3>
                  <p className={`estado ${esAprobado ? 'aprobado' : 'desaprobado'}`}>
                    {esAprobado ? '✅ APROBADO' : '❌ DESAPROBADO'}
                  </p>
                </div>
              </div>

              {/* Acciones */}
              <div className="calculadora-actions">
                <button onClick={limpiarCalculadora} className="aula-btn-cancel">
                  Limpiar Todo
                </button>
                <button onClick={() => setShowCalculadora(false)} className="aula-btn-submit">
                  Cerrar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AulaDetalle;