package modelo.catalogo;

import modelo.proveedores.Proveedor;
import java.util.Date;

public class PrecioAcordado {
    private double precio;
    private Date fechaAcuerdo;
    private Proveedor proveedor;
    private Producto producto;

    public PrecioAcordado(double precio, Date fechaAcuerdo, Proveedor proveedor, Producto producto) {
        this.precio = precio;
        this.fechaAcuerdo = fechaAcuerdo;
        this.proveedor = proveedor;
        this.producto = producto;
    }

    public double getPrecio() { return precio; }
    public Date getFechaAcuerdo() { return fechaAcuerdo; }
    public Proveedor getProveedor() { return proveedor; }
    public Producto getProducto() { return producto; }
}