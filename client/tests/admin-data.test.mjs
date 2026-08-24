import assert from 'node:assert/strict';
import test from 'node:test';
import { asArray, normalizeAdminData, normalizeMenuData, normalizeOrderList, prepareManagerOrders } from '../src/admin-data.js';

test('keeps the admin screen usable when the API response is incomplete', () => {
  assert.deepEqual(normalizeAdminData({ settings: { tip_options: null } }), {
    categories: [],
    items: [],
    members: [],
    settings: { tip_options: null },
    tipOptions: [],
  });
});

test('turns a non-list API value into an empty list', () => {
  assert.deepEqual(asArray({ broken: true }), []);
});

test('normalizes malformed menu and order lists before a screen renders them', () => {
  assert.deepEqual(normalizeMenuData({ items: { broken: true }, categories: null, settings: null }), {
    items: [],
    categories: [],
    settings: {},
  });
  assert.deepEqual(normalizeOrderList({ broken: true }), []);
});

test('keeps the manager board alive when a tunnel returns a malformed items value', () => {
  assert.doesNotThrow(() => prepareManagerOrders({ items: { broken: true } }, ['old-order']));
  assert.deepEqual(prepareManagerOrders({ items: { broken: true } }, []), {
    items: [],
    fresh: [],
    seenIds: [],
  });
});
