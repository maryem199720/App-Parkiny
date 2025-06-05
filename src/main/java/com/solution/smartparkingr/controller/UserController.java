package com.solution.smartparkingr.controller;

import com.solution.smartparkingr.load.request.ChangePasswordRequest;
import com.solution.smartparkingr.load.request.PasswordResetRequest;
import com.solution.smartparkingr.load.request.UserProfileUpdateRequest;
import com.solution.smartparkingr.load.response.UserProfileResponse;
import com.solution.smartparkingr.model.User;
import com.solution.smartparkingr.service.UserService;
import com.solution.smartparkingr.service.VehicleService; // Add this import
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService; // Add this dependency

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        User user = userService.getCurrentUserForProfile();

        List<UserProfileResponse.VehicleInfo> vehicleInfos = user.getVehicles() != null
                ? user.getVehicles().stream()
                .map(vehicle -> new UserProfileResponse.VehicleInfo(
                        vehicle.getId(),
                        vehicle.getMatricule(),
                        vehicle.getVehicleType(),
                        vehicle.getBrand(),
                        vehicle.getModel(),
                        vehicle.getColor(),
                        vehicle.getMatriculeImageUrl()
                ))
                .collect(Collectors.toList())
                : List.of();

        List<UserProfileResponse.ReservationInfo> reservationInfos = user.getReservations() != null
                ? user.getReservations().stream()
                .map(res -> new UserProfileResponse.ReservationInfo(
                        res.getParkingSpot().getId(),
                        res.getVehicle() != null ? res.getVehicle().getId() : null,
                        res.getStartTime(),
                        res.getEndTime(),
                        res.getStatus().name(),
                        res.getTotalCost(),
                        res.getCreatedAt()
                ))
                .collect(Collectors.toList())
                : List.of();

        List<UserProfileResponse.SubscriptionInfo> subscriptionInfos = user.getSubscriptions() != null
                ? user.getSubscriptions().stream()
                .map(sub -> new UserProfileResponse.SubscriptionInfo(
                        sub.getId(),
                        sub.getSubscriptionType(),
                        sub.getBillingCycle(),
                        sub.getStartDate(),
                        sub.getEndDate(),
                        sub.getStatus().name()
                ))
                .collect(Collectors.toList())
                : List.of();

        UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                vehicleInfos,
                subscriptionInfos,
                reservationInfos
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateUserProfile(@Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        User user = userService.updateUserProfile(updateRequest);

        List<UserProfileResponse.VehicleInfo> vehicleInfos = user.getVehicles() != null
                ? user.getVehicles().stream()
                .map(vehicle -> new UserProfileResponse.VehicleInfo(
                        vehicle.getId(),
                        vehicle.getMatricule(),
                        vehicle.getVehicleType(),
                        vehicle.getBrand(),
                        vehicle.getModel(),
                        vehicle.getColor(),
                        vehicle.getMatriculeImageUrl()
                ))
                .collect(Collectors.toList())
                : List.of();

        List<UserProfileResponse.ReservationInfo> reservationInfos = user.getReservations() != null
                ? user.getReservations().stream()
                .map(res -> new UserProfileResponse.ReservationInfo(
                        res.getParkingSpot().getId(),
                        res.getVehicle() != null ? res.getVehicle().getId() : null,
                        res.getStartTime(),
                        res.getEndTime(),
                        res.getStatus().name(),
                        res.getTotalCost(),
                        res.getCreatedAt()
                ))
                .collect(Collectors.toList())
                : List.of();

        List<UserProfileResponse.SubscriptionInfo> subscriptionInfos = user.getSubscriptions() != null
                ? user.getSubscriptions().stream()
                .map(sub -> new UserProfileResponse.SubscriptionInfo(
                        sub.getId(),
                        sub.getSubscriptionType(),
                        sub.getBillingCycle(),
                        sub.getStartDate(),
                        sub.getEndDate(),
                        sub.getStatus().name()
                ))
                .collect(Collectors.toList())
                : List.of();

        UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                vehicleInfos,
                subscriptionInfos,
                reservationInfos
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/request-change-password-code")
    public ResponseEntity<Map<String, String>> requestChangePasswordCode(@Valid @RequestBody ChangePasswordRequest request) {
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            throw new IllegalArgumentException("Current password is required");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("New password is required");
        }
        userService.requestChangePasswordCode(request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Verification code sent successfully"));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        if ("email".equalsIgnoreCase(request.getMethod())) {
            if (request.getEmail() == null || request.getEmail().isBlank()) {
                throw new IllegalArgumentException("Email is required for email method");
            }
        } else if ("sms".equalsIgnoreCase(request.getMethod())) {
            if (request.getPhone() == null || request.getPhone().isBlank()) {
                throw new IllegalArgumentException("Phone is required for sms method");
            }
        } else {
            throw new IllegalArgumentException("Invalid method: " + request.getMethod());
        }
        userService.requestPasswordReset(request.getMethod(), request.getEmail(), request.getPhone());
        return ResponseEntity.ok(Map.of("message", "Verification code sent successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        if (request.getVerificationCode() == null || request.getVerificationCode().isBlank()) {
            throw new IllegalArgumentException("Verification code is required");
        }
        if (request.getCurrentPassword() == null && (request.getNewPassword() == null || request.getNewPassword().isBlank())) {
            throw new IllegalArgumentException("New password is required for forgot password flow");
        }
        userService.changePassword(request.getCurrentPassword(), request.getNewPassword(), request.getVerificationCode());
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @PostMapping("/cancel-reservation/{id}")
    public ResponseEntity<Map<String, String>> cancelReservation(@PathVariable Long id) {
        userService.cancelReservation(id);
        return ResponseEntity.ok(Map.of("message", "Reservation cancelled successfully"));
    }

    @PutMapping("/update-reservation/{id}")
    public ResponseEntity<Map<String, String>> updateReservation(@PathVariable Long id,
                                                                 @RequestParam LocalDateTime newStartTime,
                                                                 @RequestParam LocalDateTime newEndTime) {
        userService.updateReservation(id, newStartTime, newEndTime);
        return ResponseEntity.ok(Map.of("message", "Reservation updated successfully"));
    }

    @PostMapping("/cancel-subscription/{id}")
    public ResponseEntity<Map<String, String>> cancelSubscription(@PathVariable Long id) {
        userService.cancelSubscription(id);
        return ResponseEntity.ok(Map.of("message", "Subscription cancelled successfully"));
    }

    @PutMapping("/update-vehicle/{id}")
    public ResponseEntity<Map<String, String>> updateVehicleInfo(@PathVariable Long id,
                                                                 @RequestParam(required = false) String matricule,
                                                                 @RequestParam(required = false) String vehicleType,
                                                                 @RequestParam(required = false) String brand,
                                                                 @RequestParam(required = false) String model,
                                                                 @RequestParam(required = false) String color,
                                                                 @RequestParam(required = false) String matriculeImageUrl) {
        userService.updateVehicleInfo(id, matricule, vehicleType, brand, model, color, matriculeImageUrl);
        return ResponseEntity.ok(Map.of("message", "Vehicle information updated successfully"));
    }

    @DeleteMapping("/vehicle/{id}")
    public ResponseEntity<Map<String, String>> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(Map.of("message", "Vehicle deleted successfully"));
    }
}