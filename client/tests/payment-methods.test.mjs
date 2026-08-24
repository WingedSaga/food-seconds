import assert from 'node:assert/strict';
import test from 'node:test';
import { availablePaymentMethods } from '../src/payment-methods.js';

test('keeps cash available when both payment settings were accidentally disabled', () => {
  assert.deepEqual(availablePaymentMethods({
    payment_cash_enabled: false,
    payment_transfer_enabled: false,
  }), [{ value: 'cash', label: 'Наличными' }]);
});

test('shows every payment method explicitly enabled by the administrator', () => {
  assert.deepEqual(availablePaymentMethods({
    payment_cash_enabled: true,
    payment_transfer_enabled: true,
  }), [
    { value: 'cash', label: 'Наличными' },
    { value: 'transfer', label: 'Переводом' },
  ]);
});
