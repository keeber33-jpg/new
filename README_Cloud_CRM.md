# ПАРТНЁР CRM — облачная версия

В репозитории находится Android-приложение и облачная версия CRM для компьютера.

## Важные файлы
- PartnerCRM_Android/app/src/main/assets/index.html — интерфейс CRM для Android.
- supabase_setup.sql — таблица общей базы и политики RLS.
- PartnerCRM_Android/README.md — инструкция по подключению.
- .github/workflows/build-android.yml — автоматическая сборка APK.

Перед первым использованием выполните SQL, создайте учётные записи менеджеров в Supabase и перенесите текущую базу JSON из старой CRM через кнопку «Импорт базы» в облачной версии. После этого подключайте телефон.
