package modelo.impuestos;

public class Tramo {
    private double montoDesde;
    private double montoHasta;
    private double porcentaje;

    public Tramo(double montoDesde, double montoHasta, double porcentaje) {
        this.montoDesde = montoDesde;
        this.montoHasta = montoHasta;
        this.porcentaje = porcentaje;
    }

    public boolean aplicaA(double monto) {
        return monto >= montoDesde && monto <= montoHasta;
    }

    public double getPorcentaje() { return porcentaje; }
    public double getMontoDesde() { return montoDesde; }
    public double getMontoHasta() { return montoHasta; }
}