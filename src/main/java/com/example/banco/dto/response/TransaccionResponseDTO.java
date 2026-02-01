package com.example.banco.dto.response;

import java.time.LocalDateTime;

public record TransaccionResponseDTO(
        Long id,
        String tipoTransaccion,
        String estadoTransaccion,
        LocalDateTime fechaHora,
        String idCuentaOrigen,
        String nroCuentaOrigen,
        String idCuentaDestino,
        String nroCuentaDestino,
        String descripcion

){
}
