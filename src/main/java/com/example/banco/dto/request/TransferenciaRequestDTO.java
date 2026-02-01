package com.example.banco.dto.request;

import java.math.BigDecimal;

public record TransferenciaRequestDTO(
        Long idCuentaOrigen,
        Long idCuentaDestino,
        BigDecimal monto
) {
}
