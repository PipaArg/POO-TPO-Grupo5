package modelo.proveedores;

import modelo.comprobantes.Comprobante;
import modelo.pagos.OrdenDePago;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CuentaCorriente {
    private List<Movimiento> movimientos;

    public CuentaCorriente() {
        this.movimientos = new ArrayList<>();
    }

    public void registrarMovimiento(Movimiento m) {
        movimientos.add(m);
    }

    public List<Movimiento> getMovimientos() { return movimientos; }

    public List<Movimiento> getMovimientosPorRango(Date desde, Date hasta) {
        return movimientos.stream()
                .filter(m -> !m.getFecha().before(desde) && !m.getFecha().after(hasta))
                .collect(Collectors.toList());
    }

    public double getSaldoActual() {
        return movimientos.stream()
                .mapToDouble(m -> m.getDebe() - m.getHaber())
                .sum();
    }

    public List<Comprobante> getDocumentosImpagos() {
        // Se implementa en el bloque 2 junto con la lógica de negocio
        return new ArrayList<>();
    }

    public List<OrdenDePago> getPagosRealizados() {
        // Se implementa en el bloque 2 junto con la lógica de negocio
        return new ArrayList<>();
    }
}