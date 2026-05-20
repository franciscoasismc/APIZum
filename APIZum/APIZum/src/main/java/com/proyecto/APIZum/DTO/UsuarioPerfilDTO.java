package com.proyecto.APIZum.DTO;

import com.proyecto.APIZum.model.Cuenta;

public class UsuarioPerfilDTO {

    private String username;
    private String nombre;
    private String apellidos;
    private String email;
    private String rol;
    private String password;
    private Cuenta numCuenta;

    public UsuarioPerfilDTO(String username, String nombre, String apellidos,
                             String email, String rol, String password, Cuenta numCuenta) {
        this.username = username;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.rol = rol;
        this.password = password;
        this.numCuenta = numCuenta;
    }

    public String getUsername(){ return username; }
    public void setUsername(String username){ this.username = username; }

    public String getNombre(){ return nombre; }
    public void setNombre(String nombre){ this.nombre = nombre; }

    public String getApellidos(){ return apellidos; }
    public void setApellidos(String apellidos){ this.apellidos = apellidos; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getRol(){ return rol; }
    public void setRol(String rol){ this.rol = rol; }

    public String getPassword(){ return password; }
    public void setPassword(String password){ this.password = password; }

    public Cuenta getNumCuenta(){ return numCuenta; }
    public void setNumCuenta(Cuenta numCuenta){ this.numCuenta = numCuenta; }
}