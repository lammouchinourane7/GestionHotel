# Plateforme de Reservation d'Hotel - Backend Microservices

Backend complet Spring Boot 3 / Java 17, structure en microservices, pret pour demonstration Postman.

## Vue d'ensemble

Modules generes :

- `eureka-server`
- `api-gateway`
- `client-service`
- `room-service`
- `reservation-service`

Technologies :

- Java 17
- Spring Boot 3
- Spring Cloud Netflix Eureka
- Spring Cloud Gateway
- Spring Cloud OpenFeign
- Spring Data JPA
- H2 Database
- MySQL
- RabbitMQ
- Lombok
- Jakarta Validation
- Springdoc OpenAPI / Swagger

Ports utilises :

- `8761` : Eureka Server
- `8080` : API Gateway
- `8081` : client-service
- `8082` : room-service
- `8083` : reservation-service
- `15672` : RabbitMQ Management

## Arborescence

Arborescence source principale du projet (hors `target/` et hors `.m2/`) :

```text
projetMS/
|-- pom.xml
|-- docker-compose.yml
|-- README.md
|-- eureka-server/
|   |-- pom.xml
|   |-- Dockerfile
|   `-- src/
|       `-- main/
|           |-- java/com/hotel/eurekaserver/EurekaServerApplication.java
|           `-- resources/application.yml
|-- api-gateway/
|   |-- pom.xml
|   |-- Dockerfile
|   `-- src/
|       `-- main/
|           |-- java/com/hotel/apigateway/ApiGatewayApplication.java
|           `-- resources/application.yml
|-- client-service/
|   |-- pom.xml
|   |-- Dockerfile
|   `-- src/
|       `-- main/
|           |-- java/com/hotel/clientservice/ClientServiceApplication.java
|           |-- java/com/hotel/clientservice/config/OpenApiConfig.java
|           |-- java/com/hotel/clientservice/config/RabbitMQConfig.java
|           |-- java/com/hotel/clientservice/controller/ClientController.java
|           |-- java/com/hotel/clientservice/dto/ClientExistenceResponse.java
|           |-- java/com/hotel/clientservice/dto/ClientRequest.java
|           |-- java/com/hotel/clientservice/dto/ClientResponse.java
|           |-- java/com/hotel/clientservice/entity/Client.java
|           |-- java/com/hotel/clientservice/exception/ApiErrorResponse.java
|           |-- java/com/hotel/clientservice/exception/ClientNotFoundException.java
|           |-- java/com/hotel/clientservice/exception/EmailAlreadyExistsException.java
|           |-- java/com/hotel/clientservice/exception/GlobalExceptionHandler.java
|           |-- java/com/hotel/clientservice/mapper/ClientMapper.java
|           |-- java/com/hotel/clientservice/messaging/event/ClientCreatedEvent.java
|           |-- java/com/hotel/clientservice/messaging/event/ClientDeletedEvent.java
|           |-- java/com/hotel/clientservice/messaging/producer/ClientEventProducer.java
|           |-- java/com/hotel/clientservice/repository/ClientRepository.java
|           |-- java/com/hotel/clientservice/service/ClientService.java
|           |-- java/com/hotel/clientservice/service/impl/ClientServiceImpl.java
|           |-- resources/application.yml
|           `-- resources/data.sql
|-- room-service/
|   |-- pom.xml
|   |-- Dockerfile
|   `-- src/
|       `-- main/
|           |-- java/com/hotel/roomservice/RoomServiceApplication.java
|           |-- java/com/hotel/roomservice/config/OpenApiConfig.java
|           |-- java/com/hotel/roomservice/config/RabbitMQConfig.java
|           |-- java/com/hotel/roomservice/controller/RoomController.java
|           |-- java/com/hotel/roomservice/dto/RoomAvailabilityResponse.java
|           |-- java/com/hotel/roomservice/dto/RoomExistenceResponse.java
|           |-- java/com/hotel/roomservice/dto/RoomPriceResponse.java
|           |-- java/com/hotel/roomservice/dto/RoomRequest.java
|           |-- java/com/hotel/roomservice/dto/RoomResponse.java
|           |-- java/com/hotel/roomservice/entity/Room.java
|           |-- java/com/hotel/roomservice/exception/ApiErrorResponse.java
|           |-- java/com/hotel/roomservice/exception/GlobalExceptionHandler.java
|           |-- java/com/hotel/roomservice/exception/RoomNotFoundException.java
|           |-- java/com/hotel/roomservice/mapper/RoomMapper.java
|           |-- java/com/hotel/roomservice/messaging/event/RoomUnavailableEvent.java
|           |-- java/com/hotel/roomservice/messaging/event/RoomUpdatedEvent.java
|           |-- java/com/hotel/roomservice/messaging/producer/RoomEventProducer.java
|           |-- java/com/hotel/roomservice/repository/RoomRepository.java
|           |-- java/com/hotel/roomservice/service/RoomService.java
|           |-- java/com/hotel/roomservice/service/impl/RoomServiceImpl.java
|           |-- resources/application.yml
|           `-- resources/data.sql
`-- reservation-service/
    |-- pom.xml
    |-- Dockerfile
    `-- src/
        `-- main/
            |-- java/com/hotel/reservationservice/ReservationServiceApplication.java
            |-- java/com/hotel/reservationservice/config/FeignConfig.java
            |-- java/com/hotel/reservationservice/config/OpenApiConfig.java
            |-- java/com/hotel/reservationservice/config/RabbitMQConfig.java
            |-- java/com/hotel/reservationservice/controller/ReservationController.java
            |-- java/com/hotel/reservationservice/dto/ClientExistenceResponse.java
            |-- java/com/hotel/reservationservice/dto/ClientSummaryResponse.java
            |-- java/com/hotel/reservationservice/dto/ReservationDetailsResponse.java
            |-- java/com/hotel/reservationservice/dto/ReservationRequest.java
            |-- java/com/hotel/reservationservice/dto/ReservationResponse.java
            |-- java/com/hotel/reservationservice/dto/RoomAvailabilityResponse.java
            |-- java/com/hotel/reservationservice/dto/RoomExistenceResponse.java
            |-- java/com/hotel/reservationservice/dto/RoomPriceResponse.java
            |-- java/com/hotel/reservationservice/dto/RoomSummaryResponse.java
            |-- java/com/hotel/reservationservice/entity/Reservation.java
            |-- java/com/hotel/reservationservice/entity/ReservationStatus.java
            |-- java/com/hotel/reservationservice/exception/ApiErrorResponse.java
            |-- java/com/hotel/reservationservice/exception/ClientNotFoundException.java
            |-- java/com/hotel/reservationservice/exception/GlobalExceptionHandler.java
            |-- java/com/hotel/reservationservice/exception/InvalidReservationDateException.java
            |-- java/com/hotel/reservationservice/exception/RemoteServiceException.java
            |-- java/com/hotel/reservationservice/exception/ReservationNotFoundException.java
            |-- java/com/hotel/reservationservice/exception/RoomNotAvailableException.java
            |-- java/com/hotel/reservationservice/exception/RoomNotFoundException.java
            |-- java/com/hotel/reservationservice/feign/ClientServiceClient.java
            |-- java/com/hotel/reservationservice/feign/RoomServiceClient.java
            |-- java/com/hotel/reservationservice/mapper/ReservationMapper.java
            |-- java/com/hotel/reservationservice/messaging/consumer/ReservationEventConsumer.java
            |-- java/com/hotel/reservationservice/messaging/event/ClientDeletedEvent.java
            |-- java/com/hotel/reservationservice/messaging/event/ReservationCancelledEvent.java
            |-- java/com/hotel/reservationservice/messaging/event/ReservationConfirmedEvent.java
            |-- java/com/hotel/reservationservice/messaging/event/ReservationCreatedEvent.java
            |-- java/com/hotel/reservationservice/messaging/event/RoomUnavailableEvent.java
            |-- java/com/hotel/reservationservice/messaging/producer/ReservationEventProducer.java
            |-- java/com/hotel/reservationservice/repository/ReservationRepository.java
            |-- java/com/hotel/reservationservice/service/ReservationService.java
            |-- java/com/hotel/reservationservice/service/impl/ReservationServiceImpl.java
            |-- resources/application.yml
            `-- resources/data.sql
```

