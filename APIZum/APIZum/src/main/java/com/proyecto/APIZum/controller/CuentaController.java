package com.proyecto.APIZum.controller;

// ================================
//  Gestiona las peticiones HTTP
// ================================

import com.proyecto.APIZum.DTO.CuentaRespuestaDTO;
import com.proyecto.APIZum.DTO.CuentaVerDTO;
import com.proyecto.APIZum.service.CuentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }


    // ======================
    // USER
    // ======================

    // Devuelve la cuenta del usuario autenticado (numCuenta, saldo y usuario propietario).
    @GetMapping("/cuentas/")
    public ResponseEntity<CuentaVerDTO> verCuentaPropia(Authentication auth) {
        return ResponseEntity.ok(cuentaService.verCuentaPropia(auth));
    }

    // Actualiza el saldo de la cuenta del usuario autenticado.
    @PutMapping("/cuentas/")
    public ResponseEntity<CuentaRespuestaDTO> actualizarCuentaPropia(
            Authentication auth,
            @RequestBody Map<String, BigDecimal> body) {
        BigDecimal nuevoSaldo = body.get("saldo");
        return ResponseEntity.ok(cuentaService.actualizarCuentaPropia(auth, nuevoSaldo));
    }


    // ======================
    // ADMIN
    // ======================

    // Devuelve cualquier cuenta por su numCuenta (incluye usuario propietario).
    @GetMapping("/cuentas/{numCuenta}")
    public ResponseEntity<CuentaVerDTO> verCuenta(@PathVariable String numCuenta) {
        return ResponseEntity.ok(cuentaService.verCuenta(numCuenta));
    }
}
