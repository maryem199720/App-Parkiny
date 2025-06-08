package com.solution.smartparkingr.admin.dto;

import lombok.Data;

@Data
public class DailyReservationDTO {
    private String date;
    private Long count;
}