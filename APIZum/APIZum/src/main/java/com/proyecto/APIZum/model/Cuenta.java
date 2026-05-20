package com.proyecto.APIZum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// ================================================================
//  Entidad Cuenta
// ================================================================

/* @JsonIgnoreProperties evita la recursión infinita al serializar. */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "cuentas")
public class Cuenta {

    @Id
    private String numCuenta;

    @Column(nullable = false)
    private BigDecimal saldo;

    // Relación 1:1
    @OneToOne(mappedBy = "cuenta")
    @JsonIgnore
    private Usuario usuario;

    // Relación 1:N
    @OneToMany(mappedBy = "cuentaOrigen")
    @JsonIgnore
    private List<Transaccion> transaccionesOrigen = new ArrayList<>();

    // Relación 1:N
    @OneToMany(mappedBy = "cuentaDestino")
    @JsonIgnore
    private List<Transaccion> transaccionesDestino = new ArrayList<>();


    // Constructores

    public Cuenta() {}

    public Cuenta(String numCuenta, BigDecimal saldo, Usuario usuario) {
        this.numCuenta = numCuenta;
        this.saldo = saldo;
        this.usuario = usuario;
    }


    // Getters y Setters

    public String getNumCuenta(){ return numCuenta; }
    public void setNumCuenta(String numCuenta){ this.numCuenta = numCuenta; }

    public BigDecimal getSaldo(){ return saldo; }
    public void setSaldo(BigDecimal saldo){ this.saldo = saldo; }

    public Usuario getUsuario(){ return usuario; }
    public void setUsuario(Usuario usuario){ this.usuario = usuario; }

    public List<Transaccion> getTransaccionesOrigen(){ return transaccionesOrigen; }
    public void setTransaccionesOrigen(List<Transaccion> list){ this.transaccionesOrigen = list; }

    public List<Transaccion> getTransaccionesDestino(){ return transaccionesDestino; }
    public void setTransaccionesDestino(List<Transaccion> list){ this.transaccionesDestino = list; }
}
