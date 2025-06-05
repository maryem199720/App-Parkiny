// subscription.service.ts
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StorageService } from 'src/app/auth/services/storage/storage.service';

export interface Subscription {
  id: number;
  userId: number;
  subscriptionType: string;
  billingCycle: 'monthly' | 'annual';
  status: string;
  remainingPlaces: number;
  startDate: string; // Adjusted to string to match backend
  endDate: string;   // Adjusted to string to match backend
}

// Remove SubscriptionHistory interface since we're using Subscription[] for history
@Injectable({
  providedIn: 'root',
})
export class SubscriptionService {
  private apiUrl = 'http://localhost:8082/parking/api';

  constructor(private http: HttpClient, private storageService: StorageService) {}

  private getAuthHeaders(): HttpHeaders {
    const token = this.storageService.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token || ''}`,
      'Content-Type': 'application/json',
    });
  }

  getSubscriptionPlans(): Observable<any> {
    return this.http.get(`${this.apiUrl}/subscription-plans`, {
      headers: this.getAuthHeaders(),
    });
  }

  getActiveSubscription(userId: number): Observable<Subscription> {
  return this.http.get<Subscription>(`${this.apiUrl}/subscriptions/active?userId=${userId}`, {
    headers: this.getAuthHeaders(),
  });
}

  getUserProfile(): Observable<any> {
    return this.http.get(`${this.apiUrl}/user/profile`, {
      headers: this.getAuthHeaders(),
    });
  }

  subscribe(
    subscriptionType: string,
    billingCycle: string,
    amount: number,
    paymentMethod: 'CARTE_BANCAIRE',
    email: string,
    cardDetails: any
  ): Observable<any> {
    const userId = this.storageService.getUserId();
    const payload = {
      userId,
      subscriptionType,
      billingCycle,
      amount,
      paymentMethod,
      paymentReference: cardDetails.cardNumber?.substring(12, 16) || 'XXXX',
      email,
      cardNumber: cardDetails.cardNumber,
      expiryDate: cardDetails.expiryDate,
      cvv: cardDetails.cvv,
      cardName: cardDetails.cardName,
    };
    return this.http.post(`${this.apiUrl}/subscribe`, payload, {
      headers: this.getAuthHeaders(),
    });
  }

  confirmSubscription(sessionId: string, confirmationCode: string): Observable<any> {
    const body = { sessionId, confirmationCode }; // Match backend expected keys
    return this.http.post(`${this.apiUrl}/confirmSubscription`, body, {
      headers: this.getAuthHeaders()
    });
  }

  getSubscriptionHistory(userId: number, month?: number, year?: number): Observable<Subscription[]> {
    let params = new HttpParams().set('userId', userId.toString());
    if (month && year) {
      params = params.set('month', month.toString()).set('year', year.toString());
    }
    return this.http.get<Subscription[]>(`${this.apiUrl}/subscriptions/history`, {
      headers: this.getAuthHeaders(),
      params
    });
  }

  deleteSubscription(subscriptionId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/subscriptions/${subscriptionId}`, {
      headers: this.getAuthHeaders(),
    });
  }
}