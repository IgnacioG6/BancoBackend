package com.example.banco.dto.response;

import java.math.BigDecimal;

public record CuentaResponseDTO (
        Long id,
        String nroCuenta,
        String tipoCuenta,
        String estadoCuenta,
        Long idCliente,
        String nombreCliente,
        BigDecimal saldo,
        int CantidadTransacciones
){
}

