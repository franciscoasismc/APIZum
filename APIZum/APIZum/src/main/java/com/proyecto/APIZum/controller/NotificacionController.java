package com.proyecto.APIZum.controller;

import com.proyecto.APIZum.DTO.NotificacionDTO;
import com.proyecto.APIZum.service.NotificacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Notificaciones", description = "Notificaciones del sistema para el usuario autenticado")
@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<Page<NotificacionDTO>> listar(Authentication auth,
            @PageableDefault(size = 20, sort = "fecha") Pageable pageable) {
        return ResponseEntity.ok(notificacionService.listar(auth, pageable));
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(Authentication auth) {
        return ResponseEntity.ok(notificacionService.contarNoLeidas(auth));
    }

    @PutMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodasLeidas(Authentication auth) {
        notificacionService.marcarTodasLeidas(auth);
        return ResponseEntity.noContent().build();
    }
}
