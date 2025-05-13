# BalanceHub

Простая банковская система с учетными записями пользователей, обновлениями баланса и безопасными денежными переводами.

## Используемые технологии

- Java 21
- Spring Boot 3.4.5
- PostgreSQL
- Redis (кэширование, очереди)
- Elasticsearch (дополнительные возможности поиска)
- Flyway (миграции БД)
- MapStruct (маппинг DTO)
- JWT (аутентификация)
- Testcontainers (интеграционные тесты)
- JUnit, Rest-Assured (тестирование)
- Maven

## Как запустить
1. Клонировать репозиторий
   git clone -b master git@github.com:Olga-Tysevich/BalanceHub.git

2. Настройте .env по образцу .env-example. Сгенерируйте ключи JWT (например, с помощью - https://jwt-keys.21no.de/), укажите срок действия ключей и заполните пути к базе данных и учетные данные.
 
3. Запустите базы данных:

   Перейдите в каталог трекера и выполните следующую команду для запуска баз данных:

   docker-compose up --build balancehub_db

   Выполните следующую команду для запуска elastichsearch:

   docker-compose up --build elasticsearch

   Выполните следующую команду для запуска redis:

   docker-compose up --build redis

   Выполните следующую команду для запуска приложения:

   docker-compose up --build app

4. Служба доступна по URL-адресу: http://localhost:8080
