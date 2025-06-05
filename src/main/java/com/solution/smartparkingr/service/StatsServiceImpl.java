package com.solution.smartparkingr.service;

import com.solution.smartparkingr.load.response.UserStatsResponse;
import com.solution.smartparkingr.repository.ParkingSpotRepository;
import com.solution.smartparkingr.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class StatsServiceImpl implements StatsService {

    private static final Logger logger = LoggerFactory.getLogger(StatsServiceImpl.class);

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public UserStatsResponse getUserStats(Long userId) {
        logger.debug("Fetching stats for user ID: {}", userId);
        UserStatsResponse stats = new UserStatsResponse();
        LocalDateTime now = LocalDateTime.now();

        // Available Spots
        long totalSpots = parkingSpotRepository.count();
        long reservedSpots = reservationRepository.countActiveReservations(now);
        int availableSpots = (int) (totalSpots - reservedSpots);
        stats.setAvailableSpots(availableSpots);
        stats.setAvailableSpotsTrend(availableSpots > 0 ? "+" + availableSpots : "0+");

        // Active Reservations
        int activeReservations = (int) reservationRepository.countUserActiveReservations(userId, now);
        stats.setActiveReservations(activeReservations);

        // Next Reservation
        reservationRepository.findNextUserReservation(userId, now)
                .ifPresentOrElse(
                        reservation -> stats.setNextReservation(reservation.getStartTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))),
                        () -> stats.setNextReservation("No upcoming reservations")
                );

        // Monthly Savings and Reservations This Month
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.withDayOfMonth(now.getMonth().length(now.toLocalDate().isLeapYear())).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        long reservationsThisMonth = reservationRepository.countUserReservationsInMonth(userId, startOfMonth, endOfMonth);
        int savings = (int) (reservationsThisMonth * 10); // $10 savings per reservation
        stats.setMonthlySavings("$" + savings);
        stats.setSavingsStatus(reservationsThisMonth > 0 ? "+" + (reservationsThisMonth * 10) + "%" : "0%");
        stats.setReservationsThisMonth((int) reservationsThisMonth);
        stats.setTotalReservations("Reservations This Month");

        logger.debug("Stats retrieved for user ID: {}", userId);
        return stats;
    }
}