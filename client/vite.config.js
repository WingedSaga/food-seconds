import { defineConfig } from 'vite';

// iPad Air 1 supports ES modules on iOS 12, but not newer JavaScript syntax.
export default defineConfig({
  // GitHub Pages serves this repository from /food-seconds/ rather than /.
  base: process.env.VITE_BASE || '/',
  build: { target: 'es2017' }
});