## Lancement rapide

### 1. Demarrer les dependances

Depuis la racine :

```bash
docker compose up -d
```

Cela demarre :

- MySQL sur `localhost:3306`
- RabbitMQ sur `localhost:5672`
- RabbitMQ Management sur `http://localhost:15672`

### 1.b Optionnel - Activer Keycloak pour securite

Le projet peut tourner sans auth (par defaut), ou avec auth JWT via Keycloak.

1. Creer un fichier `.env` a la racine avec :

```bash
AUTH_ENABLED=true
KEYCLOAK_ISSUER_URI=http://localhost:8085/realms/hotel-realm
```

2. Demarrer (ou redemarrer) la stack :

```bash
docker compose up -d --build
```

3. Keycloak est alors disponible sur :

- `http://localhost:8085` (admin/admin)

Le realm `hotel-realm` est importe automatiquement avec :

- utilisateur demo : `demo`
- mot de passe demo : `demo123`
- client public : `hotel-frontend`

Comportement frontend :

- Le frontend Angular est protege par Keycloak.
- Sans session active, l utilisateur est redirige vers la page de login Keycloak.
- Les appels API vers le gateway portent automatiquement le token bearer.

Recuperer un token (exemple password grant pour test local) :

```bash
curl -X POST "http://localhost:8085/realms/hotel-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=hotel-frontend" \
  -d "grant_type=password" \
  -d "username=demo" \
  -d "password=demo123"
```

