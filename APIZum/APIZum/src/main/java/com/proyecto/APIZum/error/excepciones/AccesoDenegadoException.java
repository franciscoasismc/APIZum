package com.proyecto.APIZum.error.excepciones;

// ====================================================================================
// Excepción personalizada que permite recibir un mensaje de error personalizado
// ====================================================================================

public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String message) {
        super(message);
    }
}