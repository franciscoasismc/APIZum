package com.proyecto.APIZum.error.excepciones;

// ====================================================================================
// Excepción personalizada que permite recibir un mensaje de error personalizado
// ====================================================================================

public class PeticionIncorrectaException extends RuntimeException {
    public PeticionIncorrectaException(String message) {
        super(message);
    }
}