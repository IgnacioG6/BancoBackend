package com.example.banco.model;

import com.example.banco.model.enums.EstadoTransaccion;
import com.example.banco.model.enums.TipoTransaccion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
@NoArgsConstructor
@Getter
@Setter
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    TipoTransaccion tipoTransaccion;

    @Enumerated(EnumType.STRING)
    EstadoTransaccion estadoTransaccion;

    LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "cuenta_origen_id")
    Cuenta cuentaOrigen;

    @ManyToOne
    @JoinColumn(name = "cuenta_destino_id")
    Cuenta cuentaDestino;

    String descripcion;

}
