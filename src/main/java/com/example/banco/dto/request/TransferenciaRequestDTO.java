package com.example.banco.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferenciaRequestDTO(
        @NotNull
        Long idCuentaOrigen,
        @NotNull
        Long idCuentaDestino,

        @DecimalMin(value = "0.1")
        BigDecimal monto
) {
}
