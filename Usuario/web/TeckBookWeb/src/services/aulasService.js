
import apiService from './apiService';
import { ENDPOINTS } from '../config/apiConfig';

class AulasService {
  // Obtener todas las aulas del usuario
  async getAll() {
    try {
      const response = await apiService.get(ENDPOINTS.AULAS.BASE);
      return response.aulas || response;
    } catch (error) {
      throw new Error('Error al obtener las aulas');
    }
  }

  // Obtener aula por ID
  async getById(id) {
    try {
      return await apiService.get(ENDPOINTS.AULAS.BY_ID(id));
    } catch (error) {
      throw new Error(`Error al obtener el aula ${id}`);
    }
  }

  // Crear nueva aula
  async create(aulaData) {
    try {
      return await apiService.post(ENDPOINTS.AULAS.CREATE, aulaData);
    } catch (error) {
      throw new Error('Error al crear el aula');
    }
  }

  // Actualizar aula
  async update(id, aulaData) {
    try {
      return await apiService.put(ENDPOINTS.AULAS.UPDATE(id), aulaData);
    } catch (error) {
      throw new Error(`Error al actualizar el aula ${id}`);
    }
  }

  // Eliminar aula
  async delete(id) {
    try {
      return await apiService.delete(ENDPOINTS.AULAS.DELETE(id));
    } catch (error) {
      throw new Error(`Error al eliminar el aula ${id}`);
    }
  }

  // === ANUNCIOS ===
  
  // Obtener anuncios de un aula
  async getAnuncios(aulaId) {
    try {
      return await apiService.get(ENDPOINTS.AULAS.ANUNCIOS.BY_AULA(aulaId));
    } catch (error) {
      throw new Error(`Error al obtener anuncios del aula ${aulaId}`);
    }
  }

  // Crear anuncio en aula
  async createAnuncio(aulaId, anuncioData) {
    try {
      return await apiService.post(ENDPOINTS.AULAS.ANUNCIOS.CREATE(aulaId), anuncioData);
    } catch (error) {
      throw new Error('Error al crear el anuncio');
    }
  }

  // Actualizar anuncio
  async updateAnuncio(aulaId, anuncioId, anuncioData) {
    try {
      return await apiService.put(ENDPOINTS.AULAS.ANUNCIOS.UPDATE(aulaId, anuncioId), anuncioData);
    } catch (error) {
      throw new Error('Error al actualizar el anuncio');
    }
  }

  // Eliminar anuncio
  async deleteAnuncio(aulaId, anuncioId) {
    try {
      return await apiService.delete(ENDPOINTS.AULAS.ANUNCIOS.DELETE(aulaId, anuncioId));
    } catch (error) {
      throw new Error('Error al eliminar el anuncio');
    }
  }
}

const aulasService = new AulasService();
export default aulasService;