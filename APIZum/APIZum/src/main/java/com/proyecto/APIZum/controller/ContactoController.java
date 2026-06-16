package com.proyecto.APIZum.controller;

import com.proyecto.APIZum.DTO.ContactoDTO;
import com.proyecto.APIZum.service.ContactoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Contactos", description = "Gestión de contactos y favoritos")
@RestController
@RequestMapping("/contactos")
public class ContactoController {

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping
    public ResponseEntity<List<ContactoDTO>> listar(Authentication auth) {
        return ResponseEntity.ok(contactoService.listar(auth));
    }

    @GetMapping("/favoritos")
    public ResponseEntity<List<ContactoDTO>> favoritos(Authentication auth) {
        return ResponseEntity.ok(contactoService.listarFavoritos(auth));
    }

    @PostMapping
    public ResponseEntity<ContactoDTO> agregar(Authentication auth, @RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contactoService.agregar(auth, body.get("contactoUsername"), body.get("alias")));
    }

    @PutMapping("/{id}/favorito")
    public ResponseEntity<ContactoDTO> toggleFavorito(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(contactoService.toggleFavorito(auth, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(Authentication auth, @PathVariable Long id) {
        contactoService.eliminar(auth, id);
        return ResponseEntity.noContent().build();
    }
}
