package com.example.banco.service;

import com.example.banco.dto.request.ClienteRequestDTO;
import com.example.banco.dto.response.ClienteResponseDTO;
import com.example.banco.exception.EntidadNoEncontradaException;
import com.example.banco.mapper.ClienteMapper;
import com.example.banco.model.Cliente;
import com.example.banco.model.enums.EstadoCliente;
import com.example.banco.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteResponseDTO crearCliente(ClienteRequestDTO clienteDto) {
        Cliente cliente = ClienteMapper.toEntity(clienteDto);
        clienteRepository.save(cliente);
        return ClienteMapper.toResponseDto(cliente);
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no encontrado con id: " + id));

        return ClienteMapper.toResponseDto(cliente);
    }

    public ClienteResponseDTO buscarPorDni(String dni) {
        Cliente cliente = clienteRepository.findByDni(dni)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no encontrado con dni: " + dni));

        return ClienteMapper.toResponseDto(cliente);
    }

    public List<ClienteResponseDTO> listarClientes() {
        return clienteRepository.findAll().stream()
                .map(ClienteMapper::toResponseDto)
                .toList();
    }

    public ClienteResponseDTO cambiarEstado(EstadoCliente estadoCliente, Long id){
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no encontrado con id: " + id));

        cliente.setEstado(estadoCliente);
        clienteRepository.save(cliente);

        return  ClienteMapper.toResponseDto(cliente);
    }

}
