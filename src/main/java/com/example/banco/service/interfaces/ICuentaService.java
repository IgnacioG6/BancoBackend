package com.example.banco.service.interfaces;

import com.example.banco.dto.request.CuentaRequestDTO;
import com.example.banco.dto.response.CuentaResponseDTO;
import com.example.banco.model.enums.EstadoCuenta;

import java.util.List;

public interface ICuentaService {
    CuentaResponseDTO crearCuenta(CuentaRequestDTO cuentaDto);
    CuentaResponseDTO buscarPorId(Long id);
    CuentaResponseDTO buscarPorNroCuenta(String nroCuenta);
    List<CuentaResponseDTO> buscarPorCliente(Long id);
    List<CuentaResponseDTO> listarCuentas();
    CuentaResponseDTO cambiarEstado(EstadoCuenta estado, Long id);
}
