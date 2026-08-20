# Еда секунды для Android

Планшетное приложение открывает «Еду секунды» без адресной строки и хранит сессию входа внутри приложения.

Откройте папку `android` в Android Studio, дождитесь Gradle Sync и выберите **Build → Build APK(s)**. Готовый APK появится в `app/build/outputs/apk/debug/app-debug.apk`.

Минимальная версия Android: 8.0 (API 26). Если временный адрес Cloudflare Tunnel изменится, замените `HOME_URL` в `MainActivity.java` и соберите APK снова.
