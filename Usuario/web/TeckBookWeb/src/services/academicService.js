
import apiService from './apiService';
import { ENDPOINTS } from '../config/apiConfig';

class AcademicService {
  // === DEPARTAMENTOS ===
  async getDepartamentos() {
    try {
      return await apiService.get(ENDPOINTS.DEPARTAMENTOS.ACTIVOS);
    } catch (error) {
      throw new Error('Error al obtener departamentos');
    }
  }

  // === CARRERAS ===
  async getCarreras() {
    try {
      return await apiService.get(ENDPOINTS.CARRERAS.ACTIVAS);
    } catch (error) {
      throw new Error('Error al obtener carreras');
    }
  }

  async getCarrerasByDepartamento(departamentoId) {
    try {
      return await apiService.get(ENDPOINTS.CARRERAS.BY_DEPARTAMENTO(departamentoId));
    } catch (error) {
      throw new Error(`Error al obtener carreras del departamento ${departamentoId}`);
    }
  }

  // === CICLOS ===
  async getCiclos() {
    try {
      return await apiService.get(ENDPOINTS.CICLOS.TODOS);
    } catch (error) {
      throw new Error('Error al obtener ciclos');
    }
  }

  async getCiclosByCarrera(carreraId) {
    try {
      return await apiService.get(ENDPOINTS.CICLOS.BY_CARRERA(carreraId));
    } catch (error) {
      throw new Error(`Error al obtener ciclos de la carrera ${carreraId}`);
    }
  }

  // === SECCIONES ===
  async getSeccionesByCarrera(carreraId) {
    try {
      return await apiService.get(ENDPOINTS.SECCIONES.BY_CARRERA(carreraId));
    } catch (error) {
      throw new Error(`Error al obtener secciones de la carrera ${carreraId}`);
    }
  }

  async getSeccionesByCarreraAndCiclo(carreraId, cicloId) {
    try {
      return await apiService.get(ENDPOINTS.SECCIONES.BY_CARRERA_CICLO(carreraId, cicloId));
    } catch (error) {
      throw new Error(`Error al obtener secciones de la carrera ${carreraId} y ciclo ${cicloId}`);
    }
  }
}

const academicService = new AcademicService();
export default academicService;
