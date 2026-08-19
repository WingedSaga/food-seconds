const token = () => localStorage.getItem('ns_token');

async function api(path, options = {}) {
  const response = await fetch(`/api${path}`, {
    ...options,
    headers: { Authorization: `Bearer ${token()}`, ...(options.headers || {}) },
  });
  if (!response.ok) throw new Error((await response.json().catch(() => ({}))).message || 'Не удалось сохранить QR-код');
  return response.json();
}

async function applyPaymentQr() {
  if (!token()) return;
  try {
    const { image } = await api('/payment-qr');
    if (!image) return;
    const replace = () => document.querySelectorAll('img.qr').forEach((img) => { img.src = image; img.alt = 'QR-код для оплаты'; });
    replace();
    new MutationObserver(replace).observe(document.body, { childList: true, subtree: true });
  } catch { /* QR is optional, keep generated code */ }
}

function addUploadButton() {
  if (location.pathname !== '/admin') return;
  const button = document.createElement('button');
  button.className = 'qr-upload';
  button.textContent = 'Загрузить QR оплаты';
  button.onclick = () => {
    const input = document.createElement('input');
    input.type = 'file'; input.accept = 'image/png,image/jpeg,image/webp';
    input.onchange = () => {
      const file = input.files?.[0];
      if (!file) return;
      if (file.size > 500 * 1024) return alert('Выберите изображение до 500 КБ.');
      const reader = new FileReader();
      reader.onload = async () => {
        try { await api('/admin/settings', { method: 'PATCH', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ transfer_qr_image: reader.result }) }); alert('QR-код сохранён.'); }
        catch (error) { alert(error.message); }
      };
      reader.readAsDataURL(file);
    };
    input.click();
  };
  document.body.append(button);
}

applyPaymentQr();
addUploadButton();
