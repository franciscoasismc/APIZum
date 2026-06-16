package com.proyecto.APIZum.repository;

import com.proyecto.APIZum.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Proporciona acceso a la tabla "transacciones" de la base de datos.
@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long>, JpaSpecificationExecutor<Transaccion> {

    // Suma el total enviado por una cuenta en un período (para límite diario).
    @Query("SELECT COALESCE(SUM(t.cantidad), 0) FROM Transaccion t " +
           "WHERE t.cuentaOrigen.numCuenta = :numCuenta " +
           "AND t.fecha >= :desde AND t.fecha < :hasta")
    BigDecimal sumCantidadByOrigenAndFechaBetween(
            @Param("numCuenta") String numCuenta,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}
