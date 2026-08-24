export const availablePaymentMethods = (settings = {}) => {
  const cashEnabled = settings.payment_cash_enabled !== false;
  const transferEnabled = settings.payment_transfer_enabled !== false;

  // An admin can accidentally turn off both toggles.  Ordering must remain
  // possible on the shared tablet, so cash is the safe fallback.
  if (!cashEnabled && !transferEnabled) {
    return [{ value: 'cash', label: 'Наличными' }];
  }

  return [
    cashEnabled && { value: 'cash', label: 'Наличными' },
    transferEnabled && { value: 'transfer', label: 'Переводом' },
  ].filter(Boolean);
};
