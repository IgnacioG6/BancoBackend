package com.example.banco.controller;

import com.example.banco.dto.request.ClienteRequestDTO;
import com.example.banco.dto.response.ClienteResponseDTO;
import com.example.banco.model.enums.EstadoCliente;
import com.example.banco.service.interfaces.IClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banco/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final IClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crearCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crearCliente(clienteRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        return  ResponseEntity.status(HttpStatus.OK).body(clienteService.listarClientes());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarClienteId(@Valid @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(clienteService.buscarPorId(id));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<ClienteResponseDTO> buscarClientePorDni(@Valid @PathVariable String dni) {
        return ResponseEntity.status(HttpStatus.OK).body(clienteService.buscarPorDni(dni));
    }

    @PutMapping("/estado/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizarEstado(@Valid @PathVariable Long id,  @Valid @RequestBody EstadoCliente estado) {
        return ResponseEntity.status(HttpStatus.OK).body(clienteService.cambiarEstado(estado,id));
    }


}
