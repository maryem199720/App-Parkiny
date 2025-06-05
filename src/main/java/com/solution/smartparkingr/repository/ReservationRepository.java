package com.solution.smartparkingr.repository;

import com.solution.smartparkingr.model.Reservation;
import com.solution.smartparkingr.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByParkingSpot_Id(Long parkingSpotId);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByEndTimeBeforeAndStatus(LocalDateTime now, ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.parkingSpot.id = :parkingSpotId " +
            "AND r.startTime < :endTime AND r.endTime > :startTime")
    List<Reservation> findByParkingSpotIdAndTimeOverlap(
            @Param("parkingSpotId") Long parkingSpotId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r FROM Reservation r WHERE r.startTime BETWEEN :start AND :end AND r.status = 'CONFIRMED'")
    List<Reservation> findUpcomingReservationsByStartTime(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT r FROM Reservation r WHERE r.endTime BETWEEN :start AND :end AND r.status = 'CONFIRMED'")
    List<Reservation> findEndingReservations(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.status = 'ACTIVE' " +
            "AND r.startTime <= :now AND r.endTime >= :now")
    long countActiveReservations(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId " +
            "AND r.status = 'ACTIVE' AND r.startTime <= :now AND r.endTime >= :now")
    long countUserActiveReservations(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId " +
            "AND r.status = 'ACTIVE' AND r.startTime >= :now ORDER BY r.startTime ASC")
    Optional<Reservation> findNextUserReservation(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId " +
            "AND r.startTime BETWEEN :start AND :end")
    long countUserReservationsInMonth(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}