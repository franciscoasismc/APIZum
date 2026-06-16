package com.proyecto.APIZum.repository;

import com.proyecto.APIZum.model.Cuenta;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Proporciona acceso a la tabla "cuentas" de la base de datos.
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, String> {

    // Bloqueo pesimista: evita condiciones de carrera al realizar transferencias simultáneas.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cuenta c WHERE c.numCuenta = :numCuenta")
    Optional<Cuenta> findByIdWithLock(@Param("numCuenta") String numCuenta);

    Page<Cuenta> findByUsuarioUsername(String username, Pageable pageable);
    Page<Cuenta> findByNumCuenta(String numCuenta, Pageable pageable);

    // Busca una cuenta por el email del usuario propietario
    @Query("SELECT c FROM Cuenta c JOIN c.usuario u WHERE u.email = :email AND u.activo = true")
    Optional<Cuenta> findByUsuarioEmail(@Param("email") String email);
}
