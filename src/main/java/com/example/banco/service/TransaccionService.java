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
import com.example.banco.service.interfaces.ITransaccionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransaccionService implements ITransaccionService {
    private final TransaccionRepository transaccionRepository;
    private final CuentaRepository cuentaRepository;

    @Transactional
    public TransaccionResponseDTO depositar(DepositoRequestDTO depositoDto) {
        Cuenta cuenta = cuentaRepository.findById(depositoDto.idCuenta())
                .orElseThrow(() -> new EntidadNoEncontradaException("Cuenta no encontrada con id: " + depositoDto.idCuenta()));

        validarEstadoCuenta(cuenta);
        Transaccion transaccion = crearTransaccionDeposito(depositoDto, cuenta);
        actualizarSaldoDeposito(depositoDto.monto(), cuenta);
        transaccion.setEstado(EstadoTransaccion.COMPLETADA);


        transaccionRepository.save(transaccion);
        cuentaRepository.save(cuenta);

        return TransaccionMapper.toResponseDto(transaccion);

    }

    private Transaccion crearTransaccionDeposito(DepositoRequestDTO depositoDto, Cuenta cuenta) {
        Transaccion transaccion = new Transaccion();
        transaccion.setTipoTransaccion(TipoTransaccion.DEPOSITO);
        transaccion.setEstado(EstadoTransaccion.PENDIENTE);
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

    @Transactional
    public TransaccionResponseDTO retirar(RetiroRequestDTO retiroDto) {
        Cuenta cuenta = cuentaRepository.findById(retiroDto.idCuenta())
                .orElseThrow(() -> new EntidadNoEncontradaException("Cuenta no encontrada con id: " + retiroDto.idCuenta()));

        validarEstadoCuenta(cuenta);
        validarSaldoRetiro(retiroDto.monto(), cuenta);
        validarLimiteRetiroDiario(cuenta, retiroDto.monto());
        Transaccion transaccion = crearTransaccionRetiro(retiroDto, cuenta);
        actualizarSaldoRetiro(retiroDto.monto(), cuenta);
        transaccion.setEstado(EstadoTransaccion.COMPLETADA);

        transaccionRepository.save(transaccion);
        cuentaRepository.save(cuenta);

        return TransaccionMapper.toResponseDto(transaccion);
    }

    private void validarSaldoRetiro(BigDecimal monto, Cuenta cuenta) {
        if (monto.compareTo(cuenta.getSaldo()) > 0) {
            throw new ValidacionException("No tienes saldo suficiente para esta operación");
        }

        BigDecimal saldoDespuesDeRetiro = cuenta.getSaldo().subtract(monto);
        if (saldoDespuesDeRetiro.compareTo(cuenta.getSaldoMinimo()) < 0) {
            throw new ValidacionException("El retiro dejaría la cuenta por debajo del saldo mínimo de $" + cuenta.getSaldoMinimo());
        }
    }

    private void validarLimiteRetiroDiario(Cuenta cuenta, BigDecimal monto) {
        LocalDateTime inicioDelDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDelDia = LocalDate.now().atTime(23, 59, 59);
        List<Transaccion> transaccionesDelDia = listarTransaccionesPorFechaHora(cuenta.getId(), inicioDelDia, finDelDia);

        List<Transaccion> retirosDeHoy = transaccionesDelDia.stream()
                .filter(transaccion -> transaccion.getTipoTransaccion().equals(TipoTransaccion.RETIRO))
                .toList();

        BigDecimal montoDiario = retirosDeHoy.stream()
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalConNuevoRetiro = montoDiario.add(monto);
        if (totalConNuevoRetiro.compareTo(cuenta.getLimiteRetiroDiario()) > 0) {
            throw new ValidacionException("El retiro excede el límite diario de $" + cuenta.getLimiteRetiroDiario() +
                    ". Ya retiraste $" + montoDiario + " hoy.");
        }

    }


    private Transaccion crearTransaccionRetiro(RetiroRequestDTO retiroDto, Cuenta cuenta) {
        Transaccion transaccion = new Transaccion();
        transaccion.setTipoTransaccion(TipoTransaccion.RETIRO);
        transaccion.setEstado(EstadoTransaccion.PENDIENTE);
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

    @Transactional
    public TransaccionResponseDTO transferir(TransferenciaRequestDTO transferenciaDto) {
        Cuenta cuentaDestino = cuentaRepository.findById(transferenciaDto.idCuentaDestino())
                .orElseThrow(() -> new EntidadNoEncontradaException("Cuenta no encontrada con id: " + transferenciaDto.idCuentaDestino()));

        Cuenta cuentaOrigen = cuentaRepository.findById(transferenciaDto.idCuentaOrigen())
                .orElseThrow(() -> new EntidadNoEncontradaException("Cuenta no encontrada con id: " + transferenciaDto.idCuentaOrigen()));

        validarTransferencia(transferenciaDto.monto(), cuentaDestino, cuentaOrigen);
        Transaccion transaccion = crearTransaccionTransferencia(transferenciaDto, cuentaDestino, cuentaOrigen);
        transaccion.setEstado(EstadoTransaccion.COMPLETADA);
        actualizarSaldoRetiro(transferenciaDto.monto(), cuentaOrigen);
        actualizarSaldoDeposito(transferenciaDto.monto(), cuentaDestino);

        transaccionRepository.save(transaccion);
        cuentaRepository.save(cuentaDestino);
        cuentaRepository.save(cuentaOrigen);

        return TransaccionMapper.toResponseDto(transaccion);



    }


    private void validarTransferencia(BigDecimal monto, Cuenta cuentaDestino, Cuenta cuentaOrigen) {

        if (monto.compareTo(cuentaOrigen.getLimiteTransferencia()) > 0) {
            throw new ValidacionException("El monto excede el límite de transferencia de $" + cuentaOrigen.getLimiteTransferencia());
        }

        if (cuentaDestino.equals(cuentaOrigen)) {
            throw new ValidacionException("No se puede transferir a uno mismo");

        }

        if (!cuentaDestino.getEstadoCuenta().equals(EstadoCuenta.ACTIVA) || !cuentaOrigen.getEstadoCuenta().equals(EstadoCuenta.ACTIVA)) {
            throw new ValidacionException("Cuentas inactivas");
        }

        validarSaldoRetiro(monto, cuentaOrigen);
        validarLimiteRetiroDiario(cuentaOrigen, monto);
    }


    private Transaccion crearTransaccionTransferencia(TransferenciaRequestDTO transferenciaDto,
                                                     Cuenta cuentaDestino,Cuenta cuentaOrigen)
    {
        Transaccion transaccion = new Transaccion();
        transaccion.setTipoTransaccion(TipoTransaccion.TRANSFERENCIA);
        transaccion.setEstado(EstadoTransaccion.PENDIENTE);
        transaccion.setCuentaDestino(cuentaDestino);
        transaccion.setCuentaOrigen(cuentaOrigen);
        transaccion.setMonto(transferenciaDto.monto());
        transaccion.setFechaHora(LocalDateTime.now());

        return transaccion;

    }

    public TransaccionResponseDTO buscarPorId(Long id){
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(()-> new EntidadNoEncontradaException("Transaccion no encontrada con id: " + id));

        return  TransaccionMapper.toResponseDto(transaccion);
    }

    public List<TransaccionResponseDTO> listarTransacciones(){
        return transaccionRepository.findAll().stream()
                .map(TransaccionMapper::toResponseDto)
                .toList();
    }

    public List<TransaccionResponseDTO> listarTransaccionesPorCuenta(Long idCuenta){
        return transaccionRepository.findByCuentaId(idCuenta).stream()
                .map(TransaccionMapper::toResponseDto)
                .toList();
    }

    public List<TransaccionResponseDTO> listarTransaccionesPorEstado(EstadoTransaccion estado){
        return transaccionRepository.findByEstado(estado).stream()
                .map(TransaccionMapper::toResponseDto)
                .toList();
    }

    protected List<Transaccion> listarTransaccionesPorFechaHora(Long cuentaId,LocalDateTime fechaInicio, LocalDateTime fechaFin){
        return transaccionRepository.findByCuentaOrigenIdAndFechaHoraBetween(cuentaId,fechaInicio,fechaFin).stream()
                .toList();
    }

}