package vistas;

import controladores.GestorPagos;
import controladores.GestorProveedores;
import modelo.comprobantes.Comprobante;
import modelo.pagos.*;
import modelo.proveedores.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PanelOrdenesPago extends JPanel {

    private GestorPagos gestorPagos;
    private GestorProveedores gestorProveedores;
    private Runnable onVolver;

    // Componentes
    private JTextField txtCuit;
    private JLabel lblProveedor;
    private JList<Comprobante> listComprobantes;
    private DefaultListModel<Comprobante> modeloListComprobantes;
    private JTable tablaRetenciones;
    private DefaultTableModel modeloRetenciones;
    private JTable tablaMedios;
    private DefaultTableModel modeloMedios;
    private JTable tablaOrdenesPago;
    private DefaultTableModel modeloOrdenesPago;
    private JLabel lblTotalComprobantes;
    private JLabel lblTotalRetenciones;
    private JLabel lblTotalNeto;
    private JComboBox<String> cmbMedioPago;
    private JTextField txtImporteMedio;
    private JButton btnBuscarProveedor;
    private JButton btnGenerarOP;
    private JButton btnAgregarMedio;
    private JButton btnConfirmarOP;
    private JButton btnLimpiar;
    private JButton btnVolver;

    private OrdenDePago opActual;
    private Proveedor proveedorActual;
    private List<MedioDePago> mediosAgregados;

    public PanelOrdenesPago(Runnable onVolver) {
        this.gestorPagos = GestorPagos.getInstancia();
        this.gestorProveedores = GestorProveedores.getInstancia();
        this.onVolver = onVolver;
        this.mediosAgregados = new ArrayList<>();
        inicializarComponentes();
        cargarTablaOP();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // ── Título y volver ─────────────────────────────────────────────
        JPanel panelTitulo = new JPanel(new BorderLayout());
        btnVolver = new JButton("← Volver al Menú");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel lblTitulo = new JLabel("Emisión de Órdenes de Pago", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelTitulo.add(btnVolver, BorderLayout.WEST);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        add(panelTitulo, BorderLayout.NORTH);

        // ── Panel izquierdo ─────────────────────────────────────────────
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.setPreferredSize(new Dimension(350, 600));

        // Búsqueda proveedor
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Proveedor"));
        panelBusqueda.add(new JLabel("CUIT:"));
        txtCuit = new JTextField(12);
        txtCuit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '-'
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE)
                    e.consume();
            }
        });
        panelBusqueda.add(txtCuit);
        btnBuscarProveedor = new JButton("Buscar");
        panelBusqueda.add(btnBuscarProveedor);
        lblProveedor = new JLabel("(no seleccionado)");
        lblProveedor.setFont(new Font("Arial", Font.BOLD, 11));
        panelBusqueda.add(lblProveedor);
        panelIzquierdo.add(panelBusqueda, BorderLayout.NORTH);

        // Comprobantes impagos
        JPanel panelImpagos = new JPanel(new BorderLayout());
        panelImpagos.setBorder(BorderFactory.createTitledBorder(
            "Comprobantes Impagos (seleccioná los que querés pagar)"));
        modeloListComprobantes = new DefaultListModel<>();
        listComprobantes = new JList<>(modeloListComprobantes);
        listComprobantes.setSelectionMode(
            ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listComprobantes.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                if (value instanceof Comprobante) {
                    Comprobante c = (Comprobante) value;
                    setText(c.getNumero() + " — "
                        + c.getDescripcionTipo() + " — $"
                        + String.format("%.2f", c.getImporteTotal()));
                }
                return this;
            }
        });
        panelImpagos.add(new JScrollPane(listComprobantes), BorderLayout.CENTER);

        btnGenerarOP = new JButton("Generar OP con seleccionados");
        btnGenerarOP.setBackground(new Color(0, 100, 180));
        btnGenerarOP.setForeground(Color.WHITE);
        btnGenerarOP.setEnabled(false);
        panelImpagos.add(btnGenerarOP, BorderLayout.SOUTH);
        panelIzquierdo.add(panelImpagos, BorderLayout.CENTER);

        // Medios de pago
        JPanel panelMedios = new JPanel(new GridBagLayout());
        panelMedios.setBorder(BorderFactory.createTitledBorder("Agregar Medio de Pago"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelMedios.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        cmbMedioPago = new JComboBox<>(new String[]{
            "Efectivo", "Transferencia", "Cheque Propio", "Cheque de Terceros"});
        panelMedios.add(cmbMedioPago, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelMedios.add(new JLabel("Importe:"), gbc);
        gbc.gridx = 1;
        txtImporteMedio = new JTextField(12);
        txtImporteMedio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.'
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE)
                    e.consume();
            }
        });
        panelMedios.add(txtImporteMedio, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnAgregarMedio = new JButton("+ Agregar Medio");
        btnAgregarMedio.setEnabled(false);
        panelMedios.add(btnAgregarMedio, gbc);

        panelIzquierdo.add(panelMedios, BorderLayout.SOUTH);
        add(panelIzquierdo, BorderLayout.WEST);

        // ── Panel central — resumen y tablas ────────────────────────────
        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));

        // Resumen
        JPanel panelResumen = new JPanel(new GridLayout(3, 1, 3, 3));
        panelResumen.setBorder(BorderFactory.createTitledBorder("Resumen OP"));
        lblTotalComprobantes = new JLabel("Total Comprobantes: $0.00");
        lblTotalRetenciones = new JLabel("Total Retenciones: $0.00");
        lblTotalNeto = new JLabel("Total Neto a Pagar: $0.00");
        lblTotalNeto.setFont(new Font("Arial", Font.BOLD, 13));
        lblTotalNeto.setForeground(new Color(0, 120, 0));
        panelResumen.add(lblTotalComprobantes);
        panelResumen.add(lblTotalRetenciones);
        panelResumen.add(lblTotalNeto);
        panelCentro.add(panelResumen, BorderLayout.NORTH);

        // Tabla retenciones
        String[] colRet = {"Impuesto", "Base Imponible", "Porcentaje", "Monto Retenido"};
        modeloRetenciones = new DefaultTableModel(colRet, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRetenciones = new JTable(modeloRetenciones);
        JScrollPane scrollRet = new JScrollPane(tablaRetenciones);
        scrollRet.setBorder(BorderFactory.createTitledBorder("Retenciones Calculadas"));
        scrollRet.setPreferredSize(new Dimension(400, 120));

        // Tabla medios
        String[] colMedios = {"Tipo Medio de Pago", "Importe"};
        modeloMedios = new DefaultTableModel(colMedios, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaMedios = new JTable(modeloMedios);
        JScrollPane scrollMedios = new JScrollPane(tablaMedios);
        scrollMedios.setBorder(BorderFactory.createTitledBorder("Medios de Pago Agregados"));
        scrollMedios.setPreferredSize(new Dimension(400, 100));

        JPanel panelTablasMedio = new JPanel(new GridLayout(2, 1, 0, 5));
        panelTablasMedio.add(scrollRet);
        panelTablasMedio.add(scrollMedios);
        panelCentro.add(panelTablasMedio, BorderLayout.CENTER);

        // Confirmar
        JPanel panelConfirmar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    btnLimpiar = new JButton("Limpiar");
    btnConfirmarOP = new JButton("✅ Confirmar Orden de Pago");
    btnConfirmarOP.setBackground(new Color(0, 150, 0));
    btnConfirmarOP.setForeground(Color.WHITE);
    btnConfirmarOP.setFont(new Font("Arial", Font.BOLD, 13));
    btnConfirmarOP.setEnabled(false);
    panelConfirmar.add(btnLimpiar);
    panelConfirmar.add(btnConfirmarOP);

        add(panelCentro, BorderLayout.CENTER);

        // Tabla órdenes de pago emitidas
        String[] colOP = {"N° OP", "Proveedor", "Fecha", "Retenciones", "Neto"};
        modeloOrdenesPago = new DefaultTableModel(colOP, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaOrdenesPago = new JTable(modeloOrdenesPago);
        JScrollPane scrollOP = new JScrollPane(tablaOrdenesPago);
        scrollOP.setBorder(BorderFactory.createTitledBorder("Órdenes de Pago Emitidas"));
        scrollOP.setPreferredSize(new Dimension(300, 200));
        add(scrollOP, BorderLayout.EAST);

        // ── Eventos ─────────────────────────────────────────────────────
        btnVolver.addActionListener(e -> {
            limpiarFormulario();
            onVolver.run();
        });
        btnBuscarProveedor.addActionListener(e -> buscarProveedor());
        btnGenerarOP.addActionListener(e -> generarOP());
        btnAgregarMedio.addActionListener(e -> agregarMedio());
        btnConfirmarOP.addActionListener(e -> confirmarOP());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    private void buscarProveedor() {
        String cuit = txtCuit.getText().trim();
        if (cuit.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Ingresá el CUIT del proveedor.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        proveedorActual = gestorProveedores.buscarProveedor(cuit);
        if (proveedorActual == null) {
            JOptionPane.showMessageDialog(this,
                "Proveedor no encontrado.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        lblProveedor.setText(proveedorActual.getRazonSocial());

        // Cargar comprobantes impagos
        modeloListComprobantes.clear();
        List<Comprobante> impagos = gestorPagos
            .consultarDocumentosImpagos(proveedorActual);
        for (Comprobante c : impagos) {
            modeloListComprobantes.addElement(c);
        }

        if (impagos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Este proveedor no tiene comprobantes impagos.",
                "Sin comprobantes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        btnGenerarOP.setEnabled(true);
    }

    private void generarOP() {
        List<Comprobante> seleccionados = listComprobantes.getSelectedValuesList();
        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Seleccioná al menos un comprobante.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        opActual = gestorPagos.generarOrdenDePago(proveedorActual, seleccionados);
        mediosAgregados.clear();
        modeloMedios.setRowCount(0);

        // Cargar retenciones calculadas
        modeloRetenciones.setRowCount(0);
        for (RetencionAplicada r : opActual.getRetenciones()) {
            modeloRetenciones.addRow(new Object[]{
                r.getImpuesto().getNombre(),
                String.format("$%.2f", r.getBaseImponible()),
                r.getPorcentajeAplicado() + "%",
                String.format("$%.2f", r.getMontoRetenido())
            });
        }

        actualizarResumen();
        btnAgregarMedio.setEnabled(true);
        btnConfirmarOP.setEnabled(true);
    }

    private void agregarMedio() {
        String importeStr = txtImporteMedio.getText().trim();
        String tipo = (String) cmbMedioPago.getSelectedItem();

        if (importeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Ingresá el importe del medio de pago.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double importe;
        try {
            importe = Double.parseDouble(importeStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "El importe debe ser numérico.",
                "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MedioDePago medio;
        switch (tipo) {
            case "Efectivo":
                medio = new Efectivo(importe);
                break;
            case "Transferencia":
                medio = new Transferencia(importe, "", "", new Date(), "");
                break;
            case "Cheque Propio":
                medio = new ChequePropio(importe, "", new Date(), new Date(), "", "", "");
                break;
            default:
                medio = new ChequeTerceros(importe, "", new Date(), new Date(), "", "");
                break;
        }

        mediosAgregados.add(medio);
        opActual.agregarMedioDePago(medio);
        modeloMedios.addRow(new Object[]{
            tipo,
            String.format("$%.2f", importe)
        });

        txtImporteMedio.setText("");
        actualizarResumen();
    }

    private void confirmarOP() {
        if (mediosAgregados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Agregá al menos un medio de pago.",
                "Sin medios de pago", JOptionPane.WARNING_MESSAGE);
            return;
        }

        gestorPagos.confirmarEmisionOP(opActual);
        cargarTablaOP();
        JOptionPane.showMessageDialog(this,
            "✅ Orden de Pago emitida correctamente.\nNúmero: "
            + opActual.getNumero(),
            "OP Confirmada", JOptionPane.INFORMATION_MESSAGE);
        limpiarFormulario();
    }

    private void actualizarResumen() {
        if (opActual == null) return;

        double totalComp = opActual.getCancelaciones().stream()
            .mapToDouble(c -> c.getMontoCancelado()).sum();
        double totalRet = opActual.getTotalRetenciones();
        double neto = opActual.getImporteNeto();

        lblTotalComprobantes.setText(
            String.format("Total Comprobantes: $%.2f", totalComp));
        lblTotalRetenciones.setText(
            String.format("Total Retenciones: $%.2f", totalRet));
        lblTotalNeto.setText(
            String.format("Total Neto a Pagar: $%.2f", neto));
    }

    private void cargarTablaOP() {
        modeloOrdenesPago.setRowCount(0);
        for (OrdenDePago op : gestorPagos.getOrdenesDePago()) {
            modeloOrdenesPago.addRow(new Object[]{
                op.getNumero(),
                op.getProveedor().getRazonSocial(),
                op.getFechaEmision(),
                String.format("$%.2f", op.getTotalRetenciones()),
                String.format("$%.2f", op.getImporteNeto())
            });
        }
    }

    private void limpiarFormulario() {
        txtCuit.setText("");
        lblProveedor.setText("(no seleccionado)");
        modeloListComprobantes.clear();
        modeloRetenciones.setRowCount(0);
        modeloMedios.setRowCount(0);
        lblTotalComprobantes.setText("Total Comprobantes: $0.00");
        lblTotalRetenciones.setText("Total Retenciones: $0.00");
        lblTotalNeto.setText("Total Neto a Pagar: $0.00");
        txtImporteMedio.setText("");
        btnGenerarOP.setEnabled(false);
        btnAgregarMedio.setEnabled(false);
        btnConfirmarOP.setEnabled(false);
        opActual = null;
        proveedorActual = null;
        mediosAgregados.clear();
    }
}