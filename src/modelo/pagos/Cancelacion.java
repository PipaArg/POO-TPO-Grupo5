package modelo.pagos;

import modelo.comprobantes.Comprobante;

public class Cancelacion {
    private Comprobante comprobante;
    private double montoCancelado;

    public Cancelacion(Comprobante comprobante, double montoCancelado) {
        this.comprobante = comprobante;
        this.montoCancelado = montoCancelado;
    }

    public Comprobante getComprobante() { return comprobante; }
    public double getMontoCancelado() { return montoCancelado; }
}