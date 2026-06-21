package modelo.sistema;

import modelo.catalogo.Producto;
import modelo.comprobantes.Comprobante;
import modelo.comprobantes.Factura;
import modelo.comprobantes.ResultadoValidacion;
import modelo.impuestos.Impuesto;
import modelo.ordenes.OrdenDeCompra;
import modelo.pagos.MedioDePago;
import modelo.pagos.OrdenDePago;
import modelo.pagos.RetencionAplicada;
import modelo.proveedores.Proveedor;
import modelo.proveedores.Rubro;
import modelo.usuarios.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sistema {

    // ── Singleton ──────────────────────────────────────────
    private static Sistema instancia;

    private Sistema() {
        proveedores     = new ArrayList<>();
        productos       = new ArrayList<>();
        rubros          = new ArrayList<>();
        impuestos       = new ArrayList<>();
        usuarios        = new ArrayList<>();
        ordenesDeCompra = new ArrayList<>();
        comprobantes    = new ArrayList<>();
        ordenesDePago   = new ArrayList<>();
    }

    public static Sistema getInstancia() {
        if (instancia == null) {
            instancia = new Sistema();
        }
        return instancia;
    }

    // ── Colecciones ────────────────────────────────────────
    private List<Proveedor>     proveedores;
    private List<Producto>      productos;
    private List<Rubro>         rubros;
    private List<Impuesto>      impuestos;
    private List<Usuario>       usuarios;
    private List<OrdenDeCompra> ordenesDeCompra;
    private List<Comprobante>   comprobantes;
    private List<OrdenDePago>   ordenesDePago;

    // ── Búsquedas ──────────────────────────────────────────
    public Proveedor buscarProveedor(String cuit) {
        for (Proveedor p : proveedores) {
            if (p.getCuit().equals(cuit)) return p;
        }
        return null;
    }

    public Producto buscarProducto(String codigo) {
        for (Producto p : productos) {
            if (p.getCodigo().equals(codigo)) return p;
        }
        return null;
    }

    public OrdenDeCompra buscarOrdenDeCompra(String numero) {
        for (OrdenDeCompra oc : ordenesDeCompra) {
            if (oc.getNumero().equals(numero)) return oc;
        }
        return null;
    }

    public Comprobante buscarComprobante(String numero) {
        for (Comprobante c : comprobantes) {
            if (c.getNumero().equals(numero)) return c;
        }
        return null;
    }

    public Usuario buscarUsuario(String nombreUsuario) {
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equals(nombreUsuario)) return u;
        }
        return null;
    }

    // ── Altas ──────────────────────────────────────────────
    public void registrarProveedor(Proveedor p)     { proveedores.add(p); }
    public void registrarProducto(Producto p)       { productos.add(p); }
    public void registrarRubro(Rubro r)             { rubros.add(r); }
    public void registrarImpuesto(Impuesto i)       { impuestos.add(i); }
    public void registrarUsuario(Usuario u)         { usuarios.add(u); }

    // ── Operaciones de negocio ─────────────────────────────
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
        if (p.puedeRecibirOC(oc.calcularTotalOC())) {
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

    public void registrarFactura(Factura f, OrdenDeCompra oc) {
        f.vincularOC(oc);
        comprobantes.add(f);
    }

    public void registrarFacturaAutorizada(Factura f, Usuario supervisor) {
        f.marcarAutorizada(supervisor, f.getMotivoDesvio());
        comprobantes.add(f);
    }

    public void rechazarFactura(Factura f) {
        // La factura no se agrega a la colección
    }

    public OrdenDePago generarOrdenDePago(Proveedor proveedor,
                                        List<Comprobante> comprobantesAPagar) {
        String numero = "OP-" + (ordenesDePago.size() + 1);
        OrdenDePago op = new OrdenDePago(numero, proveedor);
        for (Comprobante c : comprobantesAPagar) {
            op.agregarCancelacion(c, c.getImporteTotal());
        }
        // Calcular retenciones por impuesto
        for (Impuesto imp : impuestos) {
            if (!proveedor.tieneCertificadoVigenteDe(imp)) {
                double base = comprobantesAPagar.stream()
                        .mapToDouble(Comprobante::getImporteNeto)
                        .sum();
                double monto = imp.calcularRetencion(base);
                if (monto > 0) {
                    op.agregarRetencion(new RetencionAplicada(imp, base,
                            imp.getPorcentajeDefault()));
                }
            }
        }
        return op;
    }

    public void registrarMediosDePago(OrdenDePago op, List<MedioDePago> medios) {
        for (MedioDePago m : medios) {
            op.agregarMedioDePago(m);
        }
    }

    public void confirmarEmisionOP(OrdenDePago op) {
        ordenesDePago.add(op);
    }

    public void cancelarOC(OrdenDeCompra oc) {
    // La OC simplemente no se registra — no hace falta hacer nada
}

    // ── Consultas (Consigna 7) ─────────────────────────────
    public List<OrdenDeCompra> getOCEmitidasPorRango(Date desde, Date hasta) {
        return ordenesDeCompra.stream()
                .filter(oc -> !oc.getFechaEmision().before(desde)
                        && !oc.getFechaEmision().after(hasta))
                .collect(Collectors.toList());
    }

    public List<OrdenDePago> getOPEmitidasPorRango(Date desde, Date hasta) {
        return ordenesDePago.stream()
                .filter(op -> !op.getFechaEmision().before(desde)
                        && !op.getFechaEmision().after(hasta))
                .collect(Collectors.toList());
    }

    public List<Factura> getFacturasPorProveedorYFecha(Proveedor proveedor,
                                                        Date desde, Date hasta) {
        return comprobantes.stream()
                .filter(c -> c instanceof Factura
                        && c.getProveedor().equals(proveedor)
                        && !c.getFechaEmision().before(desde)
                        && !c.getFechaEmision().after(hasta))
                .map(c -> (Factura) c)
                .collect(Collectors.toList());
    }

    public Map<Proveedor, Double> getTotalDeudaVigentePorProveedor() {
        Map<Proveedor, Double> mapa = new HashMap<>();
        for (Proveedor p : proveedores) {
            mapa.put(p, p.getDeudaVigente());
        }
        return mapa;
    }

    public List<Producto> getProductos() { return productos; }
    public List<Proveedor> getProveedores() { return proveedores; }
    public List<Impuesto> getImpuestos() { return impuestos; }
    public List<OrdenDeCompra> getOrdenesDeCompra() { return ordenesDeCompra; }
    public List<Comprobante> getComprobantes() { return comprobantes; }
    public List<OrdenDePago> getOrdenesDePago() { return ordenesDePago; }
}