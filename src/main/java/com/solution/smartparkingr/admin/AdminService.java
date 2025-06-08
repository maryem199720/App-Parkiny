package com.solution.smartparkingr.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solution.smartparkingr.admin.dto.*;
import com.solution.smartparkingr.model.*;
import com.solution.smartparkingr.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingSettingsRepository parkingSettingsRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    private final List<Map<String, Object>> settingsHistory = new ArrayList<>();

    public AnalyticsDataDTO getAnalyticsData() {
        AnalyticsDataDTO analytics = new AnalyticsDataDTO();

        long totalReservations = reservationRepository.count();
        analytics.setTotalReservations((int) totalReservations);

        double totalRevenue = reservationRepository.findAll().stream()
                .filter(r -> r.getTotalCost() != null)
                .mapToDouble(Reservation::getTotalCost)
                .sum();
        analytics.setTotalRevenue(totalRevenue);

        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        double dailyRevenue = reservationRepository.findByStartTimeBetween(todayStart, todayEnd).stream()
                .filter(r -> r.getTotalCost() != null)
                .mapToDouble(Reservation::getTotalCost)
                .sum();
        analytics.setDailyRevenue(dailyRevenue);

        List<Long> activeUserIds = reservationRepository.findByStatusNotIn(
                        List.of(ReservationStatus.CANCELLED, ReservationStatus.EXPIRED)
                ).stream()
                .map(r -> r.getUser().getId())
                .distinct()
                .collect(Collectors.toList());
        analytics.setActiveUsers(activeUserIds.size());

        List<Long> vehicleIds = reservationRepository.findAll().stream()
                .filter(r -> r.getVehicle() != null && r.getVehicle().getId() != null)
                .map(r -> r.getVehicle().getId())
                .distinct()
                .collect(Collectors.toList());
        analytics.setTotalVehicles(vehicleIds.size());

        double averageParkingTime = reservationRepository.findAll().stream()
                .filter(r -> r.getStartTime() != null && r.getEndTime() != null)
                .mapToDouble(r -> ChronoUnit.MINUTES.between(r.getStartTime(), r.getEndTime()) / 60.0)
                .average()
                .orElse(0.0);
        analytics.setAverageParkingTime(averageParkingTime);

        long occupiedSpots = parkingSpotRepository.findAll().stream()
                .filter(spot -> !spot.isAvailable())
                .count();
        int totalSpots = (int) parkingSpotRepository.count();
        double occupancyRate = totalSpots > 0 ? (double) occupiedSpots / totalSpots * 100 : 0;
        analytics.setOccupancyRate(occupancyRate);

        List<Map<String, Object>> reservationsByDay = new ArrayList<>();
        int days = 7;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < days; i++) {
            LocalDateTime dayStart = now.minusDays(days - i - 1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = now.minusDays(days - i - 1).withHour(23).withMinute(59).withSecond(59);
            List<Reservation> reservations = reservationRepository.findByStartTimeBetween(dayStart, dayEnd);
            double revenue = reservations.stream()
                    .filter(r -> r.getTotalCost() != null)
                    .mapToDouble(Reservation::getTotalCost)
                    .sum();
            long count = reservations.size();
            long occupied = reservations.stream()
                    .filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                    .count();
            double rate = totalSpots > 0 ? (double) occupied / totalSpots * 100 : 0;

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", dayStart.format(dateFormatter));
            dayData.put("hour", null);
            dayData.put("count", count);
            dayData.put("revenue", revenue);
            dayData.put("rate", rate);
            reservationsByDay.add(dayData);
        }
        analytics.setReservationsByDay(reservationsByDay);

        return analytics;
    }

    public Map<String, Object> getChartData(String period) {
        Map<String, Object> response = new HashMap<>();
        int totalSpots = (int) parkingSpotRepository.count();

        List<Map<String, Object>> occupancyTrend = new ArrayList<>();
        if ("day".equalsIgnoreCase(period)) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:00");
            for (int i = 0; i < 24; i++) {
                LocalDateTime hourStart = now.minusHours(24 - i);
                long occupiedSpots = reservationRepository.findByStatus(ReservationStatus.CONFIRMED).stream()
                        .filter(r -> r.getStartTime().isAfter(hourStart) && r.getStartTime().isBefore(hourStart.plusHours(1)))
                        .count();
                double rate = totalSpots > 0 ? (double) occupiedSpots / totalSpots * 100 : 0;
                Map<String, Object> hourData = new HashMap<>();
                hourData.put("hour", hourStart.format(hourFormatter));
                hourData.put("rate", rate);
                occupancyTrend.add(hourData);
            }
        }
        response.put("occupancyTrend", occupancyTrend);

        List<Map<String, Object>> dailyRevenue = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int days = period.equalsIgnoreCase("week") ? 7 : 30;
        for (int i = 0; i < days; i++) {
            LocalDateTime dayStart = now.minusDays(days - i - 1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = now.minusDays(days - i - 1).withHour(23).withMinute(59).withSecond(59);
            double revenue = reservationRepository.findByStartTimeBetween(dayStart, dayEnd).stream()
                    .filter(r -> r.getTotalCost() != null)
                    .mapToDouble(Reservation::getTotalCost)
                    .sum();
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("day", dayStart.format(dateFormatter));
            dayData.put("revenue", revenue);
            dailyRevenue.add(dayData);
        }
        response.put("dailyRevenue", dailyRevenue);

        long occupiedSpots = parkingSpotRepository.findAll().stream()
                .filter(spot -> !spot.isAvailable())
                .count();
        double occupied = (double) occupiedSpots;
        double free = (double) (totalSpots - occupiedSpots);
        Map<String, Object> occupancy = new HashMap<>();
        occupancy.put("occupied", occupied);
        occupancy.put("free", free);
        response.put("occupancy", occupancy);

        return response;
    }

    public List<Map<String, String>> getNotifications() {
        return List.of(
                Map.of("message", "Nouvelle réservation", "timestamp", LocalDateTime.now().toString()),
                Map.of("message", "Maintenance planifiée", "timestamp", LocalDateTime.now().toString())
        );
    }

    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToUserDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé : " + id));
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        System.out.println("Données reçues pour la création : " + userDTO.toString());
        if (userDTO.getEmail() == null || userDTO.getPhone() == null || userDTO.getPassword() == null ||
                userDTO.getFirstName() == null || userDTO.getLastName() == null) {
            System.out.println("Champs manquants : " + userDTO.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les champs firstName, lastName, email, phone et password sont requis");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            System.out.println("Email déjà utilisé : " + userDTO.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email déjà utilisé : " + userDTO.getEmail());
        }
        if (userRepository.existsByPhone(userDTO.getPhone())) {
            System.out.println("Téléphone déjà utilisé : " + userDTO.getPhone());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Téléphone déjà utilisé : " + userDTO.getPhone());
        }

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setActive(userDTO.isActive());

        Role role = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> {
                    System.out.println("Rôle ROLE_USER non trouvé");
                    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle non trouvé : ROLE_USER");
                });
        user.setRoles(Set.of(role));

        User savedUser = userRepository.save(user);
        System.out.println("Utilisateur créé : " + savedUser.getEmail());
        return mapToUserDTO(savedUser);
    }

    @Transactional
    public User updateUser(Long id, Map<String, Object> updates) {
        System.out.println("Received update request for user ID " + id + " with raw data: " + updates.toString());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé : " + id));
        System.out.println("Found user: " + user.getEmail() + ", " + user.getPhone());

        if (updates.containsKey("email")) {
            String newEmail = (String) updates.get("email");
            System.out.println("Processing email update: " + newEmail);
            if (newEmail != null && !newEmail.equals(user.getEmail())) {
                boolean emailExists = userRepository.existsByEmail(newEmail);
                System.out.println("Email exists in DB: " + emailExists);
                if (emailExists) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email déjà utilisé par un autre utilisateur : " + newEmail);
                }
                user.setEmail(newEmail);
            }
        }

        if (updates.containsKey("phone")) {
            String newPhone = (String) updates.get("phone");
            System.out.println("Processing phone update: " + newPhone);
            if (newPhone != null && !newPhone.equals(user.getPhone())) {
                boolean phoneExists = userRepository.existsByPhone(newPhone);
                System.out.println("Phone exists in DB: " + phoneExists);
                if (phoneExists) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Téléphone déjà utilisé par un autre utilisateur : " + newPhone);
                }
                user.setPhone(newPhone);
            }
        }

        User savedUser = userRepository.save(user);
        System.out.println("User updated successfully: " + savedUser.getEmail());
        return savedUser;
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé : " + id));
        userRepository.delete(user);
    }

    public List<ReservationDTO> getUserReservations(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::mapToReservationDTO)
                .collect(Collectors.toList());
    }

    public ParkingSettingsDTO getParkingSettings() {
        System.out.println("Appel de getParkingSettings...");
        ParkingSettings settings = parkingSettingsRepository.findById(1L)
                .orElseGet(() -> {
                    System.out.println("Aucun paramètre trouvé, création d'un nouveau...");
                    ParkingSettings newSettings = new ParkingSettings();
                    newSettings.setMaxSlots((int) parkingSpotRepository.count());
                    newSettings.setReservedPremiumSlots((int) parkingSpotRepository.findAll().stream()
                            .filter(spot -> "premium".equalsIgnoreCase(spot.getType()))
                            .count());
                    OperatingHours hours = new OperatingHours();
                    hours.setOpen("08:00");
                    hours.setClose("20:00");
                    newSettings.setOperatingHours(hours);
                    newSettings.setMaintenanceMode(false);
                    return parkingSettingsRepository.save(newSettings);
                });

        ParkingSettingsDTO dto = mapToParkingSettingsDTO(settings);
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findAll();
        List<SubscriptionOfferDTO> offers = plans.stream().map(this::mapToSubscriptionOfferDTO).collect(Collectors.toList());
        dto.setSubscriptionOffers(offers);
        System.out.println("Paramètres renvoyés: " + dto.toString());
        return dto;
    }

    @Transactional
    public ParkingSettingsDTO saveParkingSettings(ParkingSettingsDTO settingsDTO) {
        System.out.println("Données reçues pour sauvegarde: " + settingsDTO.toString());
        ParkingSettings settings = parkingSettingsRepository.findById(1L)
                .orElse(new ParkingSettings());
        ParkingSettingsDTO oldSettingsDTO = mapToParkingSettingsDTO(settings);
        List<SubscriptionPlan> oldPlans = subscriptionPlanRepository.findAll();
        List<SubscriptionOfferDTO> oldOffers = oldPlans.stream().map(this::mapToSubscriptionOfferDTO).collect(Collectors.toList());

        // Detect changes
        Map<String, Object> changes = new HashMap<>();
        // maxSlots
        if (settingsDTO.getMaxSlots() != settings.getMaxSlots()) {
            changes.put("maxSlots", Map.of("old", settings.getMaxSlots(), "new", settingsDTO.getMaxSlots()));
        }
        // reservedPremiumSlots
        if (settingsDTO.getReservedPremiumSlots() != settings.getReservedPremiumSlots()) {
            changes.put("reservedPremiumSlots", Map.of("old", settings.getReservedPremiumSlots(), "new", settingsDTO.getReservedPremiumSlots()));
        }
        // subscriptionOffers
        List<Map<String, Object>> offerChanges = new ArrayList<>();
        List<SubscriptionOfferDTO> newOffers = settingsDTO.getSubscriptionOffers();
        Map<Long, SubscriptionOfferDTO> oldOffersMap = oldOffers.stream()
                .filter(o -> o.getId() != null)
                .collect(Collectors.toMap(SubscriptionOfferDTO::getId, o -> o));
        Map<Long, SubscriptionOfferDTO> newOffersMap = newOffers.stream()
                .filter(o -> o.getId() != null)
                .collect(Collectors.toMap(SubscriptionOfferDTO::getId, o -> o));

        // Added or updated offers
        for (SubscriptionOfferDTO newOffer : newOffers) {
            Map<String, Object> change = new HashMap<>();
            if (newOffer.getId() == null || !oldOffersMap.containsKey(newOffer.getId())) {
                // Added
                change.put("action", "added");
                change.put("new", newOffer);
                offerChanges.add(change);
            } else {
                // Check for updates
                SubscriptionOfferDTO oldOffer = oldOffersMap.get(newOffer.getId());
                if (!Objects.equals(newOffer.getName(), oldOffer.getName()) ||
                        !Objects.equals(newOffer.getPrice(), oldOffer.getPrice()) ||
                        !Objects.equals(newOffer.getDuration(), oldOffer.getDuration()) ||
                        !Objects.equals(newOffer.isActive(), oldOffer.isActive()) ||
                        !Objects.equals(newOffer.getSubscribers(), oldOffer.getSubscribers())) {
                    change.put("action", "updated");
                    change.put("old", oldOffer);
                    change.put("new", newOffer);
                    offerChanges.add(change);
                }
            }
        }

        // Deleted offers
        for (SubscriptionOfferDTO oldOffer : oldOffers) {
            if (!newOffersMap.containsKey(oldOffer.getId())) {
                Map<String, Object> change = new HashMap<>();
                change.put("action", "deleted");
                change.put("old", oldOffer);
                offerChanges.add(change);
            }
        }

        if (!offerChanges.isEmpty()) {
            changes.put("subscriptionOffers", offerChanges);
        }

        // Save settings
        settings.setMaxSlots(settingsDTO.getMaxSlots());
        settings.setReservedPremiumSlots(settingsDTO.getReservedPremiumSlots());
        settings.setOperatingHours(mapToOperatingHours(settingsDTO.getOperatingHours()));
        settings.setMaintenanceMode(settingsDTO.isMaintenanceMode());
        settings = parkingSettingsRepository.save(settings);
        System.out.println("Paramètres sauvegardés: " + settings.toString());

        // Save subscription plans
        List<Long> updatedPlanIds = new ArrayList<>();
        for (SubscriptionOfferDTO offerDTO : settingsDTO.getSubscriptionOffers()) {
            System.out.println("Traitement de l'offre: " + offerDTO.toString());
            SubscriptionPlan plan = offerDTO.getId() != null ? subscriptionPlanRepository.findById(offerDTO.getId())
                    .orElse(new SubscriptionPlan()) : new SubscriptionPlan();
            plan.setType(offerDTO.getName());
            plan.setMonthlyPrice(offerDTO.getPrice());
            plan.setParkingDurationLimit(offerDTO.getDuration());
            plan.setHasPremiumSpots(offerDTO.getName().equalsIgnoreCase("Premium") || offerDTO.getName().equalsIgnoreCase("Entreprise"));
            plan.setHasValetService(offerDTO.getName().equalsIgnoreCase("Entreprise"));
            plan.setSupportLevel(offerDTO.getName().equalsIgnoreCase("Entreprise") ? "DEDICATED" : "STANDARD");
            plan.setRemainingPlacesPerMonth(offerDTO.getSubscribers());
            subscriptionPlanRepository.save(plan);
            updatedPlanIds.add(plan.getId());
            System.out.println("Offre sauvegardée: " + plan.toString());
        }

        // Delete removed plans
        for (SubscriptionPlan plan : oldPlans) {
            if (!updatedPlanIds.contains(plan.getId())) {
                subscriptionPlanRepository.delete(plan);
                System.out.println("Offre supprimée: " + plan.toString());
            }
        }

        // Log changes to history if any
        if (!changes.isEmpty()) {
            Map<String, Object> historyEntry = new HashMap<>();
            historyEntry.put("timestamp", LocalDateTime.now().toString());
            historyEntry.put("changes", changes);
            settingsHistory.add(historyEntry);
            System.out.println("Entrée d'historique ajoutée: " + historyEntry);
        }

        return mapToParkingSettingsDTO(settings);
    }

    public List<Map<String, Object>> getSettingsHistory() {
        System.out.println("Récupération de l'historique: " + settingsHistory);
        return settingsHistory;
    }

    public String exportSettings() throws Exception {
        System.out.println("Exportation des paramètres...");
        ParkingSettingsDTO settings = getParkingSettings();
        String json = objectMapper.writeValueAsString(settings);
        System.out.println("Paramètres exportés: " + json);
        return json;
    }

    public RevenueEstimateDTO estimateRevenue() {
        RevenueEstimateDTO estimate = new RevenueEstimateDTO();

        LocalDateTime monthStart = LocalDateTime.now().minusDays(30);
        LocalDateTime monthEnd = LocalDateTime.now();

        LocalDate monthStartDate = monthStart.toLocalDate();
        LocalDate monthEndDate = monthEnd.toLocalDate();

        double monthlyReservationRevenue = reservationRepository.findByStartTimeBetween(monthStart, monthEnd).stream()
                .filter(r -> r.getTotalCost() != null)
                .mapToDouble(Reservation::getTotalCost)
                .sum();
        double monthlySubscriptionRevenue = subscriptionRepository.findAll().stream()
                .filter(s -> s.getStartDate() != null && s.getStartDate().isAfter(monthStartDate) && s.getStartDate().isBefore(monthEndDate))
                .filter(s -> s.getPrice() != null)
                .mapToDouble(Subscription::getPrice)
                .sum();
        estimate.setMonthly(monthlyReservationRevenue + monthlySubscriptionRevenue);

        estimate.setAnnual(estimate.getMonthly() * 12);

        return estimate;
    }

    public AdminProfileDTO getAdminProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = authentication.getName();

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrateur non trouvé : " + adminEmail));

        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réservé aux administrateurs");
        }

        AdminProfileDTO dto = new AdminProfileDTO();
        dto.setEmail(admin.getEmail());
        return dto;
    }

    @Transactional
    public AdminProfileDTO updateAdminProfile(AdminProfileDTO adminProfileDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = authentication.getName();

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrateur non trouvé : " + adminEmail));

        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réservé aux administrateurs");
        }

        String newEmail = adminProfileDTO.getEmail();
        if (newEmail != null && !newEmail.equals(admin.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email déjà utilisé : " + newEmail);
            }
            admin.setEmail(newEmail);
        }

        User updatedAdmin = userRepository.save(admin);
        AdminProfileDTO updatedDTO = new AdminProfileDTO();
        updatedDTO.setEmail(updatedAdmin.getEmail());
        return updatedDTO;
    }

    @Transactional
    public Map<String, String> updateAdminPassword(PasswordUpdateDTO passwordUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = authentication.getName();

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrateur non trouvé : " + adminEmail));

        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réservé aux administrateurs");
        }

        if (!passwordEncoder.matches(passwordUpdateDTO.getCurrentPassword(), admin.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mot de passe actuel incorrect.");
        }

        if (passwordUpdateDTO.getNewPassword() == null || passwordUpdateDTO.getNewPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nouveau mot de passe doit contenir au moins 8 caractères.");
        }

        admin.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        userRepository.save(admin);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Mot de passe mis à jour avec succès.");
        return response;
    }

    public UserDTO mapToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getFirstName() + " " + user.getLastName());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRoles().stream()
                .findFirst()
                .map(r -> r.getName().name().replace("ROLE_", ""))
                .orElse("Utilisateur"));
        dto.setActive(user.isActive());
        return dto;
    }

    public ReservationDTO mapToReservationDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUser() != null ? reservation.getUser().getId() : null);
        dto.setSlotId(reservation.getParkingSpot() != null ? reservation.getParkingSpot().getId() : null);
        dto.setStartTime(reservation.getStartTime() != null ? reservation.getStartTime().toString() : null);
        dto.setEndTime(reservation.getEndTime() != null ? reservation.getEndTime().toString() : null);
        dto.setStatus(reservation.getStatus() != null ? reservation.getStatus().name().toLowerCase() : null);
        dto.setCost(reservation.getTotalCost());
        return dto;
    }

    public ParkingSettingsDTO mapToParkingSettingsDTO(ParkingSettings settings) {
        ParkingSettingsDTO dto = new ParkingSettingsDTO();
        dto.setId(settings.getId());
        dto.setMaxSlots(settings.getMaxSlots());
        dto.setReservedPremiumSlots(settings.getReservedPremiumSlots());
        dto.setOperatingHours(mapToOperatingHoursDTO(settings.getOperatingHours()));
        dto.setMaintenanceMode(settings.isMaintenanceMode());
        return dto;
    }

    public SubscriptionOfferDTO mapToSubscriptionOfferDTO(SubscriptionPlan plan) {
        SubscriptionOfferDTO dto = new SubscriptionOfferDTO();
        dto.setId(plan.getId());
        dto.setName(plan.getType());
        dto.setPrice(plan.getMonthlyPrice());
        dto.setDuration(plan.getParkingDurationLimit() != null ? plan.getParkingDurationLimit() : 30);
        dto.setActive(plan.getRemainingPlacesPerMonth() > 0);
        dto.setSubscribers(subscriptionRepository.findBySubscriptionType(plan.getType()).size());
        return dto;
    }

    private OperatingHoursDTO mapToOperatingHoursDTO(OperatingHours hours) {
        if (hours == null) return null;
        OperatingHoursDTO dto = new OperatingHoursDTO();
        dto.setOpen(hours.getOpen());
        dto.setClose(hours.getClose());
        return dto;
    }

    private OperatingHours mapToOperatingHours(OperatingHoursDTO dto) {
        if (dto == null) return null;
        OperatingHours hours = new OperatingHours();
        hours.setOpen(dto.getOpen());
        hours.setClose(dto.getClose());
        return hours;
    }
}