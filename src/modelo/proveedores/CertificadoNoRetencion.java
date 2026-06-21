package modelo.proveedores;

import modelo.impuestos.Impuesto;
import java.util.Date;

public class CertificadoNoRetencion {
    private String numero;
    private Date fechaDesde;
    private Date fechaHasta;
    private Impuesto impuesto;

    public CertificadoNoRetencion(String numero, Date fechaDesde, Date fechaHasta, Impuesto impuesto) {
        this.numero = numero;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.impuesto = impuesto;
    }

    public boolean estaVigente() {
        Date hoy = new Date();
        return hoy.after(fechaDesde) && hoy.before(fechaHasta);
    }

    public String getNumero() { return numero; }
    public Impuesto getImpuesto() { return impuesto; }
    public Date getFechaDesde() { return fechaDesde; }
    public Date getFechaHasta() { return fechaHasta; }
}