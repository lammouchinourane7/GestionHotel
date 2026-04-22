import { Client } from './client.model';
import { Room } from './room.model';

export type ReservationStatus = 'CREATED' | 'CONFIRMED' | 'CANCELLED';

export interface Reservation {
  id?: number;
  clientId: number;
  roomId: number;
  startDate: string;
  endDate: string;
  totalPrice?: number;
  status?: ReservationStatus | string;
}

export interface ReservationDetails extends Reservation {
  client?: Client;
  room?: Room;
}
