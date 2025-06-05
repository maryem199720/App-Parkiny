package com.solution.smartparkingr.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VerificationCodeStore {

    // Inner class to hold the verification code, new password, and expiry time
    public static class VerificationData {
        private final String code;
        private final String newPassword;
        private final LocalDateTime expiryTime;

        public VerificationData(String code, String newPassword, LocalDateTime expiryTime) {
            this.code = code;
            this.newPassword = newPassword;
            this.expiryTime = expiryTime;
        }

        public String getCode() {
            return code;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }

    // Store email -> VerificationData
    private final Map<String, VerificationData> codeStore = new ConcurrentHashMap<>();
    private static final long CODE_VALIDITY_MINUTES = 10; // Code expires after 10 minutes

    // Store the verification code and associated new password (newPassword can be null for forgot password flow)
    public void storeCodeWithPassword(String email, String code, String newPassword) {
        LocalDateTime expiryTime = LocalDateTime.now().plus(CODE_VALIDITY_MINUTES, ChronoUnit.MINUTES);
        codeStore.put(email, new VerificationData(code, newPassword, expiryTime));
    }

    // Backward-compatible method to store only the code (sets newPassword to null)
    public void storeCode(String email, String code) {
        storeCodeWithPassword(email, code, null);
    }

    // Retrieve the full verification data (code and new password)
    public VerificationData getVerificationData(String email) {
        VerificationData data = codeStore.get(email);
        if (data == null) {
            return null;
        }
        if (data.isExpired()) {
            codeStore.remove(email);
            return null;
        }
        return data;
    }

    // Backward-compatible method to retrieve only the code
    public String getCode(String email) {
        VerificationData data = getVerificationData(email);
        return data != null ? data.getCode() : null;
    }

    // Remove the code and associated data for the given email
    public void removeCode(String email) {
        codeStore.remove(email);
    }
}