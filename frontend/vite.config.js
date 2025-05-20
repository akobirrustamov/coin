import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    allowedHosts: ['2738-213-230-108-224.ngrok-free.app'], // Allow ngrok
  },
});