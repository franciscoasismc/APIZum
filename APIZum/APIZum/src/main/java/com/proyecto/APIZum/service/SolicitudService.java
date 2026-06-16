package com.proyecto.APIZum.service;

import com.proyecto.APIZum.DTO.SolicitudDTO;
import com.proyecto.APIZum.error.excepciones.EntidadImprocesableException;
import com.proyecto.APIZum.error.excepciones.NoEncontradoException;
import com.proyecto.APIZum.model.*;
import com.proyecto.APIZum.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SolicitudService {

    private final SolicitudTransaccionRepository solicitudRepository;
    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionRepository notificacionRepository;

    public SolicitudService(SolicitudTransaccionRepository solicitudRepository,
                            CuentaRepository cuentaRepository,
                            TransaccionRepository transaccionRepository,
                            UsuarioRepository usuarioRepository,
                            NotificacionRepository notificacionRepository) {
        this.solicitudRepository = solicitudRepository;
        this.cuentaRepository = cuentaRepository;
        this.transaccionRepository = transaccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionRepository = notificacionRepository;
    }

    public Page<SolicitudDTO> listar(Authentication auth, Pageable pageable) {
        Cuenta cuenta = getCuenta(auth.getName());
        return solicitudRepository.findByParticipante(cuenta.getNumCuenta(), pageable).map(this::toDTO);
    }

    public Page<SolicitudDTO> listarPendientesRecibidas(Authentication auth, Pageable pageable) {
        Cuenta cuenta = getCuenta(auth.getName());
        return solicitudRepository.findPendientesRecibidas(cuenta.getNumCuenta(), pageable).map(this::toDTO);
    }

    @Transactional
    public SolicitudDTO crear(Authentication auth, String cuentaDestinoNum, BigDecimal cantidad, String descripcion) {
        Cuenta cuentaSolicitante = getCuenta(auth.getName());
        if (cuentaSolicitante.getNumCuenta().equals(cuentaDestinoNum)) {
            throw new EntidadImprocesableException("No puedes solicitarte dinero a ti mismo");
        }
        Cuenta cuentaDestino = cuentaRepository.findById(cuentaDestinoNum)
                .orElseThrow(() -> new NoEncontradoException("Cuenta no encontrada: " + cuentaDestinoNum));
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new EntidadImprocesableException("La cantidad debe ser mayor que 0");
        }

        SolicitudTransaccion solicitud = new SolicitudTransaccion(cuentaSolicitante, cuentaDestino, cantidad, descripcion);
        solicitudRepository.save(solicitud);

        // Notificar al destinatario
        if (cuentaDestino.getUsuario() != null) {
            notificacionRepository.save(new Notificacion(
                    cuentaDestino.getUsuario(), "SOLICITUD_RECIBIDA",
                    cuentaSolicitante.getUsuario().getNombre() + " te solicita " + cantidad + " €" +
                    (descripcion != null && !descripcion.isBlank() ? ": " + descripcion : ""),
                    solicitud.getId()));
        }

        return toDTO(solicitud);
    }

    @Transactional
    public SolicitudDTO responder(Authentication auth, Long id, boolean aceptar) {
        SolicitudTransaccion s = solicitudRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Solicitud no encontrada"));
        Cuenta miCuenta = getCuenta(auth.getName());

        if (!s.getCuentaDestino().getNumCuenta().equals(miCuenta.getNumCuenta())) {
            throw new EntidadImprocesableException("No tienes permiso para responder esta solicitud");
        }
        if (!"PENDIENTE".equals(s.getEstado())) {
            throw new EntidadImprocesableException("La solicitud ya fue procesada");
        }

        if (aceptar) {
            // Ejecutar transferencia
            Cuenta origen = cuentaRepository.findByIdWithLock(miCuenta.getNumCuenta())
                    .orElseThrow(() -> new NoEncontradoException("Cuenta no encontrada"));
            Cuenta destino = cuentaRepository.findByIdWithLock(s.getCuentaSolicitante().getNumCuenta())
                    .orElseThrow(() -> new NoEncontradoException("Cuenta no encontrada"));

            if (origen.getSaldo().compareTo(s.getCantidad()) < 0) {
                throw new EntidadImprocesableException("Saldo insuficiente para aceptar la solicitud");
            }
            origen.setSaldo(origen.getSaldo().subtract(s.getCantidad()));
            destino.setSaldo(destino.getSaldo().add(s.getCantidad()));
            transaccionRepository.save(new Transaccion(s.getCantidad(), "Solicitud #" + id + ": " + s.getDescripcion(), origen, destino));
            s.setEstado("ACEPTADA");

            // Notificar al solicitante
            if (destino.getUsuario() != null) {
                notificacionRepository.save(new Notificacion(
                        destino.getUsuario(), "SOLICITUD_ACEPTADA",
                        "Tu solicitud de " + s.getCantidad() + " € fue aceptada", id));
            }
        } else {
            s.setEstado("RECHAZADA");
            // Notificar al solicitante
            if (s.getCuentaSolicitante().getUsuario() != null) {
                notificacionRepository.save(new Notificacion(
                        s.getCuentaSolicitante().getUsuario(), "SOLICITUD_RECHAZADA",
                        "Tu solicitud de " + s.getCantidad() + " € fue rechazada", id));
            }
        }
        return toDTO(s);
    }

    @Transactional
    public SolicitudDTO cancelar(Authentication auth, Long id) {
        SolicitudTransaccion s = solicitudRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Solicitud no encontrada"));
        Cuenta miCuenta = getCuenta(auth.getName());
        if (!s.getCuentaSolicitante().getNumCuenta().equals(miCuenta.getNumCuenta())) {
            throw new EntidadImprocesableException("No tienes permiso para cancelar esta solicitud");
        }
        if (!"PENDIENTE".equals(s.getEstado())) {
            throw new EntidadImprocesableException("La solicitud ya fue procesada");
        }
        s.setEstado("CANCELADA");
        return toDTO(s);
    }

    private Cuenta getCuenta(String username) {
        Usuario u = usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado: " + username));
        if (u.getCuenta() == null) throw new NoEncontradoException("El usuario no tiene cuenta asociada");
        return u.getCuenta();
    }

    private SolicitudDTO toDTO(SolicitudTransaccion s) {
        String nombreSolicitante = (s.getCuentaSolicitante().getUsuario() != null)
                ? s.getCuentaSolicitante().getUsuario().getNombre() : "";
        String nombreDestino = (s.getCuentaDestino().getUsuario() != null)
                ? s.getCuentaDestino().getUsuario().getNombre() : "";
        return new SolicitudDTO(s.getId(),
                s.getCuentaSolicitante().getNumCuenta(), nombreSolicitante,
                s.getCuentaDestino().getNumCuenta(), nombreDestino,
                s.getCantidad(), s.getDescripcion(), s.getEstado(), s.getFecha());
    }
}
