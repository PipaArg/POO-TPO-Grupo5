package controladores;

import modelo.catalogo.Producto;
import modelo.comprobantes.ResultadoValidacion;
import modelo.ordenes.OrdenDeCompra;
import modelo.proveedores.Proveedor;
import modelo.usuarios.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GestorOC {

    // ── Singleton ──────────────────────────────────────────────────────
    private static GestorOC instancia;

    private GestorOC() {
        this.ordenesDeCompra = new ArrayList<>();
    }

    public static GestorOC getInstancia() {
        if (instancia == null) {
            instancia = new GestorOC();
        }
        return instancia;
    }

    // ── Colecciones ────────────────────────────────────────────────────
    private List<OrdenDeCompra> ordenesDeCompra;

    // ── Operaciones de negocio ─────────────────────────────────────────
    public OrdenDeCompra crearOrdenDeCompra(Proveedor proveedor) {
        String numero = "OC-" + (ordenesDeCompra.size() + 1);
        return new OrdenDeCompra(numero, proveedor);
    }

    public void agregarItemAOC(OrdenDeCompra oc, Producto producto,
                               int cantidad, double precio) {
        oc.crearItem(producto, cantidad, precio);
    }

    public ResultadoValidacion validarTopeDeDeuda(OrdenDeCompra oc) {
        Proveedor p = oc.getProveedor();
        double deudaVigente = p.getDeudaVigente();
        double totalOC = oc.calcularTotalOC();
        double topeMaximo = p.getTopeMaximoDeuda();

        if (deudaVigente + totalOC <= topeMaximo) {
            return new ResultadoValidacion(true, "Dentro del tope de deuda");
        }
        return new ResultadoValidacion(false, "Supera el tope máximo de deuda del proveedor");
    }

    public void confirmarOC(OrdenDeCompra oc) {
        oc.confirmarGeneracion();
        ordenesDeCompra.add(oc);
    }

    public void confirmarOCConAutorizacion(OrdenDeCompra oc, Usuario supervisor) {
        oc.marcarAutorizada(supervisor);
        oc.confirmarGeneracion();
        ordenesDeCompra.add(oc);
    }

    public void cancelarOC(OrdenDeCompra oc) {
        // La OC simplemente no se registra
    }

    // ── Búsquedas ──────────────────────────────────────────────────────
    public OrdenDeCompra buscarOrdenDeCompra(String numero) {
        for (OrdenDeCompra oc : ordenesDeCompra) {
            if (oc.getNumero().equals(numero)) return oc;
        }
        return null;
    }

    // ── Consultas ──────────────────────────────────────────────────────
    public List<OrdenDeCompra> getOCEmitidasPorRango(Date desde, Date hasta) {
        return ordenesDeCompra.stream()
                .filter(oc -> !oc.getFechaEmision().before(desde)
                        && !oc.getFechaEmision().after(hasta))
                .collect(Collectors.toList());
    }

    public List<OrdenDeCompra> getOrdenesDeCompra() {
        return ordenesDeCompra;
    }
}
