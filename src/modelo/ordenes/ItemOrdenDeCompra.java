package modelo.ordenes;

import modelo.catalogo.Producto;

public class ItemOrdenDeCompra {
    private int cantidad;
    private double precioUnitarioAcordado;
    private Producto producto;

    public ItemOrdenDeCompra(Producto producto, int cantidad, double precioUnitarioAcordado) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitarioAcordado = precioUnitarioAcordado;
    }

    public double getSubtotal() { return cantidad * precioUnitarioAcordado; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitarioAcordado; }
    public Producto getProducto() { return producto; }
}