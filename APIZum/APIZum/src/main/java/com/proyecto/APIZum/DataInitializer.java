package com.proyecto.APIZum;

import com.proyecto.APIZum.model.Usuario;
import com.proyecto.APIZum.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// ================================================================
//  Crea el usuario Administrador al arrancar la aplicación.
// ================================================================

/* CommandLineRunner ejecuta código automáticamente después de
    que la aplicación se haya inicializado.
    Component crea automáticamente un objeto de la clase y lo ejecuta
    dentro del contenedor Spring.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    // Dependencias
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* Método que se ejecuta automáticamente cuando arranca la aplicación
        @Override sobrescribe el método definido en la clase padre. */
    @Override
    public void run(String... args) {
        // Solo lo crea si no existe ya en la base de datos
        if (!usuarioRepository.existsById("000000000")) {
            Usuario admin = new Usuario();
            admin.setUsername("000000000");
            admin.setNombre("Administrador");
            admin.setApellidos("");
            admin.setEmail("admin@apizum.es");
            admin.setPassword(passwordEncoder.encode("administrador"));
            admin.setRol("ADMIN");
            usuarioRepository.save(admin);
            System.out.println("✓ Usuario administrador creado (username: 000000000)");
        }
    }
}