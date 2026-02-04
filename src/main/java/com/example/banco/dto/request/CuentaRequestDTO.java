package com.example.banco.dto.request;

import com.example.banco.model.enums.TipoCuenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CuentaRequestDTO(
        @NotNull(message = "El ID del cliente es obligatorio")
        Long idCliente,

        @NotNull(message = "El tipo de cuenta es obligatorio")
        TipoCuenta tipoCuenta,

        @NotNull(message = "El depósito inicial es obligatorio")
        @DecimalMin(value = "1000.0", message = "El depósito inicial mínimo es de $1000")
        BigDecimal depositoInicial
) {}
