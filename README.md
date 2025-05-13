# BalanceHub

Simple banking system with user accounts, balance updates and secure money transfers.

## Technologies Used

- Java 21
- Spring Boot 3.4.5
- PostgreSQL
- Redis (caching, queues)
- Elasticsearch (optional search capabilities)
- Flyway (DB migrations)
- MapStruct (DTO mapping)
- JWT (authentication)
- Testcontainers (integration tests)
- JUnit, Rest-Assured (testing)
- Maven

## How to Run
1. Clone the Repository
   git clone -b master git@github.com:Olga-Tysevich/BalanceHub.git

2. Set up .env like .env-example. Generate JWT keys (for example, using - https://jwt-keys.21no.de/), specify the expiration time of the keys, and fill in your database paths and credentials.

3. Run the Databases
   Navigate to the tracker directory and run the following command to start the databases:

   docker-compose up --build balancehub_db

   Run the following command to start the elastichsearch:

   docker-compose up --build elasticsearch

   Run the following command to start the redis:

   docker-compose up --build redis

   Run the following command to start the app:

   docker-compose up --build balancehub

4. The service is accessible at the URL: http://localhost:8080
