import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Notification, User, Reservation, SubscriptionOffer, OperatingHours, ParkingSettings, AdminProfile, PasswordUpdate } from '../types/types';

interface AnalyticsData {
  totalVehicles: number;
  occupancyRate: number;
  dailyRevenue: number;
  averageParkingTime: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = 'http://localhost:8082/parking/api';
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) {}

  private handleError(operation: string) {
    return (error: any) => {
      console.error(`Erreur lors de ${operation}:`, error);
      return throwError(() => new Error(error.message || `Erreur lors de ${operation}.`));
    };
  }

  getAnalyticsData(): Observable<AnalyticsData> {
    return this.http.get<AnalyticsData>(`${this.apiUrl}/admin/analytics`, this.httpOptions).pipe(
      catchError(this.handleError('la récupération des données analytiques'))
    );
  }

  getChartData(period: 'day' | 'week' | 'month' = 'week'): Observable<any> {
    return this.http.get(`${this.apiUrl}/admin/charts?period=${period}`, this.httpOptions).pipe(
      catchError(this.handleError('la récupération des données de graphique'))
    );
  }

  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/admin/notifications`, this.httpOptions).pipe(
      catchError(this.handleError('la récupération des notifications'))
    );
  }

  markAsRead(id: number): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/admin/notifications/${id}/mark-as-read`, null, this.httpOptions).pipe(
      catchError(this.handleError('le marquage de la notification'))
    );
  }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/admin/users`, this.httpOptions).pipe(
      catchError(this.handleError('la récupération des utilisateurs'))
    );
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/admin/users/${id}`, this.httpOptions).pipe(
      catchError(this.handleError('la récupération de l\'utilisateur'))
    );
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/admin/users`, user, this.httpOptions).pipe(
      catchError(this.handleError('la création de l\'utilisateur'))
    );
  }

  updateUser(id: number, user: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/admin/users/${id}`, user, this.httpOptions).pipe(
      catchError(this.handleError('la mise à jour de l\'utilisateur'))
    );
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/admin/users/${id}`, this.httpOptions).pipe(
      catchError(this.handleError('la suppression de l\'utilisateur'))
    );
  }

  getUserReservations(userId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/admin/users/${userId}/reservations`, this.httpOptions).pipe(
      catchError(this.handleError('la récupération des réservations'))
    );
  }

  getParkingSettings(): Observable<ParkingSettings> {
    return this.http.get<ParkingSettings>(`${this.apiUrl}/admin/parking-settings`, this.httpOptions).pipe(
      catchError(this.handleError('la récupération des paramètres'))
    );
  }

  saveParkingSettings(settings: ParkingSettings): Observable<ParkingSettings> {
    return this.http.post<ParkingSettings>(`${this.apiUrl}/admin/parking-settings`, settings, this.httpOptions).pipe(
      catchError(this.handleError('la sauvegarde des paramètres'))
    );
  }

  getSettingsHistory(): Observable<{ timestamp: string; changes: Partial<ParkingSettings> }[]> {
    return this.http.get<{ timestamp: string; changes: Partial<ParkingSettings> }[]>(`${this.apiUrl}/admin/parking-settings/history`, this.httpOptions).pipe(
      catchError(this.handleError('la récupération de l\'historique'))
    );
  }

  exportSettings(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/admin/parking-settings/export`, { responseType: 'blob' }).pipe(
      catchError(this.handleError('l\'exportation des paramètres'))
    );
  }

  estimateRevenue(): Observable<{ monthly: number; annual: number }> {
    return this.http.get<{ monthly: number; annual: number }>(`${this.apiUrl}/admin/estimate-revenue`, this.httpOptions).pipe(
      catchError(this.handleError('l\'estimation des revenus'))
    );
  }

  subscribeToOffer(userId: number, offerId: number): Observable<any> {
    const payload = { userId, offerId };
    return this.http.post(`${this.apiUrl}/admin/subscriptions`, payload, this.httpOptions).pipe(
      catchError(this.handleError('la souscription à l\'offre'))
    );
  }

  getAdminProfile(): Observable<AdminProfile> {
    return this.http.get<AdminProfile>(`${this.apiUrl}/admin/profile`, this.httpOptions).pipe(
      catchError(this.handleError('la récupération du profil'))
    );
  }

  updateAdminProfile(profile: AdminProfile): Observable<AdminProfile> {
    return this.http.put<AdminProfile>(`${this.apiUrl}/admin/profile`, profile, this.httpOptions).pipe(
      catchError(this.handleError('la mise à jour du profil'))
    );
  }

  updateAdminPassword(passwordUpdate: PasswordUpdate): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/admin/password`, passwordUpdate, this.httpOptions).pipe(
      catchError(this.handleError('la mise à jour du mot de passe'))
    );
  }

  logout(): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/auth/logout`, null, this.httpOptions).pipe(
      catchError((error) => {
        console.error('Erreur lors de la déconnexion:', error);
        return of({ message: 'Déconnexion côté client.' }); // Fallback
      })
    );
  }
}