import apiService from './apiService';

class InteractionsService {
  
  // ========== LIKES ==========
  
  async toggleLike(anuncioId, options = {}) {
    return await apiService.post(`/api/interactions/like/${anuncioId}`, null, {
      ...options,
      // 🚀 Timeout específico para likes (más corto)
      signal: options.signal || AbortSignal.timeout(5000)
    });
  }
  
  async getLikesCount(anuncioId, options = {}) {
    return await apiService.get(`/api/interactions/likes/${anuncioId}`, {
      ...options,
      signal: options.signal || AbortSignal.timeout(3000)
    });
  }
  
  async getUserLikes(options = {}) {
    return await apiService.get('/api/interactions/user-likes', {
      ...options,
      signal: options.signal || AbortSignal.timeout(5000)
    });
  }
  
  // ========== COMENTARIOS ==========
  
  async addComment(anuncioId, contenido, options = {}) {
    return await apiService.post(`/api/interactions/comment/${anuncioId}`, 
      { contenido }, 
      {
        ...options,
        signal: options.signal || AbortSignal.timeout(8000)
      }
    );
  }
  
  async getComments(anuncioId, options = {}) {
    return await apiService.get(`/api/interactions/comments/${anuncioId}`, {
      ...options,
      signal: options.signal || AbortSignal.timeout(5000)
    });
  }
  
  // ========== LECTURAS ==========
  
  async markAsRead(anuncioId, options = {}) {
    return await apiService.post(`/api/interactions/read/${anuncioId}`, null, {
      ...options,
      signal: options.signal || AbortSignal.timeout(3000)
    });
  }
  
  // ========== STATS COMBINADAS ==========
  
  async getAnuncioStats(anuncioId, options = {}) {
    return await apiService.get(`/api/interactions/stats/${anuncioId}`, {
      ...options,
      // 🚀 Timeout más largo para stats porque incluye varias queries
      signal: options.signal || AbortSignal.timeout(8000)
    });
  }

  // 🚀 MÉTODO OPTIMIZADO: Obtener stats básicas sin queries complejas
  async getBasicStats(anuncioId, options = {}) {
    try {
      const [likesResult, commentsResult] = await Promise.allSettled([
        this.getLikesCount(anuncioId, options),
        apiService.get(`/api/interactions/comments/${anuncioId}/count`, {
          ...options,
          signal: options.signal || AbortSignal.timeout(3000)
        })
      ]);

      return {
        likesCount: likesResult.status === 'fulfilled' ? likesResult.value.count : 0,
        commentsCount: commentsResult.status === 'fulfilled' ? commentsResult.value.count : 0,
        userLiked: false, // Se actualiza después
        userRead: false   // Se actualiza después
      };
    } catch (error) {
      console.warn('Error getting basic stats:', error);
      return {
        likesCount: 0,
        commentsCount: 0,
        userLiked: false,
        userRead: false
      };
    }
  }
}

const interactionsService = new InteractionsService();
export default interactionsService;