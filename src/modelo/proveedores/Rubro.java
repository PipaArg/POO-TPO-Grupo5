package modelo.proveedores;

public class Rubro {
    private String codigo;
    private String nombre;
    private String descripcion;

    public Rubro(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}