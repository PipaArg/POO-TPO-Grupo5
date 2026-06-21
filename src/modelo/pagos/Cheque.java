package modelo.pagos;

import java.util.Date;

public abstract class Cheque extends MedioDePago {
    private String numero;
    private Date fechaEmision;
    private Date fechaVencimiento;
    private String firmante;

    public Cheque(double importe, String numero, Date fechaEmision,
                Date fechaVencimiento, String firmante) {
        super(importe);
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.firmante = firmante;
    }

    public String getNumero() { return numero; }
    public Date getFechaEmision() { return fechaEmision; }
    public Date getFechaVencimiento() { return fechaVencimiento; }
    public String getFirmante() { return firmante; }

    @Override
    public abstract String getDescripcion();
}