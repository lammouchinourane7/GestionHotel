export interface ClientStayPreferences {
  preferredRoomType: string;
  preferredBedType: string;
  preferredFloor: number | null;
  quietRoom: boolean;
  nonSmoking: boolean;
  highFloor: boolean;
  needsBabyBed: boolean;
  specialRequests: string;
}

export interface Client {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
  nationalId: string;
  createdAt?: string;
  updatedAt?: string;
  stayPreferences?: ClientStayPreferences;
}

export interface ClientReservation {
  id: number;
  clientId: number;
  roomId: number;
  startDate: string;
  endDate: string;
  totalPrice: number;
  status: string;
}

export type ClientLoyaltyStatus = 'NEW' | 'BRONZE' | 'SILVER' | 'GOLD' | 'PLATINUM';

export interface ClientLoyalty {
  clientId: number;
  status: ClientLoyaltyStatus;
  points: number;
  reservationCount: number;
  confirmedReservations: number;
  createdReservations: number;
  cancelledReservations: number;
  totalSpent: number;
  averageSpent: number;
}

export interface ClientHistory {
  clientId: number;
  clientFullName: string;
  reservationCount: number;
  confirmedReservations: number;
  createdReservations: number;
  cancelledReservations: number;
  totalSpent: number;
  averageSpent: number;
  firstReservationDate?: string | null;
  lastReservationDate?: string | null;
  nextReservationDate?: string | null;
  reservations: ClientReservation[];
}

export interface ClientProfile {
  client: Client;
  stayPreferences: ClientStayPreferences;
  loyalty: ClientLoyalty;
  historySummary: ClientHistory;
  recentReservations: ClientReservation[];
}

export type ClientChangeAction = 'CREATED' | 'UPDATED' | 'PREFERENCES_UPDATED' | 'DELETED';

export interface ClientChange {
  id: number;
  clientId: number;
  action: ClientChangeAction;
  details: string;
  changedBy: string;
  changedAt: string;
}

export interface ClientStatistics {
  totalClients: number;
  newClientsLast30Days: number;
  clientsWithPreferences: number;
  clientsWithReservations: number;
  totalReservations: number;
  confirmedReservations: number;
  cancelledReservations: number;
  totalRevenue: number;
  averageReservationsPerClient: number;
  averageRevenuePerClient: number;
  loyaltyDistribution: Record<string, number>;
}

export type ClientPageSearchMode = 'ALL' | 'FIRSTNAME' | 'LASTNAME' | 'EMAIL';

export interface ClientPageResponse {
  content: Client[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  sortBy: string;
  direction: 'asc' | 'desc' | string;
  mode: ClientPageSearchMode;
  query: string;
}
