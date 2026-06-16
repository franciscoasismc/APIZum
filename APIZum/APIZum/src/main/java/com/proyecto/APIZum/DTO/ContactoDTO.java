package com.proyecto.APIZum.DTO;

public class ContactoDTO {
    private Long id;
    private String contactoUsername;
    private String nombre;
    private String apellidos;
    private String email;
    private String numCuenta;
    private String alias;
    private boolean esFavorito;

    public ContactoDTO() {}
    public ContactoDTO(Long id, String contactoUsername, String nombre, String apellidos,
                       String email, String numCuenta, String alias, boolean esFavorito) {
        this.id = id;
        this.contactoUsername = contactoUsername;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.numCuenta = numCuenta;
        this.alias = alias;
        this.esFavorito = esFavorito;
    }

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
    public String getContactoUsername(){ return contactoUsername; }
    public void setContactoUsername(String u){ this.contactoUsername = u; }
    public String getNombre(){ return nombre; }
    public void setNombre(String n){ this.nombre = n; }
    public String getApellidos(){ return apellidos; }
    public void setApellidos(String a){ this.apellidos = a; }
    public String getEmail(){ return email; }
    public void setEmail(String e){ this.email = e; }
    public String getNumCuenta(){ return numCuenta; }
    public void setNumCuenta(String n){ this.numCuenta = n; }
    public String getAlias(){ return alias; }
    public void setAlias(String a){ this.alias = a; }
    public boolean isEsFavorito(){ return esFavorito; }
    public void setEsFavorito(boolean f){ this.esFavorito = f; }
}
