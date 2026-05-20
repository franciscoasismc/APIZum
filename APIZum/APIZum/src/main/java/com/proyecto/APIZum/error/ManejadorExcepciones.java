package com.proyecto.APIZum.error;

// ====================================================================
// Maneja la transformación de excepciones en respuestas HTTP
// ====================================================================

import com.proyecto.APIZum.DTO.FormatoErroresDTO;
import com.proyecto.APIZum.error.excepciones.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorExcepciones {
    @ExceptionHandler({PeticionIncorrectaException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FormatoErroresDTO errorBadRequest(PeticionIncorrectaException e){
        return new FormatoErroresDTO(e.getMessage(), "BAD_REQUEST");}

    @ExceptionHandler({NoAutorizadoException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public FormatoErroresDTO errorUnauthorized(NoAutorizadoException e){
        return new FormatoErroresDTO(e.getMessage(), "UNAUTHORIZED");}

    @ExceptionHandler({AccesoDenegadoException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public FormatoErroresDTO errorForbidden(AccesoDenegadoException e){
        return new FormatoErroresDTO(e.getMessage(), "FORBIDDEN");}

    @ExceptionHandler({NoEncontradoException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public FormatoErroresDTO errorNotFound(NoEncontradoException e){
        return new FormatoErroresDTO(e.getMessage(), "NOT_FOUND");}

    @ExceptionHandler({EntidadImprocesableException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public FormatoErroresDTO errorUnprocessableEntity(EntidadImprocesableException e){
        return new FormatoErroresDTO(e.getMessage(), "UNPROCESSABLE_ENTITY");}

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public FormatoErroresDTO errorInternalServer(Exception e){
        return new FormatoErroresDTO(e.getMessage(), "INTERNAL_SERVER_ERROR");}
}