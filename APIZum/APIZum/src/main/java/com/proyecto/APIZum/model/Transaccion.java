package com.proyecto.APIZum.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// ================================================================
//  Entidad Transaccion
// ================================================================
@Entity
@Table(name = "transacciones")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransaccion;

    @Column(nullable = false)
    private BigDecimal cantidad;

    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    // Relación 1:N
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuentaOrigen", nullable = false)
    private Cuenta cuentaOrigen;

    // Relación 1:N
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuentaDestino", nullable = false)
    private Cuenta cuentaDestino;


    // Constructores

    public Transaccion() {}

    public Transaccion(BigDecimal cantidad, String descripcion,
                       Cuenta cuentaOrigen, Cuenta cuentaDestino) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
    }


    // Getters y Setters

    public Long getIdTransaccion(){ return idTransaccion; }
    public void setIdTransaccion(Long id){ this.idTransaccion = id; }

    public BigDecimal getCantidad(){ return cantidad; }
    public void setCantidad(BigDecimal cantidad){ this.cantidad = cantidad; }

    public String getDescripcion(){ return descripcion; }
    public void setDescripcion(String descripcion){ this.descripcion = descripcion; }

    public LocalDateTime getFecha(){ return fecha; }
    public void setFecha(LocalDateTime fecha){ this.fecha = fecha; }

    public Cuenta getCuentaOrigen(){ return cuentaOrigen; }
    public void setCuentaOrigen(Cuenta cuentaOrigen){ this.cuentaOrigen = cuentaOrigen; }

    public Cuenta getCuentaDestino(){ return cuentaDestino; }
    public void setCuentaDestino(Cuenta cuentaDestino){ this.cuentaDestino = cuentaDestino; }
}
