import assert from 'node:assert/strict';
import test from 'node:test';
import { readFile } from 'node:fs/promises';

const sourcePath = new URL('../src/main.jsx', import.meta.url);

async function loadReadCart(storageValue) {
  const source = await readFile(sourcePath, 'utf8');
  const match = source.match(/const readCart=.*?;(?=const saveCart=)/s);
  assert.ok(match, 'readCart must remain available for cart recovery');

  const sessionStorage = {
    getItem: () => storageValue,
  };

  return Function('sessionStorage', `${match[0]} return readCart;`)(sessionStorage);
}

test('recovers from a corrupted cart value instead of crashing the order screen', async () => {
  const readCart = await loadReadCart('null');
  assert.deepEqual(readCart(), []);
});
