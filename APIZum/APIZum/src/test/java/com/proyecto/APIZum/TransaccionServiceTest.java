package com.proyecto.APIZum;

import com.proyecto.APIZum.error.excepciones.EntidadImprocesableException;
import com.proyecto.APIZum.error.excepciones.NoEncontradoException;
import com.proyecto.APIZum.error.excepciones.PeticionIncorrectaException;
import com.proyecto.APIZum.model.Cuenta;
import com.proyecto.APIZum.model.Usuario;
import com.proyecto.APIZum.repository.CuentaRepository;
import com.proyecto.APIZum.repository.NotificacionRepository;
import com.proyecto.APIZum.repository.TransaccionRepository;
import com.proyecto.APIZum.repository.UsuarioRepository;
import com.proyecto.APIZum.service.TransaccionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ================================================================
//  Tests unitarios para TransaccionService
//  Usan Mockito (sin contexto Spring ni base de datos real).
// ================================================================

@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    @Mock TransaccionRepository transaccionRepository;
    @Mock CuentaRepository cuentaRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock NotificacionRepository notificacionRepository;
    @Mock Authentication auth;

    @InjectMocks
    TransaccionService transaccionService;

    // IBAN español de prueba válido (módulo 97 = 1)
    private static final String IBAN_ORIGEN  = "ES9121000418450200051332";
    private static final String IBAN_DESTINO = "ES7921000813610123456789";

    private Usuario usuarioOrigen;
    private Cuenta cuentaOrigen;
    private Cuenta cuentaDestino;

    @BeforeEach
    void setUp() {
        usuarioOrigen = new Usuario();
        usuarioOrigen.setUsername("123456789");
        usuarioOrigen.setNombre("Test");
        usuarioOrigen.setActivo(true);

        cuentaOrigen = new Cuenta();
        cuentaOrigen.setNumCuenta(IBAN_ORIGEN);
        cuentaOrigen.setSaldo(new BigDecimal("500.00"));
        cuentaOrigen.setUsuario(usuarioOrigen);
        usuarioOrigen.setCuenta(cuentaOrigen);

        cuentaDestino = new Cuenta();
        cuentaDestino.setNumCuenta(IBAN_DESTINO);
        cuentaDestino.setSaldo(new BigDecimal("100.00"));

        when(auth.getName()).thenReturn("123456789");
        when(usuarioRepository.findByUsernameAndActivoTrue("123456789"))
                .thenReturn(Optional.of(usuarioOrigen));
    }

    // ── 1. Caso feliz ─────────────────────────────────────────────

    @Test
    @DisplayName("Transferencia correcta: descuenta origen y acredita destino")
    void transferencia_casoFeliz() {
        BigDecimal cantidad = new BigDecimal("100.00");

        when(transaccionRepository.sumCantidadByOrigenAndFechaBetween(
                eq(IBAN_ORIGEN), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(BigDecimal.ZERO);
        when(cuentaRepository.findByIdWithLock(IBAN_ORIGEN)).thenReturn(Optional.of(cuentaOrigen));
        when(cuentaRepository.findByIdWithLock(IBAN_DESTINO)).thenReturn(Optional.of(cuentaDestino));
        when(transaccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        transaccionService.crearTransaccion(auth, IBAN_DESTINO, cantidad, "Prueba");

        assertThat(cuentaOrigen.getSaldo()).isEqualByComparingTo("400.00");
        assertThat(cuentaDestino.getSaldo()).isEqualByComparingTo("200.00");
        verify(transaccionRepository).save(any());
    }

    // ── 2. Saldo insuficiente ─────────────────────────────────────

    @Test
    @DisplayName("Saldo insuficiente lanza EntidadImprocesableException")
    void transferencia_saldoInsuficiente() {
        BigDecimal cantidad = new BigDecimal("999.00"); // saldo = 500

        when(transaccionRepository.sumCantidadByOrigenAndFechaBetween(
                eq(IBAN_ORIGEN), any(), any())).thenReturn(BigDecimal.ZERO);
        when(cuentaRepository.findByIdWithLock(IBAN_ORIGEN)).thenReturn(Optional.of(cuentaOrigen));
        when(cuentaRepository.findByIdWithLock(IBAN_DESTINO)).thenReturn(Optional.of(cuentaDestino));

        assertThatThrownBy(() ->
                transaccionService.crearTransaccion(auth, IBAN_DESTINO, cantidad, null))
                .isInstanceOf(EntidadImprocesableException.class)
                .hasMessageContaining("Saldo insuficiente");

        verify(transaccionRepository, never()).save(any());
    }

    // ── 3. Cuenta destino inexistente ─────────────────────────────

    @Test
    @DisplayName("Cuenta destino inexistente lanza NoEncontradoException")
    void transferencia_cuentaDestinoInexistente() {
        String ibanInexistente = "ES8200012345678901234567";

        when(transaccionRepository.sumCantidadByOrigenAndFechaBetween(
                eq(IBAN_ORIGEN), any(), any())).thenReturn(BigDecimal.ZERO);
        when(cuentaRepository.findByIdWithLock(IBAN_ORIGEN)).thenReturn(Optional.of(cuentaOrigen));
        when(cuentaRepository.findByIdWithLock(ibanInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transaccionService.crearTransaccion(auth, ibanInexistente, new BigDecimal("10.00"), null))
                .isInstanceOf(NoEncontradoException.class)
                .hasMessageContaining("Cuenta destino no encontrada");
    }

    // ── 4. Límite diario superado ─────────────────────────────────

    @Test
    @DisplayName("Superar el límite diario de 1000 € lanza EntidadImprocesableException")
    void transferencia_limiteDiarioSuperado() {
        // Ya se han enviado 900 € hoy; se intenta enviar 200 más
        when(transaccionRepository.sumCantidadByOrigenAndFechaBetween(
                eq(IBAN_ORIGEN), any(), any())).thenReturn(new BigDecimal("900.00"));

        assertThatThrownBy(() ->
                transaccionService.crearTransaccion(auth, IBAN_DESTINO, new BigDecimal("200.00"), null))
                .isInstanceOf(EntidadImprocesableException.class)
                .hasMessageContaining("Límite diario");
    }

    // ── 5. Misma cuenta origen y destino ─────────────────────────

    @Test
    @DisplayName("Transferir a la misma cuenta lanza PeticionIncorrectaException")
    void transferencia_mismaCuenta() {
        assertThatThrownBy(() ->
                transaccionService.crearTransaccion(auth, IBAN_ORIGEN, new BigDecimal("10.00"), null))
                .isInstanceOf(PeticionIncorrectaException.class)
                .hasMessageContaining("no pueden ser la misma");
    }

    // ── 6. IBAN inválido ─────────────────────────────────────────

    @Test
    @DisplayName("IBAN de destino inválido lanza PeticionIncorrectaException")
    void transferencia_ibanInvalido() {
        assertThatThrownBy(() ->
                transaccionService.crearTransaccion(auth, "ES0000000000000000000000", new BigDecimal("10.00"), null))
                .isInstanceOf(PeticionIncorrectaException.class)
                .hasMessageContaining("IBAN");
    }

    // ── 7. Cantidad cero o negativa ──────────────────────────────

    @Test
    @DisplayName("Cantidad <= 0 lanza PeticionIncorrectaException")
    void transferencia_cantidadInvalida() {
        assertThatThrownBy(() ->
                transaccionService.crearTransaccion(auth, IBAN_DESTINO, BigDecimal.ZERO, null))
                .isInstanceOf(PeticionIncorrectaException.class)
                .hasMessageContaining("mayor que 0");
    }
}
