import assert from 'node:assert/strict';
import test from 'node:test';
import { normalizeAdminData } from '../src/admin-data.js';

test('keeps the admin screen usable when the API response is incomplete', () => {
  assert.deepEqual(normalizeAdminData({ settings: { tip_options: null } }), {
    categories: [],
    items: [],
    members: [],
    settings: { tip_options: null },
    tipOptions: [],
  });
});
