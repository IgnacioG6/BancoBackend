package com.example.banco.mapper;

import com.example.banco.dto.request.CuentaRequestDTO;
import com.example.banco.dto.response.CuentaResponseDTO;
import com.example.banco.model.Cliente;
import com.example.banco.model.Cuenta;
import com.example.banco.model.enums.EstadoCuenta;
import com.example.banco.model.enums.TipoCuenta;

import java.math.BigDecimal;

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
                cuenta.getSaldo()
        );

    }

    public static Cuenta toEntity(CuentaRequestDTO cuentaRequestDTO, Cliente cliente) {
        if (cuentaRequestDTO == null) {return null;}
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoCuenta(cuentaRequestDTO.tipoCuenta());
        cuenta.setCliente(cliente);
        cuenta.setEstadoCuenta(EstadoCuenta.ACTIVA);

        if (cuenta.getTipoCuenta() == TipoCuenta.CORRIENTE) {
            cuenta.setLimiteRetiroDiario(new BigDecimal("50000"));
            cuenta.setSaldoMinimo(new BigDecimal("500"));
            cuenta.setMontoMinimoApertura(new BigDecimal("1000"));
            cuenta.setLimiteTransferencia(new BigDecimal("100000"));
        } else {
            cuenta.setLimiteRetiroDiario(new BigDecimal("30000"));
            cuenta.setSaldoMinimo(new BigDecimal("1000"));
            cuenta.setMontoMinimoApertura(new BigDecimal("2000"));
            cuenta.setLimiteTransferencia(new BigDecimal("50000"));
        }


        return cuenta;
    }

}
