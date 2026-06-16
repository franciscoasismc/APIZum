package com.proyecto.APIZum.controller;

// ================================
//  Gestiona las peticiones HTTP
// ================================

import com.proyecto.APIZum.DTO.TransaccionRespuestaDTO;
import com.proyecto.APIZum.DTO.TransaccionVerDTO;
import com.proyecto.APIZum.service.TransaccionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "Transacciones", description = "Historial y realización de transferencias monetarias")
@RestController
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }


    // ======================
    // USER
    // ======================

    // Crea una transacción desde la cuenta del usuario autenticado.
    @PostMapping("/transacciones/")
    public ResponseEntity<TransaccionRespuestaDTO> crearTransaccion(
            Authentication auth,
            @RequestBody Map<String, Object> body) {

        String numCuentaDestino = (String) body.get("numCuentaDestino");
        BigDecimal cantidad = new BigDecimal(body.get("cantidad").toString());
        String descripcion = (String) body.getOrDefault("descripcion", null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transaccionService.crearTransaccion(auth, numCuentaDestino, cantidad, descripcion));
    }

    // Devuelve el historial del usuario autenticado (como origen o destino), paginado.
    @GetMapping("/transacciones/")
    public ResponseEntity<Page<TransaccionVerDTO>> verHistorialPropio(
            Authentication auth,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(transaccionService.verHistorialPropio(auth, pageable));
    }


    // ======================
    // ADMIN
    // ======================

    // Devuelve el historial de transacciones de un usuario por username.
    @GetMapping("/transacciones/usuario/{username}")
    public ResponseEntity<Page<TransaccionVerDTO>> verHistorialPorUsername(
            @PathVariable String username,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(transaccionService.verHistorialPorUsername(username, pageable));
    }

    // Devuelve el historial de transacciones de una cuenta por numCuenta.
    @GetMapping("/transacciones/cuenta/{numCuenta}")
    public ResponseEntity<Page<TransaccionVerDTO>> verHistorialPorCuenta(
            @PathVariable String numCuenta,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(transaccionService.verHistorialPorCuenta(numCuenta, pageable));
    }

    // Devuelve una transacción concreta por su id.
    @GetMapping("/transacciones/{id}")
    public ResponseEntity<TransaccionVerDTO> verTransaccion(@PathVariable Long id) {
        return ResponseEntity.ok(transaccionService.verTransaccion(id));
    }
}