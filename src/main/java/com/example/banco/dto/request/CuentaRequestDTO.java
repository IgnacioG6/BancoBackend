package com.example.banco.dto.request;

import com.example.banco.model.enums.TipoCuenta;
import jakarta.validation.constraints.NotNull;

public record CuentaRequestDTO(
        @NotNull
        Long idCliente,
        @NotNull
        TipoCuenta tipoCuenta
) {
}
