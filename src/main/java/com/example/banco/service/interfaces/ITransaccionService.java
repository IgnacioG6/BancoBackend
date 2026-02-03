package com.example.banco.service.interfaces;

import com.example.banco.dto.request.DepositoRequestDTO;
import com.example.banco.dto.request.RetiroRequestDTO;
import com.example.banco.dto.request.TransferenciaRequestDTO;
import com.example.banco.dto.response.TransaccionResponseDTO;
import com.example.banco.model.enums.EstadoTransaccion;

import java.util.List;

public interface ITransaccionService {
    TransaccionResponseDTO depositar(DepositoRequestDTO depositoRequestDTO);
    TransaccionResponseDTO retirar(RetiroRequestDTO retiroRequestDTO);
    TransaccionResponseDTO transferir(TransferenciaRequestDTO transferenciaRequestDTO);
    TransaccionResponseDTO buscarPorId(Long id);
    List<TransaccionResponseDTO> listarTransacciones();
    List<TransaccionResponseDTO> listarTransaccionesPorCuenta(Long idCuenta);
    List<TransaccionResponseDTO> listarTransaccionesPorEstado(EstadoTransaccion estado);

}
