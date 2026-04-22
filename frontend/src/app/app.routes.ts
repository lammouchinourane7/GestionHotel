import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';
import { AppShellComponent } from './layout/app-shell/app-shell.component';

export const routes: Routes = [
  {
    path: '',
    component: AppShellComponent,
    canActivateChild: [authGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/dashboard/dashboard-page.component').then((m) => m.DashboardPageComponent)
      },
      {
        path: 'clients',
        loadComponent: () =>
          import('./pages/clients/client-list/client-list-page.component').then(
            (m) => m.ClientListPageComponent
          )
      },
      {
        path: 'clients/new',
        loadComponent: () =>
          import('./pages/clients/client-form/client-form-page.component').then(
            (m) => m.ClientFormPageComponent
          )
      },
      {
        path: 'clients/edit/:id',
        loadComponent: () =>
          import('./pages/clients/client-form/client-form-page.component').then(
            (m) => m.ClientFormPageComponent
          )
      },
      {
        path: 'clients/:id',
        loadComponent: () =>
          import('./pages/clients/client-profile/client-profile-page.component').then(
            (m) => m.ClientProfilePageComponent
          )
      },
      {
        path: 'rooms',
        loadComponent: () =>
          import('./pages/rooms/room-list/room-list-page.component').then((m) => m.RoomListPageComponent)
      },
      {
        path: 'rooms/new',
        loadComponent: () =>
          import('./pages/rooms/room-form/room-form-page.component').then((m) => m.RoomFormPageComponent)
      },
      {
        path: 'rooms/edit/:id',
        loadComponent: () =>
          import('./pages/rooms/room-form/room-form-page.component').then((m) => m.RoomFormPageComponent)
      },
      {
        path: 'reservations',
        loadComponent: () =>
          import('./pages/reservations/reservation-list/reservation-list-page.component').then(
            (m) => m.ReservationListPageComponent
          )
      },
      {
        path: 'reservations/new',
        loadComponent: () =>
          import('./pages/reservations/reservation-form/reservation-form-page.component').then(
            (m) => m.ReservationFormPageComponent
          )
      },
      {
        path: 'reservations/:id',
        loadComponent: () =>
          import('./pages/reservations/reservation-details/reservation-details-page.component').then(
            (m) => m.ReservationDetailsPageComponent
          )
      },
      {
        path: 'not-found',
        loadComponent: () =>
          import('./pages/not-found/not-found-page.component').then((m) => m.NotFoundPageComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/not-found'
  }
];
