import { Injectable, inject } from '@angular/core';
import { forkJoin, map, Observable } from 'rxjs';

import { DashboardOverview } from '../models/dashboard.model';
import { ClientApiService } from './client-api.service';
import { ReservationApiService } from './reservation-api.service';
import { RoomApiService } from './room-api.service';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly clientApi = inject(ClientApiService);
  private readonly roomApi = inject(RoomApiService);
  private readonly reservationApi = inject(ReservationApiService);

  getDashboardOverview(): Observable<DashboardOverview> {
    return forkJoin({
      clients: this.clientApi.getAllClients(),
      rooms: this.roomApi.getAllRooms(),
      reservations: this.reservationApi.getAllReservations()
    }).pipe(
      map(({ clients, rooms, reservations }) => ({
        stats: {
          totalClients: clients.length,
          totalRooms: rooms.length,
          totalReservations: reservations.length,
          availableRooms: rooms.filter((room) => room.available).length,
          confirmedReservations: reservations.filter((reservation) => reservation.status === 'CONFIRMED').length,
          createdReservations: reservations.filter((reservation) => reservation.status === 'CREATED').length,
          cancelledReservations: reservations.filter((reservation) => reservation.status === 'CANCELLED').length
        },
        recentReservations: [...reservations].sort((a, b) => (b.id ?? 0) - (a.id ?? 0)).slice(0, 5),
        featuredRooms: [...rooms]
          .sort((a, b) => Number(b.available) - Number(a.available) || b.pricePerNight - a.pricePerNight)
          .slice(0, 4),
        newestClients: [...clients]
          .sort(
            (a, b) =>
              new Date(b.createdAt ?? 0).getTime() - new Date(a.createdAt ?? 0).getTime()
          )
          .slice(0, 4)
      }))
    );
  }
}
