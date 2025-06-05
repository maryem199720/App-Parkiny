package com.solution.smartparkingr.load.request;

import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {

    private String currentPassword; // Optional, required only for change password flow

    @Size(min = 8, max = 50, message = "New password must be between 8 and 50 characters")
    private String newPassword; // Optional, required only in forgot password flow or initial request

    private String verificationCode; // Optional, required only in change-password step

    // Getters and Setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}