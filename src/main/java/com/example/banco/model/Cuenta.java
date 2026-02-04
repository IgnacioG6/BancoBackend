package com.example.banco.model;

import com.example.banco.model.enums.EstadoCuenta;
import com.example.banco.model.enums.TipoCuenta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "cuentas")
@NoArgsConstructor
@Getter
@Setter
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nroCuenta;

    @Enumerated(EnumType.STRING)
    private EstadoCuenta estadoCuenta;

    @Enumerated(EnumType.STRING)
    private TipoCuenta tipoCuenta;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private BigDecimal limiteRetiroDiario;
    private BigDecimal saldoMinimo;
    private BigDecimal montoMinimoApertura;
    private BigDecimal limiteTransferencia;


    private BigDecimal saldo;
}
