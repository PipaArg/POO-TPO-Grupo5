package modelo.pagos;

import modelo.impuestos.Impuesto;

public class RetencionAplicada {
    private double baseImponible;
    private double porcentajeAplicado;
    private double montoRetenido;
    private Impuesto impuesto;

    public RetencionAplicada(Impuesto impuesto, double baseImponible, double porcentajeAplicado) {
        this.impuesto = impuesto;
        this.baseImponible = baseImponible;
        this.porcentajeAplicado = porcentajeAplicado;
        this.montoRetenido = baseImponible * porcentajeAplicado / 100;
    }

    public double getMontoRetenido() { return montoRetenido; }
    public Impuesto getImpuesto() { return impuesto; }
    public double getBaseImponible() { return baseImponible; }
    public double getPorcentajeAplicado() { return porcentajeAplicado; }
}