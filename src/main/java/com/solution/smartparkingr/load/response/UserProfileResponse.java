package com.solution.smartparkingr.load.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private List<VehicleInfo> vehicles;
    private List<SubscriptionInfo> subscriptions; // Subscription history
    private List<ReservationInfo> reservationHistory;

    public UserProfileResponse(Long id, String firstName, String lastName, String email, String phone,
                               List<VehicleInfo> vehicles, List<SubscriptionInfo> subscriptions,
                               List<ReservationInfo> reservationHistory) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.vehicles = vehicles;
        this.subscriptions = subscriptions;
        this.reservationHistory = reservationHistory;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public List<VehicleInfo> getVehicles() { return vehicles; }
    public void setVehicles(List<VehicleInfo> vehicles) { this.vehicles = vehicles; }
    public List<SubscriptionInfo> getSubscriptions() { return subscriptions; }
    public void setSubscriptions(List<SubscriptionInfo> subscriptions) { this.subscriptions = subscriptions; }
    public List<ReservationInfo> getReservationHistory() { return reservationHistory; }
    public void setReservationHistory(List<ReservationInfo> reservationHistory) { this.reservationHistory = reservationHistory; }

    public static class VehicleInfo {
        private Long id;
        private String matricule;
        private String vehicleType;
        private String brand;
        private String model;
        private String color;
        private String matriculeImageUrl;

        public VehicleInfo(Long id, String matricule, String vehicleType, String brand, String model, String color, String matriculeImageUrl) {
            this.id = id;
            this.matricule = matricule;
            this.vehicleType = vehicleType;
            this.brand = brand;
            this.model = model;
            this.color = color;
            this.matriculeImageUrl = matriculeImageUrl;
        }

        // Getters
        public Long getId() { return id; }
        public String getMatricule() { return matricule; }
        public String getVehicleType() { return vehicleType; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public String getColor() { return color; }
        public String getMatriculeImageUrl() { return matriculeImageUrl; }
    }

    public static class SubscriptionInfo {
        private Long id;
        private String type;
        private String billingCycle;
        private LocalDate startDate; // Changed from LocalDateTime to LocalDate
        private LocalDate endDate;   // Changed from LocalDateTime to LocalDate
        private String status;

        public SubscriptionInfo(Long id, String type, String billingCycle, LocalDate startDate, LocalDate endDate, String status) {
            this.id = id;
            this.type = type;
            this.billingCycle = billingCycle;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
        }

        // Getters
        public Long getId() { return id; }
        public String getType() { return type; }
        public String getBillingCycle() { return billingCycle; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public String getStatus() { return status; }
    }

    public static class ReservationInfo {
        private Long parkingSpotId;
        private Long vehicleId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private Double totalCost;
        private LocalDateTime createdAt;

        public ReservationInfo(Long parkingSpotId, Long vehicleId, LocalDateTime startTime, LocalDateTime endTime,
                               String status, Double totalCost, LocalDateTime createdAt) {
            this.parkingSpotId = parkingSpotId;
            this.vehicleId = vehicleId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.status = status;
            this.totalCost = totalCost;
            this.createdAt = createdAt;
        }

        // Getters
        public Long getParkingSpotId() { return parkingSpotId; }
        public Long getVehicleId() { return vehicleId; }
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public String getStatus() { return status; }
        public Double getTotalCost() { return totalCost; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
}