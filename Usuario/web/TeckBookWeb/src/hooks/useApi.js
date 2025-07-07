import { useState, useEffect, useCallback } from 'react';

export const useApi = (apiCall, dependencies = [], options = {}) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const { 
    immediate = true, 
    onSuccess, 
    onError 
  } = options;

  const execute = useCallback(async (...args) => {
    try {
      setLoading(true);
      setError(null);
      
      const result = await apiCall(...args);
      setData(result);
      
      if (onSuccess) onSuccess(result);
      return result;
      
    } catch (err) {
      setError(err.message);
      if (onError) onError(err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [apiCall, onSuccess, onError]);

  useEffect(() => {
    if (immediate) {
      execute();
    }
  }, dependencies);

  return { 
    data, 
    loading, 
    error, 
    execute,
    refetch: execute 
  };
};

// Hook específico para datos de usuario
export const useUserData = () => {
  return useApi(
    () => import('./services/authService').then(module => module.default.getUser()),
    []
  );
};

// Hook específico para aulas
export const useAulas = () => {
  return useApi(
    () => import('./services/aulasService').then(module => module.default.getAll()),
    []
  );
};