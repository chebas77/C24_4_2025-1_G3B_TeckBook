// 🚀 CREAR ESTE ARCHIVO: src/hooks/useOptimizedLoading.js
import { useState, useEffect } from 'react';

export const useOptimizedLoading = () => {
  const [loadingState, setLoadingState] = useState({
    isLoading: true,
    loadingMessage: 'Iniciando...',
    progress: 0
  });

  const updateLoading = (message, progress) => {
    setLoadingState({
      isLoading: progress < 100,
      loadingMessage: message,
      progress: Math.min(progress, 100)
    });
  };

  const completeLoading = () => {
    setLoadingState({
      isLoading: false,
      loadingMessage: 'Completado',
      progress: 100
    });
  };

  return {
    ...loadingState,
    updateLoading,
    completeLoading
  };
};
