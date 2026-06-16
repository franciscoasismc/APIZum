package com.proyecto.APIZum.DTO;

public class UsuarioRespuestaDTO {
    private String username;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private String repetirPassword;

    public UsuarioRespuestaDTO() {}

    public UsuarioRespuestaDTO(String username, String nombre, String apellidos,
                                String email, String password, String repetirPassword) {
        this.username = username;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.repetirPassword = repetirPassword;
    }

    public String getUsername(){ return username; }
    public void  setUsername(String username){ this.username = username; }

    public String getNombre(){ return nombre; }
    public void  setNombre(String nombre){ this.nombre = nombre; }

    public String getApellidos(){ return apellidos; }
    public void  setApellidos(String apellidos){ this.apellidos = apellidos; }

    public String getEmail(){ return email; }
    public void  setEmail(String email){ this.email = email; }

    public String getPassword(){ return password; }
    public void  setPassword(String password){ this.password = password; }

    public String getRepetirPassword(){ return repetirPassword; }
    public void  setRepetirPassword(String rp){ this.repetirPassword = rp; }
}
