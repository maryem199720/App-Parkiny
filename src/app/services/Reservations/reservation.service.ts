// src/app/services/reservation.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reservation, ReservationResponse } from 'src/app/models/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = 'http://localhost:8082/parking/api'; // Your backend URL

  constructor(private http: HttpClient) {}

  getUserReservations(userId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/reservations/user/${userId}`, {
      headers: this.getAuthHeaders()
    });
  }

  createReservation(reservation: Reservation): Observable<ReservationResponse> {
    return this.http.post<ReservationResponse>(`${this.apiUrl}/createReservation`, reservation, {
      headers: this.getAuthHeaders()
    });
  }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); // Align with AuthService
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    });
  }
}