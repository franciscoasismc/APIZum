package com.proyecto.APIZum.service;

// ================================
//  Gestiona la lógica de negocio
// ================================

import com.proyecto.APIZum.DTO.AdminActualizarDTO;
import com.proyecto.APIZum.DTO.UsuarioLoginDTO;
import com.proyecto.APIZum.DTO.UsuarioPerfilDTO;
import com.proyecto.APIZum.DTO.UsuarioRespuestaDTO;
import com.proyecto.APIZum.error.excepciones.EntidadImprocesableException;
import com.proyecto.APIZum.error.excepciones.NoEncontradoException;
import com.proyecto.APIZum.error.excepciones.PeticionIncorrectaException;
import com.proyecto.APIZum.model.Cuenta;
import com.proyecto.APIZum.model.Usuario;
import com.proyecto.APIZum.repository.CuentaRepository;
import com.proyecto.APIZum.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
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

    //Actualiza los datos del usuario autenticado.
    @Transactional
    public UsuarioPerfilDTO actualizarPerfil(Authentication auth, UsuarioRespuestaDTO dto) {
        Usuario usuario = buscarUsuario(auth.getName());
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        validarEmail(dto.getEmail());
        validarEmailUnico(dto.getEmail(), usuario.getUsername());
        usuario.setEmail(dto.getEmail());
        validarPassword(dto.getPassword());
        validarPasswordsCoinciden(dto.getPassword(), dto.getRepetirPassword());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        return mapPerfil(usuario);
    }


    // ======================
    // ADMIN
    // ======================

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

    // Elimina un usuario por su username.
    @Transactional
    public void eliminarUsuario(String username) {
        if (!usuarioRepository.existsById(username)) {
            throw new NoEncontradoException("Usuario no encontrado: " + username);
        }
        usuarioRepository.deleteById(username);
    }


    // ======================
    // AUTH
    // ======================

    // Registra un nuevo usuario sin cuenta asociada.
    @Transactional
    public UsuarioPerfilDTO registrar(UsuarioRespuestaDTO dto) {
        validarUsername(dto.getUsername());
        if (usuarioRepository.existsById(dto.getUsername())) {
            throw new EntidadImprocesableException("El username ya está en uso: " + dto.getUsername());
        }
        validarEmail(dto.getEmail());
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
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
        return mapPerfil(usuarioRepository.save(usuario));
    }

    // Crea y asocia una cuenta a un usuario ya registrado.
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

    /* Spring Security llama a este método automáticamente al autenticar en el login.
       Carga el usuario de la BD y construye el objeto UserDetails con su rol. */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))
        );
    }


    // ======================
    // Validaciones
    // ======================

    // username: exactamente 9 dígitos numéricos
    private void validarUsername(String username) {
        if (username == null || !username.matches("\\d{9}")) {
            throw new PeticionIncorrectaException("El username debe tener exactamente 9 dígitos numéricos");
        }
    }

    // email: debe contener @ y terminar en .com o .es
    private void validarEmail(String email) {
        if (email == null || !email.matches("^[^@]+@[^@]+\\.(com|es)$")) {
            throw new PeticionIncorrectaException("El email debe contener @ y terminar en .com o .es");
        }
    }

    //  email: no puede estar ya en uso por otro usuario
    private void validarEmailUnico(String email, String usernameActual) {
        usuarioRepository.findByEmail(email).ifPresent(u -> {
            if (!u.getUsername().equals(usernameActual)) {
                throw new EntidadImprocesableException("El email ya está en uso: " + email);
            }
        });
    }

    // password: entre 8 y 20 caracteres alfanuméricos
    private void validarPassword(String password) {
        if (password == null || !password.matches("^[a-zA-Z0-9]{8,20}$")) {
            throw new PeticionIncorrectaException("La password debe tener entre 8 y 20 caracteres alfanuméricos");
        }
    }

    // password y repetirPassword deben coincidir exactamente
    private void validarPasswordsCoinciden(String password, String repetirPassword) {
        if (password == null || !password.equals(repetirPassword)) {
            throw new PeticionIncorrectaException("Las passwords no coinciden");
        }
    }

    // rol: solo puede ser USER o ADMIN
    private void validarRol(String rol) {
        if (rol == null || (!rol.equalsIgnoreCase("USER") && !rol.equalsIgnoreCase("ADMIN"))) {
            throw new PeticionIncorrectaException("El rol solo puede ser USER o ADMIN");
        }
    }

    // numCuenta: debe empezar por "ES"
    private void validarNumCuenta(String numCuenta) {
        if (numCuenta == null || !numCuenta.startsWith("ES")) {
            throw new PeticionIncorrectaException("El número de cuenta debe empezar por ES");
        }
    }


    // ======================
    // Búsqueda común
    // ======================

    private Usuario buscarUsuario(String username) {
        return usuarioRepository.findById(username)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado: " + username));
    }


    // ======================
    // Mapeos
    // ======================

    // Vista unificada para USER y ADMIN:
    private UsuarioPerfilDTO mapPerfil(Usuario u) {
        return new UsuarioPerfilDTO(
                u.getUsername(),
                u.getNombre(),
                u.getApellidos(),
                u.getEmail(),
                u.getRol(),
                null,         // password nunca se expone
                u.getCuenta()
        );
    }
}