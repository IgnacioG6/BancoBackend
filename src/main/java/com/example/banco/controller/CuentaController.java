package com.example.banco.controller;

import com.example.banco.dto.request.CuentaRequestDTO;
import com.example.banco.dto.response.CuentaResponseDTO;
import com.example.banco.model.enums.EstadoCuenta;
import com.example.banco.service.interfaces.ICuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banco/cuentas")
@RequiredArgsConstructor
public class CuentaController {
    private final ICuentaService cuentaService;

    @PostMapping
    public ResponseEntity<CuentaResponseDTO> crearCuenta(@Valid @RequestBody CuentaRequestDTO cuentaRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.crearCuenta(cuentaRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponseDTO>> listarCuentas() {
        return ResponseEntity.status(HttpStatus.OK).body(cuentaService.listarCuentas());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<CuentaResponseDTO> buscarCuentaPorId(@Valid @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(cuentaService.buscarPorId(id));
    }

    @GetMapping("/nro-cuenta/{nroCuenta}")
    public ResponseEntity<CuentaResponseDTO> buscarCuentaPorNroCuenta(@Valid @PathVariable String nroCuenta) {
        return ResponseEntity.status(HttpStatus.OK).body(cuentaService.buscarPorNroCuenta(nroCuenta));
    }

    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<CuentaResponseDTO>> buscarCuentaPorCliente(@Valid @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(cuentaService.buscarPorCliente(id));
    }

    @PutMapping("/estado/{id}")
    public ResponseEntity<CuentaResponseDTO> actualizarEstado(@Valid @PathVariable Long id,@Valid @RequestBody EstadoCuenta estado) {
        return ResponseEntity.status(HttpStatus.OK).body(cuentaService.cambiarEstado(estado,id));
    }
}
