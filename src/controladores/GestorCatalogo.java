package controladores;

import modelo.catalogo.Producto;
import java.util.ArrayList;
import java.util.List;

public class GestorCatalogo {

    // ── Singleton ──────────────────────────────────────────────────────
    private static GestorCatalogo instancia;

    private GestorCatalogo() {
        this.productos = new ArrayList<>();
    }

    public static GestorCatalogo getInstancia() {
        if (instancia == null) {
            instancia = new GestorCatalogo();
        }
        return instancia;
    }

    // ── Colecciones ────────────────────────────────────────────────────
    private List<Producto> productos;

    // ── Altas ──────────────────────────────────────────────────────────
    public void registrarProducto(Producto p) {
        productos.add(p);
    }

    // ── Búsquedas ──────────────────────────────────────────────────────
    public Producto buscarProducto(String codigo) {
        for (Producto p : productos) {
            if (p.getCodigo().equals(codigo)) return p;
        }
        return null;
    }

    // ── Consultas ──────────────────────────────────────────────────────
    public List<Producto> getProductos() {
        return productos;
    }
}
