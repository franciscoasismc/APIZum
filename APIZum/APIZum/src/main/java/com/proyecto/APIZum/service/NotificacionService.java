package com.proyecto.APIZum.service;

import com.proyecto.APIZum.DTO.NotificacionDTO;
import com.proyecto.APIZum.repository.NotificacionRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public Page<NotificacionDTO> listar(Authentication auth, Pageable pageable) {
        return notificacionRepository
                .findByUsuarioUsernameOrderByFechaDesc(auth.getName(), pageable)
                .map(n -> new NotificacionDTO(n.getId(), n.getTipo(), n.getMensaje(),
                        n.isLeida(), n.getFecha(), n.getReferenciaId()));
    }

    public Map<String, Long> contarNoLeidas(Authentication auth) {
        long count = notificacionRepository.countByUsuarioUsernameAndLeidaFalse(auth.getName());
        return Map.of("noLeidas", count);
    }

    @Transactional
    public void marcarTodasLeidas(Authentication auth) {
        notificacionRepository.marcarTodasLeidas(auth.getName());
    }
}
