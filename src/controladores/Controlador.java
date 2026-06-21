package controladores;

import modelo.catalogo.Producto;
import modelo.comprobantes.Comprobante;
import modelo.comprobantes.Factura;
import modelo.comprobantes.ResultadoValidacion;
import modelo.impuestos.Impuesto;
import modelo.ordenes.OrdenDeCompra;
import modelo.pagos.MedioDePago;
import modelo.pagos.OrdenDePago;
import modelo.pagos.RetencionAplicada;
import modelo.proveedores.CuentaCorriente;
import modelo.proveedores.Movimiento;
import modelo.proveedores.Proveedor;
import modelo.sistema.Sistema;
import modelo.usuarios.Usuario;

import java.util.List;

public class Controlador {

    // ── Singleton ──────────────────────────────────────────────────────────────
    private static Controlador instancia;
    private Sistema sistema;

    private Controlador() {
        this.sistema = Sistema.getInstancia();
    }

    public static Controlador getInstancia() {
        if (instancia == null) {
            instancia = new Controlador();
        }
        return instancia;
    }

    // ==========================================================================
    // CASO DE USO 1 — Generar Orden de Compra
    // Diagrama: generarOC_v2_corregido.puml
    // ==========================================================================

    public Proveedor buscarProveedor(String cuit) {
        return sistema.buscarProveedor(cuit);
    }

    public OrdenDeCompra crearOrdenDeCompra(Proveedor proveedor) {
        return sistema.crearOrdenDeCompra(proveedor);
    }

    public Producto buscarProducto(String codigo) {
        return sistema.buscarProducto(codigo);
    }

    public void agregarItemAOC(OrdenDeCompra oc, Producto producto,
                               int cantidad, double precio) {
        sistema.agregarItemAOC(oc, producto, cantidad, precio);
    }

    public ResultadoValidacion validarTopeDeDeuda(OrdenDeCompra oc) {
        return sistema.validarTopeDeDeuda(oc);
    }

    public void confirmarOC(OrdenDeCompra oc) {
        sistema.confirmarOC(oc);
    }

    public void confirmarOCConAutorizacion(OrdenDeCompra oc, Usuario supervisor) {
        sistema.confirmarOCConAutorizacion(oc, supervisor);
    }

    public void cancelarOC(OrdenDeCompra oc) {
        // La OC no se registra en el sistema — simplemente se descarta
    }

    // ==========================================================================
    // CASO DE USO 2 — Registrar Factura y validar contra OC
    // Diagrama: RegistrarFyValidarOC_v2_corregido.puml
    // ==========================================================================

    public OrdenDeCompra buscarOrdenDeCompra(String numero) {
        return sistema.buscarOrdenDeCompra(numero);
    }

    public ResultadoValidacion registrarFactura(Factura factura, OrdenDeCompra oc) {
        // El sistema delega la validación a la Factura (diagrama de secuencia)
        ResultadoValidacion resultado = factura.validarContraOC(oc);

        if (resultado.isValido()) {
            // Flujo exitoso — precios coinciden
            sistema.registrarFactura(factura, oc);
        }
        // Si no es válido, el resultado se devuelve a la vista
        // para que solicite autorización al supervisor
        return resultado;
    }

    public void registrarFacturaAutorizada(Factura factura, Usuario supervisor) {
        sistema.registrarFacturaAutorizada(factura, supervisor);
    }

    public void rechazarFactura(Factura factura) {
        sistema.rechazarFactura(factura);
    }

    // ==========================================================================
    // CASO DE USO 3 — Emitir Orden de Pago
    // Diagrama: EmitirOP_v2_corregido.puml
    // ==========================================================================

    public List<Comprobante> getDocumentosImpagos(Proveedor proveedor) {
        return proveedor.getDocumentosImpagos();
    }

    public OrdenDePago generarOrdenDePago(Proveedor proveedor,
                                          List<Comprobante> comprobantes) {
        return sistema.generarOrdenDePago(proveedor, comprobantes);
    }

    public void registrarMediosDePago(OrdenDePago op, List<MedioDePago> medios) {
        sistema.registrarMediosDePago(op, medios);
    }

    public void confirmarEmisionOP(OrdenDePago op) {
        // Finaliza la OP: afecta cuenta corriente y registra cancelaciones
        Proveedor proveedor = op.getProveedor();

        // Por cada comprobante en la OP — registrarCancelacion (diagrama de secuencia)
        for (var cancelacion : op.getCancelaciones()) {
            cancelacion.getComprobante().registrarCancelacion(op);
        }

        // Afecta la cuenta corriente del proveedor
        proveedor.getCuentaCorriente().registrarMovimiento(
            new Movimiento(
                new java.util.Date(),
                "Orden de Pago",
                op.getNumero(),
                0,
                op.getImporteNeto(),
                proveedor.getCuentaCorriente().getSaldoActual() - op.getImporteNeto()
            )
        );

        sistema.confirmarEmisionOP(op);
    }

    // ==========================================================================
    // CASO DE USO 4 — Consultar Cuenta Corriente
    // Diagrama: CCProveedor_v2_corregido.puml
    // ==========================================================================

    public CuentaCorriente obtenerCuentaCorriente(Proveedor proveedor) {
        if (proveedor == null) {
            return null;
        }
        return proveedor.getCuentaCorriente();
    }

    public List<Movimiento> getMovimientos(CuentaCorriente cc) {
        return cc.getMovimientos();
    }

    public List<Comprobante> getDocumentosImpagosDesdCC(CuentaCorriente cc) {
        return cc.getDocumentosImpagos();
    }

    public List<OrdenDePago> getPagosRealizados(CuentaCorriente cc) {
        return cc.getPagosRealizados();
    }
}