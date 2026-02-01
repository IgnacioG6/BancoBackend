package com.example.banco.dto.request;

import com.example.banco.model.enums.TipoCuenta;

public record CuentaRequestDTO(
        Long idCliente,
        TipoCuenta tipoCuenta
) {
}
