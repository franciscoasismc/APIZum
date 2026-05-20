package com.proyecto.APIZum.DTO;

import java.math.BigDecimal;

public class CuentaRespuestaDTO {
    private String numCuenta;
    private BigDecimal saldo;

    public CuentaRespuestaDTO(String numCuenta, BigDecimal saldo) {
        this.numCuenta = numCuenta;
        this.saldo = saldo;
    }

    public String getNumCuenta(){ return numCuenta; }
    public void setNumCuenta(String numCuenta){ this.numCuenta = numCuenta; }

    public BigDecimal getSaldo(){ return saldo; }
    public void setSaldo(BigDecimal saldo){ this.saldo = saldo; }
}
