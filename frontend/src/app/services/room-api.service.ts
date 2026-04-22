import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Room, RoomAvailabilityResponse, RoomPriceResponse } from '../models/room.model';

@Injectable({
  providedIn: 'root'
})
export class RoomApiService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/rooms';

  getAllRooms(): Observable<Room[]> {
    return this.http.get<Room[]>(this.endpoint);
  }

  getRoomById(id: number): Observable<Room> {
    return this.http.get<Room>(`${this.endpoint}/${id}`);
  }

  createRoom(room: Room): Observable<Room> {
    return this.http.post<Room>(this.endpoint, room);
  }

  updateRoom(id: number, room: Room): Observable<Room> {
    return this.http.put<Room>(`${this.endpoint}/${id}`, room);
  }

  deleteRoom(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpoint}/${id}`);
  }

  searchByType(type: string): Observable<Room[]> {
    return this.http.get<Room[]>(`${this.endpoint}/search/type/${encodeURIComponent(type)}`);
  }

  searchByAvailability(available: boolean): Observable<Room[]> {
    return this.http.get<Room[]>(`${this.endpoint}/search/availability/${available}`);
  }

  searchByCapacity(capacity: number): Observable<Room[]> {
    return this.http.get<Room[]>(`${this.endpoint}/search/capacity/${capacity}`);
  }

  getRoomPrice(id: number): Observable<RoomPriceResponse> {
    return this.http.get<RoomPriceResponse>(`${this.endpoint}/${id}/price`);
  }

  getRoomAvailability(id: number): Observable<RoomAvailabilityResponse> {
    return this.http.get<RoomAvailabilityResponse>(`${this.endpoint}/${id}/availability`);
  }
}
