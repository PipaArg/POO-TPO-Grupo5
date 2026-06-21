package modelo.pagos;

import java.util.Date;

public class ChequePropio extends Cheque {
    private String bancoEmisor;
    private String numeroCuenta;

    public ChequePropio(double importe, String numero, Date fechaEmision,
                        Date fechaVencimiento, String firmante,
                        String bancoEmisor, String numeroCuenta) {
        super(importe, numero, fechaEmision, fechaVencimiento, firmante);
        this.bancoEmisor = bancoEmisor;
        this.numeroCuenta = numeroCuenta;
    }

    @Override
    public String getDescripcion() {
        return "Cheque Propio #" + getNumero() + " - Banco: " + bancoEmisor + " por $" + getImporte();
    }

    public String getBancoEmisor() { return bancoEmisor; }
    public String getNumeroCuenta() { return numeroCuenta; }
}