package modelo.pagos;

public abstract class MedioDePago {
    private double importe;

    public MedioDePago(double importe) {
        this.importe = importe;
    }

    public double getImporte() { return importe; }
    public abstract String getDescripcion();
}