package com.example.banco.dto.request;

import java.math.BigDecimal;

public record DepositoRequestDTO(
        Long idCuenta,
        BigDecimal monto
) {
}
