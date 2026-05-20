package com.proyecto.APIZum.DTO;

import com.proyecto.APIZum.model.Cuenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransaccionRespuestaDTO {
    private BigDecimal cantidad;
    private String descripcion;
    private LocalDateTime fecha;
    private Cuenta cuentaOrigen;
    private Cuenta cuentaDestino;

    public TransaccionRespuestaDTO(BigDecimal cantidad, String descripcion,
                                    LocalDateTime fecha, Cuenta cuentaOrigen, Cuenta cuentaDestino) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
    }

    public BigDecimal getCantidad(){ return cantidad; }
    public void setCantidad(BigDecimal cantidad){ this.cantidad = cantidad; }

    public String getDescripcion(){ return descripcion; }
    public void setDescripcion(String descripcion){ this.descripcion = descripcion; }

    public LocalDateTime getFecha(){ return fecha; }
    public void setFecha(LocalDateTime fecha){ this.fecha = fecha; }

    public Cuenta getCuentaOrigen(){ return cuentaOrigen; }
    public void  setCuentaOrigen(Cuenta cuentaOrigen){ this.cuentaOrigen = cuentaOrigen; }

    public Cuenta getCuentaDestino(){ return cuentaDestino; }
    public void setCuentaDestino(Cuenta cuentaDestino){ this.cuentaDestino = cuentaDestino; }
}
