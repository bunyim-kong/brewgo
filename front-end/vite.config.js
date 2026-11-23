import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'
import { defineConfig, loadEnv } from 'vite'

export default defineConfig(({ mode }) => {
  const { BREWGO_API_ORIGIN } = loadEnv(mode, '.', 'BREWGO_')
  const proxy = { '/api': BREWGO_API_ORIGIN || 'http://localhost:8080' }

  return {
    plugins: [react(), tailwindcss()],
    server: { proxy },
    preview: { proxy },
  }
})
