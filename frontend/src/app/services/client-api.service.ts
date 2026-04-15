import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  Client,
  ClientChange,
  ClientHistory,
  ClientPageResponse,
  ClientPageSearchMode,
  ClientProfile,
  ClientStatistics
} from '../models/client.model';

@Injectable({
  providedIn: 'root'
})
export class ClientApiService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/clients';

  getAllClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.endpoint);
  }

  getClientsPage(options: {
    page: number;
    size: number;
    sortBy: string;
    direction: string;
    mode: ClientPageSearchMode;
    query: string;
  }): Observable<ClientPageResponse> {
    const params = new HttpParams()
      .set('page', String(options.page))
      .set('size', String(options.size))
      .set('sortBy', options.sortBy)
      .set('direction', options.direction)
      .set('mode', options.mode)
      .set('query', options.query);

    return this.http.get<ClientPageResponse>(`${this.endpoint}/page`, { params });
  }

  getClientStatistics(): Observable<ClientStatistics> {
    return this.http.get<ClientStatistics>(`${this.endpoint}/statistics`);
  }

  downloadClientsCsv(): Observable<Blob> {
    return this.http.get(`${this.endpoint}/export/csv`, { responseType: 'blob' });
  }

  getClientById(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.endpoint}/${id}`);
  }

  getClientProfile(id: number): Observable<ClientProfile> {
    return this.http.get<ClientProfile>(`${this.endpoint}/${id}/profile`);
  }

  getClientHistory(id: number): Observable<ClientHistory> {
    return this.http.get<ClientHistory>(`${this.endpoint}/${id}/history`);
  }

  getClientChanges(id: number): Observable<ClientChange[]> {
    return this.http.get<ClientChange[]>(`${this.endpoint}/${id}/changes`);
  }

  createClient(client: Client): Observable<Client> {
    return this.http.post<Client>(this.endpoint, client);
  }

  updateClient(id: number, client: Client): Observable<Client> {
    return this.http.put<Client>(`${this.endpoint}/${id}`, client);
  }

  deleteClient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpoint}/${id}`);
  }

  searchByFirstName(firstName: string): Observable<Client[]> {
    return this.http.get<Client[]>(`${this.endpoint}/search/firstname/${encodeURIComponent(firstName)}`);
  }

  searchByLastName(lastName: string): Observable<Client[]> {
    return this.http.get<Client[]>(`${this.endpoint}/search/lastname/${encodeURIComponent(lastName)}`);
  }

  searchByEmail(email: string): Observable<Client> {
    return this.http.get<Client>(`${this.endpoint}/search/email/${encodeURIComponent(email)}`);
  }
}
