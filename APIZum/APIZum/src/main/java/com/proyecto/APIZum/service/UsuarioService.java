package com.proyecto.APIZum.service;

// ================================
//  Gestiona la lógica de negocio
// ================================

import com.proyecto.APIZum.DTO.*;
import com.proyecto.APIZum.error.excepciones.EntidadImprocesableException;
import com.proyecto.APIZum.error.excepciones.NoEncontradoException;
import com.proyecto.APIZum.error.excepciones.PeticionIncorrectaException;
import com.proyecto.APIZum.model.Cuenta;
import com.proyecto.APIZum.model.Usuario;
import com.proyecto.APIZum.repository.CuentaRepository;
import com.proyecto.APIZum.repository.UsuarioRepository;
import com.proyecto.APIZum.util.IBANValidator;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final CuentaRepository cuentaRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          CuentaRepository cuentaRepository,
                          PasswordEncoder passwordEncoder,
                          TokenService tokenService,
                          @Lazy AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.cuentaRepository = cuentaRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }


    // ======================
    // USER
    // ======================

    // Perfil del usuario autenticado (la password nunca se expone en la respuesta).
    public UsuarioPerfilDTO verPerfil(Authentication auth) {
        return mapPerfil(buscarUsuario(auth.getName()));
    }

    // Actualiza los datos del usuario autenticado.
    @Transactional
    public UsuarioPerfilDTO actualizarPerfil(Authentication auth, UsuarioRespuestaDTO dto) {
        Usuario usuario = buscarUsuario(auth.getName());
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) usuario.setNombre(dto.getNombre());
        if (dto.getApellidos() != null) usuario.setApellidos(dto.getApellidos());
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            validarEmail(dto.getEmail());
            validarEmailUnico(dto.getEmail(), usuario.getUsername());
            usuario.setEmail(dto.getEmail());
        }
        // Password solo se actualiza si se proporciona
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            validarPassword(dto.getPassword());
            validarPasswordsCoinciden(dto.getPassword(), dto.getRepetirPassword());
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return mapPerfil(usuario);
    }

    // Busca un usuario por email y devuelve solo datos públicos (para buscar destinatario).
    public UsuarioResumenDTO buscarPorEmail(String email) {
        Cuenta cuenta = cuentaRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new NoEncontradoException("No se encontró ningún usuario con ese email"));
        Usuario u = cuenta.getUsuario();
        return new UsuarioResumenDTO(u.getUsername(), u.getNombre(), u.getApellidos(), cuenta.getNumCuenta());
    }


    // ======================
    // ADMIN
    // ======================

    // Lista todos los usuarios activos con paginación.
    public Page<UsuarioPerfilDTO> listarUsuarios(Pageable pageable) {
        return usuarioRepository.findAllByActivoTrue(pageable).map(this::mapPerfil);
    }

    // Ver cualquier usuario por username.
    public UsuarioPerfilDTO verUsuario(String username) {
        return mapPerfil(buscarUsuario(username));
    }

    // Actualiza cualquier usuario por username.
    @Transactional
    public UsuarioPerfilDTO actualizarUsuario(String username, AdminActualizarDTO dto) {
        Usuario usuario = buscarUsuario(username);
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        validarEmail(dto.getEmail());
        validarEmailUnico(dto.getEmail(), username);
        usuario.setEmail(dto.getEmail());
        validarRol(dto.getRol());
        usuario.setRol(dto.getRol().toUpperCase());
        // Password opcional en la actualización por admin
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            validarPassword(dto.getPassword());
            validarPasswordsCoinciden(dto.getPassword(), dto.getRepetirPassword());
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return mapPerfil(usuario);
    }

    // Soft delete: desactiva el usuario en lugar de eliminarlo físicamente.
    // En sistemas financieros el historial nunca se borra.
    @Transactional
    public void eliminarUsuario(String username) {
        Usuario usuario = usuarioRepository.findById(username)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado: " + username));
        usuario.setActivo(false);
    }


    // ======================
    // AUTH
    // ======================

    // Registra un nuevo usuario sin cuenta asociada y devuelve un JWT temporal
    // para que el frontend pueda completar el paso 2 (crear cuenta) de forma autenticada.
    @Transactional
    public UsuarioLoginDTO registrar(UsuarioRespuestaDTO dto) {
        validarUsername(dto.getUsername());
        if (usuarioRepository.existsByUsernameAndActivoTrue(dto.getUsername()) ||
            usuarioRepository.existsById(dto.getUsername())) {
            throw new EntidadImprocesableException("El username ya está en uso: " + dto.getUsername());
        }
        validarEmail(dto.getEmail());
        if (usuarioRepository.findByEmailAndActivoTrue(dto.getEmail()).isPresent()) {
            throw new EntidadImprocesableException("El email ya está en uso: " + dto.getEmail());
        }
        validarPassword(dto.getPassword());
        validarPasswordsCoinciden(dto.getPassword(), dto.getRepetirPassword());
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol("USER");
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        // Emitir JWT para que el paso 2 (crear cuenta) sea autenticado
        Authentication auth = new UsernamePasswordAuthenticationToken(
                usuario.getUsername(), null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String token = tokenService.generateToken(auth);
        return new UsuarioLoginDTO(token, usuario.getUsername(), null);
    }

    // Crea y asocia una cuenta a un usuario ya registrado (requiere JWT del paso 1).
    @Transactional
    public UsuarioPerfilDTO registrarCuenta(String username, String numCuenta) {
        Usuario usuario = buscarUsuario(username);
        if (usuario.getCuenta() != null) {
            throw new EntidadImprocesableException("El usuario ya tiene una cuenta asociada");
        }
        validarNumCuenta(numCuenta);
        if (cuentaRepository.existsById(numCuenta)) {
            throw new EntidadImprocesableException("El número de cuenta ya está en uso: " + numCuenta);
        }
        Cuenta cuenta = new Cuenta(numCuenta, BigDecimal.ZERO, usuario);
        usuario.setCuenta(cuenta);
        usuarioRepository.save(usuario);
        return mapPerfil(usuario);
    }

    // Autentica al usuario y devuelve un JWT.
    public UsuarioLoginDTO login(UsuarioLoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        String token = tokenService.generateToken(authentication);
        return new UsuarioLoginDTO(token, dto.getUsername(), null);
    }


    // ======================
    // UserDetailsService
    // ======================

    // Solo usuarios activos pueden autenticarse (soft delete).
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo: " + username));
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))
        );
    }


    // ======================
    // Validaciones
    // ======================

    private void validarUsername(String username) {
        if (username == null || !username.matches("\\d{9}")) {
            throw new PeticionIncorrectaException("El username debe tener exactamente 9 dígitos numéricos");
        }
    }

    private void validarEmail(String email) {
        if (email == null || !email.matches("^[^@]+@[^@]+\\.(com|es)$")) {
            throw new PeticionIncorrectaException("El email debe contener @ y terminar en .com o .es");
        }
    }

    private void validarEmailUnico(String email, String usernameActual) {
        usuarioRepository.findByEmail(email).ifPresent(u -> {
            if (!u.getUsername().equals(usernameActual)) {
                throw new EntidadImprocesableException("El email ya está en uso: " + email);
            }
        });
    }

    private void validarPassword(String password) {
        if (password == null || !password.matches("^[a-zA-Z0-9]{8,20}$")) {
            throw new PeticionIncorrectaException("La password debe tener entre 8 y 20 caracteres alfanuméricos");
        }
    }

    private void validarPasswordsCoinciden(String password, String repetirPassword) {
        if (password == null || !password.equals(repetirPassword)) {
            throw new PeticionIncorrectaException("Las passwords no coinciden");
        }
    }

    private void validarRol(String rol) {
        if (rol == null || (!rol.equalsIgnoreCase("USER") && !rol.equalsIgnoreCase("ADMIN"))) {
            throw new PeticionIncorrectaException("El rol solo puede ser USER o ADMIN");
        }
    }

    // Validación IBAN completa según ISO 13616
    private void validarNumCuenta(String numCuenta) {
        if (!IBANValidator.isValidSpanish(numCuenta)) {
            throw new PeticionIncorrectaException(
                "El número de cuenta debe ser un IBAN español válido (ES + 22 dígitos, verificado según ISO 13616)");
        }
    }


    // ======================
    // Búsqueda común
    // ======================

    private Usuario buscarUsuario(String username) {
        return usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado: " + username));
    }


    // ======================
    // Mapeos
    // ======================

    private UsuarioPerfilDTO mapPerfil(Usuario u) {
        return new UsuarioPerfilDTO(
                u.getUsername(),
                u.getNombre(),
                u.getApellidos(),
                u.getEmail(),
                u.getRol(),
                null,       // password nunca se expone
                u.getCuenta()
        );
    }
}
