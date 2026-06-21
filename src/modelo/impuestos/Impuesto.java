package modelo.impuestos;

import java.util.ArrayList;
import java.util.List;

public class Impuesto {
    private String codigo;
    private String nombre;
    private double porcentajeDefault;
    private double minimoNoImponible;
    private List<Tramo> tramos;

    public Impuesto(String codigo, String nombre, double porcentajeDefault, double minimoNoImponible) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.porcentajeDefault = porcentajeDefault;
        this.minimoNoImponible = minimoNoImponible;
        this.tramos = new ArrayList<>();
    }

    public double calcularRetencion(double montoBase) {
        if (montoBase <= minimoNoImponible) return 0;
        if (!tramos.isEmpty()) {
            for (Tramo t : tramos) {
                if (t.aplicaA(montoBase)) {
                    return montoBase * t.getPorcentaje() / 100;
                }
            }
        }
        return montoBase * porcentajeDefault / 100;
    }

    public boolean tieneTramos() { return !tramos.isEmpty(); }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPorcentajeDefault() { return porcentajeDefault; }
    public double getMinimoNoImponible() { return minimoNoImponible; }
}