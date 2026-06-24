package vistas;

import controladores.Controlador;
import modelo.comprobantes.Comprobante;
import modelo.pagos.OrdenDePago;
import modelo.proveedores.CuentaCorriente;
import modelo.proveedores.Movimiento;
import modelo.proveedores.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelConsultarCC extends JPanel {

    private Controlador controlador;
    private Runnable onVolver;

    // Componentes
    private JTextField txtCuit;
    private JLabel lblProveedor;
    private JLabel lblSaldo;
    private JTable tablaMovimientos;
    private DefaultTableModel modeloMovimientos;
    private JTable tablaImpagos;
    private DefaultTableModel modeloImpagos;
    private JTable tablaPagos;
    private DefaultTableModel modeloPagos;
    private JButton btnBuscar;
    private JButton btnLimpiar;
    private JButton btnVolver;

    public PanelConsultarCC(Runnable onVolver) {
        this.controlador = Controlador.getInstancia();
        this.onVolver = onVolver;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // ── Panel superior — título y volver ────────────────────────────
        JPanel panelTitulo = new JPanel(new BorderLayout());
        btnVolver = new JButton("← Volver al Menú");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel lblTitulo = new JLabel("Consultar Cuenta Corriente", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelTitulo.add(btnVolver, BorderLayout.WEST);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);

        // ── Panel búsqueda ──────────────────────────────────────────────
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Buscar Proveedor"));
        panelBusqueda.add(new JLabel("CUIT:"));
        txtCuit = new JTextField(15);
    txtCuit.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent e) {
        char c = e.getKeyChar();
        if (!Character.isDigit(c) && c != '-' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
            e.consume();
        }
    }
});
        panelBusqueda.add(txtCuit);
        btnBuscar = new JButton("Buscar");
        panelBusqueda.add(btnBuscar);
        btnLimpiar = new JButton("Limpiar");
        panelBusqueda.add(btnLimpiar);
        lblProveedor = new JLabel("Proveedor: (no seleccionado)");
        lblProveedor.setFont(new Font("Arial", Font.BOLD, 12));
        panelBusqueda.add(lblProveedor);

        JPanel panelNorth = new JPanel(new GridLayout(2, 1));
        panelNorth.add(panelTitulo);
        panelNorth.add(panelBusqueda);
        add(panelNorth, BorderLayout.NORTH);

        // ── Tabs ────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();

        String[] columnasMovimientos = {"Fecha", "Tipo", "N° Documento", "Debe", "Haber", "Saldo"};
        modeloMovimientos = new DefaultTableModel(columnasMovimientos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaMovimientos = new JTable(modeloMovimientos);
        tabs.addTab("Movimientos", new JScrollPane(tablaMovimientos));

        String[] columnasImpagos = {"N° Comprobante", "Tipo", "Fecha Emisión", "Importe Total"};
        modeloImpagos = new DefaultTableModel(columnasImpagos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaImpagos = new JTable(modeloImpagos);
        tabs.addTab("Documentos Impagos", new JScrollPane(tablaImpagos));

        String[] columnasPagos = {"N° Orden Pago", "Fecha", "Total Medios Pago", "Retenciones", "Importe Neto"};
        modeloPagos = new DefaultTableModel(columnasPagos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPagos = new JTable(modeloPagos);
        tabs.addTab("Pagos Realizados", new JScrollPane(tablaPagos));

        add(tabs, BorderLayout.CENTER);

        // ── Panel inferior — saldo ───────────────────────────────────────
        JPanel panelSaldo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        lblSaldo = new JLabel("Saldo Actual: $0.00");
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 14));
        lblSaldo.setForeground(new Color(150, 0, 0));
        panelSaldo.add(lblSaldo);
        add(panelSaldo, BorderLayout.SOUTH);

        // ── Eventos ─────────────────────────────────────────────────────
        btnVolver.addActionListener(e -> {
            limpiarFormulario();
            onVolver.run();
        });
        btnBuscar.addActionListener(e -> buscarCuentaCorriente());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    private void buscarCuentaCorriente() {
        String cuit = txtCuit.getText().trim();
        if (cuit.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Ingresá el CUIT del proveedor.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Proveedor proveedor = controlador.buscarProveedor(cuit);
        if (proveedor == null) {
            JOptionPane.showMessageDialog(this,
                "Proveedor no encontrado con CUIT: " + cuit,
                "No encontrado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        lblProveedor.setText("Proveedor: " + proveedor.getRazonSocial()
                + " | CUIT: " + proveedor.getCuit());

        CuentaCorriente cc = controlador.obtenerCuentaCorriente(proveedor);
        cargarMovimientos(cc);
        cargarDocumentosImpagos(cc);
        cargarPagosRealizados(cc);

        double saldo = cc.getSaldoActual();
        lblSaldo.setText(String.format("Saldo Actual: $%.2f", saldo));
        lblSaldo.setForeground(saldo > 0
                ? new Color(150, 0, 0)
                : new Color(0, 120, 0));
    }

    private void cargarMovimientos(CuentaCorriente cc) {
        modeloMovimientos.setRowCount(0);
        List<Movimiento> movimientos = controlador.getMovimientos(cc);
        for (Movimiento m : movimientos) {
            modeloMovimientos.addRow(new Object[]{
                m.getFecha(),
                m.getTipoMovimiento(),
                m.getNumeroDocumento(),
                String.format("$%.2f", m.getDebe()),
                String.format("$%.2f", m.getHaber()),
                String.format("$%.2f", m.getSaldo())
            });
        }
    }

    private void cargarDocumentosImpagos(CuentaCorriente cc) {
        modeloImpagos.setRowCount(0);
        List<Comprobante> impagos = controlador.getDocumentosImpagos(cc);
        for (Comprobante c : impagos) {
            modeloImpagos.addRow(new Object[]{
                c.getNumero(),
                c.getDescripcionTipo(),
                c.getFechaEmision(),
                String.format("$%.2f", c.getImporteTotal())
            });
        }
    }

    private void cargarPagosRealizados(CuentaCorriente cc) {
        modeloPagos.setRowCount(0);
        List<OrdenDePago> pagos = controlador.getPagosRealizados(cc);
        for (OrdenDePago op : pagos) {
            modeloPagos.addRow(new Object[]{
                op.getNumero(),
                op.getFechaEmision(),
                String.format("$%.2f", op.getTotalMediosDePago()),
                String.format("$%.2f", op.getTotalRetenciones()),
                String.format("$%.2f", op.getImporteNeto())
            });
        }
    }

    private void limpiarFormulario() {
        txtCuit.setText("");
        lblProveedor.setText("Proveedor: (no seleccionado)");
        lblSaldo.setText("Saldo Actual: $0.00");
        lblSaldo.setForeground(new Color(150, 0, 0));
        modeloMovimientos.setRowCount(0);
        modeloImpagos.setRowCount(0);
        modeloPagos.setRowCount(0);
    }
}