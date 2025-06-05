package com.solution.smartparkingr.load.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solution.smartparkingr.model.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Parking Place ID is required")
    private Long parkingPlaceId;

    @NotBlank(message = "Matricule is required")
    @Pattern(regexp = "^[A-Z0-9\\s\\u0600-\\u06FF]{3,15}$", message = "Invalid matricule format. Use 3 to 15 characters (letters, numbers, spaces, or Arabic characters).")
    private String matricule;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    private String specialRequest;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private Long subscriptionId; // Optional, null for non-subscribed users

    // Normalize matricule before validation
    public void setMatricule(String matricule) {
        if (matricule != null) {
            // Trim whitespace and normalize spaces
            this.matricule = matricule.trim().replaceAll("\\s+", " ");
        } else {
            this.matricule = null;
        }
    }
}