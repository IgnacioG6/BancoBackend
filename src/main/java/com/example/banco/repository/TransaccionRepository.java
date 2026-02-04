package com.example.banco.repository;

import com.example.banco.model.Transaccion;
import com.example.banco.model.enums.EstadoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByEstado(EstadoTransaccion estado);

    @Query("SELECT t FROM Transaccion t WHERE t.cuentaOrigen.id = :cuentaId OR t.cuentaDestino.id = :cuentaId")
    List<Transaccion> findByCuentaId(@Param("cuentaId") Long cuentaId);

    List<Transaccion> findByCuentaOrigenIdAndFechaHoraBetween(
            Long cuentaId,
            LocalDateTime inicio,
            LocalDateTime fin
    );
}
