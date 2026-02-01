package com.example.banco.dto.response;

public record ClienteResponseDTO (
        Long id,
        String nombre,
        String dni,
        String email,
        String telefono,
        String estado,
        int CantidadCuentas
){
}
