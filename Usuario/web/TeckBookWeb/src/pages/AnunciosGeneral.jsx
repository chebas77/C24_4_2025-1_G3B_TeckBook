import React, { useEffect, useState } from 'react';
import {
  FileText, Upload, Filter, Search, Calendar, User, Tag,
  Download, MessageCircle, Heart, Eye, X, Plus, File
} from 'lucide-react';
import Header from '../components/Header';
import '../css/AnunciosGeneral.css';

export default function AnunciosGeneral() {
  const [anuncios, setAnuncios] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newAnuncio, setNewAnuncio] = useState({
    titulo: '',
    contenido: '',
    tipo: 'anuncio',
    categoria: '',
    etiquetas: '',
    archivo: null
  });
  const [filtro, setFiltro] = useState('todos');
  const [busqueda, setBusqueda] = useState('');

  useEffect(() => {
    fetchAnuncios();
  }, []);

  const fetchAnuncios = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const token = localStorage.getItem('token');
      const res = await fetch('http://localhost:8080/api/anuncios/general/todos', {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('No se pudieron cargar los anuncios');
      setAnuncios(await res.json());
    } catch (e) {
      setError(e.message);
      setAnuncios([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateAnuncio = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const formData = new FormData();
      formData.append('titulo', newAnuncio.titulo);
      formData.append('contenido', newAnuncio.contenido);
      formData.append('tipo', newAnuncio.tipo);
      formData.append('categoria', newAnuncio.categoria);
      formData.append(
        'etiquetas',
        JSON.stringify(newAnuncio.etiquetas.split(',').map(et => et.trim()).filter(Boolean))
      );
      if (newAnuncio.archivo) formData.append('archivo', newAnuncio.archivo);

      const res = await fetch('http://localhost:8080/api/anuncios/general', {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
        body: formData
      });

      if (res.ok) {
        setShowCreateModal(false);
        setNewAnuncio({
          titulo: '',
          contenido: '',
          tipo: 'anuncio',
          categoria: '',
          etiquetas: '',
          archivo: null
        });
        fetchAnuncios();
      } else {
        alert('Error al crear el anuncio general');
      }
    } catch (e) {
      alert('Error al crear el anuncio general');
    }
  };

  const parseEtiquetas = (str) => {
    try {
      return JSON.parse(str || '[]');
    } catch {
      return [];
    }
  };

  const anunciosFiltrados = anuncios.filter((a) => {
    const coincideBusqueda =
      a.titulo?.toLowerCase().includes(busqueda.toLowerCase()) ||
      a.contenido?.toLowerCase().includes(busqueda.toLowerCase());

    if (filtro === 'soloGenerales') return a.esGeneral && coincideBusqueda;
    if (filtro === 'soloAula') return !a.esGeneral && coincideBusqueda;
    if (filtro === 'preguntas') return a.tipo === 'pregunta' && coincideBusqueda;
    if (filtro === 'materiales') return a.tipo === 'material' && coincideBusqueda;
    if (filtro === 'archivos') return a.tipo === 'archivo' && coincideBusqueda;
    return coincideBusqueda; // 'todos'
  });

  const getTipoIcon = (tipo) => {
    switch (tipo) {
      case 'pregunta':
        return <MessageCircle size={16} />;
      case 'material':
        return <FileText size={16} />;
      case 'archivo':
        return <File size={16} />;
      default:
        return <FileText size={16} />;
    }
  };

  const getTipoColor = (tipo) => {
    switch (tipo) {
      case 'pregunta':
        return 'var(--color-tipo-pregunta)';
      case 'material':
        return 'var(--color-tipo-material)';
      case 'archivo':
        return 'var(--color-tipo-archivo)';
      default:
        return 'var(--color-tipo-anuncio)';
    }
  };

  const formatFileSize = (bytes) => {
    if (!bytes) return '';
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    return Math.round((bytes / Math.pow(1024, i)) * 100) / 100 + ' ' + sizes[i];
  };

  return (
    <>
      <Header />
      <div className="anuncios-general-container">
        <div className="anuncios-general-section-header">
          <h2 className="anuncios-general-title">
            <FileText size={28} />
            Anuncios Generales
          </h2>
          <button onClick={() => setShowCreateModal(true)} className="anuncios-general-upload-btn">
            <Plus size={18} />
            Crear Anuncio
          </button>
        </div>

        <div className="anuncios-general-controls">
          <div className="anuncios-general-search">
            <Search className="search-icon" size={18} />
            <input
              type="text"
              placeholder="Buscar anuncios..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
          </div>

          <div className="anuncios-general-filter">
            <Filter size={16} />
            <select value={filtro} onChange={(e) => setFiltro(e.target.value)}>
              <option value="todos">Todos los anuncios</option>
              <option value="soloGenerales">Solo generales</option>
              <option value="soloAula">Solo de aula</option>
              <option value="preguntas">Preguntas</option>
              <option value="materiales">Materiales</option>
              <option value="archivos">Archivos</option>
            </select>
          </div>
        </div>

        <main className="anuncios-general-list">
          {isLoading ? (
            <div className="anuncios-general-loading">
              <div className="spinner"></div>
              Cargando anuncios...
            </div>
          ) : error ? (
            <div className="anuncios-general-error">
              <FileText size={48} />
              <h3>Error al cargar</h3>
              <p>{error}</p>
              <button onClick={fetchAnuncios}>Reintentar</button>
            </div>
          ) : anunciosFiltrados.length === 0 ? (
            <div className="anuncios-general-empty">
              <FileText size={64} />
              <h3>No hay anuncios</h3>
              <p>No se encontraron anuncios que coincidan con tu búsqueda</p>
            </div>
          ) : (
            anunciosFiltrados.map((anuncio) => (
              <article
                key={anuncio.id}
                className={`anuncios-general-card ${anuncio.esGeneral ? 'anuncio-general' : 'anuncio-aula'}`}
              >
                <div className="anuncios-general-card-header">
                  <div>
                    <h4 className="anuncios-general-card-title">{anuncio.titulo}</h4>
                    {anuncio.categoria && (
                      <div className="anuncios-general-card-categoria">
                        <Tag size={14} />
                        {anuncio.categoria}
                      </div>
                    )}
                    {!anuncio.esGeneral && anuncio.aulaId && (
                      <div className="anuncios-general-card-aula">
                        <User size={14} /> Aula ID: {anuncio.aulaId}
                      </div>
                    )}
                  </div>
                  <div
                    className="anuncios-general-card-tipo"
                    style={{ backgroundColor: getTipoColor(anuncio.tipo) }}
                  >
                    {getTipoIcon(anuncio.tipo)}
                    {anuncio.tipo}
                  </div>
                </div>

                <div className="anuncios-general-card-content">
                  <p>{anuncio.contenido}</p>
                </div>

                {anuncio.archivoNombre && (
                  <div className="anuncios-general-card-archivo">
                    <File size={20} />
                    <div className="anuncios-general-card-archivo-info">
                      <a href={anuncio.archivoUrl} target="_blank" rel="noopener noreferrer">
                        {anuncio.archivoNombre}
                      </a>
                      {anuncio.archivoTamaño && (
                        <span className="anuncios-general-card-archivo-size">
                          {formatFileSize(anuncio.archivoTamaño)}
                        </span>
                      )}
                    </div>
                    <Download size={16} />
                  </div>
                )}

                {anuncio.etiquetas && (
                  <div className="anuncios-general-card-etiquetas">
                    {parseEtiquetas(anuncio.etiquetas).map((etiqueta) => (
                      <span key={etiqueta} className="etiqueta">
                        #{etiqueta}
                      </span>
                    ))}
                  </div>
                )}

                <div className="anuncios-general-card-meta">
                  <div className="anuncios-general-card-date">
                    <Calendar size={14} />
                    {anuncio.fechaPublicacion
                      ? new Date(anuncio.fechaPublicacion).toLocaleString('es-ES', {
                          year: 'numeric',
                          month: 'short',
                          day: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit'
                        })
                      : 'Fecha no disponible'}
                  </div>

                  <div className="anuncios-general-card-stats">
                    <div className="anuncios-general-card-stat">
                      <Heart size={14} />
                      {anuncio.totalLikes || 0}
                    </div>
                    <div className="anuncios-general-card-stat">
                      <MessageCircle size={14} />
                      {anuncio.totalComentarios || 0}
                    </div>
                    <div className="anuncios-general-card-stat">
                      <Eye size={14} />
                      {anuncio.totalVistas || 0}
                    </div>
                  </div>
                </div>
              </article>
            ))
          )}
        </main>

        {showCreateModal && (
          <div className="anuncios-general-modal-overlay" onClick={() => setShowCreateModal(false)}>
            <div className="anuncios-general-modal" onClick={(e) => e.stopPropagation()}>
              <div className="anuncios-general-modal-header">
                <h3 className="anuncios-general-modal-title">Crear Nuevo Anuncio</h3>
                <button
                  className="anuncios-general-modal-close"
                  onClick={() => setShowCreateModal(false)}
                >
                  <X size={24} />
                </button>
              </div>

              <form className="anuncios-general-form" onSubmit={handleCreateAnuncio}>
                <div className="anuncios-general-form-group">
                  <label className="anuncios-general-form-label">Título *</label>
                  <input
                    type="text"
                    placeholder="Título del anuncio"
                    value={newAnuncio.titulo}
                    onChange={(e) => setNewAnuncio({ ...newAnuncio, titulo: e.target.value })}
                    required
                  />
                </div>

                <div className="anuncios-general-form-group">
                  <label className="anuncios-general-form-label">Contenido *</label>
                  <textarea
                    placeholder="Describe tu anuncio..."
                    value={newAnuncio.contenido}
                    onChange={(e) => setNewAnuncio({ ...newAnuncio, contenido: e.target.value })}
                    required
                  />
                </div>

                <div className="anuncios-general-form-row">
                  <div className="anuncios-general-form-group">
                    <label className="anuncios-general-form-label">Tipo</label>
                    <select
                      value={newAnuncio.tipo}
                      onChange={(e) => setNewAnuncio({ ...newAnuncio, tipo: e.target.value })}
                    >
                      <option value="anuncio">Anuncio</option>
                      <option value="pregunta">Pregunta</option>
                      <option value="material">Material</option>
                      <option value="archivo">Archivo</option>
                    </select>
                  </div>

                  <div className="anuncios-general-form-group">
                    <label className="anuncios-general-form-label">Categoría</label>
                    <input
                      type="text"
                      placeholder="Ej: Importante, Evento, etc."
                      value={newAnuncio.categoria}
                      onChange={(e) =>
                        setNewAnuncio({ ...newAnuncio, categoria: e.target.value })
                      }
                    />
                  </div>
                </div>

                <div className="anuncios-general-form-group">
                  <label className="anuncios-general-form-label">Etiquetas</label>
                  <input
                    type="text"
                    placeholder="Separa las etiquetas con comas (ej: evento, importante, deadline)"
                    value={newAnuncio.etiquetas}
                    onChange={(e) => setNewAnuncio({ ...newAnuncio, etiquetas: e.target.value })}
                  />
                </div>

                <div className="anuncios-general-form-group">
                  <label className="anuncios-general-form-label">Archivo adjunto</label>
                  <div className="anuncios-general-file-upload">
                    <input
                      type="file"
                      onChange={(e) =>
                        setNewAnuncio({ ...newAnuncio, archivo: e.target.files[0] })
                      }
                    />
                    <Upload size={32} />
                    <p>
                      {newAnuncio.archivo
                        ? `Archivo seleccionado: ${newAnuncio.archivo.name}`
                        : 'Haz clic o arrastra un archivo aquí'}
                    </p>
                    <span>Tamaño máximo: 10MB</span>
                  </div>
                </div>

                <div className="anuncios-general-modal-actions">
                  <button
                    type="submit"
                    className="anuncios-general-btn anuncios-general-btn_primary"
                  >
                    Publicar Anuncio
                  </button>
                  <button
                    type="button"
                    className="anuncios-general-btn anuncios-general-btn_secondary"
                    onClick={() => setShowCreateModal(false)}
                  >
                    Cancelar
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </>
  );
}
