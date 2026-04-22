import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  Reservation,
  ReservationClientOption,
  ReservationDetails,
  ReservationRoomOption
} from '../models/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class ReservationApiService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/reservations';

  getAllReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.endpoint);
  }

  getMyReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.endpoint}/me`);
  }

  getReservationById(id: number): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.endpoint}/${id}`);
  }

  getReservationDetails(id: number): Observable<ReservationDetails> {
    return this.http.get<ReservationDetails>(`${this.endpoint}/${id}/details`);
  }

  createReservation(reservation: Reservation): Observable<Reservation> {
    return this.http.post<Reservation>(this.endpoint, reservation);
  }

  deleteReservation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpoint}/${id}`);
  }

  confirmReservation(id: number): Observable<Reservation> {
    return this.http.put<Reservation>(`${this.endpoint}/${id}/confirm`, {});
  }

  cancelReservation(id: number): Observable<Reservation> {
    return this.http.put<Reservation>(`${this.endpoint}/${id}/cancel`, {});
  }

  getReservationsByClientId(clientId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.endpoint}/client/${clientId}`);
  }

  getReservationsByRoomId(roomId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.endpoint}/room/${roomId}`);
  }

  getReservationClientOptions(): Observable<ReservationClientOption[]> {
    return this.http.get<ReservationClientOption[]>(`${this.endpoint}/options/clients`);
  }

  getReservationRoomOptions(): Observable<ReservationRoomOption[]> {
    return this.http.get<ReservationRoomOption[]>(`${this.endpoint}/options/rooms`);
  }
}
