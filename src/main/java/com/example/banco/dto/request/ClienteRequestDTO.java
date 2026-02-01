package com.example.banco.dto.request;

public record ClienteRequestDTO(
        String nombre,
        String dni,
        String email,
        String telefono
) {
}
