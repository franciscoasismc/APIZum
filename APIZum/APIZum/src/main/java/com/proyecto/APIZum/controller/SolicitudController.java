package com.proyecto.APIZum.controller;

import com.proyecto.APIZum.DTO.SolicitudDTO;
import com.proyecto.APIZum.service.SolicitudService;
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

@Tag(name = "Solicitudes", description = "Solicitudes de pago entre usuarios")
@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping
    public ResponseEntity<Page<SolicitudDTO>> listar(Authentication auth,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(solicitudService.listar(auth, pageable));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<Page<SolicitudDTO>> pendientes(Authentication auth,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(solicitudService.listarPendientesRecibidas(auth, pageable));
    }

    @PostMapping
    public ResponseEntity<SolicitudDTO> crear(Authentication auth, @RequestBody Map<String, Object> body) {
        String cuentaDestino = (String) body.get("cuentaDestino");
        BigDecimal cantidad  = new BigDecimal(body.get("cantidad").toString());
        String descripcion   = (String) body.getOrDefault("descripcion", null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solicitudService.crear(auth, cuentaDestino, cantidad, descripcion));
    }

    @PutMapping("/{id}/responder")
    public ResponseEntity<SolicitudDTO> responder(Authentication auth, @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(solicitudService.responder(auth, id, body.get("aceptar")));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<SolicitudDTO> cancelar(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.cancelar(auth, id));
    }
}
