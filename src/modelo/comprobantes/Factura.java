package modelo.comprobantes;

import modelo.enums.LetraComprobante;
import modelo.ordenes.OrdenDeCompra;
import modelo.proveedores.Proveedor;
import modelo.usuarios.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Factura extends Comprobante {
    private boolean requirioAutorizacion;
    private Date fechaAutorizacion;
    private String motivoDesvio;
    private List<OrdenDeCompra> ordenesDeCompra;
    private Usuario supervisor;

    public Factura(String numero, Date fechaEmision, double importeNeto,
                double importeIVA, LetraComprobante letra, Proveedor proveedor) {
        super(numero, fechaEmision, importeNeto, importeIVA, letra, proveedor);
        this.requirioAutorizacion = false;
        this.ordenesDeCompra = new ArrayList<>();
    }

    public ResultadoValidacion validarContraOC(OrdenDeCompra oc) {
        for (ItemComprobante itemF : getItems()) {
            var itemOC = oc.buscarItemPorProducto(itemF.getProducto());
            if (itemOC == null) {
                return new ResultadoValidacion(false, "Producto no encontrado en la OC: "
                        + itemF.getProducto().getDescripcion());
            }
            if (itemF.getPrecioUnitario() > itemOC.getPrecioUnitario()) {
                return new ResultadoValidacion(false, "Precio facturado supera el acordado en OC para: "
                        + itemF.getProducto().getDescripcion());
            }
        }
        return new ResultadoValidacion(true, "Factura validada correctamente contra OC");
    }

    public void marcarAutorizada(Usuario supervisor, String motivo) {
        this.supervisor = supervisor;
        this.requirioAutorizacion = true;
        this.fechaAutorizacion = new Date();
        this.motivoDesvio = motivo;
    }

    public void vincularOC(OrdenDeCompra oc) { ordenesDeCompra.add(oc); }

    @Override
    public double afectaCuentaCorriente() { return getImporteTotal(); }

    @Override
    public String getDescripcionTipo() { return "Factura"; }

    @Override
    public boolean aumentaDeuda() { return true; }

    public boolean requirioAutorizacion() { return requirioAutorizacion; }
    public List<OrdenDeCompra> getOrdenesDeCompra() { return ordenesDeCompra; }
    public String getMotivoDesvio() { return motivoDesvio; }
}