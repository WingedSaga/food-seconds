const isRecord = value => Boolean(value) && typeof value === 'object' && !Array.isArray(value);

export const normalizeAdminData = payload => {
  const settings = isRecord(payload?.settings) ? payload.settings : {};

  return {
    categories: Array.isArray(payload?.categories) ? payload.categories : [],
    items: Array.isArray(payload?.items) ? payload.items : [],
    members: Array.isArray(payload?.members) ? payload.members : [],
    settings,
    tipOptions: Array.isArray(settings.tip_options) ? settings.tip_options : [],
  };
};
