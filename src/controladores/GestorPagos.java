package controladores;

import modelo.comprobantes.Comprobante;
import modelo.impuestos.Impuesto;
import modelo.pagos.MedioDePago;
import modelo.pagos.OrdenDePago;
import modelo.pagos.RetencionAplicada;
import modelo.proveedores.Movimiento;
import modelo.proveedores.Proveedor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GestorPagos {

    // ── Singleton ──────────────────────────────────────────────────────
    private static GestorPagos instancia;

    private GestorPagos() {
        this.ordenesDePago = new ArrayList<>();
    }

    public static GestorPagos getInstancia() {
        if (instancia == null) {
            instancia = new GestorPagos();
        }
        return instancia;
    }

    // ── Colecciones ────────────────────────────────────────────────────
    private List<OrdenDePago> ordenesDePago;

    // ── Operaciones de negocio ─────────────────────────────────────────
    public List<Comprobante> consultarDocumentosImpagos(Proveedor proveedor) {
        return proveedor.getCuentaCorriente().getDocumentosImpagos();
    }

    public OrdenDePago generarOrdenDePago(Proveedor proveedor,
                                          List<Comprobante> comprobantes) {
        String numero = "OP-" + (ordenesDePago.size() + 1);
        OrdenDePago op = new OrdenDePago(numero, proveedor);

        // Agregar comprobantes seleccionados
        for (Comprobante c : comprobantes) {
            op.agregarCancelacion(c, c.getImporteTotal());
        }

        // Calcular retenciones por impuesto
        List<Impuesto> impuestos = GestorImpuestos.getInstancia().getImpuestos();
        for (Impuesto imp : impuestos) {
            if (!proveedor.tieneCertificadoVigenteDe(imp)) {
                double base = comprobantes.stream()
                        .mapToDouble(Comprobante::getImporteNeto)
                        .sum();
                double monto = imp.calcularRetencion(base);
                if (monto > 0) {
                    op.agregarRetencion(
                        new RetencionAplicada(imp, base, imp.getPorcentajeDefault())
                    );
                }
            } else {
                // Con certificado vigente → retención 0
                op.agregarRetencion(
                    new RetencionAplicada(imp, 0, 0)
                );
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
        // Afectar cuenta corriente del proveedor
        Proveedor proveedor = op.getProveedor();
        proveedor.getCuentaCorriente().registrarMovimiento(
            new Movimiento(
                new Date(),
                "Orden de Pago",
                op.getNumero(),
                0,
                op.getImporteNeto(),
                proveedor.getCuentaCorriente().getSaldoActual() - op.getImporteNeto()
            )
        );

        // Registrar cancelación en cada comprobante
        for (var cancelacion : op.getCancelaciones()) {
            cancelacion.getComprobante().registrarCancelacion(op);
        }

        ordenesDePago.add(op);
    }

    // ── Consultas ──────────────────────────────────────────────────────
    public List<OrdenDePago> getOPEmitidasPorRango(Date desde, Date hasta) {
        return ordenesDePago.stream()
                .filter(op -> !op.getFechaEmision().before(desde)
                        && !op.getFechaEmision().after(hasta))
                .collect(Collectors.toList());
    }

    public List<OrdenDePago> getOrdenesDePago() {
        return ordenesDePago;
    }
}
