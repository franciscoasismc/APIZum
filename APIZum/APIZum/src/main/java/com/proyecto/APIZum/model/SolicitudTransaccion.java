package com.proyecto.APIZum.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// ================================================================
//  Entidad SolicitudTransaccion
// ================================================================
//  Permite a un usuario solicitar dinero a otro.
//  El destinatario puede aceptar (se ejecuta la transferencia),
//  rechazar o dejar caducar la solicitud.
// ================================================================

@Entity
@Table(name = "solicitudes")
public class SolicitudTransaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quien solicita el dinero (cuenta que lo recibirá si se acepta)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_solicitante", nullable = false)
    private Cuenta cuentaSolicitante;

    // A quien se le solicita (cuenta que pagará si acepta)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_destino", nullable = false)
    private Cuenta cuentaDestino;

    @Column(nullable = false)
    private BigDecimal cantidad;

    private String descripcion;

    // PENDIENTE → ACEPTADA / RECHAZADA / CANCELADA
    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();


    // Constructores

    public SolicitudTransaccion() {}

    public SolicitudTransaccion(Cuenta cuentaSolicitante, Cuenta cuentaDestino,
                                 BigDecimal cantidad, String descripcion) {
        this.cuentaSolicitante = cuentaSolicitante;
        this.cuentaDestino = cuentaDestino;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
    }


    // Getters y Setters

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public Cuenta getCuentaSolicitante(){ return cuentaSolicitante; }
    public void setCuentaSolicitante(Cuenta c){ this.cuentaSolicitante = c; }

    public Cuenta getCuentaDestino(){ return cuentaDestino; }
    public void setCuentaDestino(Cuenta c){ this.cuentaDestino = c; }

    public BigDecimal getCantidad(){ return cantidad; }
    public void setCantidad(BigDecimal cantidad){ this.cantidad = cantidad; }

    public String getDescripcion(){ return descripcion; }
    public void setDescripcion(String descripcion){ this.descripcion = descripcion; }

    public String getEstado(){ return estado; }
    public void setEstado(String estado){ this.estado = estado; }

    public LocalDateTime getFecha(){ return fecha; }
    public void setFecha(LocalDateTime fecha){ this.fecha = fecha; }
}
