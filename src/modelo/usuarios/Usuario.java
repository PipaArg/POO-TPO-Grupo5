package modelo.usuarios;

import modelo.enums.Rol;

public class Usuario {
    private String nombreCompleto;
    private String nombreUsuario;
    private String email;
    private Rol rol;
    private boolean activo;

    public Usuario(String nombreCompleto, String nombreUsuario, String email, Rol rol) {
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.rol = rol;
        this.activo = true;
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public String getNombreUsuario() { return nombreUsuario; }
    public boolean esSupervisor() { return rol == Rol.SUPERVISOR; }
    public boolean puedeAutorizar() { return rol == Rol.SUPERVISOR || rol == Rol.ADMINISTRADOR; }
    public boolean estaActivo() { return activo; }
    public String getEmail() { return email; }
    public Rol getRol() { return rol; }
}