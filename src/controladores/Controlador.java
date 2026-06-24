package controladores;

import modelo.catalogo.Producto;
import modelo.comprobantes.Comprobante;
import modelo.comprobantes.Factura;
import modelo.comprobantes.ResultadoValidacion;
import modelo.ordenes.OrdenDeCompra;
import modelo.pagos.MedioDePago;
import modelo.pagos.OrdenDePago;
import modelo.proveedores.CuentaCorriente;
import modelo.proveedores.Movimiento;
import modelo.proveedores.Proveedor;
import modelo.usuarios.Usuario;

import java.util.List;

public class Controlador {

    // ── Singleton ──────────────────────────────────────────────────────
    private static Controlador instancia;

    private GestorProveedores gestorProveedores;
    private GestorCatalogo gestorCatalogo;
    private GestorOC gestorOC;
    private GestorComprobantes gestorComprobantes;
    private GestorPagos gestorPagos;
    private GestorImpuestos gestorImpuestos;
    private GestorUsuarios gestorUsuarios;

    private Controlador() {
        this.gestorProveedores  = GestorProveedores.getInstancia();
        this.gestorCatalogo     = GestorCatalogo.getInstancia();
        this.gestorOC           = GestorOC.getInstancia();
        this.gestorComprobantes = GestorComprobantes.getInstancia();
        this.gestorPagos        = GestorPagos.getInstancia();
        this.gestorImpuestos    = GestorImpuestos.getInstancia();
        this.gestorUsuarios     = GestorUsuarios.getInstancia();
    }

    public static Controlador getInstancia() {
        if (instancia == null) {
            instancia = new Controlador();
        }
        return instancia;
    }

    // ==========================================================================
    // CASO DE USO 1 — Generar Orden de Compra
    // ==========================================================================

    public Proveedor buscarProveedor(String cuit) {
        return gestorProveedores.buscarProveedor(cuit);
    }

    public OrdenDeCompra crearOrdenDeCompra(Proveedor proveedor) {
        return gestorOC.crearOrdenDeCompra(proveedor);
    }

    public Producto buscarProducto(String codigo) {
        return gestorCatalogo.buscarProducto(codigo);
    }

    public void agregarItemAOC(OrdenDeCompra oc, Producto producto,
                            int cantidad, double precio) {
        gestorOC.agregarItemAOC(oc, producto, cantidad, precio);
    }

    public ResultadoValidacion validarTopeDeDeuda(OrdenDeCompra oc) {
        return gestorOC.validarTopeDeDeuda(oc);
    }

    public void confirmarOC(OrdenDeCompra oc) {
        gestorOC.confirmarOC(oc);
    }

    public void confirmarOCConAutorizacion(OrdenDeCompra oc, Usuario supervisor) {
        gestorOC.confirmarOCConAutorizacion(oc, supervisor);
    }

    public void cancelarOC(OrdenDeCompra oc) {
        gestorOC.cancelarOC(oc);
    }

    // ==========================================================================
    // CASO DE USO 2 — Registrar Factura y validar contra OC
    // ==========================================================================

    public OrdenDeCompra buscarOrdenDeCompra(String numero) {
        return gestorOC.buscarOrdenDeCompra(numero);
    }

    public ResultadoValidacion registrarFactura(Factura factura, OrdenDeCompra oc) {
        return gestorComprobantes.registrarFactura(factura, oc);
    }

    public void registrarFacturaAutorizada(Factura factura, Usuario supervisor) {
        gestorComprobantes.registrarFacturaAutorizada(factura, supervisor);
    }

    public void rechazarFactura(Factura factura) {
        gestorComprobantes.rechazarFactura(factura);
    }

    // ==========================================================================
    // CASO DE USO 3 — Emitir Orden de Pago
    // ==========================================================================

    public List<Comprobante> consultarDocumentosImpagos(Proveedor proveedor) {
        return gestorPagos.consultarDocumentosImpagos(proveedor);
    }

    public OrdenDePago generarOrdenDePago(Proveedor proveedor,
                                        List<Comprobante> comprobantes) {
        return gestorPagos.generarOrdenDePago(proveedor, comprobantes);
    }

    public void registrarMediosDePago(OrdenDePago op, List<MedioDePago> medios) {
        gestorPagos.registrarMediosDePago(op, medios);
    }

    public void confirmarEmisionOP(OrdenDePago op) {
        gestorPagos.confirmarEmisionOP(op);
    }

    // ==========================================================================
    // CASO DE USO 4 — Consultar Cuenta Corriente
    // ==========================================================================

    public CuentaCorriente obtenerCuentaCorriente(Proveedor proveedor) {
        if (proveedor == null) return null;
        return gestorProveedores.obtenerCuentaCorriente(proveedor);
    }

    public List<Movimiento> getMovimientos(CuentaCorriente cc) {
        return cc.getMovimientos();
    }

    public List<Comprobante> getDocumentosImpagos(CuentaCorriente cc) {
        return cc.getDocumentosImpagos();
    }

    public List<OrdenDePago> getPagosRealizados(CuentaCorriente cc) {
        return cc.getPagosRealizados();
    }

    // ==========================================================================
    // GESTIÓN DE USUARIOS
    // ==========================================================================

    public void registrarUsuario(Usuario u) {
        gestorUsuarios.registrarUsuario(u);
    }

    public Usuario buscarUsuario(String nombreUsuario) {
        return gestorUsuarios.buscarUsuario(nombreUsuario);
    }

    public Usuario buscarSupervisor() {
        return gestorUsuarios.buscarSupervisor();
    }
}
