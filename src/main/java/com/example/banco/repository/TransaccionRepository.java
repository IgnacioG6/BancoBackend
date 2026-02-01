package com.example.banco.repository;

import com.example.banco.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByIdCuentaOrigen(Long  idCuentaOrigen);
    List<Transaccion> findByIdCuentaDestino(Long  idCuentaDestino);
    List<Transaccion> findByEstado(String estado);

}
