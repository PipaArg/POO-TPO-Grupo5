package tests;

import controladores.GestorOC;
import controladores.GestorProveedores;
import controladores.GestorImpuestos;
import modelo.catalogo.Producto;
import modelo.comprobantes.Factura;
import modelo.comprobantes.ItemComprobante;
import modelo.comprobantes.ResultadoValidacion;
import modelo.enums.CondicionIVA;
import modelo.enums.LetraComprobante;
import modelo.enums.TipoIVA;
import modelo.enums.UnidadDeMedida;
import modelo.impuestos.Impuesto;
import modelo.ordenes.OrdenDeCompra;
import modelo.proveedores.Proveedor;
import modelo.proveedores.Rubro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FarmaRedTests {

    private Proveedor proveedor;
    private Producto producto;
    private Rubro rubro;
    private Impuesto impuesto;

    // ── Setup — se ejecuta antes de CADA test ──────────────────────────
    @BeforeEach
    public void setUp() {
        rubro = new Rubro("R001", "Medicamentos", "Medicamentos en general");

        proveedor = new Proveedor(
            "20-12345678-9",
            "Laboratorio ABC S.A.",
            "LabABC",
            "Av. Corrientes 1234",
            "011-4444-5555",
            "lab@abc.com",
            CondicionIVA.RESPONSABLE_INSCRIPTO,
            "123456789",
            new Date(),
            10000.0
        );

        producto = new Producto(
            "P001",
            "Ibuprofeno 400mg x 20",
            UnidadDeMedida.CAJA,
            TipoIVA.VEINTIUNO,
            rubro
        );

        impuesto = new Impuesto(
            "IVA-RET",
            "Retención IVA",
            10.5,
            1000.0
        );
    }

    // ══════════════════════════════════════════════════════════════════
    // TEST 1 — Proveedor puede recibir OC dentro del tope
    // ══════════════════════════════════════════════════════════════════
    @Test
    public void testProveedorPuedeRecibirOCDentroDelTope() {
        // Setup
        double montoOC = 5000.0;

        // Exercise
        boolean puede = proveedor.puedeRecibirOC(montoOC);

        // Verify
        assertTrue(puede,
            "El proveedor debería poder recibir la OC porque no supera el tope");
    }

    // ══════════════════════════════════════════════════════════════════
    // TEST 2 — Proveedor NO puede recibir OC que supera el tope
    // ══════════════════════════════════════════════════════════════════
    @Test
    public void testProveedorNoPuedeRecibirOCFueraDelTope() {
        // Setup
        double montoOC = 15000.0;

        // Exercise
        boolean puede = proveedor.puedeRecibirOC(montoOC);

        // Verify
        assertFalse(puede,
            "El proveedor NO debería poder recibir la OC porque supera el tope");
    }

    // ══════════════════════════════════════════════════════════════════
    // TEST 3 — Total de la OC se calcula correctamente
    // ══════════════════════════════════════════════════════════════════
    @Test
    public void testCalculoTotalOrdenDeCompra() {
        // Setup
        OrdenDeCompra oc = new OrdenDeCompra("OC-001", proveedor);

        // Exercise
        oc.crearItem(producto, 10, 100.0);
        oc.crearItem(producto, 5, 200.0);
        double total = oc.calcularTotalOC();

        // Verify
        assertEquals(2000.0, total, 0.01,
            "El total de la OC debería ser 2000.0");
    }

    // ══════════════════════════════════════════════════════════════════
    // TEST 4 — Factura válida cuando precios coinciden con OC
    // ══════════════════════════════════════════════════════════════════
    @Test
    public void testValidacionFacturaContraOCExitosa() {
        // Setup
        OrdenDeCompra oc = new OrdenDeCompra("OC-001", proveedor);
        oc.crearItem(producto, 10, 100.0);

        Factura factura = new Factura(
            "F-001",
            new Date(),
            800.0,
            168.0,
            LetraComprobante.A,
            proveedor
        );
        factura.agregarItem(new ItemComprobante(producto, 10, 100.0));

        // Exercise
        ResultadoValidacion resultado = factura.validarContraOC(oc);

        // Verify
        assertTrue(resultado.isValido(),
            "La factura debería ser válida porque los precios coinciden");
    }

    // ══════════════════════════════════════════════════════════════════
    // TEST 5 — Factura inválida cuando precio supera el de la OC
    // ══════════════════════════════════════════════════════════════════
    @Test
    public void testValidacionFacturaContraOCConDesvio() {
        // Setup
        OrdenDeCompra oc = new OrdenDeCompra("OC-001", proveedor);
        oc.crearItem(producto, 10, 100.0);

        Factura factura = new Factura(
            "F-002",
            new Date(),
            1200.0,
            252.0,
            LetraComprobante.A,
            proveedor
        );
        factura.agregarItem(new ItemComprobante(producto, 10, 150.0));

        // Exercise
        ResultadoValidacion resultado = factura.validarContraOC(oc);

        // Verify
        assertFalse(resultado.isValido(),
            "La factura NO debería ser válida porque el precio supera el acordado");
    }
}