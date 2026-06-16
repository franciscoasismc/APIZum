package com.proyecto.APIZum.DTO;

import java.time.LocalDateTime;

public class NotificacionDTO {
    private Long id;
    private String tipo;
    private String mensaje;
    private boolean leida;
    private LocalDateTime fecha;
    private Long referenciaId;

    public NotificacionDTO() {}
    public NotificacionDTO(Long id, String tipo, String mensaje, boolean leida,
                           LocalDateTime fecha, Long referenciaId) {
        this.id = id;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.leida = leida;
        this.fecha = fecha;
        this.referenciaId = referenciaId;
    }

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
    public String getTipo(){ return tipo; }
    public void setTipo(String t){ this.tipo = t; }
    public String getMensaje(){ return mensaje; }
    public void setMensaje(String m){ this.mensaje = m; }
    public boolean isLeida(){ return leida; }
    public void setLeida(boolean l){ this.leida = l; }
    public LocalDateTime getFecha(){ return fecha; }
    public void setFecha(LocalDateTime f){ this.fecha = f; }
    public Long getReferenciaId(){ return referenciaId; }
    public void setReferenciaId(Long r){ this.referenciaId = r; }
}
