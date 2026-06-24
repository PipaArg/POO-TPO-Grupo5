import vistas.*;
import controladores.*;
import modelo.proveedores.*;
import modelo.catalogo.*;
import modelo.enums.*;
import modelo.impuestos.*;
import modelo.comprobantes.*;
import modelo.ordenes.*;
import modelo.pagos.*;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        cargarDatosDePrueba();
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }

    public static class VentanaPrincipal extends JFrame {

        public static CardLayout cardLayout;
        public static JPanel panelPrincipal;

        public VentanaPrincipal() {
            setTitle("FarmaRed - Sistema de Gestión");
            setSize(900, 650);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            cardLayout = new CardLayout();
            panelPrincipal = new JPanel(cardLayout);

            Runnable volverAlMenu = () -> {
                cardLayout.show(panelPrincipal, "MENU");
            };

            panelPrincipal.add(crearPanelMenu(),                              "MENU");
            panelPrincipal.add(new PanelGestionUsuarios(volverAlMenu),         "USUARIOS");
            panelPrincipal.add(new PanelGestionProveedores(volverAlMenu),      "PROVEEDORES");
            panelPrincipal.add(new PanelCatalogo(volverAlMenu),                "CATALOGO");
            panelPrincipal.add(new PanelGenerarOC(volverAlMenu),               "GENERAR_OC");
            panelPrincipal.add(new PanelComprobantes(volverAlMenu),            "COMPROBANTES");
            panelPrincipal.add(new PanelOrdenesPago(volverAlMenu),             "ORDENES_PAGO");
            panelPrincipal.add(new PanelConsultas(volverAlMenu),               "CONSULTAS");
            panelPrincipal.add(new PanelConsultarCC(volverAlMenu),             "CONSULTAR_CC");

            add(panelPrincipal);
            cardLayout.show(panelPrincipal, "MENU");
        }

        private JPanel crearPanelMenu() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBackground(new Color(245, 245, 245));

            // Título
            JLabel lblTitulo = new JLabel("FarmaRed", SwingConstants.CENTER);
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 36));
            lblTitulo.setForeground(new Color(0, 100, 180));
            lblTitulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 5, 0));

            JLabel lblSubtitulo = new JLabel(
                "Sistema de Gestión de Compras", SwingConstants.CENTER);
            lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
            lblSubtitulo.setForeground(Color.GRAY);

            JPanel panelTitulos = new JPanel(new GridLayout(2, 1));
            panelTitulos.setBackground(new Color(245, 245, 245));
            panelTitulos.add(lblTitulo);
            panelTitulos.add(lblSubtitulo);

            // Botones
            JPanel panelBotones = new JPanel(new GridLayout(4, 2, 15, 15));
            panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
            panelBotones.setBackground(new Color(245, 245, 245));

            JButton btn1 = crearBoton("👤  Gestión de Usuarios",      new Color(70, 130, 180));
            JButton btn2 = crearBoton("🏢  Gestión de Proveedores",   new Color(70, 130, 180));
            JButton btn3 = crearBoton("📦  Catálogo de Productos",    new Color(70, 130, 180));
            JButton btn4 = crearBoton("📋  Órdenes de Compra",        new Color(70, 130, 180));
            JButton btn5 = crearBoton("🧾  Comprobantes",             new Color(70, 130, 180));
            JButton btn6 = crearBoton("💰  Órdenes de Pago",          new Color(70, 130, 180));
            JButton btn7 = crearBoton("📊  Consultas y Reportes",     new Color(70, 130, 180));
            JButton btn8 = crearBoton("❌  Salir",                    new Color(180, 60, 60));

            panelBotones.add(btn1);
            panelBotones.add(btn2);
            panelBotones.add(btn3);
            panelBotones.add(btn4);
            panelBotones.add(btn5);
            panelBotones.add(btn6);
            panelBotones.add(btn7);
            panelBotones.add(btn8);

            panel.add(panelTitulos, BorderLayout.NORTH);
            panel.add(panelBotones, BorderLayout.CENTER);

            btn1.addActionListener(e -> cardLayout.show(panelPrincipal, "USUARIOS"));
            btn2.addActionListener(e -> cardLayout.show(panelPrincipal, "PROVEEDORES"));
            btn3.addActionListener(e -> cardLayout.show(panelPrincipal, "CATALOGO"));
            btn4.addActionListener(e -> cardLayout.show(panelPrincipal, "GENERAR_OC"));
            btn5.addActionListener(e -> cardLayout.show(panelPrincipal, "COMPROBANTES"));
            btn6.addActionListener(e -> cardLayout.show(panelPrincipal, "ORDENES_PAGO"));
            btn7.addActionListener(e -> cardLayout.show(panelPrincipal, "CONSULTAS"));
            btn8.addActionListener(e -> System.exit(0));

            return panel;
        }

        private JButton crearBoton(String texto, Color color) {
            JButton btn = new JButton(texto);
            btn.setFont(new Font("Arial", Font.PLAIN, 14));
            btn.setBackground(color);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setPreferredSize(new Dimension(200, 60));
            return btn;
        }
    }
    private static void cargarDatosDePrueba() {
        GestorProveedores gestorProv = GestorProveedores.getInstancia();
        GestorCatalogo gestorCat = GestorCatalogo.getInstancia();
        GestorImpuestos gestorImp = GestorImpuestos.getInstancia();
        GestorComprobantes gestorComp = GestorComprobantes.getInstancia();
        GestorUsuarios gestorUsr = GestorUsuarios.getInstancia();
        GestorOC gestorOC = GestorOC.getInstancia();
        GestorPagos gestorPagos = GestorPagos.getInstancia();

        // ── USUARIOS ──────────────────────────────────────────────────────
        gestorUsr.registrarUsuario(new modelo.usuarios.Usuario(
            "Admin Principal", "admin", "admin@farmared.com",
            modelo.enums.Rol.ADMINISTRADOR));
        gestorUsr.registrarUsuario(new modelo.usuarios.Usuario(
            "Supervisor General", "supervisor", "sup@farmared.com",
            modelo.enums.Rol.SUPERVISOR));
        gestorUsr.registrarUsuario(new modelo.usuarios.Usuario(
            "Juan Pérez", "jperez", "jperez@farmared.com",
            modelo.enums.Rol.USUARIO));

        // ── RUBROS ────────────────────────────────────────────────────────
        Rubro rubroMed = new Rubro("R01", "Medicamentos", "Fármacos generales");
        Rubro rubroIns = new Rubro("R02", "Insumos", "Insumos descartables");
        Rubro rubroLim = new Rubro("R03", "Limpieza", "Productos de limpieza");
        gestorProv.registrarRubro(rubroMed);
        gestorProv.registrarRubro(rubroIns);
        gestorProv.registrarRubro(rubroLim);

        // ── PROVEEDORES ───────────────────────────────────────────────────
        Proveedor prov1 = new Proveedor(
            "30-11111111-9", "Laboratorio Roemmers S.A.", "Roemmers",
            "Carlos Calvo 3135, CABA", "011-4321-5678",
            "contacto@roemmers.com.ar",
            CondicionIVA.RESPONSABLE_INSCRIPTO, "901-234567-8",
            new Date(), 100000.0);
        prov1.agregarRubro(rubroMed);
        gestorProv.registrarProveedor(prov1);

        Proveedor prov2 = new Proveedor(
            "30-22222222-9", "Droguería Del Sud S.A.", "Del Sud",
            "Av. Rivadavia 5000, CABA", "011-4567-8901",
            "contacto@delsud.com.ar",
            CondicionIVA.RESPONSABLE_INSCRIPTO, "901-987654-3",
            new Date(), 50000.0);
        prov2.agregarRubro(rubroIns);
        gestorProv.registrarProveedor(prov2);

        Proveedor prov3 = new Proveedor(
            "30-33333333-9", "Limpieza Total S.R.L.", "LimTotal",
            "Corrientes 2500, CABA", "011-4111-2222",
            "info@limpiezatotal.com.ar",
            CondicionIVA.MONOTRIBUTISTA, "901-111222-3",
            new Date(), 20000.0);
        prov3.agregarRubro(rubroLim);
        gestorProv.registrarProveedor(prov3);

        // ── PRODUCTOS ─────────────────────────────────────────────────────
        Producto p1 = new Producto("P01", "Amoxidal 500mg",
            UnidadDeMedida.UNIDAD, TipoIVA.VEINTIUNO, rubroMed);
        Producto p2 = new Producto("P02", "Paracetamol 1g",
            UnidadDeMedida.CAJA, TipoIVA.DIEZ_CINCO, rubroMed);
        Producto p3 = new Producto("P03", "Ibuprofeno 400mg",
            UnidadDeMedida.UNIDAD, TipoIVA.VEINTIUNO, rubroMed);
        Producto p4 = new Producto("P04", "Guantes descartables x100",
            UnidadDeMedida.CAJA, TipoIVA.VEINTIUNO, rubroIns);
        Producto p5 = new Producto("P05", "Jeringa 5ml x50",
            UnidadDeMedida.CAJA, TipoIVA.VEINTIUNO, rubroIns);
        Producto p6 = new Producto("P06", "Lavandina 1L",
            UnidadDeMedida.LITRO, TipoIVA.VEINTIUNO, rubroLim);

        gestorCat.registrarProducto(p1);
        gestorCat.registrarProducto(p2);
        gestorCat.registrarProducto(p3);
        gestorCat.registrarProducto(p4);
        gestorCat.registrarProducto(p5);
        gestorCat.registrarProducto(p6);

        p1.agregarPrecioAcordado(new PrecioAcordado(1500.0, new Date(), prov1, p1));
        p2.agregarPrecioAcordado(new PrecioAcordado(800.0, new Date(), prov1, p2));
        p3.agregarPrecioAcordado(new PrecioAcordado(950.0, new Date(), prov1, p3));
        p4.agregarPrecioAcordado(new PrecioAcordado(2500.0, new Date(), prov2, p4));
        p5.agregarPrecioAcordado(new PrecioAcordado(3200.0, new Date(), prov2, p5));
        p6.agregarPrecioAcordado(new PrecioAcordado(400.0, new Date(), prov3, p6));

        // ── IMPUESTOS ─────────────────────────────────────────────────────
        Impuesto ganancias = new Impuesto(
            "IMP01", "Retención Ganancias", 2.0, 1000.0);
        Impuesto iibb = new Impuesto(
            "IMP02", "Ingresos Brutos", 1.5, 500.0);
        Impuesto ivaRet = new Impuesto(
            "IMP03", "Retención IVA", 10.5, 2000.0);
        gestorImp.registrarImpuesto(ganancias);
        gestorImp.registrarImpuesto(iibb);
        gestorImp.registrarImpuesto(ivaRet);

        // ── ÓRDENES DE COMPRA ─────────────────────────────────────────────
        OrdenDeCompra oc1 = new OrdenDeCompra("OC-001", prov1);
        oc1.crearItem(p1, 20, 1500.0);
        oc1.crearItem(p2, 10, 800.0);
        oc1.confirmarGeneracion();
        gestorOC.getOrdenesDeCompra().add(oc1);

        OrdenDeCompra oc2 = new OrdenDeCompra("OC-002", prov2);
        oc2.crearItem(p4, 5, 2500.0);
        oc2.crearItem(p5, 3, 3200.0);
        oc2.confirmarGeneracion();
        gestorOC.getOrdenesDeCompra().add(oc2);

        OrdenDeCompra oc3 = new OrdenDeCompra("OC-003", prov3);
        oc3.crearItem(p6, 50, 400.0);
        oc3.confirmarGeneracion();
        gestorOC.getOrdenesDeCompra().add(oc3);

        // ── COMPROBANTES ──────────────────────────────────────────────────
        Factura f1 = new Factura("FAC-000001", new Date(),
            38000.0, 7980.0, LetraComprobante.A, prov1);
        f1.agregarItem(new ItemComprobante(p1, 20, 1500.0));
        f1.agregarItem(new ItemComprobante(p2, 10, 800.0));
        f1.vincularOC(oc1);
        gestorComp.getComprobantes().add(f1);
        prov1.getCuentaCorriente().asociarComprobante(f1);
        prov1.getCuentaCorriente().registrarMovimiento(new Movimiento(
            new Date(), "Factura", f1.getNumero(),
            f1.getImporteTotal(), 0,
            prov1.getCuentaCorriente().getSaldoActual() + f1.getImporteTotal()));

        Factura f2 = new Factura("FAC-000002", new Date(),
            22100.0, 4641.0, LetraComprobante.A, prov2);
        f2.agregarItem(new ItemComprobante(p4, 5, 2500.0));
        f2.agregarItem(new ItemComprobante(p5, 3, 3200.0));
        f2.vincularOC(oc2);
        gestorComp.getComprobantes().add(f2);
        prov2.getCuentaCorriente().asociarComprobante(f2);
        prov2.getCuentaCorriente().registrarMovimiento(new Movimiento(
            new Date(), "Factura", f2.getNumero(),
            f2.getImporteTotal(), 0,
            prov2.getCuentaCorriente().getSaldoActual() + f2.getImporteTotal()));

        Factura f3 = new Factura("FAC-000003", new Date(),
            20000.0, 4200.0, LetraComprobante.C, prov3);
        f3.agregarItem(new ItemComprobante(p6, 50, 400.0));
        f3.vincularOC(oc3);
        gestorComp.getComprobantes().add(f3);
        prov3.getCuentaCorriente().asociarComprobante(f3);
        prov3.getCuentaCorriente().registrarMovimiento(new Movimiento(
            new Date(), "Factura", f3.getNumero(),
            f3.getImporteTotal(), 0,
            prov3.getCuentaCorriente().getSaldoActual() + f3.getImporteTotal()));

        NotaDeCredito nc1 = new NotaDeCredito("NC-000001", new Date(),
            5000.0, 1050.0, LetraComprobante.A, prov1, f1);
        gestorComp.getComprobantes().add(nc1);
        prov1.getCuentaCorriente().asociarComprobante(nc1);
        prov1.getCuentaCorriente().registrarMovimiento(new Movimiento(
            new Date(), "Nota de Crédito", nc1.getNumero(),
            0, nc1.getImporteTotal(),
            prov1.getCuentaCorriente().getSaldoActual() - nc1.getImporteTotal()));

        // ── ORDEN DE PAGO ─────────────────────────────────────────────────
        OrdenDePago op1 = new OrdenDePago("OP-001", prov1);
        op1.agregarCancelacion(f1, f1.getImporteTotal());
        op1.agregarRetencion(new RetencionAplicada(
            ganancias, f1.getImporteNeto(), ganancias.getPorcentajeDefault()));
        op1.agregarRetencion(new RetencionAplicada(
            iibb, f1.getImporteNeto(), iibb.getPorcentajeDefault()));
        op1.agregarMedioDePago(new Efectivo(op1.getImporteNeto()));
        f1.registrarCancelacion(op1);
        prov1.getCuentaCorriente().asociarPago(op1);
        prov1.getCuentaCorriente().registrarMovimiento(new Movimiento(
            new Date(), "Orden de Pago", op1.getNumero(),
            0, op1.getImporteNeto(),
            prov1.getCuentaCorriente().getSaldoActual() - op1.getImporteNeto()));
        gestorPagos.getOrdenesDePago().add(op1);
    }
    
}