(function () {
  'use strict';
  function request(method, path, body, done) {
    var xhr = new XMLHttpRequest();
    xhr.open(method, '/api' + path, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    var token = localStorage.getItem('ns_token');
    if (token) xhr.setRequestHeader('Authorization', 'Bearer ' + token);
    xhr.onreadystatechange = function () {
      if (xhr.readyState !== 4) return;
      var data = {};
      try { data = JSON.parse(xhr.responseText || '{}'); } catch (_) {}
      done(xhr.status, data);
    };
    xhr.send(body ? JSON.stringify(body) : null);
  }
  function login(root) {
    root.innerHTML = '<main class="center receipt"><h1>Еда секунды</h1><p>Вход аккаунтом «Новости секунды»</p><form id="legacy-login"><input name="email" type="email" placeholder="Почта" required><input name="password" type="password" placeholder="Пароль" required><button class="primary">Войти</button><p id="legacy-error"></p></form></main>';
    document.getElementById('legacy-login').onsubmit = function (event) {
      event.preventDefault();
      var form = event.target;
      request('POST', '/auth/login', { email: form.email.value, password: form.password.value }, function (status, data) {
        if (status >= 200 && status < 300) { localStorage.setItem('ns_token', data.token); location.reload(); return; }
        document.getElementById('legacy-error').innerHTML = data.message || 'Не удалось войти';
      });
    };
  }
  function menu(root, me) {
    request('GET', '/menu', null, function (status, data) {
      if (status !== 200) { login(root); return; }
      var categories = data.categories || [], items = data.items || [], active = 'all';
      function draw() {
        var side = '<aside><b>Еда секунды</b><button data-cat="all">Все</button>';
        var i, c;
        for (i = 0; i < categories.length; i++) { c = categories[i]; side += '<button data-cat="' + c.id + '">' + c.name + '</button>'; }
        side += '<a class="legacy-link" href="/orders">Мои заказы</a>';
        if (me.food_role === 'admin' || me.food_role === 'manager') side += '<a class="legacy-link" href="/manager">Заказы</a>';
        if (me.food_role === 'admin') side += '<a class="legacy-link" href="/admin">Админ-панель</a>';
        side += '</aside><main><header><h1>Меню</h1></header><div class="grid">';
        for (i = 0; i < items.length; i++) { var item = items[i]; if (active !== 'all' && item.category_id !== active) continue; side += '<article><img src="' + (item.image_url || '') + '" alt=""><h3>' + item.name + '</h3><p>' + (item.description || '') + '</p><strong>' + (item.price_cents / 100).toFixed(2) + ' €</strong><button' + (item.is_available ? '' : ' disabled') + '>' + (item.is_available ? 'Добавить' : 'Нет в наличии') + '</button></article>'; }
        root.innerHTML = '<div class="app">' + side + '</div></main></div>';
        var buttons = root.querySelectorAll('[data-cat]');
        for (i = 0; i < buttons.length; i++) buttons[i].onclick = function () { active = this.getAttribute('data-cat'); draw(); };
      }
      draw();
    });
  }
  function start() {
    var root = document.getElementById('root');
    if (!root || root.children.length) return;
    request('GET', '/me', null, function (status, data) { if (status === 200) menu(root, data.user); else login(root); });
  }
  setTimeout(start, 1800);
}());