Puis appeler le gateway avec :

```bash
Authorization: Bearer <access_token>
```

Creer un nouvel utilisateur auth (fresh session) :

```bash
docker exec hotel-keycloak /opt/keycloak/bin/kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin --password admin
docker exec hotel-keycloak /opt/keycloak/bin/kcadm.sh create users -r hotel-realm -s username=<new-username> -s enabled=true -s emailVerified=true
docker exec hotel-keycloak /opt/keycloak/bin/kcadm.sh set-password -r hotel-realm --username <new-username> --new-password <new-password> --temporary=false
docker exec hotel-keycloak /opt/keycloak/bin/kcadm.sh update users/<user-id> -r hotel-realm -s requiredActions=[]
```

PowerShell (pour recuperer `<user-id>`) :

```powershell
$user = docker exec hotel-keycloak /opt/keycloak/bin/kcadm.sh get users -r hotel-realm -q username=<new-username>
$userId = ($user | ConvertFrom-Json)[0].id
docker exec hotel-keycloak /opt/keycloak/bin/kcadm.sh update users/$userId -r hotel-realm -s requiredActions=[]
```

Note navigateur :

- Utiliser soit `http://localhost:4200`, soit `http://127.0.0.1:4200`.
- Les deux origines sont autorisees dans la config Gateway/Keycloak.

Commande token testee et fonctionnelle :

```bash
curl -X POST "http://localhost:8085/realms/hotel-realm/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=hotel-frontend" \
  -d "grant_type=password" \
  -d "username=demo" \
  -d "password=demo123"
```

### 2. Compiler le projet

```bash
mvn clean package -DskipTests
```

### 3. Demarrer les services dans cet ordre

Option A - via Maven :

```bash
mvn -pl eureka-server spring-boot:run
mvn -pl api-gateway spring-boot:run
mvn -pl client-service spring-boot:run
mvn -pl room-service spring-boot:run
mvn -pl reservation-service spring-boot:run
```

Option B - via les jars :

```bash
java -jar eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar
java -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar
java -jar client-service/target/client-service-0.0.1-SNAPSHOT.jar
java -jar room-service/target/room-service-0.0.1-SNAPSHOT.jar
java -jar reservation-service/target/reservation-service-0.0.1-SNAPSHOT.jar
```

## URLs utiles

