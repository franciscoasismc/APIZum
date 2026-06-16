package com.proyecto.APIZum.model;

import jakarta.persistence.*;

// ================================================================
//  Entidad Contacto — Relación N:M entre usuarios
// ================================================================
//  Un usuario puede guardar a otros usuarios como contactos.
//  Relación N:M: un usuario tiene muchos contactos,
//                un usuario puede aparecer en muchas listas de contactos.
//
//  Se modela como entidad intermedia (en lugar de @ManyToMany puro)
//  para poder añadir atributos propios (alias, esFavorito).
// ================================================================

@Entity
@Table(
    name = "contactos",
    uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_username", "contacto_username"})
)
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El usuario que guarda el contacto
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_username", nullable = false)
    private Usuario usuario;

    // El usuario guardado como contacto
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contacto_username", nullable = false)
    private Usuario contacto;

    private String alias;

    @Column(nullable = false)
    private boolean esFavorito = false;


    // Constructores

    public Contacto() {}

    public Contacto(Usuario usuario, Usuario contacto, String alias) {
        this.usuario = usuario;
        this.contacto = contacto;
        this.alias = alias;
        this.esFavorito = false;
    }


    // Getters y Setters

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public Usuario getUsuario(){ return usuario; }
    public void setUsuario(Usuario usuario){ this.usuario = usuario; }

    public Usuario getContacto(){ return contacto; }
    public void setContacto(Usuario contacto){ this.contacto = contacto; }

    public String getAlias(){ return alias; }
    public void setAlias(String alias){ this.alias = alias; }

    public boolean isEsFavorito(){ return esFavorito; }
    public void setEsFavorito(boolean esFavorito){ this.esFavorito = esFavorito; }
}
