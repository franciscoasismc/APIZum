package com.proyecto.APIZum.repository;

import com.proyecto.APIZum.model.SolicitudTransaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudTransaccionRepository extends JpaRepository<SolicitudTransaccion, Long> {

    // Solicitudes donde el usuario autenticado es solicitante o destinatario
    @Query("SELECT s FROM SolicitudTransaccion s WHERE " +
           "s.cuentaSolicitante.numCuenta = :numCuenta OR s.cuentaDestino.numCuenta = :numCuenta " +
           "ORDER BY s.fecha DESC")
    Page<SolicitudTransaccion> findByParticipante(@Param("numCuenta") String numCuenta, Pageable pageable);

    // Solicitudes pendientes recibidas (el usuario debe pagar)
    @Query("SELECT s FROM SolicitudTransaccion s WHERE " +
           "s.cuentaDestino.numCuenta = :numCuenta AND s.estado = 'PENDIENTE' " +
           "ORDER BY s.fecha DESC")
    Page<SolicitudTransaccion> findPendientesRecibidas(@Param("numCuenta") String numCuenta, Pageable pageable);
}
