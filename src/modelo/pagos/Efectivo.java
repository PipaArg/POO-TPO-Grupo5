package modelo.pagos;

public class Efectivo extends MedioDePago {

    public Efectivo(double importe) {
        super(importe);
    }

    @Override
    public String getDescripcion() {
        return "Efectivo por $" + getImporte();
    }
}