package modelo.ordenes;

import modelo.catalogo.Producto;
import modelo.proveedores.Proveedor;
import modelo.usuarios.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdenDeCompra {
    private String numero;
    private Date fechaEmision;
    private boolean requirioAutorizacion;
    private Date fechaAutorizacion;
    private List<ItemOrdenDeCompra> items;
    private Proveedor proveedor;
    private Usuario supervisor;

    public OrdenDeCompra(String numero, Proveedor proveedor) {
        this.numero = numero;
        this.proveedor = proveedor;
        this.fechaEmision = new Date();
        this.requirioAutorizacion = false;
        this.items = new ArrayList<>();
    }

    public void crearItem(Producto p, int cantidad, double precio) {
        items.add(new ItemOrdenDeCompra(p, cantidad, precio));
    }

    public void confirmarGeneracion() {
        this.fechaEmision = new Date();
    }

    public void marcarAutorizada(Usuario supervisor) {
        this.supervisor = supervisor;
        this.requirioAutorizacion = true;
        this.fechaAutorizacion = new Date();
    }

    public ItemOrdenDeCompra buscarItemPorProducto(Producto p) {
        for (ItemOrdenDeCompra item : items) {
            if (item.getProducto().equals(p)) {
                return item;
            }
        }
        return null;
    }

    public double calcularTotalOC() {
        double total = 0;
        for (ItemOrdenDeCompra item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public double getImporteTotal() { return calcularTotalOC(); }
    public String getNumero() { return numero; }
    public Date getFechaEmision() { return fechaEmision; }
    public boolean estaAutorizada() { return requirioAutorizacion; }
    public Usuario getSupervisor() { return supervisor; }
    public List<ItemOrdenDeCompra> getItems() { return items; }
    public Proveedor getProveedor() { return proveedor; }
}