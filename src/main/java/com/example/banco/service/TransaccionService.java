package com.example.banco.service;

import com.example.banco.dto.request.DepositoRequestDTO;
import com.example.banco.dto.request.RetiroRequestDTO;
import com.example.banco.dto.request.TransferenciaRequestDTO;
import com.example.banco.dto.response.TransaccionResponseDTO;
import com.example.banco.exception.EntidadNoEncontradaException;
import com.example.banco.exception.ValidacionException;
import com.example.banco.mapper.TransaccionMapper;
import com.example.banco.model.Cuenta;
import com.example.banco.model.Transaccion;
import com.example.banco.model.enums.EstadoCuenta;
import com.example.banco.model.enums.EstadoTransaccion;
import com.example.banco.model.enums.TipoTransaccion;
import com.example.banco.repository.CuentaRepository;
import com.example.banco.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransaccionService {
    private final TransaccionRepository transaccionRepository;
    private final CuentaRepository cuentaRepository;

    public TransaccionResponseDTO depositar(DepositoRequestDTO depositoDto) {
        Cuenta cuenta = cuentaRepository.findById(depositoDto.idCuenta())
                .orElseThrow(() -> new EntidadNoEncontradaException("Cuenta no encontrada con id: " + depositoDto.idCuenta()));

        validarEstadoCuenta(cuenta);
        Transaccion transaccion = crearTransaccionDeposito(depositoDto, cuenta);
        actualizarSaldoDeposito(depositoDto.monto(), cuenta);
        transaccion.setEstadoTransaccion(EstadoTransaccion.COMPLETADA);


        transaccionRepository.save(transaccion);
        cuentaRepository.save(cuenta);

        return TransaccionMapper.toResponseDto(transaccion);

    }

    private Transaccion crearTransaccionDeposito(DepositoRequestDTO depositoDto, Cuenta cuenta) {
        Transaccion transaccion = new Transaccion();
        transaccion.setTipoTransaccion(TipoTransaccion.DEPOSITO);
        transaccion.setEstadoTransaccion(EstadoTransaccion.PENDIENTE);
        transaccion.setCuentaDestino(cuenta);
        transaccion.setCuentaOrigen(null);
        transaccion.setMonto(depositoDto.monto());
        transaccion.setFechaHora(LocalDateTime.now());

        return transaccion;
    }


    private void actualizarSaldoDeposito(BigDecimal monto, Cuenta cuenta) {
        cuenta.setSaldo(cuenta.getSaldo().add(monto));
    }

    private void validarEstadoCuenta(Cuenta cuenta) {
        if (!cuenta.getEstadoCuenta().equals(EstadoCuenta.ACTIVA)) {
            throw new ValidacionException("No se puede depositar a una cuenta inactiva");
        }
    }


    public TransaccionResponseDTO retirar(RetiroRequestDTO retiroDto) {
        Cuenta cuenta = cuentaRepository.findById(retiroDto.idCuenta())
                .orElseThrow(() -> new EntidadNoEncontradaException("Cuenta no encontrada con id: " + retiroDto.idCuenta()));

        validarEstadoCuenta(cuenta);
        Transaccion transaccion = crearTransaccionRetiro(retiroDto, cuenta);
        actualizarSaldoRetiro(retiroDto.monto(), cuenta);
        transaccion.setEstadoTransaccion(EstadoTransaccion.COMPLETADA);

        transaccionRepository.save(transaccion);
        cuentaRepository.save(cuenta);

        return TransaccionMapper.toResponseDto(transaccion);
    }

    private Transaccion crearTransaccionRetiro(RetiroRequestDTO retiroDto, Cuenta cuenta) {
        Transaccion transaccion = new Transaccion();
        transaccion.setTipoTransaccion(TipoTransaccion.RETIRO);
        transaccion.setEstadoTransaccion(EstadoTransaccion.PENDIENTE);
        transaccion.setCuentaDestino(null);
        transaccion.setCuentaOrigen(cuenta);
        transaccion.setMonto(retiroDto.monto());
        transaccion.setFechaHora(LocalDateTime.now());

        return transaccion;
    }

    private void actualizarSaldoRetiro(BigDecimal monto, Cuenta cuenta) {
        validarSaldo(monto, cuenta);
        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
    }

    private void validarSaldo(BigDecimal monto, Cuenta cuenta) {
        if (monto.compareTo(cuenta.getSaldo()) > 0) {
            throw new ValidacionException("No tienes saldo suficiente para esta operación");
        }
    }


    public TransaccionResponseDTO transferir(TransferenciaRequestDTO transferenciaDto) {
        Cuenta cuentaDestino = cuentaRepository.findById(transferenciaDto.idCuentaDestino())
                .orElseThrow(() -> new EntidadNoEncontradaException("Cuenta no encontrada con id: " + transferenciaDto.idCuentaDestino()));

        Cuenta cuentaOrigen = cuentaRepository.findById(transferenciaDto.idCuentaOrigen())
                .orElseThrow(() -> new EntidadNoEncontradaException("Cuenta no encontrada con id: " + transferenciaDto.idCuentaOrigen()));

        validarTransferencia(transferenciaDto.monto(), cuentaDestino, cuentaOrigen);
        Transaccion transaccion = crearTransaccionTransferencia(transferenciaDto, cuentaDestino, cuentaOrigen);
        transaccion.setEstadoTransaccion(EstadoTransaccion.COMPLETADA);
        actualizarSaldoRetiro(transferenciaDto.monto(), cuentaOrigen);
        actualizarSaldoDeposito(transferenciaDto.monto(), cuentaDestino);

        transaccionRepository.save(transaccion);
        cuentaRepository.save(cuentaDestino);
        cuentaRepository.save(cuentaOrigen);

        return TransaccionMapper.toResponseDto(transaccion);



    }


    public void validarTransferencia(BigDecimal monto, Cuenta cuentaDestino, Cuenta cuentaOrigen) {

        if (cuentaDestino.equals(cuentaOrigen)) {
            throw new ValidacionException("No se puede transferir a uno mismo");

        }

        if (!cuentaDestino.getEstadoCuenta().equals(EstadoCuenta.ACTIVA) || !cuentaOrigen.getEstadoCuenta().equals(EstadoCuenta.ACTIVA)) {
            throw new ValidacionException("Cuentas inactivas");
        }

        validarSaldo(monto, cuentaOrigen);
    }

    public Transaccion crearTransaccionTransferencia(TransferenciaRequestDTO transferenciaDto,
                                                     Cuenta cuentaDestino,Cuenta cuentaOrigen)
    {
        Transaccion transaccion = new Transaccion();
        transaccion.setTipoTransaccion(TipoTransaccion.TRANSFERENCIA);
        transaccion.setEstadoTransaccion(EstadoTransaccion.PENDIENTE);
        transaccion.setCuentaDestino(cuentaDestino);
        transaccion.setCuentaOrigen(cuentaOrigen);
        transaccion.setMonto(transferenciaDto.monto());
        transaccion.setFechaHora(LocalDateTime.now());

        return transaccion;

    }
}