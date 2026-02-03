package com.example.banco.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RetiroRequestDTO(
        @NotNull
        Long idCuenta,

        @DecimalMin(value = "0.1", message = "El monto debe ser mayor a 0")
        BigDecimal monto
) {
}
