package com.proyecto.APIZum;

import com.proyecto.APIZum.model.Cuenta;
import com.proyecto.APIZum.model.Usuario;
import com.proyecto.APIZum.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// ================================================================
//  Crea el usuario Administrador y los usuarios de prueba al arrancar.
//  Si ya existen en la BD, no hace nada (idempotente).
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
        crearAdmin();
        crearUsuarioPrueba("111111111", "Usuario",  "Uno",  "uno@pruebas.com",  "usutest01", "ES6300010001000111111111");
        crearUsuarioPrueba("222222222", "Usuario",  "Dos",  "dos@pruebas.com",  "usutest02", "ES4400020002000222222222");
        crearUsuarioPrueba("333333333", "Usuario",  "Tres", "tres@pruebas.com", "usutest03", "ES2500030003000333333333");
    }

    // ─── Admin ───────────────────────────────────────────────────────────────

    private void crearAdmin() {
        if (!usuarioRepository.existsById("000000000")) {
            Usuario admin = new Usuario();
            admin.setUsername("000000000");
            admin.setNombre("Administrador");
            admin.setApellidos("");
            admin.setEmail("admin@apizum.es");
            admin.setPassword(passwordEncoder.encode("administrador"));
            admin.setRol("ADMIN");
            admin.setActivo(true);
            usuarioRepository.save(admin);
            System.out.println("✓ Usuario administrador creado (username: 000000000)");
        }
    }

    // ─── Usuarios de prueba ───────────────────────────────────────────────────

    private void crearUsuarioPrueba(String username, String nombre, String apellidos,
                                    String email, String password, String numCuenta) {
        if (!usuarioRepository.existsById(username)) {
            Usuario u = new Usuario();
            u.setUsername(username);
            u.setNombre(nombre);
            u.setApellidos(apellidos);
            u.setEmail(email);
            u.setPassword(passwordEncoder.encode(password));
            u.setRol("USER");
            u.setActivo(true);

            // Cuenta bancaria con saldo inicial de 1.000 € para pruebas
            Cuenta cuenta = new Cuenta(numCuenta, BigDecimal.valueOf(1000), u);
            u.setCuenta(cuenta);

            usuarioRepository.save(u);  // CascadeType.ALL guarda también la Cuenta
            System.out.println("✓ Usuario de prueba creado (username: " + username + ", IBAN: " + numCuenta + ")");
        }
    }
}
