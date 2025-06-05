package com.solution.smartparkingr.load.request;

import com.solution.smartparkingr.model.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SubscriptionRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Subscription type is required")
    private String subscriptionType;

    @NotBlank(message = "Billing cycle is required")
    private String billingCycle;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // Change to String

    @NotBlank(message = "Payment reference is required")
    private String paymentReference;

    private String cardNumber;

    private String expiryDate;

    private String cvv;

    private String cardName;

    @NotBlank(message = "Email is required")
    private String email;

    // Getter to convert string to enum
    public PaymentMethod getPaymentMethodAsEnum() {
        try {
            return PaymentMethod.valueOf(paymentMethod.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment method: " + paymentMethod);
        }
    }
}