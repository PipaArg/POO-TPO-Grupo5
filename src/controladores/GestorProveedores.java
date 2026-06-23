package controladores;

import modelo.impuestos.Impuesto;
import modelo.proveedores.CertificadoNoRetencion;
import modelo.proveedores.CuentaCorriente;
import modelo.proveedores.Proveedor;
import modelo.proveedores.Rubro;
import java.util.ArrayList;
import java.util.List;

public class GestorProveedores {

    // ── Singleton ──────────────────────────────────────────────────────
    private static GestorProveedores instancia;

    private GestorProveedores() {
        this.proveedores = new ArrayList<>();
        this.rubros = new ArrayList<>();
    }

    public static GestorProveedores getInstancia() {
        if (instancia == null) {
            instancia = new GestorProveedores();
        }
        return instancia;
    }

    // ── Colecciones ────────────────────────────────────────────────────
    private List<Proveedor> proveedores;
    private List<Rubro> rubros;

    // ── Altas ──────────────────────────────────────────────────────────
    public void registrarProveedor(Proveedor p) {
        proveedores.add(p);
    }

    public void registrarRubro(Rubro r) {
        rubros.add(r);
    }

    // ── Búsquedas ──────────────────────────────────────────────────────
    public Proveedor buscarProveedor(String cuit) {
        for (Proveedor p : proveedores) {
            if (p.getCuit().equals(cuit)) return p;
        }
        return null;
    }

    public Rubro buscarRubro(String codigo) {
        for (Rubro r : rubros) {
            if (r.getCodigo().equals(codigo)) return r;
        }
        return null;
    }

    // ── Cuenta Corriente ───────────────────────────────────────────────
    public CuentaCorriente obtenerCuentaCorriente(Proveedor proveedor) {
        return proveedor.getCuentaCorriente();
    }

    // ── Consultas ──────────────────────────────────────────────────────
    public List<Proveedor> getProveedores() {
        return proveedores;
    }

    public List<Rubro> getRubros() {
        return rubros;
    }
}
