package com.proyecto.APIZum.controller;

// ================================
//  Gestiona las peticiones HTTP
// ================================

import com.proyecto.APIZum.DTO.AdminActualizarDTO;
import com.proyecto.APIZum.DTO.UsuarioLoginDTO;
import com.proyecto.APIZum.DTO.UsuarioPerfilDTO;
import com.proyecto.APIZum.DTO.UsuarioRespuestaDTO;
import com.proyecto.APIZum.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    // ======================
    // USER
    // ======================

    // Devuelve el perfil del usuario autenticado (incluye rol y cuenta, sin password).
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


    // ======================
    // ADMIN
    // ======================

    //Devuelve los datos de cualquier usuario (incluye rol y cuenta, sin password).
    @GetMapping("/usuarios/{username}")
    public ResponseEntity<UsuarioPerfilDTO> verUsuario(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.verUsuario(username));
    }

    //Actualiza cualquier usuario. Permite cambiar el rol (USER/ADMIN).
    @PutMapping("/usuarios/{username}")
    public ResponseEntity<UsuarioPerfilDTO> actualizarUsuario(
            @PathVariable String username,
            @RequestBody AdminActualizarDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(username, dto));
    }

    // Elimina un usuario y su cuenta asociada (relación fuerte, cascade).
    @DeleteMapping("/usuarios/{username}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String username) {
        usuarioService.eliminarUsuario(username);
        return ResponseEntity.noContent().build();
    }


    // ======================
    // AUTH
    // ======================

    // Registra un nuevo usuario sin cuenta. Rol por defecto: USER.
    @PostMapping("/auth/registro")
    public ResponseEntity<UsuarioPerfilDTO> registro(@RequestBody UsuarioRespuestaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(dto));
    }

    // Da de alta la cuenta del usuario recién registrado (numCuenta debe empezar por "ES")
    @PostMapping("/auth/registro/cuenta")
    public ResponseEntity<UsuarioPerfilDTO> registrarCuenta(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String numCuenta = body.get("numCuenta");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.registrarCuenta(username, numCuenta));
    }

    // Autentifica al usuario y devuelve un JWT.
    @PostMapping("/auth/login")
    public ResponseEntity<UsuarioLoginDTO> login(@RequestBody UsuarioLoginDTO dto) {
        return ResponseEntity.ok(usuarioService.login(dto));
    }
}