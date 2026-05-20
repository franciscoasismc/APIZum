package com.proyecto.APIZum.error.excepciones;

// ====================================================================================
// Excepción personalizada que permite recibir un mensaje de error personalizado
// ====================================================================================

public class NoAutorizadoException extends RuntimeException {
    public NoAutorizadoException(String message) {
        super(message);
    }
}