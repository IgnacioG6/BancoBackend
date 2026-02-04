package com.example.banco.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransaccionResponseDTO(
        Long id,
        String tipoTransaccion,
        String estadoTransaccion,
        LocalDateTime fechaHora,
        Long  idCuentaOrigen,
        String nroCuentaOrigen,
        Long idCuentaDestino,
        String nroCuentaDestino,
        BigDecimal monto,
        String descripcion

){
}
