export interface Room {
  id?: number;
  number: string;
  type: string;
  capacity: number;
  pricePerNight: number;
  available: boolean;
}

export interface RoomPriceResponse {
  roomId: number;
  pricePerNight: number;
}

export interface RoomAvailabilityResponse {
  roomId: number;
  available: boolean;
}
