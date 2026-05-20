package com.proyecto.APIZum.service;

// ================================
//  Gestiona la lógica de negocio
// ================================

import com.proyecto.APIZum.DTO.CuentaRespuestaDTO;
import com.proyecto.APIZum.DTO.CuentaVerDTO;
import com.proyecto.APIZum.error.excepciones.NoEncontradoException;
import com.proyecto.APIZum.error.excepciones.PeticionIncorrectaException;
import com.proyecto.APIZum.model.Cuenta;
import com.proyecto.APIZum.model.Usuario;
import com.proyecto.APIZum.repository.CuentaRepository;
import com.proyecto.APIZum.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;

    public CuentaService(CuentaRepository cuentaRepository,
                         UsuarioRepository usuarioRepository) {
        this.cuentaRepository = cuentaRepository;
        this.usuarioRepository = usuarioRepository;
    }


    // ======================
    // USER
    // ======================

    // Devuelve la cuenta del usuario autenticado.
    @Transactional
    public CuentaVerDTO verCuentaPropia(Authentication auth) {
        Usuario usuario = buscarUsuario(auth.getName());
        Cuenta cuenta = usuario.getCuenta();
        if (cuenta == null) {
            throw new NoEncontradoException("El usuario no tiene ninguna cuenta asociada");
        }
        return mapVer(cuenta);
    }

    // Actualiza el saldo de la cuenta del usuario autenticado
    @Transactional
    public CuentaRespuestaDTO actualizarCuentaPropia(Authentication auth, BigDecimal nuevoSaldo) {
        Usuario usuario = buscarUsuario(auth.getName());
        Cuenta cuenta = usuario.getCuenta();
        if (cuenta == null) {
            throw new NoEncontradoException("El usuario no tiene ninguna cuenta asociada");
        }
        validarSaldo(nuevoSaldo);
        cuenta.setSaldo(nuevoSaldo);
        return mapRespuesta(cuenta);
    }


    // ======================
    // ADMIN
    // ======================

    // Devuelve cualquier cuenta por su numCuenta.
    public CuentaVerDTO verCuenta(String numCuenta) {
        return mapVer(buscarCuenta(numCuenta));
    }


    // ======================
    // Validaciones
    // ======================

    //  El saldo debe ser mayor o igual a 0
    private void validarSaldo(BigDecimal saldo) {
        if (saldo == null || saldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new PeticionIncorrectaException("El saldo no puede ser negativo");
        }
    }


    // ======================
    // Búsqueda común
    // ======================

    private Usuario buscarUsuario(String username) {
        return usuarioRepository.findById(username)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado: " + username));
    }

    private Cuenta buscarCuenta(String numCuenta) {
        return cuentaRepository.findById(numCuenta)
                .orElseThrow(() -> new NoEncontradoException("Cuenta no encontrada: " + numCuenta));
    }


    // ======================
    // Mapeos
    // ======================

    // Vista de actualización: solo numCuenta y saldo
    private CuentaRespuestaDTO mapRespuesta(Cuenta c) {
        return new CuentaRespuestaDTO(c.getNumCuenta(), c.getSaldo());
    }

    //  Vista completa: numCuenta, saldo y usuario propietario
    private CuentaVerDTO mapVer(Cuenta c) {
        return new CuentaVerDTO(c.getNumCuenta(), c.getSaldo(), c.getUsuario());
    }
}