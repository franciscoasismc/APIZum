package com.proyecto.APIZum.error.excepciones;

// ====================================================================================
// Excepción personalizada que permite recibir un mensaje de error personalizado
// ====================================================================================

public class NoEncontradoException extends RuntimeException {
    public NoEncontradoException(String message) {
        super(message);
    }
}