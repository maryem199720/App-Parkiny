package com.solution.smartparkingr.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class OperatingHours {
    private String open;
    private String close;
}