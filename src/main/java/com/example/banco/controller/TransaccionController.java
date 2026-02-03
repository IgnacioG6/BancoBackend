package com.example.banco.controller;

import com.example.banco.dto.request.DepositoRequestDTO;
import com.example.banco.dto.request.RetiroRequestDTO;
import com.example.banco.dto.request.TransferenciaRequestDTO;
import com.example.banco.dto.response.TransaccionResponseDTO;
import com.example.banco.model.enums.EstadoTransaccion;
import com.example.banco.service.interfaces.ITransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("banco/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final ITransaccionService transaccionService;

    @PostMapping("/deposito")
    public ResponseEntity<TransaccionResponseDTO> depositar(@RequestBody DepositoRequestDTO depositoRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(transaccionService.depositar(depositoRequestDTO));
    }

    @PostMapping("/retiro")
    public ResponseEntity<TransaccionResponseDTO> retirar(@RequestBody RetiroRequestDTO retiroRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(transaccionService.retirar(retiroRequestDTO));
    }

    @PostMapping("/transferencia")
    public ResponseEntity<TransaccionResponseDTO> transferir(@RequestBody TransferenciaRequestDTO transferenciaRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(transaccionService.transferir(transferenciaRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<TransaccionResponseDTO>> listarTransacciones(){
        return ResponseEntity.status(HttpStatus.OK).body(transaccionService.listarTransacciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionResponseDTO> buscarClientePorId(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(transaccionService.buscarPorId(id));
    }

    @GetMapping("/cuenta/{id}")
    public ResponseEntity<List<TransaccionResponseDTO>> buscarTransaccionPorCuenta(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(transaccionService.listarTransaccionesPorCuenta(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TransaccionResponseDTO>> buscarTransaccionPorEstado(@PathVariable EstadoTransaccion estado){
        return ResponseEntity.status(HttpStatus.OK).body(transaccionService.listarTransaccionesPorEstado(estado));
    }

}
