package com.proyecto.APIZum.error.excepciones;

// ====================================================================================
// Excepción personalizada que permite recibir un mensaje de error personalizado
// ====================================================================================

public class EntidadImprocesableException extends RuntimeException {
    public EntidadImprocesableException(String message) {
        super(message);
    }
}