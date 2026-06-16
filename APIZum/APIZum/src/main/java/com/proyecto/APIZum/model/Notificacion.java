package com.proyecto.APIZum.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ================================================================
//  Entidad Notificacion
// ================================================================
//  Registra eventos relevantes para el usuario:
//  transferencia enviada/recibida, solicitud recibida/aceptada/rechazada.
// ================================================================

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario destinatario de la notificación
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_username", nullable = false)
    private Usuario usuario;

    // Tipo: TRANSFERENCIA_ENVIADA, TRANSFERENCIA_RECIBIDA,
    //       SOLICITUD_RECIBIDA, SOLICITUD_ACEPTADA, SOLICITUD_RECHAZADA
    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false)
    private boolean leida = false;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    // Referencia opcional a la transacción o solicitud relacionada
    private Long referenciaId;


    // Constructores

    public Notificacion() {}

    public Notificacion(Usuario usuario, String tipo, String mensaje, Long referenciaId) {
        this.usuario = usuario;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.referenciaId = referenciaId;
    }


    // Getters y Setters

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public Usuario getUsuario(){ return usuario; }
    public void setUsuario(Usuario usuario){ this.usuario = usuario; }

    public String getTipo(){ return tipo; }
    public void setTipo(String tipo){ this.tipo = tipo; }

    public String getMensaje(){ return mensaje; }
    public void setMensaje(String mensaje){ this.mensaje = mensaje; }

    public boolean isLeida(){ return leida; }
    public void setLeida(boolean leida){ this.leida = leida; }

    public LocalDateTime getFecha(){ return fecha; }
    public void setFecha(LocalDateTime fecha){ this.fecha = fecha; }

    public Long getReferenciaId(){ return referenciaId; }
    public void setReferenciaId(Long referenciaId){ this.referenciaId = referenciaId; }
}
