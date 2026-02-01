package com.example.banco.dto.request;

import java.math.BigDecimal;

public record RetiroRequestDTO(
        Long idCuenta,
        BigDecimal monto
) {
}
