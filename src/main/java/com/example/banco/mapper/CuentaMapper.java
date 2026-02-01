package com.example.banco.mapper;

import com.example.banco.dto.request.CuentaRequestDTO;
import com.example.banco.dto.response.CuentaResponseDTO;
import com.example.banco.model.Cliente;
import com.example.banco.model.Cuenta;
import com.example.banco.model.enums.EstadoCuenta;

public class CuentaMapper {

    public static CuentaResponseDTO toResponseDto(Cuenta cuenta) {
        if (cuenta == null) {return null;}

        return new CuentaResponseDTO(
                cuenta.getId(),
                cuenta.getNroCuenta(),
                cuenta.getTipoCuenta().toString(),
                cuenta.getEstadoCuenta().toString(),
                cuenta.getCliente().getId(),
                cuenta.getCliente().getNombre(),
                cuenta.getSaldo(),
                cuenta.getTransacciones().size()
        );

    }

    public static Cuenta toEntity(CuentaRequestDTO cuentaRequestDTO, Cliente cliente) {
        if (cuentaRequestDTO == null) {return null;}
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoCuenta(cuentaRequestDTO.tipoCuenta());
        cuenta.setCliente(cliente);
        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVA);

        return cuenta;
    }

}
