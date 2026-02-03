package com.example.banco.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "^[0-9]{7,8}$", message = "El DNI debe tener entre 7 y 8 números")
        String dni,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @Size(min = 1, max = 20, message = "El teléfono debe tener entre 1 y 20 caracteres")
        String telefono
) {
}
