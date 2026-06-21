package modelo.comprobantes;

import modelo.enums.LetraComprobante;
import modelo.proveedores.Proveedor;
import java.util.Date;

public class NotaDeCredito extends Comprobante {
    private Factura facturaOrigen;

    public NotaDeCredito(String numero, Date fechaEmision, double importeNeto,
                        double importeIVA, LetraComprobante letra, Proveedor proveedor,
                        Factura facturaOrigen) {
        super(numero, fechaEmision, importeNeto, importeIVA, letra, proveedor);
        this.facturaOrigen = facturaOrigen;
    }

    @Override
    public double afectaCuentaCorriente() { return -getImporteTotal(); }

    @Override
    public String getDescripcionTipo() { return "Nota de Crédito"; }

    @Override
    public boolean aumentaDeuda() { return false; }

    public Factura getFacturaOrigen() { return facturaOrigen; }
}