package com.example.banco.service.interfaces;

import com.example.banco.dto.request.ClienteRequestDTO;
import com.example.banco.dto.response.ClienteResponseDTO;
import com.example.banco.model.enums.EstadoCliente;

import java.util.List;

public interface IClienteService {
    ClienteResponseDTO crearCliente(ClienteRequestDTO clienteDto);
    ClienteResponseDTO buscarPorId(Long id);
    ClienteResponseDTO buscarPorDni(String dni);
    List<ClienteResponseDTO> listarClientes();
    ClienteResponseDTO cambiarEstado(EstadoCliente estadoCliente, Long id);
}
