package com.proyecto.APIZum.repository;

import com.proyecto.APIZum.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Proporciona acceso a la tabla "usuarios" de la base de datos.
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String>, JpaSpecificationExecutor<Usuario> {

    Optional<Usuario> findByEmail(String email);

    // Solo usuarios activos (soft delete)
    Optional<Usuario> findByUsernameAndActivoTrue(String username);
    Optional<Usuario> findByEmailAndActivoTrue(String email);
    Page<Usuario> findAllByActivoTrue(Pageable pageable);
    boolean existsByUsernameAndActivoTrue(String username);
}
