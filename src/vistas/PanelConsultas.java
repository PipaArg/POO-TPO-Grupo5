package vistas;

import controladores.GestorComprobantes;
import controladores.GestorOC;
import controladores.GestorPagos;
import controladores.GestorProveedores;
import controladores.GestorImpuestos;
import modelo.comprobantes.Comprobante;
import modelo.comprobantes.Factura;
import modelo.impuestos.Impuesto;
import modelo.ordenes.OrdenDeCompra;
import modelo.pagos.OrdenDePago;
import modelo.pagos.RetencionAplicada;
import modelo.proveedores.Proveedor;
import modelo.catalogo.PrecioAcordado;
import modelo.catalogo.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PanelConsultas extends JPanel {

    private GestorProveedores gestorProveedores;
    private GestorComprobantes gestorComprobantes;
    private GestorOC gestorOC;
    private GestorPagos gestorPagos;
    private GestorImpuestos gestorImpuestos;
    private Runnable onVolver;

    // Componentes
    private JTabbedPane tabs;
    private JButton btnVolver;

    // Tab 1 — Cuenta Corriente
    private JTextField txtCuitCC;
    private JTable tablaCC;
    private DefaultTableModel modeloCC;

    // Tab 2 — Documentos Impagos
    private JTextField txtCuitImpagos;
    private JTable tablaImpagos;
    private DefaultTableModel modeloImpagos;

    // Tab 3 — Pagos Realizados
    private JTextField txtCuitPagos;
    private JTable tablaPagos;
    private DefaultTableModel modeloPagos;

    // Tab 4 — Deuda Vigente por Proveedor
    private JTable tablaDeuda;
    private DefaultTableModel modeloDeuda;

    // Tab 5 — OC Emitidas
    private JTable tablaOC;
    private DefaultTableModel modeloOC;

    // Tab 6 — OP Emitidas
    private JTable tablaOP;
    private DefaultTableModel modeloOP;

    // Tab 7 — Total Retenido por Impuesto
    private JTable tablaRetenciones;
    private DefaultTableModel modeloRetenciones;

    // Tab 8 — Compulsa de Precios
    private JTextField txtCodigoProducto;
    private JTable tablaPrecios;
    private DefaultTableModel modeloPrecios;

    // Tab 9 — Libro IVA
    private JTable tablaLibroIVA;
    private DefaultTableModel modeloLibroIVA;

    public PanelConsultas(Runnable onVolver) {
        this.gestorProveedores  = GestorProveedores.getInstancia();
        this.gestorComprobantes = GestorComprobantes.getInstancia();
        this.gestorOC           = GestorOC.getInstancia();
        this.gestorPagos        = GestorPagos.getInstancia();
        this.gestorImpuestos    = GestorImpuestos.getInstancia();
        this.onVolver = onVolver;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // ── Título y volver ─────────────────────────────────────────────
        JPanel panelTitulo = new JPanel(new BorderLayout());
        btnVolver = new JButton("← Volver al Menú");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel lblTitulo = new JLabel("Consultas y Reportes", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelTitulo.add(btnVolver, BorderLayout.WEST);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        add(panelTitulo, BorderLayout.NORTH);

        // ── Tabs ────────────────────────────────────────────────────────
        tabs = new JTabbedPane();

        tabs.addTab("Cuenta Corriente",     crearTabCC());
        tabs.addTab("Docs. Impagos",        crearTabImpagos());
        tabs.addTab("Pagos Realizados",     crearTabPagos());
        tabs.addTab("Deuda por Proveedor",  crearTabDeuda());
        tabs.addTab("OC Emitidas",          crearTabOC());
        tabs.addTab("OP Emitidas",          crearTabOP());
        tabs.addTab("Retenciones",          crearTabRetenciones());
        tabs.addTab("Compulsa Precios",     crearTabPrecios());
        tabs.addTab("Libro IVA",            crearTabLibroIVA());

        add(tabs, BorderLayout.CENTER);

        // ── Eventos ─────────────────────────────────────────────────────
        btnVolver.addActionListener(e -> onVolver.run());
    }

    // ══════════════════════════════════════════════════════════════════
    // TAB 1 — Cuenta Corriente
    // ══════════════════════════════════════════════════════════════════
    private JPanel crearTabCC() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBusqueda.add(new JLabel("CUIT:"));
        txtCuitCC = new JTextField(15);
        txtCuitCC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '-'
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE)
                    e.consume();
            }
        });
        panelBusqueda.add(txtCuitCC);
        JButton btnBuscar = new JButton("Consultar");
        panelBusqueda.add(btnBuscar);
        panel.add(panelBusqueda, BorderLayout.NORTH);

        String[] col = {"Fecha", "Tipo", "N° Documento", "Debe", "Haber", "Saldo"};
        modeloCC = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCC = new JTable(modeloCC);
        panel.add(new JScrollPane(tablaCC), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> {
            String cuit = txtCuitCC.getText().trim();
            Proveedor p = gestorProveedores.buscarProveedor(cuit);
            if (p == null) {
                JOptionPane.showMessageDialog(this,
                    "Proveedor no encontrado.", "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            modeloCC.setRowCount(0);
            p.getCuentaCorriente().getMovimientos().forEach(m ->
                modeloCC.addRow(new Object[]{
                    m.getFecha(), m.getTipoMovimiento(),
                    m.getNumeroDocumento(),
                    String.format("$%.2f", m.getDebe()),
                    String.format("$%.2f", m.getHaber()),
                    String.format("$%.2f", m.getSaldo())
                })
            );
        });

        return panel;
    }

    // ══════════════════════════════════════════════════════════════════
    // TAB 2 — Documentos Impagos
    // ══════════════════════════════════════════════════════════════════
    private JPanel crearTabImpagos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBusqueda.add(new JLabel("CUIT:"));
        txtCuitImpagos = new JTextField(15);
        txtCuitImpagos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '-'
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE)
                    e.consume();
            }
        });
        panelBusqueda.add(txtCuitImpagos);
        JButton btnBuscar = new JButton("Consultar");
        panelBusqueda.add(btnBuscar);
        panel.add(panelBusqueda, BorderLayout.NORTH);

        String[] col = {"N° Comprobante", "Tipo", "Fecha", "Importe Total"};
        modeloImpagos = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaImpagos = new JTable(modeloImpagos);
        panel.add(new JScrollPane(tablaImpagos), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> {
            String cuit = txtCuitImpagos.getText().trim();
            Proveedor p = gestorProveedores.buscarProveedor(cuit);
            if (p == null) {
                JOptionPane.showMessageDialog(this,
                    "Proveedor no encontrado.", "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            modeloImpagos.setRowCount(0);
            p.getCuentaCorriente().getDocumentosImpagos().forEach(c ->
                modeloImpagos.addRow(new Object[]{
                    c.getNumero(), c.getDescripcionTipo(),
                    c.getFechaEmision(),
                    String.format("$%.2f", c.getImporteTotal())
                })
            );
        });

        return panel;
    }

    // ══════════════════════════════════════════════════════════════════
    // TAB 3 — Pagos Realizados
    // ══════════════════════════════════════════════════════════════════
    private JPanel crearTabPagos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBusqueda.add(new JLabel("CUIT:"));
        txtCuitPagos = new JTextField(15);
        txtCuitPagos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '-'
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE)
                    e.consume();
            }
        });
        panelBusqueda.add(txtCuitPagos);
        JButton btnBuscar = new JButton("Consultar");
        panelBusqueda.add(btnBuscar);
        panel.add(panelBusqueda, BorderLayout.NORTH);

        String[] col = {"N° OP", "Fecha", "Total Medios", "Retenciones", "Neto"};
        modeloPagos = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPagos = new JTable(modeloPagos);
        panel.add(new JScrollPane(tablaPagos), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> {
            String cuit = txtCuitPagos.getText().trim();
            Proveedor p = gestorProveedores.buscarProveedor(cuit);
            if (p == null) {
                JOptionPane.showMessageDialog(this,
                    "Proveedor no encontrado.", "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            modeloPagos.setRowCount(0);
            p.getCuentaCorriente().getPagosRealizados().forEach(op ->
                modeloPagos.addRow(new Object[]{
                    op.getNumero(), op.getFechaEmision(),
                    String.format("$%.2f", op.getTotalMediosDePago()),
                    String.format("$%.2f", op.getTotalRetenciones()),
                    String.format("$%.2f", op.getImporteNeto())
                })
            );
        });

        return panel;
    }

    // ══════════════════════════════════════════════════════════════════
    // TAB 4 — Deuda Vigente por Proveedor
    // ══════════════════════════════════════════════════════════════════
    private JPanel crearTabDeuda() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JButton btnActualizar = new JButton("Actualizar");
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBtn.add(btnActualizar);
        panel.add(panelBtn, BorderLayout.NORTH);

        String[] col = {"CUIT", "Razón Social", "Tope Máximo", "Deuda Vigente", "Disponible"};
        modeloDeuda = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaDeuda = new JTable(modeloDeuda);
        panel.add(new JScrollPane(tablaDeuda), BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> {
            modeloDeuda.setRowCount(0);
            gestorProveedores.getProveedores().forEach(p ->
                modeloDeuda.addRow(new Object[]{
                    p.getCuit(),
                    p.getRazonSocial(),
                    String.format("$%.2f", p.getTopeMaximoDeuda()),
                    String.format("$%.2f", p.getDeudaVigente()),
                    String.format("$%.2f",
                        p.getTopeMaximoDeuda() - p.getDeudaVigente())
                })
            );
        });

        // Cargar al inicio
        btnActualizar.doClick();
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════
    // TAB 5 — OC Emitidas
    // ══════════════════════════════════════════════════════════════════
    private JPanel crearTabOC() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JButton btnActualizar = new JButton("Actualizar");
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBtn.add(btnActualizar);
        panel.add(panelBtn, BorderLayout.NORTH);

        String[] col = {"N° OC", "Proveedor", "Fecha Emisión",
                        "Total", "Autorizada"};
        modeloOC = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaOC = new JTable(modeloOC);
        panel.add(new JScrollPane(tablaOC), BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> {
            modeloOC.setRowCount(0);
            gestorOC.getOrdenesDeCompra().forEach(oc ->
                modeloOC.addRow(new Object[]{
                    oc.getNumero(),
                    oc.getProveedor().getRazonSocial(),
                    oc.getFechaEmision(),
                    String.format("$%.2f", oc.calcularTotalOC()),
                    oc.estaAutorizada() ? "✅ Sí" : "No"
                })
            );
        });

        btnActualizar.doClick();
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════
    // TAB 6 — OP Emitidas
    // ══════════════════════════════════════════════════════════════════
    private JPanel crearTabOP() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JButton btnActualizar = new JButton("Actualizar");
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBtn.add(btnActualizar);
        panel.add(panelBtn, BorderLayout.NORTH);

        String[] col = {"N° OP", "Proveedor", "Fecha",
                        "Retenciones", "Neto"};
        modeloOP = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaOP = new JTable(modeloOP);
        panel.add(new JScrollPane(tablaOP), BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> {
            modeloOP.setRowCount(0);
            gestorPagos.getOrdenesDePago().forEach(op ->
                modeloOP.addRow(new Object[]{
                    op.getNumero(),
                    op.getProveedor().getRazonSocial(),
                    op.getFechaEmision(),
                    String.format("$%.2f", op.getTotalRetenciones()),
                    String.format("$%.2f", op.getImporteNeto())
                })
            );
        });

        btnActualizar.doClick();
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════
    // TAB 7 — Total Retenido por Impuesto
    // ══════════════════════════════════════════════════════════════════
    private JPanel crearTabRetenciones() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JButton btnActualizar = new JButton("Actualizar");
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBtn.add(btnActualizar);
        panel.add(panelBtn, BorderLayout.NORTH);

        String[] col = {"Impuesto", "Total Retenido"};
        modeloRetenciones = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRetenciones = new JTable(modeloRetenciones);
        panel.add(new JScrollPane(tablaRetenciones), BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> {
            modeloRetenciones.setRowCount(0);
            Map<String, Double> totales = new HashMap<>();

            gestorPagos.getOrdenesDePago().forEach(op ->
                op.getRetenciones().forEach(r -> {
                    String nombre = r.getImpuesto().getNombre();
                    totales.merge(nombre, r.getMontoRetenido(), Double::sum);
                })
            );

            totales.forEach((nombre, total) ->
                modeloRetenciones.addRow(new Object[]{
                    nombre,
                    String.format("$%.2f", total)
                })
            );
        });

        btnActualizar.doClick();
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════
    // TAB 8 — Compulsa de Precios
    // ══════════════════════════════════════════════════════════════════
    private JPanel crearTabPrecios() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBusqueda.add(new JLabel("Código Producto:"));
        txtCodigoProducto = new JTextField(15);
        panelBusqueda.add(txtCodigoProducto);
        JButton btnBuscar = new JButton("Consultar");
        panelBusqueda.add(btnBuscar);
        panel.add(panelBusqueda, BorderLayout.NORTH);

        String[] col = {"Producto", "Proveedor", "Precio Acordado", "Fecha Acuerdo"};
        modeloPrecios = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPrecios = new JTable(modeloPrecios);
        panel.add(new JScrollPane(tablaPrecios), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> {
            String codigo = txtCodigoProducto.getText().trim();
            Producto prod = controladores.GestorCatalogo
                .getInstancia().buscarProducto(codigo);
            if (prod == null) {
                JOptionPane.showMessageDialog(this,
                    "Producto no encontrado.", "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            modeloPrecios.setRowCount(0);
            prod.getHistorialPrecios().forEach(pa ->
                modeloPrecios.addRow(new Object[]{
                    prod.getDescripcion(),
                    pa.getProveedor().getRazonSocial(),
                    String.format("$%.2f", pa.getPrecio()),
                    pa.getFechaAcuerdo()
                })
            );
        });

        return panel;
    }

    // ══════════════════════════════════════════════════════════════════
    // TAB 9 — Libro IVA Compras
    // ══════════════════════════════════════════════════════════════════
    private JPanel crearTabLibroIVA() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JButton btnActualizar = new JButton("Actualizar");
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBtn.add(btnActualizar);
        panel.add(panelBtn, BorderLayout.NORTH);

        String[] col = {"CUIT", "Razón Social", "Fecha",
                        "Tipo Comp.", "Letra", "Neto", "IVA", "Total"};
        modeloLibroIVA = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaLibroIVA = new JTable(modeloLibroIVA);
        panel.add(new JScrollPane(tablaLibroIVA), BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> {
            modeloLibroIVA.setRowCount(0);
            gestorComprobantes.getComprobantes().forEach(c ->
                modeloLibroIVA.addRow(new Object[]{
                    c.getProveedor().getCuit(),
                    c.getProveedor().getRazonSocial(),
                    c.getFechaEmision(),
                    c.getDescripcionTipo(),
                    c.getLetra(),
                    String.format("$%.2f", c.getImporteNeto()),
                    String.format("$%.2f", c.getImporteIVA()),
                    String.format("$%.2f", c.getImporteTotal())
                })
            );
        });

        btnActualizar.doClick();
        return panel;
    }
}