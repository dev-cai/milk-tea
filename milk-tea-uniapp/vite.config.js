import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    uni(),
  ],
  css: {
    preprocessorOptions: {
      scss: {
        // Silence deprecation warnings from dependencies like uview-plus
        silenceDeprecations: ['legacy-js-api', 'import']
      }
    }
  }
})
