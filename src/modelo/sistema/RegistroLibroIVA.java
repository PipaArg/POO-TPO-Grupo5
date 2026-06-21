package modelo.sistema;

import modelo.enums.LetraComprobante;
import java.util.Date;

public class RegistroLibroIVA {
    private String cuitProveedor;
    private String razonSocialProveedor;
    private Date fechaEmision;
    private String tipoComprobante;
    private LetraComprobante letra;
    private double baseImponible21;
    private double iva21;
    private double baseImponible105;
    private double iva105;
    private double importeTotal;

    public RegistroLibroIVA(String cuitProveedor, String razonSocialProveedor,
                            Date fechaEmision, String tipoComprobante,
                            LetraComprobante letra, double baseImponible21,
                            double iva21, double baseImponible105,
                            double iva105, double importeTotal) {
        this.cuitProveedor = cuitProveedor;
        this.razonSocialProveedor = razonSocialProveedor;
        this.fechaEmision = fechaEmision;
        this.tipoComprobante = tipoComprobante;
        this.letra = letra;
        this.baseImponible21 = baseImponible21;
        this.iva21 = iva21;
        this.baseImponible105 = baseImponible105;
        this.iva105 = iva105;
        this.importeTotal = importeTotal;
    }

    public String getCuitProveedor() { return cuitProveedor; }
    public String getRazonSocialProveedor() { return razonSocialProveedor; }
    public Date getFechaEmision() { return fechaEmision; }
    public String getTipoComprobante() { return tipoComprobante; }
    public double getImporteTotal() { return importeTotal; }
}