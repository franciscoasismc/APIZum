package com.proyecto.APIZum.model;

import jakarta.persistence.*;

// ================================================================
//  Entidad Usuario
// ================================================================

@Entity
@Table(name = "usuarios")
public class Usuario {

    // Clave primaria: 9 dígitos numéricos (p. ej. "123456789").
    @Id
    @Column(length = 9)
    private String username;

    @Column(nullable = false)
    private String nombre;

    private String apellidos;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    //  @Transient indica que no se almacena en la base de datos.
    @Transient
    private String repetirPassword;

    @Column(nullable = false)
    private String rol = "USER";

    // Soft delete: los usuarios nunca se eliminan físicamente en un sistema financiero.
    @Column(nullable = false)
    private boolean activo = true;

    /* Relación 1:1
        Las operaciones sobre Usuario se propagan a Cuenta y se
        eliminan de la base de datos.
        La cuenta no carga hasta acceder a ella. */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "numCuenta", unique = true)
    private Cuenta cuenta;


    // Constructores

    public Usuario() {}

    public Usuario(String username, String nombre, String apellidos, String email,
                   String password, String rol, Cuenta cuenta) {
        this.username = username;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.rol = (rol == null || rol.isBlank()) ? "USER" : rol.toUpperCase();
        this.cuenta = cuenta;
        this.activo = true;
    }


    // Getters y Setters

    public String getUsername(){ return username; }
    public void setUsername(String username){ this.username = username; }

    public String getNombre(){ return nombre; }
    public void setNombre(String nombre){ this.nombre = nombre; }

    public String getApellidos(){ return apellidos; }
    public void setApellidos(String apellidos){ this.apellidos = apellidos; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getPassword(){ return password; }
    public void setPassword(String password){ this.password = password; }

    public String getRepetirPassword(){ return repetirPassword; }
    public void setRepetirPassword(String rp){ this.repetirPassword = rp; }

    public String getRol(){ return rol; }
    public void setRol(String rol){ this.rol = rol; }

    public boolean isActivo(){ return activo; }
    public void setActivo(boolean activo){ this.activo = activo; }

    public Cuenta getCuenta(){ return cuenta; }
    public void setCuenta(Cuenta cuenta){ this.cuenta = cuenta; }
}
