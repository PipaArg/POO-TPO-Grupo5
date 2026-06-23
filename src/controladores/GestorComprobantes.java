package controladores;

import modelo.comprobantes.Comprobante;
import modelo.comprobantes.Factura;
import modelo.comprobantes.ResultadoValidacion;
import modelo.ordenes.OrdenDeCompra;
import modelo.usuarios.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GestorComprobantes {

    // ── Singleton ──────────────────────────────────────────────────────
    private static GestorComprobantes instancia;

    private GestorComprobantes() {
        this.comprobantes = new ArrayList<>();
    }

    public static GestorComprobantes getInstancia() {
        if (instancia == null) {
            instancia = new GestorComprobantes();
        }
        return instancia;
    }

    // ── Colecciones ────────────────────────────────────────────────────
    private List<Comprobante> comprobantes;

    // ── Operaciones de negocio ─────────────────────────────────────────
    public ResultadoValidacion registrarFactura(Factura factura, OrdenDeCompra oc) {
        ResultadoValidacion resultado = factura.validarContraOC(oc);
        if (resultado.isValido()) {
            factura.vincularOC(oc);
            comprobantes.add(factura);
        }
        return resultado;
    }

    public void registrarFacturaAutorizada(Factura factura, Usuario supervisor) {
        factura.marcarAutorizada(supervisor, factura.getMotivoDesvio());
        comprobantes.add(factura);
    }

    public void rechazarFactura(Factura factura) {
        // La factura no se registra
    }

    // ── Búsquedas ──────────────────────────────────────────────────────
    public Comprobante buscarComprobante(String numero) {
        for (Comprobante c : comprobantes) {
            if (c.getNumero().equals(numero)) return c;
        }
        return null;
    }

    // ── Consultas ──────────────────────────────────────────────────────
    public List<Comprobante> getComprobantes() {
        return comprobantes;
    }
}
