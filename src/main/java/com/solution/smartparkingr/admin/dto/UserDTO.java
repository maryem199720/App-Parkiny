package com.solution.smartparkingr.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String firstName;
    private String lastName;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le téléphone est requis")
    @Size(min = 8, max = 15, message = "Le téléphone doit contenir entre 8 et 15 chiffres")
    private String phone;

    @Size(min = 6, max = 120, message = "Le mot de passe doit contenir entre 6 et 120 caractères")
    private String password;

    private String role;
    private boolean active;
    private List<ReservationDTO> reservations;
}