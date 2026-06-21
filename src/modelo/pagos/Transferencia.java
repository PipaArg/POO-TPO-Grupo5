package modelo.pagos;

import java.util.Date;

public class Transferencia extends MedioDePago {
    private String cbuOrigen;
    private String cbuDestino;
    private Date fechaTransferencia;
    private String numeroOperacion;

    public Transferencia(double importe, String cbuOrigen, String cbuDestino,
                        Date fechaTransferencia, String numeroOperacion) {
        super(importe);
        this.cbuOrigen = cbuOrigen;
        this.cbuDestino = cbuDestino;
        this.fechaTransferencia = fechaTransferencia;
        this.numeroOperacion = numeroOperacion;
    }

    @Override
    public String getDescripcion() {
        return "Transferencia #" + numeroOperacion + " por $" + getImporte();
    }

    public String getCbuOrigen() { return cbuOrigen; }
    public String getCbuDestino() { return cbuDestino; }
    public String getNumeroOperacion() { return numeroOperacion; }
}