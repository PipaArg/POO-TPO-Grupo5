package controladores;

import modelo.impuestos.Impuesto;
import java.util.ArrayList;
import java.util.List;

public class GestorImpuestos {

    // ── Singleton ──────────────────────────────────────────────────────
    private static GestorImpuestos instancia;

    private GestorImpuestos() {
        this.impuestos = new ArrayList<>();
    }

    public static GestorImpuestos getInstancia() {
        if (instancia == null) {
            instancia = new GestorImpuestos();
        }
        return instancia;
    }

    // ── Colecciones ────────────────────────────────────────────────────
    private List<Impuesto> impuestos;

    // ── Altas ──────────────────────────────────────────────────────────
    public void registrarImpuesto(Impuesto i) {
        impuestos.add(i);
    }

    // ── Consultas ──────────────────────────────────────────────────────
    public List<Impuesto> getImpuestos() {
        return impuestos;
    }

    public Impuesto buscarImpuesto(String codigo) {
        for (Impuesto i : impuestos) {
            if (i.getCodigo().equals(codigo)) return i;
        }
        return null;
    }
}
