package modelo.comprobantes;

import modelo.enums.LetraComprobante;
import modelo.pagos.OrdenDePago;
import modelo.proveedores.Proveedor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Comprobante {
    private String numero;
    private Date fechaEmision;
    private double importeNeto;
    private double importeIVA;
    private double importeTotal;
    private LetraComprobante letra;
    private List<ItemComprobante> items;
    private OrdenDePago ordenDePago;
    private Proveedor proveedor;

    public Comprobante(String numero, Date fechaEmision, double importeNeto,
                    double importeIVA, LetraComprobante letra, Proveedor proveedor) {
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.importeNeto = importeNeto;
        this.importeIVA = importeIVA;
        this.importeTotal = importeNeto + importeIVA;
        this.letra = letra;
        this.proveedor = proveedor;
        this.items = new ArrayList<>();
    }

    public void registrarCancelacion(OrdenDePago op) {
    this.ordenDePago = op;
}

    public void agregarItem(ItemComprobante item) {
        items.add(item);
    }

    // Métodos abstractos — cada subclase los implementa
    public abstract double afectaCuentaCorriente();
    public abstract String getDescripcionTipo();
    public abstract boolean aumentaDeuda();

    public String getNumero() { return numero; }
    public Date getFechaEmision() { return fechaEmision; }
    public double getImporteNeto() { return importeNeto; }
    public double getImporteIVA() { return importeIVA; }
    public double getImporteTotal() { return importeTotal; }
    public LetraComprobante getLetra() { return letra; }
    public List<ItemComprobante> getItems() { return items; }
    public Proveedor getProveedor() { return proveedor; }

    public OrdenDePago getOrdenDePago() { return ordenDePago; }
public boolean estaCancelado() { return ordenDePago != null; }
}