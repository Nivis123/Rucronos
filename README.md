# RuChronos Manager — Control Plane BFF

Стартовый backend-проект **Java 21 + Spring Boot 3.4.5 + Gradle 8.14.3** для IntelliJ IDEA.

## Что реализовано

- Базовый путь `/api/v1`.
- `GET /health`: `status`, `version`, `uptimeSeconds`, `databaseConnected`, `k8sConnected`.
- `GET /auth/config`: `mode`, `issuerUrl`, `clientId` для mock/OIDC.
- `POST /auth/login`, `POST /auth/refresh` (refresh rotation), `POST /auth/logout` (204).
- JWT HS256 с claims `sub`, `roles`, `exp`, `sid`; Spring Security фильтр проверяет подпись, срок действия и активность сессии.
- `GET /me`, `GET/POST /me/devices`, `DELETE /me/devices/{deviceId}`.
- RFC 9457 для ошибок: `application/problem+json` и поля `type`, `title`, `status`, `detail`, `instance`, `code`.
- Интеграционные тесты mock login, профиль, CRUD устройств и формат 401.
- Исходная спецификация сохранена в `docs/openapi.yaml`; согласованный контракт реализованного объёма — `docs/openapi-implemented.yaml`.

## Важные уточнения по контракту

В загруженном OpenAPI схема `Device` содержит `id`, `pushToken`, `platform`, `deviceName`, `createdAt`, `lastSeenAt`; повторная регистрация того же pushToken возвращает **200** и обновлённую запись, новая — **201**. Реализация возвращает поля исходного файла и совместимые алиасы `deviceId`/`registeredAt` из текстового задания; повторная регистрация того же pushToken возвращает **200** и обновлённую запись, новая — **201**.

В загруженном OpenAPI `AuthConfig` использует вложенный объект `oidc`, а `TokenPair` не включает `user`. Реализация возвращает плоские `issuerUrl`/`clientId` из задания и также сохраняет вложенный `oidc` из файла; `user` внутри TokenPair добавлен по тексту задачи. `User.email` также добавлен по описанию задачи.

В файле `/health` описан через `status`, `version`, `database`; реализация использует расширенную схему из текстового задания. Исходный OpenAPI сохранён без изменений.

## Запуск в IntelliJ IDEA

1. Распакуйте архив.
2. Установите JDK 21 (например, Temurin 21).
3. В IntelliJ IDEA выберите **File → Open** и откройте корневую папку `ruchronos-manager` (где находятся `settings.gradle` и `build.gradle`).
4. В **Settings → Build, Execution, Deployment → Build Tools → Gradle** укажите **Gradle JVM = JDK 21**, а Gradle distribution — **Wrapper**.
5. Дождитесь импорта Gradle. При первом запуске wrapper скачает Gradle 8.14.3; Gradle скачает зависимости с Maven Central. Нужен интернет.
6. Запустите `RuChronosManagerApplication` из IntelliJ или в терминале выполните:

   ```bash
   ./gradlew bootRun
   ```
   Windows: `gradlew.bat bootRun`.

7. API будет доступен по адресу `http://localhost:8080/api/v1`.

## Проверка

```bash
./gradlew clean test
./gradlew bootRun
```

Mock-учётные записи (пароль совпадает с логином): `admin/admin`, `operator/operator`, `viewer/viewer`. По умолчанию роль соответствует имени пользователя; при необходимости можно передать `role` в login request.

```bash
curl http://localhost:8080/api/v1/health

curl -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin"}'
```

Используйте `accessToken` из ответа как Bearer token:

```bash
curl http://localhost:8080/api/v1/me -H 'Authorization: Bearer <accessToken>'

curl -X POST http://localhost:8080/api/v1/me/devices \
  -H 'Authorization: Bearer <accessToken>' -H 'Content-Type: application/json' \
  -d '{"provider":"fcm","pushToken":"sample-push-token","platform":"android","deviceName":"Pixel 8"}'
```

Обновление и отзыв refresh-токена:

```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H 'Content-Type: application/json' -d '{"refreshToken":"<refreshToken>"}'

curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H 'Authorization: Bearer <accessToken>' -H 'Content-Type: application/json' \
  -d '{"refreshToken":"<refreshToken>"}'
```

## OIDC и переменные окружения

По умолчанию `AUTH_MODE=mock`. Для конфигурационного режима OIDC задайте:

- `AUTH_MODE=oidc`
- `OIDC_ISSUER_URL=https://sso.example.org/realms/main`
- `OIDC_CLIENT_ID=ruchronos-mobile`

Тогда `/auth/config` вернёт OIDC параметры, а `/auth/login` — 404. Это подготовка конфигурации, а не завершённая интеграция с IdP: discovery, Authorization Code + PKCE, JWKS и проверка issuer/audience требуют согласования конкретного провайдера.

Для любого не локального окружения обязательно задайте `JWT_SECRET` — случайную строку не короче 32 байт — и храните её в Secret Manager/Kubernetes Secret. Mock-пароли и in-memory хранилища предназначены только для локальной разработки: сессии и устройства теряются при перезапуске и не разделяются между инстансами.

`K8S_CONNECTED` по умолчанию `false`, поэтому `/health` показывает `DEGRADED`, пока не подключена реальная проверка Kubernetes API. H2 используется только как локальная БД для проверки подключения.

## Структура

- `auth/` — mock login, JWT, refresh/logout, security filter.
- `me/` — профиль пользователя.
- `device/` — регистрация и хранение устройств.
- `health/` — системный статус.
- `problem/` — RFC 9457 error handler.
- `config/` — настройки приложения и Spring Security.
- `docs/openapi.yaml` — исходный полный OpenAPI 3.1.0.
- `docs/openapi-implemented.yaml` — OpenAPI 3.1.0 для реализованных ручек и расширений из задачи.

## Границы этапа

Реализованы эндпоинты, перечисленные в постановке (Health, Auth, Me/Devices). Остальные разделы полного OpenAPI — Cluster, Tables, Quick actions, Thresholds, Notification channels, Incidents и WebSocket — оставлены для следующих этапов. OIDC подготовлен конфигурационно, но внешний login flow ещё не реализован.
