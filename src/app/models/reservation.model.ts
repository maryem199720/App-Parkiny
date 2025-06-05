// src/app/models/reservation.model.ts
export interface Reservation {
  userId: number;
  parkingPlaceId: number;
  matricule: string;
  startTime: string; // Matches the formatted string sent to the server
  endTime: string;   // Matches the formatted string sent to the server
  vehicleType: string;
  paymentMethod: string;
  email: string;
  subscriptionId?: number | null; // Optional, matches server
  specialRequest: string;
}

export interface ReservationResponse {
  reservationId: string; // Match server response (e.g., "RES-1")
  message?: string;      // Optional message from server
  sessionId?: string;    // Optional sessionId
  paymentVerificationCode?: string | null; // Optional code
  reservationConfirmationCode?: string | null; // Optional code
  redirect_url?: string; // Optional redirect URL
}