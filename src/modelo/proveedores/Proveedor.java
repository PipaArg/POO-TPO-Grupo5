package modelo.proveedores;

import modelo.comprobantes.Comprobante;
import modelo.enums.CondicionIVA;
import modelo.impuestos.Impuesto;
import modelo.pagos.OrdenDePago;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Proveedor {
    private String cuit;
    private String razonSocial;
    private String nombreFantasia;
    private String domicilioComercial;
    private String telefono;
    private String email;
    private CondicionIVA condicionIVA;
    private String numeroIngresosBrutos;
    private Date fechaInicioActividades;
    private double topeMaximoDeuda;
    private List<Rubro> rubros;
    private List<CertificadoNoRetencion> certificados;
    private CuentaCorriente cuentaCorriente;

    public Proveedor(String cuit, String razonSocial, String nombreFantasia,
                    String domicilioComercial, String telefono, String email,
                    CondicionIVA condicionIVA, String numeroIngresosBrutos,
                    Date fechaInicioActividades, double topeMaximoDeuda) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.nombreFantasia = nombreFantasia;
        this.domicilioComercial = domicilioComercial;
        this.telefono = telefono;
        this.email = email;
        this.condicionIVA = condicionIVA;
        this.numeroIngresosBrutos = numeroIngresosBrutos;
        this.fechaInicioActividades = fechaInicioActividades;
        this.topeMaximoDeuda = topeMaximoDeuda;
        this.rubros = new ArrayList<>();
        this.certificados = new ArrayList<>();
        this.cuentaCorriente = new CuentaCorriente();
    }

    public void actualizarDatos(String razonSocial, String domicilio, String telefono, String email) {
        this.razonSocial = razonSocial;
        this.domicilioComercial = domicilio;
        this.telefono = telefono;
        this.email = email;
    }

    public void agregarRubro(Rubro r) { rubros.add(r); }

    public double getDeudaVigente() {
        return cuentaCorriente.getSaldoActual();
    }

    public boolean puedeRecibirOC(double monto) {
        return (getDeudaVigente() + monto) <= topeMaximoDeuda;
    }

    public CertificadoNoRetencion getCertificadoVigente(Impuesto impuesto) {
        for (CertificadoNoRetencion c : certificados) {
            if (c.getImpuesto().equals(impuesto) && c.estaVigente()) {
                return c;
            }
        }
        return null;
    }

    public boolean tieneCertificadoVigenteDe(Impuesto impuesto) {
        return getCertificadoVigente(impuesto) != null;
    }

    public CuentaCorriente getCuentaCorriente() { return cuentaCorriente; }
    public String getCuit() { return cuit; }
    public String getRazonSocial() { return razonSocial; }
    public double getTopeMaximoDeuda() { return topeMaximoDeuda; }

    // Consultas (Consigna 7) — se completan en Bloque 2
    public List<Comprobante> getDocumentosImpagos() {
        return cuentaCorriente.getDocumentosImpagos();
    }

    public List<OrdenDePago> getPagosRealizados() {
        return cuentaCorriente.getPagosRealizados();
    }
}