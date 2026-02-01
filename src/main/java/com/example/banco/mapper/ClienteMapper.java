package com.example.banco.mapper;

import com.example.banco.dto.request.ClienteRequestDTO;
import com.example.banco.dto.response.ClienteResponseDTO;
import com.example.banco.model.Cliente;
import com.example.banco.model.enums.EstadoCliente;


public class ClienteMapper {

    public static ClienteResponseDTO toResponseDto(Cliente cliente) {
        if (cliente == null) {return null;}

        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getDni(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getEstado().toString(),
                cliente.getCuentas().size()
        );

    }

    public static Cliente toEntity(ClienteRequestDTO clienteRequestDTO) {
        if (clienteRequestDTO == null) {return null;}

        Cliente cliente = new Cliente();
        cliente.setNombre(clienteRequestDTO.nombre());
        cliente.setDni(clienteRequestDTO.dni());
        cliente.setEmail(clienteRequestDTO.email());
        cliente.setTelefono(clienteRequestDTO.telefono());
        cliente.setEstado(EstadoCliente.ACTIVO);

        return cliente;
    }



}
