package com.proyecto.APIZum.service;

// ================================
//  Gestiona la lógica de negocio
// ================================

import com.proyecto.APIZum.DTO.TransaccionRespuestaDTO;
import com.proyecto.APIZum.DTO.TransaccionVerDTO;
import com.proyecto.APIZum.error.excepciones.EntidadImprocesableException;
import com.proyecto.APIZum.error.excepciones.NoEncontradoException;
import com.proyecto.APIZum.error.excepciones.PeticionIncorrectaException;
import com.proyecto.APIZum.model.*;
import com.proyecto.APIZum.repository.*;
import com.proyecto.APIZum.util.IBANValidator;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TransaccionService {

    // Límite diario de transferencias por cuenta (configurable)
    private static final BigDecimal LIMITE_DIARIO = new BigDecimal("1000.00");

    private final TransaccionRepository transaccionRepository;
    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionRepository notificacionRepository;

    public TransaccionService(TransaccionRepository transaccionRepository,
                              CuentaRepository cuentaRepository,
                              UsuarioRepository usuarioRepository,
                              NotificacionRepository notificacionRepository) {
        this.transaccionRepository = transaccionRepository;
        this.cuentaRepository = cuentaRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionRepository = notificacionRepository;
    }


    // ======================
    // USER
    // ======================

    // Realiza una transferencia con bloqueo pesimista para evitar condiciones de carrera.
    @Transactional
    public TransaccionRespuestaDTO crearTransaccion(Authentication auth,
                                                    String numCuentaDestino,
                                                    BigDecimal cantidad,
                                                    String descripcion) {
        Usuario usuario = buscarUsuario(auth.getName());
        Cuenta cuentaOrigen = usuario.getCuenta();
        if (cuentaOrigen == null) {
            throw new EntidadImprocesableException("El usuario no tiene una cuenta asociada");
        }

        if (numCuentaDestino == null || numCuentaDestino.isBlank()) {
            throw new PeticionIncorrectaException("Debe indicar una cuenta destino");
        }
        // Validación IBAN completa
        if (!IBANValidator.isValidSpanish(numCuentaDestino)) {
            throw new PeticionIncorrectaException(
                "La cuenta destino no es un IBAN español válido (ISO 13616)");
        }
        if (cuentaOrigen.getNumCuenta().equals(numCuentaDestino)) {
            throw new PeticionIncorrectaException("La cuenta origen y la cuenta destino no pueden ser la misma");
        }

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PeticionIncorrectaException("La cantidad debe ser mayor que 0");
        }

        // Comprobación del límite diario
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia    = inicioDia.plusDays(1);
        BigDecimal totalHoy = transaccionRepository.sumCantidadByOrigenAndFechaBetween(
                cuentaOrigen.getNumCuenta(), inicioDia, finDia);
        if (totalHoy.add(cantidad).compareTo(LIMITE_DIARIO) > 0) {
            BigDecimal restante = LIMITE_DIARIO.subtract(totalHoy);
            throw new EntidadImprocesableException(
                "Límite diario de " + LIMITE_DIARIO + " € superado. Puedes transferir " +
                (restante.compareTo(BigDecimal.ZERO) > 0 ? restante + " €" : "0 €") + " más hoy.");
        }

        // Bloqueo pesimista: adquiere lock sobre ambas cuentas para evitar race conditions
        Cuenta cuentaOrigenLocked = cuentaRepository.findByIdWithLock(cuentaOrigen.getNumCuenta())
                .orElseThrow(() -> new NoEncontradoException("Cuenta origen no encontrada"));
        Cuenta cuentaDestinoLocked = cuentaRepository.findByIdWithLock(numCuentaDestino)
                .orElseThrow(() -> new NoEncontradoException("Cuenta destino no encontrada: " + numCuentaDestino));

        if (cuentaOrigenLocked.getSaldo().compareTo(cantidad) < 0) {
            throw new EntidadImprocesableException(
                    "Saldo insuficiente. Saldo actual: " + cuentaOrigenLocked.getSaldo() +
                    ", cantidad solicitada: " + cantidad);
        }

        cuentaOrigenLocked.setSaldo(cuentaOrigenLocked.getSaldo().subtract(cantidad));
        cuentaDestinoLocked.setSaldo(cuentaDestinoLocked.getSaldo().add(cantidad));

        Transaccion transaccion = new Transaccion(cantidad, descripcion, cuentaOrigenLocked, cuentaDestinoLocked);
        transaccionRepository.save(transaccion);

        // Notificaciones
        crearNotificacion(cuentaOrigenLocked.getUsuario(), "TRANSFERENCIA_ENVIADA",
                "Has enviado " + cantidad + " € a la cuenta " + numCuentaDestino +
                (descripcion != null && !descripcion.isBlank() ? " (" + descripcion + ")" : ""),
                transaccion.getIdTransaccion());
        if (cuentaDestinoLocked.getUsuario() != null) {
            crearNotificacion(cuentaDestinoLocked.getUsuario(), "TRANSFERENCIA_RECIBIDA",
                    "Has recibido " + cantidad + " € desde la cuenta " + cuentaOrigenLocked.getNumCuenta() +
                    (descripcion != null && !descripcion.isBlank() ? " (" + descripcion + ")" : ""),
                    transaccion.getIdTransaccion());
        }

        return mapRespuesta(transaccion);
    }

    // Devuelve el historial del usuario autenticado (origen o destino).
    public Page<TransaccionVerDTO> verHistorialPropio(Authentication auth, Pageable pageable) {
        Usuario usuario = buscarUsuario(auth.getName());
        Cuenta cuenta = usuario.getCuenta();
        if (cuenta == null) {
            throw new NoEncontradoException("El usuario no tiene ninguna cuenta asociada");
        }
        String numCuenta = cuenta.getNumCuenta();
        return transaccionRepository.findAll(
                (root, query, cb) -> cb.or(
                        cb.equal(root.get("cuentaOrigen").get("numCuenta"), numCuenta),
                        cb.equal(root.get("cuentaDestino").get("numCuenta"), numCuenta)
                ),
                pageable
        ).map(t -> mapVerConDireccion(t, numCuenta));
    }


    // ======================
    // ADMIN
    // ======================

    public Page<TransaccionVerDTO> verHistorialPorUsername(String username, Pageable pageable) {
        Usuario usuario = buscarUsuario(username);
        Cuenta cuenta = usuario.getCuenta();
        if (cuenta == null) {
            throw new NoEncontradoException("El usuario no tiene ninguna cuenta asociada");
        }
        String numCuenta = cuenta.getNumCuenta();
        return transaccionRepository.findAll(
                (root, query, cb) -> cb.or(
                        cb.equal(root.get("cuentaOrigen").get("numCuenta"), numCuenta),
                        cb.equal(root.get("cuentaDestino").get("numCuenta"), numCuenta)
                ),
                pageable
        ).map(this::mapVer);
    }

    public Page<TransaccionVerDTO> verHistorialPorCuenta(String numCuenta, Pageable pageable) {
        if (!cuentaRepository.existsById(numCuenta)) {
            throw new NoEncontradoException("Cuenta no encontrada: " + numCuenta);
        }
        return transaccionRepository.findAll(
                (root, query, cb) -> cb.or(
                        cb.equal(root.get("cuentaOrigen").get("numCuenta"), numCuenta),
                        cb.equal(root.get("cuentaDestino").get("numCuenta"), numCuenta)
                ),
                pageable
        ).map(this::mapVer);
    }

    public TransaccionVerDTO verTransaccion(Long idTransaccion) {
        Transaccion transaccion = transaccionRepository.findById(idTransaccion)
                .orElseThrow(() -> new NoEncontradoException("Transacción no encontrada: " + idTransaccion));
        return mapVer(transaccion);
    }


    // ======================
    // Notificaciones internas
    // ======================

    private void crearNotificacion(Usuario usuario, String tipo, String mensaje, Long referenciaId) {
        if (usuario == null) return;
        notificacionRepository.save(new Notificacion(usuario, tipo, mensaje, referenciaId));
    }


    // ======================
    // Búsqueda común
    // ======================

    private Usuario buscarUsuario(String username) {
        return usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado: " + username));
    }


    // ======================
    // Mapeos
    // ======================

    private TransaccionRespuestaDTO mapRespuesta(Transaccion t) {
        return new TransaccionRespuestaDTO(
                t.getCantidad(), t.getDescripcion(), t.getFecha(),
                t.getCuentaOrigen(), t.getCuentaDestino());
    }

    private TransaccionVerDTO mapVer(Transaccion t) {
        return new TransaccionVerDTO(
                t.getIdTransaccion(), t.getCantidad(), t.getDescripcion(), t.getFecha(),
                t.getCuentaOrigen(), t.getCuentaDestino());
    }

    // Incluye dirección (ENVIADA/RECIBIDA) desde la perspectiva de la cuenta propia
    private TransaccionVerDTO mapVerConDireccion(Transaccion t, String miCuenta) {
        TransaccionVerDTO dto = mapVer(t);
        // La dirección se determina en frontend con cuentaOrigen/cuentaDestino
        return dto;
    }
}
