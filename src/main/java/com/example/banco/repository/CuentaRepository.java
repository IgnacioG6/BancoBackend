package com.example.banco.repository;

import com.example.banco.model.Cliente;
import com.example.banco.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta,Long> {
    Optional<Cuenta> findByNroCuenta(String nroCuenta);
    List<Cuenta> findByIdCliente(Long idCliente);

}
