package com.proyecto.APIZum.repository;

import com.proyecto.APIZum.model.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    Page<Notificacion> findByUsuarioUsernameOrderByFechaDesc(String username, Pageable pageable);

    long countByUsuarioUsernameAndLeidaFalse(String username);

    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.usuario.username = :username")
    void marcarTodasLeidas(@Param("username") String username);
}
