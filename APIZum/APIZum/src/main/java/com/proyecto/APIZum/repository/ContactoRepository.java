package com.proyecto.APIZum.repository;

import com.proyecto.APIZum.model.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Long> {

    List<Contacto> findByUsuarioUsername(String username);
    List<Contacto> findByUsuarioUsernameAndEsFavoritoTrue(String username);
    Optional<Contacto> findByUsuarioUsernameAndContactoUsername(String usuarioUsername, String contactoUsername);
    boolean existsByUsuarioUsernameAndContactoUsername(String usuarioUsername, String contactoUsername);
}
