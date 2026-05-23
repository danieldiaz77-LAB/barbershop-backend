import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  server: {
    port: 3000,
    proxy: {
      // redirige las llamadas /api al backend Spring Boot
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})