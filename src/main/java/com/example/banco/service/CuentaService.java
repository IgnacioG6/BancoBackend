package com.example.banco.service;

import com.example.banco.dto.request.CuentaRequestDTO;
import com.example.banco.dto.response.CuentaResponseDTO;
import com.example.banco.exception.EntidadNoEncontradaException;
import com.example.banco.exception.EstadoInvalidoException;
import com.example.banco.mapper.CuentaMapper;
import com.example.banco.model.Cliente;
import com.example.banco.model.Cuenta;
import com.example.banco.model.enums.EstadoCuenta;
import com.example.banco.repository.ClienteRepository;
import com.example.banco.repository.CuentaRepository;
import com.example.banco.service.interfaces.ICuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaService implements ICuentaService {
    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    public CuentaResponseDTO crearCuenta(CuentaRequestDTO cuentaDto) {
        Cliente cliente = clienteRepository.findById(cuentaDto.idCliente())
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no encontrado con id: " + cuentaDto.idCliente()));

        Cuenta cuenta = CuentaMapper.toEntity(cuentaDto, cliente);
        cuentaRepository.save(cuenta);
        cuenta.setNroCuenta(String.format("CTA-%05d", cuenta.getId()));


        cuentaRepository.save(cuenta);
        return CuentaMapper.toResponseDto(cuenta);
    }

    public CuentaResponseDTO buscarPorId(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cuenta no encontrada con id: " + id));

        return CuentaMapper.toResponseDto(cuenta);
    }

    public CuentaResponseDTO buscarPorNroCuenta(String nroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNroCuenta(nroCuenta)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cuenta no encontrada con numero de cuenta: " + nroCuenta));

        return CuentaMapper.toResponseDto(cuenta);
    }

    public List<CuentaResponseDTO> buscarPorCliente(Long id){
        return cuentaRepository.findByIdCliente(id).stream().map(CuentaMapper::toResponseDto).toList();
    }

    public List<CuentaResponseDTO> listarCuentas(){
        return cuentaRepository.findAll().stream().map(CuentaMapper::toResponseDto).toList();
    }


    public CuentaResponseDTO cambiarEstado(EstadoCuenta estado, Long id){
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cuenta no encontrada con id: " + id));

        validarTransicionEstado(cuenta.getEstadoCuenta(),estado);
        cuenta.setEstadoCuenta(estado);
        cuentaRepository.save(cuenta);

        return CuentaMapper.toResponseDto(cuenta);
    }


    private void validarTransicionEstado(EstadoCuenta actual, EstadoCuenta nuevo) {
        if (actual == EstadoCuenta.CERRADA) {
            throw new EstadoInvalidoException("La cuenta está CERRADA y no admite más cambios.");
        }

        if (actual == nuevo) {
            return;
        }

        boolean esValida = switch (actual) {
            case ACTIVA -> (nuevo == EstadoCuenta.BLOQUEADA || nuevo == EstadoCuenta.CERRADA);
            case BLOQUEADA -> (nuevo == EstadoCuenta.ACTIVA);
            default -> false;
        };

        if (!esValida) {
            throw new EstadoInvalidoException(
                    String.format("Transición de estado no permitida: %s -> %s", actual, nuevo)
            );
        }
    }
}