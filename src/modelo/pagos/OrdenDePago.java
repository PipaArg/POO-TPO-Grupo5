package modelo.pagos;

import modelo.comprobantes.Comprobante;
import modelo.proveedores.Proveedor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdenDePago {
    private String numero;
    private Date fechaEmision;
    private Proveedor proveedor;
    private List<Cancelacion> cancelaciones;
    private List<RetencionAplicada> retenciones;
    private List<MedioDePago> mediosDePago;

    public OrdenDePago(String numero, Proveedor proveedor) {
        this.numero = numero;
        this.proveedor = proveedor;
        this.fechaEmision = new Date();
        this.cancelaciones = new ArrayList<>();
        this.retenciones = new ArrayList<>();
        this.mediosDePago = new ArrayList<>();
    }

    public void agregarCancelacion(Comprobante comprobante, double monto) {
        cancelaciones.add(new Cancelacion(comprobante, monto));
    }

    public void agregarRetencion(RetencionAplicada retencion) {
        retenciones.add(retencion);
    }

    public void agregarMedioDePago(MedioDePago medio) {
        mediosDePago.add(medio);
    }

    public double getTotalRetenciones() {
        return retenciones.stream()
                .mapToDouble(RetencionAplicada::getMontoRetenido)
                .sum();
    }

    public double getTotalMediosDePago() {
        return mediosDePago.stream()
                .mapToDouble(MedioDePago::getImporte)
                .sum();
    }

    public double getImporteNeto() {
        double totalComprobantes = cancelaciones.stream()
                .mapToDouble(Cancelacion::getMontoCancelado)
                .sum();
        return totalComprobantes - getTotalRetenciones();
    }

    public String getNumero() { return numero; }
    public Date getFechaEmision() { return fechaEmision; }
    public Proveedor getProveedor() { return proveedor; }
    public List<Cancelacion> getCancelaciones() { return cancelaciones; }
    public List<RetencionAplicada> getRetenciones() { return retenciones; }
    public List<MedioDePago> getMediosDePago() { return mediosDePago; }
}