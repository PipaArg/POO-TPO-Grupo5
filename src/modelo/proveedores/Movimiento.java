package modelo.proveedores;

import java.util.Date;

public class Movimiento {
    private Date fecha;
    private String tipoMovimiento;
    private String numeroDocumento;
    private double debe;
    private double haber;
    private double saldo;

    public Movimiento(Date fecha, String tipoMovimiento, String numeroDocumento, double debe, double haber, double saldo) {
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.numeroDocumento = numeroDocumento;
        this.debe = debe;
        this.haber = haber;
        this.saldo = saldo;
    }

    public Date getFecha() { return fecha; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public double getDebe() { return debe; }
    public double getHaber() { return haber; }
    public double getSaldo() { return saldo; }
}