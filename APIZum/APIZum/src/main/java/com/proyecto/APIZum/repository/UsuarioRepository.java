package com.proyecto.APIZum.repository;

import com.proyecto.APIZum.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Proporciona acceso a la tabla "usuarios" de la base de datos.
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String>, JpaSpecificationExecutor<Usuario> {
    Optional<Usuario> findByEmail(String email);
}
