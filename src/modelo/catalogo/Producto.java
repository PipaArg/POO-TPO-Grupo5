package modelo.catalogo;

import modelo.enums.TipoIVA;
import modelo.enums.UnidadDeMedida;
import modelo.proveedores.Rubro;
import java.util.ArrayList;
import java.util.List;

public class Producto {
    private String codigo;
    private String descripcion;
    private UnidadDeMedida unidadDeMedida;
    private TipoIVA tipoIVA;
    private Rubro rubro;
    private List<PrecioAcordado> preciosAcordados;

    public Producto(String codigo, String descripcion, UnidadDeMedida unidadDeMedida, TipoIVA tipoIVA, Rubro rubro) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.unidadDeMedida = unidadDeMedida;
        this.tipoIVA = tipoIVA;
        this.rubro = rubro;
        this.preciosAcordados = new ArrayList<>();
    }

    public void agregarPrecioAcordado(PrecioAcordado p) {
        preciosAcordados.add(p);
    }

    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public TipoIVA getTipoIVA() { return tipoIVA; }
    public UnidadDeMedida getUnidadDeMedida() { return unidadDeMedida; }
    public List<PrecioAcordado> getHistorialPrecios() { return preciosAcordados; }
    public Rubro getRubro() { return rubro; }
}
