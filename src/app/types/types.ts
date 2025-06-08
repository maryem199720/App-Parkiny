export interface Notification {
  id: number;
  message: string;
  is_read: boolean; // Corrigez le nommage
  timestamp: string;
  
}

export interface Reservation {
  id: number;
  userId: number;
  slotId: number;
  startTime: string;
  endTime: string;
  status: 'confirmed' | 'cancelled' | 'completed' | 'expired';
  cost: number;
}

export interface SubscriptionOffer {
  id: number;
  name: 'Basique' | 'Premium' | 'Entreprise';
  price: number;
  duration: number;
  active: boolean;
  subscribers: number;
}

export interface OperatingHours {
  open: string;
  close: string;
}

export interface ParkingSettings {
  id?: number;
  maxSlots: number;
  subscriptionOffers: SubscriptionOffer[];
  reservedPremiumSlots: number;
  operatingHours: OperatingHours;
  maintenanceMode: boolean;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  password?: string;
  role: 'ROLE_ADMIN' | 'ROLE_USER';
  active: boolean;
  reservations?: Reservation[];
}
export interface AdminProfile {
  email: string;
}

export interface PasswordUpdate {
  currentPassword: string;
  newPassword: string;
}