package modelo.comprobantes;

import modelo.catalogo.Producto;

public class ItemComprobante {
    private Producto producto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public ItemComprobante(Producto producto, int cantidad, double precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }
}