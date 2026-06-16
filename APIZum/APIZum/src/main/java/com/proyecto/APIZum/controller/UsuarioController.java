package com.proyecto.APIZum.controller;

// ================================
//  Gestiona las peticiones HTTP
// ================================

import com.proyecto.APIZum.DTO.*;
import com.proyecto.APIZum.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Usuarios & Auth", description = "Registro, login, perfil y gestión de usuarios (ADMIN)")
@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    // ======================
    // USER
    // ======================

    // Devuelve el perfil del usuario autenticado.
    @GetMapping("/usuarios/")
    public ResponseEntity<UsuarioPerfilDTO> verPerfil(Authentication auth) {
        return ResponseEntity.ok(usuarioService.verPerfil(auth));
    }

    // Actualiza los datos del usuario autenticado.
    @PutMapping("/usuarios/")
    public ResponseEntity<UsuarioPerfilDTO> actualizarPerfil(
            Authentication auth,
            @RequestBody UsuarioRespuestaDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(auth, dto));
    }

    // Busca un destinatario por email (para nueva transacción / añadir contacto).
    @GetMapping("/usuarios/buscar")
    public ResponseEntity<UsuarioResumenDTO> buscarPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }


    // ======================
    // ADMIN
    // ======================

    // Lista todos los usuarios activos con paginación.
    @GetMapping("/usuarios")
    public ResponseEntity<Page<UsuarioPerfilDTO>> listarUsuarios(
            @PageableDefault(size = 10, sort = "username") Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarUsuarios(pageable));
    }

    // Devuelve los datos de cualquier usuario.
    @GetMapping("/usuarios/{username}")
    public ResponseEntity<UsuarioPerfilDTO> verUsuario(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.verUsuario(username));
    }

    // Actualiza cualquier usuario (permite cambiar el rol).
    @PutMapping("/usuarios/{username}")
    public ResponseEntity<UsuarioPerfilDTO> actualizarUsuario(
            @PathVariable String username,
            @RequestBody AdminActualizarDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(username, dto));
    }

    // Soft delete: desactiva el usuario sin eliminar su historial financiero.
    @DeleteMapping("/usuarios/{username}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String username) {
        usuarioService.eliminarUsuario(username);
        return ResponseEntity.noContent().build();
    }


    // ======================
    // AUTH
    // ======================

    // Registra un nuevo usuario. Devuelve un JWT temporal para el paso 2 (crear cuenta).
    @PostMapping("/auth/registro")
    public ResponseEntity<UsuarioLoginDTO> registro(@RequestBody UsuarioRespuestaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(dto));
    }

    // Asocia una cuenta bancaria al usuario recién registrado (requiere JWT del paso 1).
    @PostMapping("/auth/registro/cuenta")
    public ResponseEntity<UsuarioPerfilDTO> registrarCuenta(
            Authentication auth,
            @RequestBody Map<String, String> body) {
        String numCuenta = body.get("numCuenta");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registrarCuenta(auth.getName(), numCuenta));
    }

    // Autentica al usuario y devuelve un JWT.
    @PostMapping("/auth/login")
    public ResponseEntity<UsuarioLoginDTO> login(@RequestBody UsuarioLoginDTO dto) {
        return ResponseEntity.ok(usuarioService.login(dto));
    }
}
