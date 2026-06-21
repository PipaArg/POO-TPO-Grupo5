package modelo.comprobantes;

import modelo.enums.LetraComprobante;
import modelo.proveedores.Proveedor;
import java.util.Date;

public class NotaDeDebito extends Comprobante {

    public NotaDeDebito(String numero, Date fechaEmision, double importeNeto,
                        double importeIVA, LetraComprobante letra, Proveedor proveedor) {
        super(numero, fechaEmision, importeNeto, importeIVA, letra, proveedor);
    }

    @Override
    public double afectaCuentaCorriente() { return getImporteTotal(); }

    @Override
    public String getDescripcionTipo() { return "Nota de Débito"; }

    @Override
    public boolean aumentaDeuda() { return true; }
}