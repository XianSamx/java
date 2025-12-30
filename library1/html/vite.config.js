import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0', // 允许所有 IP 访问
    port: 5173,      // 指定端口号
    strictPort: true, // 端口占用时直接退出
  },
})
