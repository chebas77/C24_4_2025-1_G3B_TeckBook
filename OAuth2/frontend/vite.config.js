import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173, // Asegúrate de que este sea el puerto de tu frontend
    host: true, // Permite conexiones desde cualquier host (puede ser menos seguro en producción)
    allowedHosts: [
      'localhost',
      '.ngrok-free.app', // Permite todos los subdominios de ngrok-free.app
      '22ac-2800-200-e810-55b-9d09-ae3c-3f47-8ec2.ngrok-free.app' // O puedes ser más específico con la URL actual
    ],
  },
});