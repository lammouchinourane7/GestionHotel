# Hotel Reservation Platform Frontend

Frontend Angular 18 standalone pour la plateforme de reservation d hotel.

## Stack

- Angular 18
- TypeScript
- Standalone Components
- Angular Router
- Angular HttpClient
- Reactive Forms
- Tailwind CSS

## Base API

Toutes les requetes passent par l API Gateway :

- `http://localhost:8080`

L interception automatique est definie dans :

- [src/app/core/interceptors/api-prefix.interceptor.ts](</c:/Users/NOURANE/Downloads/projetMS/frontend/src/app/core/interceptors/api-prefix.interceptor.ts:1>)

## Authentification Keycloak

Le frontend est configure pour utiliser Keycloak avec le realm `hotel-realm`.

Configuration actuelle :

- URL Keycloak : `http://localhost:8085`
- Realm : `hotel-realm`
- Client : `hotel-frontend`

Fichier de configuration :

- `src/app/core/constants/auth.constants.ts`

Comportement :

- Les routes de l application sont protegees via un guard Angular.
- Les appels API vers `http://localhost:8080` envoient automatiquement le bearer token.
- Pour desactiver l auth cote frontend, passer `enabled` a `false` dans `auth.constants.ts`.

## Routes principales

- `/dashboard`
- `/clients`
- `/clients/new`
- `/clients/edit/:id`
- `/rooms`
- `/rooms/new`
- `/rooms/edit/:id`
- `/reservations`
- `/reservations/new`
- `/reservations/:id`
- `/not-found`

## Arborescence

```text
frontend/
|-- angular.json
|-- package.json
|-- postcss.config.js
|-- tailwind.config.js
`-- src/
    |-- index.html
    |-- main.ts
    |-- styles.css
    `-- app/
        |-- app.component.ts
        |-- app.component.html
        |-- app.config.ts
        |-- app.routes.ts
        |-- core/
        |   |-- constants/api.constants.ts
        |   |-- interceptors/
        |   |   |-- api-prefix.interceptor.ts
        |   |   `-- loading.interceptor.ts
        |   `-- services/
        |       |-- error-message.service.ts
        |       |-- layout.service.ts
        |       |-- loading.service.ts
        |       `-- notification.service.ts
        |-- layout/
        |   |-- app-shell/
        |   |-- sidebar/
        |   `-- topbar/
        |-- models/
        |   |-- api-error.model.ts
        |   |-- client.model.ts
        |   |-- dashboard.model.ts
        |   |-- reservation.model.ts
        |   `-- room.model.ts
        |-- pages/
        |   |-- dashboard/
        |   |-- clients/
        |   |   |-- client-list/
        |   |   `-- client-form/
        |   |-- rooms/
        |   |   |-- room-list/
        |   |   `-- room-form/
        |   |-- reservations/
        |   |   |-- reservation-list/
        |   |   |-- reservation-form/
        |   |   `-- reservation-details/
        |   `-- not-found/
        |-- services/
        |   |-- client-api.service.ts
        |   |-- dashboard.service.ts
        |   |-- reservation-api.service.ts
        |   `-- room-api.service.ts
        `-- shared/
            |-- components/
            |   |-- empty-state/
            |   |-- form-field-error/
            |   |-- loading-spinner/
            |   |-- page-header/
            |   |-- section-card/
            |   |-- stat-card/
            |   |-- status-badge/
            |   `-- toast-container/
            `-- validators/
                `-- date-range.validator.ts
```

## Lancement

Depuis le dossier `frontend/` :

```bash
npm install
npm start
```

Application disponible sur :

- `http://localhost:4200`

Pour exposer sur le reseau local :

```bash
npm run start:host
```

## Build de production

```bash
npm run build
```

Le dossier de sortie est :

- `dist/frontend`

## Verification effectuee

- `npm install` execute
- `npm run build` execute avec succes

## Pages et fichiers utiles

- Shell global : [src/app/layout/app-shell/app-shell.component.ts](</c:/Users/NOURANE/Downloads/projetMS/frontend/src/app/layout/app-shell/app-shell.component.ts:1>)
- Dashboard : [src/app/pages/dashboard/dashboard-page.component.ts](</c:/Users/NOURANE/Downloads/projetMS/frontend/src/app/pages/dashboard/dashboard-page.component.ts:1>)
- Clients : [src/app/pages/clients/client-list/client-list-page.component.ts](</c:/Users/NOURANE/Downloads/projetMS/frontend/src/app/pages/clients/client-list/client-list-page.component.ts:1>)
- Formulaire client : [src/app/pages/clients/client-form/client-form-page.component.ts](</c:/Users/NOURANE/Downloads/projetMS/frontend/src/app/pages/clients/client-form/client-form-page.component.ts:1>)
- Rooms : [src/app/pages/rooms/room-list/room-list-page.component.ts](</c:/Users/NOURANE/Downloads/projetMS/frontend/src/app/pages/rooms/room-list/room-list-page.component.ts:1>)
- Formulaire room : [src/app/pages/rooms/room-form/room-form-page.component.ts](</c:/Users/NOURANE/Downloads/projetMS/frontend/src/app/pages/rooms/room-form/room-form-page.component.ts:1>)
- Reservations : [src/app/pages/reservations/reservation-list/reservation-list-page.component.ts](</c:/Users/NOURANE/Downloads/projetMS/frontend/src/app/pages/reservations/reservation-list/reservation-list-page.component.ts:1>)
- Formulaire reservation : [src/app/pages/reservations/reservation-form/reservation-form-page.component.ts](</c:/Users/NOURANE/Downloads/projetMS/frontend/src/app/pages/reservations/reservation-form/reservation-form-page.component.ts:1>)
- Details reservation : [src/app/pages/reservations/reservation-details/reservation-details-page.component.ts](</c:/Users/NOURANE/Downloads/projetMS/frontend/src/app/pages/reservations/reservation-details/reservation-details-page.component.ts:1>)
- Styles globaux Tailwind : [src/styles.css](</c:/Users/NOURANE/Downloads/projetMS/frontend/src/styles.css:1>)
