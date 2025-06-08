package com.solution.smartparkingr.controller;

import com.solution.smartparkingr.admin.AdminService;
import com.solution.smartparkingr.admin.dto.AdminProfileDTO;
import com.solution.smartparkingr.admin.dto.AnalyticsDataDTO;
import com.solution.smartparkingr.admin.dto.ParkingSettingsDTO;
import com.solution.smartparkingr.admin.dto.PasswordUpdateDTO;
import com.solution.smartparkingr.admin.dto.ReservationDTO;
import com.solution.smartparkingr.admin.dto.UserDTO;
import com.solution.smartparkingr.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/profile")
    public ResponseEntity<AdminProfileDTO> getAdminProfile() {
        return ResponseEntity.ok(adminService.getAdminProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<AdminProfileDTO> updateAdminProfile(@Valid @RequestBody AdminProfileDTO adminProfileDTO) {
        return ResponseEntity.ok(adminService.updateAdminProfile(adminProfileDTO));
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> updateAdminPassword(@Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return ResponseEntity.ok(adminService.updateAdminPassword(passwordUpdateDTO));
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsDataDTO> getAnalyticsData() {
        return ResponseEntity.ok(adminService.getAnalyticsData());
    }

    @GetMapping("/charts")
    public ResponseEntity<Map<String, Object>> getChartData(@RequestParam String period) {
        return ResponseEntity.ok(adminService.getChartData(period));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(adminService.createUser(userDTO));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        User updatedUser = adminService.updateUser(id, updates);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}/reservations")
    public ResponseEntity<List<ReservationDTO>> getUserReservations(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserReservations(userId));
    }

    @GetMapping("/parking-settings")
    public ResponseEntity<ParkingSettingsDTO> getParkingSettings() {
        return ResponseEntity.ok(adminService.getParkingSettings());
    }

    @PostMapping("/parking-settings")
    public ResponseEntity<ParkingSettingsDTO> saveParkingSettings(@RequestBody ParkingSettingsDTO settingsDTO) {
        return ResponseEntity.ok(adminService.saveParkingSettings(settingsDTO));
    }

    @GetMapping("/parking-settings/history")
    public ResponseEntity<List<Map<String, Object>>> getSettingsHistory() {
        return ResponseEntity.ok(adminService.getSettingsHistory());
    }

    @GetMapping("/parking-settings/export")
    public ResponseEntity<byte[]> exportSettings() throws Exception {
        String json = adminService.exportSettings();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=parking-settings.json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json.getBytes());
    }

    @GetMapping("/estimate-revenue")
    public ResponseEntity<Map<String, Double>> estimateRevenue() {
        Map<String, Double> response = new HashMap<>();
        response.put("monthly", adminService.estimateRevenue().getMonthly());
        response.put("annual", adminService.estimateRevenue().getAnnual());
        return ResponseEntity.ok(response);
    }
}