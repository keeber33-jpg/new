# ПАРТНЁР CRM — Android

Android-оболочка для автономной HTML-CRM. Интерфейс открывается локально в WebView.

## Сборка APK
Откройте папку PartnerCRM_Android в Android Studio, дождитесь синхронизации Gradle и выберите Build → Build APK(s).

## GitHub Actions
Workflow Build Android APK собирает debug APK при push в main или вручную через Actions. Результат публикуется как артефакт PartnerCRM-debug-apk.

## Данные
База хранится локально на устройстве. Для переноса используйте экспорт/импорт. Пароль входа в исходной CRM: 0000.

Примечание: HTML-файл интерфейса CRM должен находиться в app/src/main/assets/index.html.