- Eureka Dashboard : `http://localhost:8761`
- Gateway base URL : `http://localhost:8080`
- Client Swagger : `http://localhost:8081/swagger-ui.html`
- Room Swagger : `http://localhost:8082/swagger-ui.html`
- Reservation Swagger : `http://localhost:8083/swagger-ui.html`
- H2 Console : `http://localhost:8081/h2-console`
- RabbitMQ Management : `http://localhost:15672` (`guest/guest`)

H2 config :

- JDBC URL : `jdbc:h2:mem:clientdb`
- Username : `sa`
- Password : vide

## Configuration des bases

### client-service

- H2 in-memory
- Donnees inserees avec `client-service/src/main/resources/data.sql`

### room-service

- Database : `roomdb`
- URL : `jdbc:mysql://localhost:3306/roomdb?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC`
- Username : `root`
- Password : vide

### reservation-service

- Database : `reservationdb`
- URL : `jdbc:mysql://localhost:3306/reservationdb?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC`
- Username : `root`
- Password : vide

## Routes Gateway

Toutes les requetes peuvent passer par le Gateway :

- `/api/clients/**`
- `/api/rooms/**`
- `/api/reservations/**`

Exemples :

- `GET http://localhost:8080/api/clients`
- `GET http://localhost:8080/api/rooms`
- `GET http://localhost:8080/api/reservations`

## Endpoints REST

### Client Service

- `GET /api/clients`
- `GET /api/clients/{id}`
- `POST /api/clients`
- `PUT /api/clients/{id}`
- `DELETE /api/clients/{id}`
- `GET /api/clients/search/firstname/{firstName}`
- `GET /api/clients/search/lastname/{lastName}`
- `GET /api/clients/search/email/{email}`
- `GET /api/clients/{id}/exists`

### Room Service

- `GET /api/rooms`
- `GET /api/rooms/{id}`
- `POST /api/rooms`
- `PUT /api/rooms/{id}`
- `DELETE /api/rooms/{id}`
- `GET /api/rooms/search/type/{type}`
- `GET /api/rooms/search/availability/{available}`
- `GET /api/rooms/search/capacity/{capacity}`
- `GET /api/rooms/{id}/exists`
- `GET /api/rooms/{id}/availability`
- `GET /api/rooms/{id}/price`

### Reservation Service

- `GET /api/reservations`
- `GET /api/reservations/{id}`
- `GET /api/reservations/{id}/details`
- `POST /api/reservations`
- `DELETE /api/reservations/{id}`
- `PUT /api/reservations/{id}/confirm`
- `PUT /api/reservations/{id}/cancel`
- `GET /api/reservations/client/{clientId}`
- `GET /api/reservations/room/{roomId}`

## Exemples JSON

### Creer un client

Request:

```json
{
  "firstName": "Sarra",
  "lastName": "Khadhraoui",
  "email": "sarra.khadhraoui@example.com",
  "phone": "+21622001122",
  "address": "Nabeul",
  "nationalId": "CIN10044"
}
```

Response:

```json
{
  "id": 4,
  "firstName": "Sarra",
  "lastName": "Khadhraoui",
  "email": "sarra.khadhraoui@example.com",
  "phone": "+21622001122",
  "address": "Nabeul",
  "nationalId": "CIN10044",
  "createdAt": "2026-04-14T12:00:00"
}
```

### Creer une chambre

Request:

```json
{
  "number": "301",
  "type": "SUITE",
  "capacity": 4,
  "pricePerNight": 480.0,
  "available": true
}
```

Response:

```json
{
  "id": 5,
  "number": "301",
  "type": "SUITE",
  "capacity": 4,
  "pricePerNight": 480.0,
  "available": true
}
```

### Creer une reservation

Request:

```json
{
  "clientId": 1,
  "roomId": 2,
  "startDate": "2026-06-10",
  "endDate": "2026-06-13"
}
```

Response:

```json
{
  "id": 3,
  "clientId": 1,
  "roomId": 2,
  "startDate": "2026-06-10",
  "endDate": "2026-06-13",
  "totalPrice": 780.0,
  "status": "CREATED"
}
```

