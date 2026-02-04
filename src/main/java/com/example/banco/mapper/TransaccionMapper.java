package com.example.banco.mapper;

import com.example.banco.dto.response.TransaccionResponseDTO;
import com.example.banco.model.Transaccion;

public class TransaccionMapper {

    public static TransaccionResponseDTO toResponseDto(Transaccion transaccion) {
        if (transaccion == null) {return null;}


        return new TransaccionResponseDTO(
                    transaccion.getId(),
                    transaccion.getTipoTransaccion().toString(),
                    transaccion.getEstado().toString(),
                    transaccion.getFechaHora(),
                    transaccion.getCuentaOrigen() != null ? transaccion.getCuentaOrigen().getId() : null,
                    transaccion.getCuentaOrigen() != null ? transaccion.getCuentaOrigen().getNroCuenta(): null,
                    transaccion.getCuentaDestino() != null ? transaccion.getCuentaDestino().getId() : null,
                    transaccion.getCuentaDestino()  != null ? transaccion.getCuentaDestino().getNroCuenta(): null,
                    transaccion.getMonto(),
                    transaccion.getDescripcion()
        );

    }

}
