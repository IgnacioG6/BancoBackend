package com.example.banco.service;

import com.example.banco.dto.request.DepositoRequestDTO;
import com.example.banco.dto.request.RetiroRequestDTO;
import com.example.banco.dto.request.TransferenciaRequestDTO;
import com.example.banco.dto.response.TransaccionResponseDTO;
import com.example.banco.exception.EntidadNoEncontradaException;
import com.example.banco.exception.ValidacionException;
import com.example.banco.model.Cuenta;
import com.example.banco.model.Transaccion;
import com.example.banco.model.enums.EstadoCuenta;
import com.example.banco.model.enums.TipoCuenta;
import com.example.banco.model.enums.TipoTransaccion;
import com.example.banco.repository.CuentaRepository;
import com.example.banco.repository.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private TransaccionService transaccionService;

    private Cuenta cuentaOrigen;
    private Cuenta cuentaDestino;
    private BigDecimal saldoInicial;

    @BeforeEach
    void setUp() {
        saldoInicial = new BigDecimal("10000");

        cuentaOrigen = new Cuenta();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setNroCuenta("CTA-00001");
        cuentaOrigen.setSaldo(saldoInicial);
        cuentaOrigen.setEstadoCuenta(EstadoCuenta.ACTIVA);
        cuentaOrigen.setTipoCuenta(TipoCuenta.CORRIENTE);
        cuentaOrigen.setLimiteRetiroDiario(new BigDecimal("50000"));
        cuentaOrigen.setSaldoMinimo(new BigDecimal("500"));
        cuentaOrigen.setLimiteTransferencia(new BigDecimal("100000"));

        cuentaDestino = new Cuenta();
        cuentaDestino.setId(2L);
        cuentaDestino.setNroCuenta("CTA-00002");
        cuentaDestino.setSaldo(new BigDecimal("5000"));
        cuentaDestino.setEstadoCuenta(EstadoCuenta.ACTIVA);
        cuentaDestino.setTipoCuenta(TipoCuenta.AHORRO);
        cuentaDestino.setLimiteRetiroDiario(new BigDecimal("30000"));
        cuentaDestino.setSaldoMinimo(new BigDecimal("1000"));
        cuentaDestino.setLimiteTransferencia(new BigDecimal("50000"));
    }

    @Nested
    @DisplayName("Depositar")
    class DepositarTests {

        @Test
        @DisplayName("Depositar exitosamente")
        void depositar_exitoso() {
            DepositoRequestDTO dto = new DepositoRequestDTO(1L, new BigDecimal("1000"));

            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaDestino));

            TransaccionResponseDTO resultado = transaccionService.depositar(dto);

            assertNotNull(resultado);
            assertEquals(new BigDecimal("6000"), cuentaDestino.getSaldo());
            verify(transaccionRepository).save(any(Transaccion.class));
            verify(cuentaRepository).save(cuentaDestino);
        }

        @Test
        @DisplayName("Falla si la cuenta no existe")
        void depositar_cuentaNoExiste() {
            DepositoRequestDTO dto = new DepositoRequestDTO(99L, new BigDecimal("1000"));

            when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());

            EntidadNoEncontradaException ex = assertThrows(EntidadNoEncontradaException.class,
                    () -> transaccionService.depositar(dto));

            assertEquals("Cuenta no encontrada con id: 99", ex.getMessage());
            verify(transaccionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Falla si la cuenta no está activa")
        void depositar_cuentaInactiva() {
            cuentaDestino.setEstadoCuenta(EstadoCuenta.BLOQUEADA);
            DepositoRequestDTO dto = new DepositoRequestDTO(2L, new BigDecimal("1000"));

            when(cuentaRepository.findById(2L)).thenReturn(Optional.of(cuentaDestino));

            ValidacionException ex = assertThrows(ValidacionException.class,
                    () -> transaccionService.depositar(dto));

            assertEquals("No se puede depositar a una cuenta inactiva", ex.getMessage());
            verify(transaccionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Retirar")
    class RetirarTests {

        @Test
        @DisplayName("Retirar exitosamente")
        void retirar_exitoso() {
            RetiroRequestDTO dto = new RetiroRequestDTO(1L, new BigDecimal("2000"));

            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));
            when(transaccionRepository.findByCuentaOrigenIdAndFechaHoraBetween(
                    anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());

            TransaccionResponseDTO resultado = transaccionService.retirar(dto);

            assertNotNull(resultado);
            assertEquals(new BigDecimal("8000"), cuentaOrigen.getSaldo());
            verify(transaccionRepository).save(any(Transaccion.class));
            verify(cuentaRepository).save(cuentaOrigen);
        }

        @Test
        @DisplayName("Falla si no hay saldo suficiente")
        void retirar_saldoInsuficiente() {
            RetiroRequestDTO dto = new RetiroRequestDTO(1L, new BigDecimal("15000"));

            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));

            ValidacionException ex = assertThrows(ValidacionException.class,
                    () -> transaccionService.retirar(dto));

            assertEquals("No tienes saldo suficiente para esta operación", ex.getMessage());
            verify(transaccionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Falla si el retiro viola el saldo mínimo")
        void retirar_violaSaldoMinimo() {
            RetiroRequestDTO dto = new RetiroRequestDTO(1L, new BigDecimal("9600"));

            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));

            ValidacionException ex = assertThrows(ValidacionException.class,
                    () -> transaccionService.retirar(dto));

            assertTrue(ex.getMessage().contains("saldo mínimo"));
            verify(transaccionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Falla si excede el límite diario")
        void retirar_excedeLimiteDiario() {
            RetiroRequestDTO dto = new RetiroRequestDTO(1L, new BigDecimal("6000"));

            Transaccion retiroPrevio = new Transaccion();
            retiroPrevio.setTipoTransaccion(TipoTransaccion.RETIRO);
            retiroPrevio.setMonto(new BigDecimal("45000"));

            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));
            when(transaccionRepository.findByCuentaOrigenIdAndFechaHoraBetween(
                    anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(retiroPrevio));

            ValidacionException ex = assertThrows(ValidacionException.class,
                    () -> transaccionService.retirar(dto));

            System.out.println("Mensaje actual: " + ex.getMessage());
            assertTrue(ex.getMessage().toLowerCase().contains("límite diario"));
        }
    }

    @Nested
    @DisplayName("Transferir")
    class TransferirTests {

        @Test
        @DisplayName("Transferir exitosamente")
        void transferir_exitoso() {
            TransferenciaRequestDTO dto = new TransferenciaRequestDTO(1L, 2L, new BigDecimal("3000"));

            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));
            when(cuentaRepository.findById(2L)).thenReturn(Optional.of(cuentaDestino));
            when(transaccionRepository.findByCuentaOrigenIdAndFechaHoraBetween(
                    anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());

            TransaccionResponseDTO resultado = transaccionService.transferir(dto);

            assertNotNull(resultado);
            assertEquals(new BigDecimal("7000"), cuentaOrigen.getSaldo());
            assertEquals(new BigDecimal("8000"), cuentaDestino.getSaldo());
            verify(transaccionRepository).save(any(Transaccion.class));
            verify(cuentaRepository).save(cuentaOrigen);
            verify(cuentaRepository).save(cuentaDestino);
        }

        @Test
        @DisplayName("Falla si las cuentas son la misma")
        void transferir_mismaCuenta() {
            TransferenciaRequestDTO dto = new TransferenciaRequestDTO(1L, 1L, new BigDecimal("1000"));

            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));

            ValidacionException ex = assertThrows(ValidacionException.class,
                    () -> transaccionService.transferir(dto));

            assertEquals("No se puede transferir a uno mismo", ex.getMessage());
            verify(transaccionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Falla si la cuenta destino está inactiva")
        void transferir_cuentaDestinoInactiva() {
            cuentaDestino.setEstadoCuenta(EstadoCuenta.BLOQUEADA);
            TransferenciaRequestDTO dto = new TransferenciaRequestDTO(1L, 2L, new BigDecimal("1000"));

            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));
            when(cuentaRepository.findById(2L)).thenReturn(Optional.of(cuentaDestino));

            ValidacionException ex = assertThrows(ValidacionException.class,
                    () -> transaccionService.transferir(dto));

            assertEquals("Cuentas inactivas", ex.getMessage());
            verify(transaccionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Falla si excede el límite de transferencia")
        void transferir_excedeLimite() {
            TransferenciaRequestDTO dto = new TransferenciaRequestDTO(1L, 2L, new BigDecimal("150000"));

            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));
            when(cuentaRepository.findById(2L)).thenReturn(Optional.of(cuentaDestino));

            ValidacionException ex = assertThrows(ValidacionException.class,
                    () -> transaccionService.transferir(dto));

            assertTrue(ex.getMessage().contains("límite de transferencia"));
            verify(transaccionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Falla si viola el saldo mínimo en cuenta origen")
        void transferir_violaSaldoMinimo() {
            TransferenciaRequestDTO dto = new TransferenciaRequestDTO(1L, 2L, new BigDecimal("9600"));

            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaOrigen));
            when(cuentaRepository.findById(2L)).thenReturn(Optional.of(cuentaDestino));

            ValidacionException ex = assertThrows(ValidacionException.class,
                    () -> transaccionService.transferir(dto));

            assertTrue(ex.getMessage().contains("saldo mínimo"));
            verify(transaccionRepository, never()).save(any());
        }
    }
}