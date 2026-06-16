package com.proyecto.APIZum.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SolicitudDTO {
    private Long id;
    private String cuentaSolicitante;
    private String nombreSolicitante;
    private String cuentaDestino;
    private String nombreDestino;
    private BigDecimal cantidad;
    private String descripcion;
    private String estado;
    private LocalDateTime fecha;

    public SolicitudDTO() {}
    public SolicitudDTO(Long id, String cuentaSolicitante, String nombreSolicitante,
                        String cuentaDestino, String nombreDestino, BigDecimal cantidad,
                        String descripcion, String estado, LocalDateTime fecha) {
        this.id = id;
        this.cuentaSolicitante = cuentaSolicitante;
        this.nombreSolicitante = nombreSolicitante;
        this.cuentaDestino = cuentaDestino;
        this.nombreDestino = nombreDestino;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fecha = fecha;
    }

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
    public String getCuentaSolicitante(){ return cuentaSolicitante; }
    public void setCuentaSolicitante(String c){ this.cuentaSolicitante = c; }
    public String getNombreSolicitante(){ return nombreSolicitante; }
    public void setNombreSolicitante(String n){ this.nombreSolicitante = n; }
    public String getCuentaDestino(){ return cuentaDestino; }
    public void setCuentaDestino(String c){ this.cuentaDestino = c; }
    public String getNombreDestino(){ return nombreDestino; }
    public void setNombreDestino(String n){ this.nombreDestino = n; }
    public BigDecimal getCantidad(){ return cantidad; }
    public void setCantidad(BigDecimal c){ this.cantidad = c; }
    public String getDescripcion(){ return descripcion; }
    public void setDescripcion(String d){ this.descripcion = d; }
    public String getEstado(){ return estado; }
    public void setEstado(String e){ this.estado = e; }
    public LocalDateTime getFecha(){ return fecha; }
    public void setFecha(LocalDateTime f){ this.fecha = f; }
}
