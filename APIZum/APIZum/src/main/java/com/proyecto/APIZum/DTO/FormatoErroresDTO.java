package com.proyecto.APIZum.DTO;

public class FormatoErroresDTO {
    private String error;
    private String code;

    public FormatoErroresDTO(String error, String code) {
        this.error = error;
        this.code = code;
    }

    public String getError() { return error; }
    public String getCode() { return code; }
}