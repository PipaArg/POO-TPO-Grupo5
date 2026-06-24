package modelo.proveedores;

import modelo.comprobantes.Comprobante;
import modelo.pagos.Cancelacion;
import modelo.pagos.OrdenDePago;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CuentaCorriente {
    private List<Movimiento> movimientos;
    private List<Comprobante> comprobantesAsociados;
    private List<OrdenDePago> pagosEmitidos;

    public CuentaCorriente() {
        this.movimientos = new ArrayList<>();
        this.comprobantesAsociados = new ArrayList<>();
        this.pagosEmitidos = new ArrayList<>();
    }

    public void registrarMovimiento(Movimiento m) {
        movimientos.add(m);
    }

    public void asociarComprobante(Comprobante c) {
        comprobantesAsociados.add(c);
    }

    public void asociarPago(OrdenDePago op) {
        pagosEmitidos.add(op);
    }

    public List<Movimiento> getMovimientos() { return movimientos; }

    public double getSaldoActual() {
        return movimientos.stream()
                .mapToDouble(m -> m.getDebe() - m.getHaber())
                .sum();
    }

    public List<Comprobante> getDocumentosImpagos() {
        return comprobantesAsociados.stream()
                .filter(c -> c.aumentaDeuda())
                .filter(c -> {
                    for (OrdenDePago op : pagosEmitidos) {
                        for (Cancelacion canc : op.getCancelaciones()) {
                            if (canc.getComprobante().getNumero().equals(c.getNumero())) {
                                return false;
                            }
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public List<OrdenDePago> getPagosRealizados() {
        return pagosEmitidos;
    }

    public List<Comprobante> getMovimientosPorRango(Date desde, Date hasta) {
        return comprobantesAsociados.stream()
                .filter(c -> !c.getFechaEmision().before(desde)
                        && !c.getFechaEmision().after(hasta))
                .collect(Collectors.toList());
    }
}