package modelo.pagos;

import java.util.Date;

public class ChequeTerceros extends Cheque {
    private String librador;

    public ChequeTerceros(double importe, String numero, Date fechaEmision,
                        Date fechaVencimiento, String firmante, String librador) {
        super(importe, numero, fechaEmision, fechaVencimiento, firmante);
        this.librador = librador;
    }

    @Override
    public String getDescripcion() {
        return "Cheque de Terceros #" + getNumero() + " - Librador: " + librador + " por $" + getImporte();
    }

    public String getLibrador() { return librador; }
}