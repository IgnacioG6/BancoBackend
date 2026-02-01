package com.example.banco.model;

import com.example.banco.model.enums.EstadoCliente;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@NoArgsConstructor
@Getter
@Setter
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true, nullable = false)
    private String dni;

    private String email;
    private String telefono;

    @Enumerated(EnumType.STRING)
    private EstadoCliente estado;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Cuenta> cuentas = new ArrayList<>();
}