package com.proyecto.APIZum.repository;

import com.proyecto.APIZum.model.Cuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Proporciona acceso a la tabla "cuentas" de la base de datos.
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, String> {
    Page<Cuenta> findByUsuarioUsername(String username, Pageable pageable);
    Page<Cuenta> findByNumCuenta(String numCuenta, Pageable pageable);
}
