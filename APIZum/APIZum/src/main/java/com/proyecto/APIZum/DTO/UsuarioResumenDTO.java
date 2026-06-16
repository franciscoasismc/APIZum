package com.proyecto.APIZum.DTO;

// DTO reducido para búsqueda de destinatarios (no expone datos sensibles)
public class UsuarioResumenDTO {
    private String username;
    private String nombre;
    private String apellidos;
    private String numCuenta;

    public UsuarioResumenDTO() {}
    public UsuarioResumenDTO(String username, String nombre, String apellidos, String numCuenta) {
        this.username = username;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.numCuenta = numCuenta;
    }

    public String getUsername(){ return username; }
    public void setUsername(String u){ this.username = u; }
    public String getNombre(){ return nombre; }
    public void setNombre(String n){ this.nombre = n; }
    public String getApellidos(){ return apellidos; }
    public void setApellidos(String a){ this.apellidos = a; }
    public String getNumCuenta(){ return numCuenta; }
    public void setNumCuenta(String n){ this.numCuenta = n; }
}
