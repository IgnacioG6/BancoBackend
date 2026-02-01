package com.example.banco.model;

import com.example.banco.model.enums.EstadoCuenta;
import com.example.banco.model.enums.TipoCuenta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "cuentas")
@NoArgsConstructor
@Getter
@Setter
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String nroCuenta;

    @Enumerated(EnumType.STRING)
    EstadoCuenta estadoCuenta;

    @Enumerated(EnumType.STRING)
    TipoCuenta tipoCuenta;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    Cliente cliente;

    @OneToMany(mappedBy = "cuenta", fetch = FetchType.LAZY)
    List<Transaccion> transacciones;

    BigDecimal saldo = BigDecimal.ZERO;

}
