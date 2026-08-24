import { availablePaymentMethods } from './payment-methods.js';

const isRecord = value => Boolean(value) && typeof value === 'object' && !Array.isArray(value);
export const asArray = value => Array.isArray(value) ? value : [];

export const normalizeMenuData = payload => {
  const settings = isRecord(payload?.settings) ? payload.settings : {};
  const paymentMethods = availablePaymentMethods(settings);
  const safeSettings = paymentMethods.length === 1 && paymentMethods[0].value === 'cash'
    ? { ...settings, payment_cash_enabled: true }
    : settings;

  return {
    ...(isRecord(payload) ? payload : {}),
    categories: asArray(payload?.categories).filter(isRecord),
    items: asArray(payload?.items).filter(item => isRecord(item) && typeof item.name === 'string'),
    settings: safeSettings,
  };
};

export const normalizeOrder = order => isRecord(order) ? {
  ...order,
  items: asArray(order.items).filter(isRecord),
} : null;

export const normalizeOrderList = orders => asArray(orders).map(normalizeOrder).filter(Boolean);

// Keep the manager board independent from a proxy/interceptor.  A temporary
// tunnel response must never make the whole React app crash because `items`
// is not an array yet.
export const prepareManagerOrders = (payload, seen) => {
  const items = normalizeOrderList(payload?.items);
  const knownIds = asArray(seen);

  return {
    items,
    fresh: items.filter(order => order.status === 'new' && !knownIds.includes(order.id)),
    seenIds: items.map(order => order.id),
  };
};

export const normalizeAdminData = payload => {
  const settings = isRecord(payload?.settings) ? payload.settings : {};

  return {
    categories: asArray(payload?.categories).filter(isRecord),
    items: asArray(payload?.items).filter(isRecord),
    members: asArray(payload?.members).filter(isRecord),
    settings,
    tipOptions: asArray(settings.tip_options).filter(isRecord),
  };
};
