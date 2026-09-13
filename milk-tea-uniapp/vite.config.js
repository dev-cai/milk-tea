import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
// https://vitejs.dev/config/
export default defineConfig({
  // Inject the API endpoint at build time. Avoid import.meta.env in the
  // mini-program bundle, where Vite's URL polyfill requires Node's `url`
  // module and causes "module 'utils/url.js' is not defined" at startup.
  define: {
    __API_BASE_URL__: JSON.stringify(process.env.VITE_API_BASE_URL || 'http://localhost:8080/api')
  },
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
