export interface Subscription {
    id: number;
    subscriptionType: string;
    startDate: string;
    endDate: string;
    status: string;
    price: number;
    billingCycle: string;
    parkingDurationLimit: number | null;
    advanceReservationDays: number;
    hasPremiumSpots: boolean;
    hasValetService: boolean;
    supportLevel: string;
    remainingPlaces: number;
    paymentStatus: string;
    sessionId: string;
    autoRenewal: boolean;
    user: { id: number };
  }
  

