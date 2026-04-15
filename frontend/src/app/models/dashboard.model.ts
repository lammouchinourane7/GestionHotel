import { Client } from './client.model';
import { Reservation } from './reservation.model';
import { Room } from './room.model';

export interface DashboardStats {
  totalClients: number;
  totalRooms: number;
  totalReservations: number;
  availableRooms: number;
  confirmedReservations: number;
  createdReservations: number;
  cancelledReservations: number;
}

export interface DashboardOverview {
  stats: DashboardStats;
  recentReservations: Reservation[];
  featuredRooms: Room[];
  newestClients: Client[];
}
