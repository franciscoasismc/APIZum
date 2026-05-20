package com.proyecto.APIZum.service;

// ================================
//  Gestiona la lógica de negocio
// ================================

import com.proyecto.APIZum.DTO.TransaccionRespuestaDTO;
import com.proyecto.APIZum.DTO.TransaccionVerDTO;
import com.proyecto.APIZum.error.excepciones.EntidadImprocesableException;
import com.proyecto.APIZum.error.excepciones.NoEncontradoException;
import com.proyecto.APIZum.error.excepciones.PeticionIncorrectaException;
import com.proyecto.APIZum.model.Cuenta;
import com.proyecto.APIZum.model.Transaccion;
import com.proyecto.APIZum.model.Usuario;
import com.proyecto.APIZum.repository.CuentaRepository;
import com.proyecto.APIZum.repository.TransaccionRepository;
import com.proyecto.APIZum.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;

    public TransaccionService(TransaccionRepository transaccionRepository,
                              CuentaRepository cuentaRepository,
                              UsuarioRepository usuarioRepository) {
        this.transaccionRepository = transaccionRepository;
        this.cuentaRepository = cuentaRepository;
        this.usuarioRepository = usuarioRepository;
    }


    // ======================
    // USER
    // ======================

    // Realiza una transferencia desde la cuenta del usuario autenticado.
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
        if (cuentaOrigen.getNumCuenta().equals(numCuentaDestino)) {
            throw new PeticionIncorrectaException("La cuenta origen y la cuenta destino no pueden ser la misma");
        }
        Cuenta cuentaDestino = cuentaRepository.findById(numCuentaDestino)
                .orElseThrow(() -> new NoEncontradoException("Cuenta destino no encontrada: " + numCuentaDestino));

        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PeticionIncorrectaException("La cantidad debe ser mayor que 0");
        }
        if (cuentaOrigen.getSaldo().compareTo(cantidad) < 0) {
            throw new EntidadImprocesableException(
                    "Saldo insuficiente. Saldo actual: " + cuentaOrigen.getSaldo() + ", cantidad solicitada: " + cantidad
            );
        }

        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(cantidad));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(cantidad));

        Transaccion transaccion = new Transaccion(cantidad, descripcion, cuentaOrigen, cuentaDestino);
        transaccionRepository.save(transaccion);

        return mapRespuesta(transaccion);
    }

    //  Devuelve el historial del usuario autenticado (origen o destino).
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
        ).map(this::mapVer);
    }


    // ======================
    // ADMIN
    // ======================

    //  Devuelve el historial de transacciones de un usuario concreto (por username).
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

    // Devuelve el historial de transacciones de una cuenta concreta (por numCuenta).
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

    // Devuelve una transacción concreta por su id.
    public TransaccionVerDTO verTransaccion(Long idTransaccion) {
        Transaccion transaccion = transaccionRepository.findById(idTransaccion)
                .orElseThrow(() -> new NoEncontradoException("Transacción no encontrada: " + idTransaccion));
        return mapVer(transaccion);
    }


    // ======================
    // Búsqueda común
    // ======================

    private Usuario buscarUsuario(String username) {
        return usuarioRepository.findById(username)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado: " + username));
    }


    // ======================
    // Mapeos
    // ======================

    // Vista de creación: sin id de transacción
    private TransaccionRespuestaDTO mapRespuesta(Transaccion t) {
        return new TransaccionRespuestaDTO(
                t.getCantidad(),
                t.getDescripcion(),
                t.getFecha(),
                t.getCuentaOrigen(),
                t.getCuentaDestino()
        );
    }

    // Vista completa: incluye id de transacción
    private TransaccionVerDTO mapVer(Transaccion t) {
        return new TransaccionVerDTO(
                t.getIdTransaccion(),
                t.getCantidad(),
                t.getDescripcion(),
                t.getFecha(),
                t.getCuentaOrigen(),
                t.getCuentaDestino()
        );
    }
}