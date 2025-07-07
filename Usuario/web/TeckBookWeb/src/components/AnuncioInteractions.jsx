import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Heart, MessageCircle, Eye } from 'lucide-react';
import interactionsService from '../services/interactionsService';

// 🔥 Cache global para evitar peticiones duplicadas
const statsCache = new Map();
const pendingRequests = new Map();

const AnuncioInteractions = ({ anuncioId, onStatsChange }) => {
  const [stats, setStats] = useState({
    likesCount: 0,
    commentsCount: 0,
    userLiked: false,
    userRead: false
  });
  const [comentarios, setComentarios] = useState([]);
  const [nuevoComentario, setNuevoComentario] = useState('');
  const [showComments, setShowComments] = useState(false);
  const [loading, setLoading] = useState(false);
  const [statsLoaded, setStatsLoaded] = useState(false);
  const [error, setError] = useState(null);
  
  // 🔥 Refs para controlar lifecycle del componente
  const mountedRef = useRef(true);
  const abortControllerRef = useRef(null);
  const loadTimeoutRef = useRef(null);
  const readMarkedRef = useRef(false);

  // 🔥 Función memoizada para cargar stats con cache y debounce
  const loadStatsWithCache = useCallback(async () => {
    if (!mountedRef.current || statsLoaded) return;

    const cacheKey = `stats_${anuncioId}`;
    
    // 🔥 Verificar cache primero
    if (statsCache.has(cacheKey)) {
      const cachedStats = statsCache.get(cacheKey);
      const cacheAge = Date.now() - cachedStats.timestamp;
      
      // Usar cache si es menor a 30 segundos
      if (cacheAge < 30000) {
        setStats(cachedStats.data);
        setStatsLoaded(true);
        if (onStatsChange) onStatsChange(cachedStats.data);
        return;
      }
    }

    // 🔥 Verificar si ya hay una petición en curso para este anuncio
    if (pendingRequests.has(cacheKey)) {
      try {
        const data = await pendingRequests.get(cacheKey);
        if (mountedRef.current) {
          setStats(data);
          setStatsLoaded(true);
          if (onStatsChange) onStatsChange(data);
        }
      } catch (error) {
        if (mountedRef.current) {
          setError('Error cargando stats');
          setStatsLoaded(true);
        }
      }
      return;
    }

    try {
      setError(null);
      
      // 🔥 Crear AbortController para esta petición
      abortControllerRef.current = new AbortController();
      
      // 🔥 Crear promesa de la petición y guardarla en pending
      const requestPromise = interactionsService.getAnuncioStats(anuncioId, {
        signal: abortControllerRef.current.signal
      });
      
      pendingRequests.set(cacheKey, requestPromise);
      
      // 🔥 Timeout para la petición
      const timeoutId = setTimeout(() => {
        if (abortControllerRef.current) {
          abortControllerRef.current.abort();
        }
      }, 8000);

      const data = await requestPromise;
      clearTimeout(timeoutId);
      
      // 🔥 Limpiar de pending requests y guardar en cache
      pendingRequests.delete(cacheKey);
      statsCache.set(cacheKey, {
        data,
        timestamp: Date.now()
      });
      
      if (mountedRef.current) {
        setStats(data);
        setStatsLoaded(true);
        if (onStatsChange) onStatsChange(data);
      }
      
    } catch (error) {
      pendingRequests.delete(cacheKey);
      
      if (mountedRef.current && error.name !== 'AbortError') {
        console.warn(`⚠️ Stats error para anuncio ${anuncioId}:`, error.message);
        setError('Error cargando interacciones');
        setStatsLoaded(true);
      }
    }
  }, [anuncioId, statsLoaded, onStatsChange]);

  // 🔥 Función optimizada para marcar como leído (una sola vez)
  const markAsReadOnce = useCallback(async () => {
    if (readMarkedRef.current) return;
    readMarkedRef.current = true;

    try {
      const controller = new AbortController();
      setTimeout(() => controller.abort(), 3000);

      await interactionsService.markAsRead(anuncioId, {
        signal: controller.signal
      });
    } catch (error) {
      // Silencioso, no mostrar error al usuario
      console.debug(`📖 Read marking failed para anuncio ${anuncioId}`);
    }
  }, [anuncioId]);

  // 🔥 Effect optimizado con delay escalonado
  useEffect(() => {
    mountedRef.current = true;
    
    // 🔥 Delay escalonado basado en el ID para evitar todos los requests al mismo tiempo
    const baseDelay = parseInt(anuncioId) % 10; // 0-9 basado en último dígito
    const randomDelay = Math.random() * 1000; // 0-1000ms adicional
    const totalDelay = (baseDelay * 200) + randomDelay; // 0-2800ms + random
    
    loadTimeoutRef.current = setTimeout(() => {
      if (mountedRef.current) {
        loadStatsWithCache();
        markAsReadOnce();
      }
    }, totalDelay);

    return () => {
      mountedRef.current = false;
      readMarkedRef.current = false;
      
      if (loadTimeoutRef.current) {
        clearTimeout(loadTimeoutRef.current);
      }
      
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [anuncioId, loadStatsWithCache, markAsReadOnce]);

  // 🔥 Función para cargar comentarios con cache
  const loadComments = useCallback(async () => {
    if (!mountedRef.current) return;
    
    const commentsCacheKey = `comments_${anuncioId}`;
    
    // Verificar cache de comentarios
    if (statsCache.has(commentsCacheKey)) {
      const cached = statsCache.get(commentsCacheKey);
      if (Date.now() - cached.timestamp < 10000) { // 10 segundos
        setComentarios(cached.data);
        return;
      }
    }
    
    try {
      setLoading(true);
      
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 5000);

      const data = await interactionsService.getComments(anuncioId, {
        signal: controller.signal
      });
      
      clearTimeout(timeoutId);
      
      // Guardar en cache
      statsCache.set(commentsCacheKey, {
        data: data.comentarios || [],
        timestamp: Date.now()
      });
      
      if (mountedRef.current) {
        setComentarios(data.comentarios || []);
      }
    } catch (error) {
      if (mountedRef.current && error.name !== 'AbortError') {
        console.warn(`⚠️ Comments error para anuncio ${anuncioId}:`, error.message);
        setComentarios([]);
      }
    } finally {
      if (mountedRef.current) {
        setLoading(false);
      }
    }
  }, [anuncioId]);

  // 🔥 Handle like con optimistic updates y throttling
  const handleLike = useCallback(async () => {
    if (loading || !statsLoaded) return;
    
    try {
      setLoading(true);
      
      // 🔥 Optimistic update
      const newLiked = !stats.userLiked;
      const newCount = newLiked ? stats.likesCount + 1 : stats.likesCount - 1;
      
      const optimisticStats = {
        ...stats,
        userLiked: newLiked,
        likesCount: Math.max(0, newCount)
      };
      
      setStats(optimisticStats);

      // 🔥 Actualizar cache inmediatamente
      const cacheKey = `stats_${anuncioId}`;
      statsCache.set(cacheKey, {
        data: optimisticStats,
        timestamp: Date.now()
      });

      const controller = new AbortController();
      setTimeout(() => controller.abort(), 5000);

      await interactionsService.toggleLike(anuncioId, {
        signal: controller.signal
      });
      
      // 🔥 Invalidar cache para forzar refresh en siguiente carga
      statsCache.delete(cacheKey);
      
    } catch (error) {
      if (mountedRef.current && error.name !== 'AbortError') {
        console.error('Error toggling like:', error);
        
        // 🔥 Revertir optimistic update
        setStats(prevStats => ({
          ...prevStats,
          userLiked: stats.userLiked,
          likesCount: stats.likesCount
        }));
        
        // Restaurar cache
        const cacheKey = `stats_${anuncioId}`;
        statsCache.set(cacheKey, {
          data: stats,
          timestamp: Date.now()
        });
      }
    } finally {
      if (mountedRef.current) {
        setLoading(false);
      }
    }
  }, [anuncioId, loading, statsLoaded, stats]);

  // 🔥 Handle add comment optimizado
  const handleAddComment = useCallback(async (e) => {
    e.preventDefault();
    if (!nuevoComentario.trim() || loading) return;

    const comentarioTexto = nuevoComentario.trim();
    
    try {
      setLoading(true);
      setNuevoComentario('');
      
      const controller = new AbortController();
      setTimeout(() => controller.abort(), 8000);

      await interactionsService.addComment(anuncioId, comentarioTexto, {
        signal: controller.signal
      });
      
      // 🔥 Update optimistic de stats
      setStats(prev => ({
        ...prev,
        commentsCount: prev.commentsCount + 1
      }));
      
      // 🔥 Invalidar cache de comentarios
      const commentsCacheKey = `comments_${anuncioId}`;
      statsCache.delete(commentsCacheKey);
      
      // 🔥 Recargar comentarios si están visibles
      if (showComments) {
        await loadComments();
      }
      
    } catch (error) {
      if (mountedRef.current && error.name !== 'AbortError') {
        console.error('Error adding comment:', error);
        setNuevoComentario(comentarioTexto);
      }
    } finally {
      if (mountedRef.current) {
        setLoading(false);
      }
    }
  }, [anuncioId, nuevoComentario, loading, showComments, loadComments]);

  // 🔥 Toggle comments optimizado
  const toggleComments = useCallback(async () => {
    if (!showComments && !loading) {
      setShowComments(true);
      await loadComments();
    } else {
      setShowComments(!showComments);
    }
  }, [showComments, loading, loadComments]);

  // 🔥 Render optimizado con skeleton loading
  if (!statsLoaded && !error) {
    return (
      <div className="anuncio-interactions" style={{ 
        padding: '10px 0', 
        borderTop: '1px solid #e0e0e0',
        display: 'flex',
        alignItems: 'center',
        gap: '15px',
        color: '#999',
        fontSize: '14px'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
          <div style={{ 
            width: '18px', 
            height: '18px', 
            backgroundColor: '#f0f0f0', 
            borderRadius: '2px',
            animation: 'pulse 1.5s infinite'
          }} />
          <span>...</span>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
          <div style={{ 
            width: '18px', 
            height: '18px', 
            backgroundColor: '#f0f0f0', 
            borderRadius: '2px',
            animation: 'pulse 1.5s infinite'
          }} />
          <span>...</span>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
          <div style={{ 
            width: '16px', 
            height: '16px', 
            backgroundColor: '#f0f0f0', 
            borderRadius: '2px',
            animation: 'pulse 1.5s infinite'
          }} />
          <span>Cargando...</span>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="anuncio-interactions" style={{ 
        padding: '10px 0', 
        borderTop: '1px solid #e0e0e0',
        display: 'flex',
        alignItems: 'center',
        gap: '10px',
        color: '#999',
        fontSize: '12px'
      }}>
        <span>⚠️ Error cargando interacciones</span>
        <button 
          onClick={loadStatsWithCache}
          style={{
            background: 'none',
            border: 'none',
            color: '#007bff',
            cursor: 'pointer',
            fontSize: '12px',
            textDecoration: 'underline'
          }}
        >
          Reintentar
        </button>
      </div>
    );
  }

  return (
    <div className="anuncio-interactions">
      {/* Botones de interacción */}
      <div className="interactions-bar" style={{ 
        display: 'flex', 
        gap: '15px', 
        alignItems: 'center', 
        padding: '10px 0', 
        borderTop: '1px solid #e0e0e0' 
      }}>
        <button 
          onClick={handleLike}
          disabled={loading}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '5px',
            background: 'none',
            border: 'none',
            cursor: loading ? 'default' : 'pointer',
            color: stats.userLiked ? '#e91e63' : '#666',
            fontSize: '14px',
            opacity: loading ? 0.7 : 1,
            transition: 'all 0.2s ease'
          }}
        >
          <Heart 
            size={18} 
            fill={stats.userLiked ? '#e91e63' : 'none'} 
            stroke={stats.userLiked ? '#e91e63' : '#666'}
          />
          {stats.likesCount}
        </button>

        <button 
          onClick={toggleComments}
          disabled={loading}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '5px',
            background: 'none',
            border: 'none',
            cursor: loading ? 'default' : 'pointer',
            color: showComments ? '#007bff' : '#666',
            fontSize: '14px',
            opacity: loading ? 0.7 : 1,
            transition: 'all 0.2s ease'
          }}
        >
          <MessageCircle size={18} />
          {stats.commentsCount}
        </button>

        <div style={{ 
          display: 'flex', 
          alignItems: 'center', 
          gap: '5px', 
          color: '#999', 
          fontSize: '12px' 
        }}>
          <Eye size={16} />
          {stats.userRead ? 'Leído' : 'No leído'}
        </div>
      </div>

      {/* Formulario de comentario */}
      <form onSubmit={handleAddComment} style={{ marginTop: '10px' }}>
        <div style={{ display: 'flex', gap: '10px' }}>
          <input
            type="text"
            value={nuevoComentario}
            onChange={(e) => setNuevoComentario(e.target.value)}
            placeholder="Escribe un comentario..."
            disabled={loading}
            style={{
              flex: 1,
              padding: '8px 12px',
              border: '1px solid #ddd',
              borderRadius: '20px',
              fontSize: '14px',
              opacity: loading ? 0.7 : 1
            }}
          />
          <button 
            type="submit"
            disabled={loading || !nuevoComentario.trim()}
            style={{
              padding: '8px 16px',
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '20px',
              fontSize: '14px',
              cursor: 'pointer',
              opacity: loading || !nuevoComentario.trim() ? 0.6 : 1,
              transition: 'opacity 0.2s ease'
            }}
          >
            {loading ? '...' : 'Enviar'}
          </button>
        </div>
      </form>

      {/* Lista de comentarios */}
      {showComments && (
        <div className="comments-section" style={{ 
          marginTop: '15px',
          maxHeight: '300px',
          overflowY: 'auto',
          border: '1px solid #e5e7eb',
          borderRadius: '8px',
          backgroundColor: '#f9fafb'
        }}>
          {loading && comentarios.length === 0 ? (
            <p style={{ 
              color: '#999', 
              fontSize: '14px', 
              textAlign: 'center', 
              padding: '20px' 
            }}>
              Cargando comentarios...
            </p>
          ) : comentarios.length === 0 ? (
            <p style={{ 
              color: '#999', 
              fontSize: '14px', 
              textAlign: 'center', 
              padding: '20px' 
            }}>
              No hay comentarios aún
            </p>
          ) : (
            comentarios.map((comentario) => (
              <div 
                key={comentario.id} 
                style={{
                  padding: '12px',
                  borderBottom: '1px solid #f0f0f0',
                  fontSize: '14px',
                  backgroundColor: 'white',
                  margin: '0 0 1px 0'
                }}
              >
                <div style={{ 
                  fontWeight: 'bold', 
                  color: '#333',
                  marginBottom: '4px'
                }}>
                  {comentario.autor}
                </div>
                <div style={{ 
                  color: '#666', 
                  margin: '5px 0',
                  lineHeight: '1.4'
                }}>
                  {comentario.contenido}
                </div>
                <div style={{ 
                  color: '#999', 
                  fontSize: '12px',
                  marginTop: '6px'
                }}>
                  {new Date(comentario.fechaCreacion).toLocaleString()}
                </div>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
};

// 🔥 Función para limpiar cache (usar cuando sea necesario)
AnuncioInteractions.clearCache = () => {
  statsCache.clear();
  pendingRequests.clear();
};

export default React.memo(AnuncioInteractions);