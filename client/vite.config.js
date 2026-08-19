import { defineConfig } from 'vite';

// iPad Air 1 supports ES modules on iOS 12, but not newer JavaScript syntax.
export default defineConfig({ build: { target: 'es2017' } });
