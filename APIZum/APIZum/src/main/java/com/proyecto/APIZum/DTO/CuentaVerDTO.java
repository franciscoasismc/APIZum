package com.proyecto.APIZum.DTO;

import com.proyecto.APIZum.model.Usuario;
import java.math.BigDecimal;

public class CuentaVerDTO {
    private String numCuenta;
    private BigDecimal saldo;
    private Usuario usuario;

    public CuentaVerDTO(String numCuenta, BigDecimal saldo, Usuario usuario) {
        this.numCuenta = numCuenta;
        this.saldo = saldo;
        this.usuario = usuario;
    }

    public String getNumCuenta(){ return numCuenta; }
    public void setNumCuenta(String numCuenta){ this.numCuenta = numCuenta; }

    public BigDecimal getSaldo(){ return saldo; }
    public void setSaldo(BigDecimal saldo){ this.saldo = saldo; }

    public Usuario getUsuario(){ return usuario; }
    public void setUsuario(Usuario usuario){ this.usuario = usuario; }
}
