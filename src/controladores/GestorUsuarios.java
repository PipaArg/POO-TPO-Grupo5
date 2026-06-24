package controladores;

import modelo.enums.Rol;
import modelo.usuarios.Usuario;
import java.util.ArrayList;
import java.util.List;

public class GestorUsuarios {

    // ── Singleton ──────────────────────────────────────────────────────
    private static GestorUsuarios instancia;

    private GestorUsuarios() {
        this.usuarios = new ArrayList<>();
    }

    public static GestorUsuarios getInstancia() {
        if (instancia == null) {
            instancia = new GestorUsuarios();
        }
        return instancia;
    }

    // ── Colecciones ────────────────────────────────────────────────────
    private List<Usuario> usuarios;

    // ── Altas ──────────────────────────────────────────────────────────
    public void registrarUsuario(Usuario u) {
        usuarios.add(u);
    }

    // ── Búsquedas ──────────────────────────────────────────────────────
    public Usuario buscarUsuario(String nombreUsuario) {
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equals(nombreUsuario)) return u;
        }
        return null;
    }

    public Usuario buscarSupervisor() {
        for (Usuario u : usuarios) {
            if (u.esSupervisor() && u.estaActivo()) return u;
        }
        return null;
    }

    // ── Consultas ──────────────────────────────────────────────────────
    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}