### Reservation enrichie via OpenFeign

Request:

```http
GET /api/reservations/1/details
```

Response:

```json
{
  "id": 1,
  "clientId": 1,
  "roomId": 1,
  "startDate": "2026-05-10",
  "endDate": "2026-05-13",
  "totalPrice": 540.0,
  "status": "CONFIRMED",
  "client": {
    "id": 1,
    "firstName": "Nour",
    "lastName": "Haddad",
    "email": "nour.haddad@example.com",
    "phone": "+21650111222",
    "address": "Tunis Centre",
    "nationalId": "CIN10001",
    "createdAt": "2026-04-01T10:00:00"
  },
  "room": {
    "id": 1,
    "number": "101",
    "type": "STANDARD",
    "capacity": 2,
    "pricePerNight": 180.0,
    "available": true
  }
}
```

### Format d'erreur JSON

```json
{
  "timestamp": "2026-04-14T12:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/reservations",
  "validationErrors": {
    "startDate": "Start date is required"
  }
}
```

## Scenarios OpenFeign a demontrer via Postman

### Scenario 1 - Creation d'une reservation

1. Verifier qu'un client existe avec `GET /api/clients/1/exists`
2. Verifier qu'une chambre existe avec `GET /api/rooms/2/exists`
3. Creer la reservation via `POST /api/reservations`
4. Le `reservation-service` verifie :
   - client existe
   - chambre existe
   - chambre disponible
   - prix par nuit
5. Le total est calcule automatiquement et le statut est `CREATED`

### Scenario 2 - Reservation enrichie

1. Appeler `GET /api/reservations/{id}/details`
2. Le `reservation-service` appelle :
   - `client-service` pour le detail du client
   - `room-service` pour le detail de la chambre
3. La reponse regroupe reservation + client + room

## Evenements RabbitMQ

Exchange :

- `hotel.events.exchange`

Routing keys publiees :

- `client.created`
- `client.deleted`
- `room.updated`
- `room.unavailable`
- `reservation.created`
- `reservation.cancelled`
- `reservation.confirmed`

Queues consommees par `reservation-service` :

- `reservation.client.deleted.queue`
- `reservation.room.unavailable.queue`

## Scenarios RabbitMQ a demontrer via Postman

### Scenario 1 - Suppression d'un client

1. Verifier les reservations d'un client : `GET /api/reservations/client/1`
2. Supprimer le client : `DELETE /api/clients/1`
3. `client-service` publie `client.deleted`
4. `reservation-service` consomme l'evenement et annule les reservations liees
5. Re-verifier : `GET /api/reservations/client/1`

### Scenario 2 - Chambre indisponible

1. Verifier les reservations d'une chambre : `GET /api/reservations/room/2`
2. Mettre la chambre indisponible avec :

```json
{
  "number": "102",
  "type": "DELUXE",
  "capacity": 3,
  "pricePerNight": 260.0,
  "available": false
}
```

3. Appeler `PUT /api/rooms/2`
4. `room-service` publie `room.updated` puis `room.unavailable`
5. `reservation-service` consomme `room.unavailable` et annule les reservations associees
6. Re-verifier : `GET /api/reservations/room/2`

## Validation et bonnes pratiques integrees

- validation `@Valid` sur les DTOs Request
- DTO Request / Response distincts
- mappers dedies
- services interfaces + implementations
- `GlobalExceptionHandler` dans chaque microservice
- Swagger actif sur les 3 microservices metier
- H2 console active sur `client-service`
- OpenFeign actif dans `reservation-service`
- RabbitMQ producteur / consommateur integres
- projet multi-modules Maven pret pour demo

## Dockerfiles

Chaque module contient un `Dockerfile` simple base sur Java 17 :

- `eureka-server/Dockerfile`
- `api-gateway/Dockerfile`
- `client-service/Dockerfile`
- `room-service/Dockerfile`
- `reservation-service/Dockerfile`

## Verification effectuee

Le projet a ete compile avec succes depuis la racine via Maven (`package -DskipTests`).
