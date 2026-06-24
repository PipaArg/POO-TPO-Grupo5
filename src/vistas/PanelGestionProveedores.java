package vistas;

import controladores.GestorProveedores;
import modelo.enums.CondicionIVA;
import modelo.proveedores.Proveedor;
import modelo.proveedores.Rubro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class PanelGestionProveedores extends JPanel {

    private GestorProveedores gestorProveedores;
    private Runnable onVolver;

    // Componentes formulario
    private JTextField txtCuit;
    private JTextField txtRazonSocial;
    private JTextField txtNombreFantasia;
    private JTextField txtDomicilio;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JComboBox<CondicionIVA> cmbCondicionIVA;
    private JTextField txtIngresosBrutos;
    private JTextField txtTopeDeuda;
    private JList<Rubro> listRubros;

    // Tabla
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;

    // Botones
    private JButton btnAgregar;
    private JButton btnLimpiar;
    private JButton btnVolver;

    public PanelGestionProveedores(Runnable onVolver) {
        this.gestorProveedores = GestorProveedores.getInstancia();
        this.onVolver = onVolver;
        inicializarComponentes();
        cargarTabla();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // ── Título y volver ─────────────────────────────────────────────
        JPanel panelTitulo = new JPanel(new BorderLayout());
        btnVolver = new JButton("← Volver al Menú");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel lblTitulo = new JLabel("Gestión de Proveedores", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelTitulo.add(btnVolver, BorderLayout.WEST);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        add(panelTitulo, BorderLayout.NORTH);

        // ── Formulario ──────────────────────────────────────────────────
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Nuevo Proveedor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // CUIT
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("CUIT:"), gbc);
        gbc.gridx = 1;
        txtCuit = new JTextField(18);
        txtCuit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '-'
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        panelFormulario.add(txtCuit, gbc);

        // Razón Social
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Razón Social:"), gbc);
        gbc.gridx = 1;
        txtRazonSocial = new JTextField(18);
        panelFormulario.add(txtRazonSocial, gbc);

        // Nombre Fantasía
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Nombre Fantasía:"), gbc);
        gbc.gridx = 1;
        txtNombreFantasia = new JTextField(18);
        panelFormulario.add(txtNombreFantasia, gbc);

        // Domicilio
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Domicilio:"), gbc);
        gbc.gridx = 1;
        txtDomicilio = new JTextField(18);
        panelFormulario.add(txtDomicilio, gbc);

        // Teléfono
        gbc.gridx = 0; gbc.gridy = 4;
        panelFormulario.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(18);
        panelFormulario.add(txtTelefono, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 5;
        panelFormulario.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(18);
        panelFormulario.add(txtEmail, gbc);

        // Condición IVA
        gbc.gridx = 0; gbc.gridy = 6;
        panelFormulario.add(new JLabel("Condición IVA:"), gbc);
        gbc.gridx = 1;
        cmbCondicionIVA = new JComboBox<>(CondicionIVA.values());
        panelFormulario.add(cmbCondicionIVA, gbc);

        // Ingresos Brutos
        gbc.gridx = 0; gbc.gridy = 7;
        panelFormulario.add(new JLabel("N° Ing. Brutos:"), gbc);
        gbc.gridx = 1;
        txtIngresosBrutos = new JTextField(18);
        panelFormulario.add(txtIngresosBrutos, gbc);

        // Tope Deuda
        gbc.gridx = 0; gbc.gridy = 8;
        panelFormulario.add(new JLabel("Tope Máx. Deuda:"), gbc);
        gbc.gridx = 1;
        txtTopeDeuda = new JTextField(18);
        txtTopeDeuda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.'
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        panelFormulario.add(txtTopeDeuda, gbc);

        // Rubros
        gbc.gridx = 0; gbc.gridy = 9;
        panelFormulario.add(new JLabel("Rubros:"), gbc);
        gbc.gridx = 1;
        List<Rubro> rubros = gestorProveedores.getRubros();
        listRubros = new JList<>(rubros.toArray(new Rubro[0]));
        listRubros.setVisibleRowCount(3);
        listRubros.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listRubros.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                if (value instanceof Rubro) {
                    setText(((Rubro) value).getNombre());
                }
                return this;
            }
        });
        panelFormulario.add(new JScrollPane(listRubros), gbc);

        // Botones
        gbc.gridx = 0; gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnAgregar = new JButton("Registrar Proveedor");
        btnAgregar.setBackground(new Color(0, 150, 0));
        btnAgregar.setForeground(Color.WHITE);
        btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnAgregar);
        panelBotones.add(btnLimpiar);
        panelFormulario.add(panelBotones, gbc);

        JScrollPane scrollForm = new JScrollPane(panelFormulario);
        scrollForm.setPreferredSize(new Dimension(320, 500));
        add(scrollForm, BorderLayout.WEST);

        // ── Tabla ───────────────────────────────────────────────────────
        String[] columnas = {"CUIT", "Razón Social", "Nombre Fantasía",
                    "Condición IVA", "Ing. Brutos", "Rubros",
                    "Tope Deuda", "Deuda Vigente"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaProveedores = new JTable(modeloTabla);
        tablaProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tablaProveedores);
        scroll.setBorder(BorderFactory.createTitledBorder("Proveedores registrados"));
        add(scroll, BorderLayout.CENTER);

        // ── Eventos ─────────────────────────────────────────────────────
        btnVolver.addActionListener(e -> onVolver.run());
        btnAgregar.addActionListener(e -> agregarProveedor());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    private void agregarProveedor() {
        String cuit = txtCuit.getText().trim();
        String razonSocial = txtRazonSocial.getText().trim();
        String nombreFantasia = txtNombreFantasia.getText().trim();
        String domicilio = txtDomicilio.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        String ingresosBrutos = txtIngresosBrutos.getText().trim();
        String topeStr = txtTopeDeuda.getText().trim();
        CondicionIVA condicion = (CondicionIVA) cmbCondicionIVA.getSelectedItem();

        if (cuit.isEmpty() || razonSocial.isEmpty() || topeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "CUIT, Razón Social y Tope de Deuda son obligatorios.",
                "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (gestorProveedores.buscarProveedor(cuit) != null) {
            JOptionPane.showMessageDialog(this,
                "Ya existe un proveedor con ese CUIT.",
                "CUIT duplicado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double tope;
        try {
            tope = Double.parseDouble(topeStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "El tope de deuda debe ser un valor numérico.",
                "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Proveedor p = new Proveedor(cuit, razonSocial, nombreFantasia,
            domicilio, telefono, email, condicion,
            ingresosBrutos, new Date(), tope);

        // Agregar rubros seleccionados
        List<Rubro> rubrosSeleccionados = listRubros.getSelectedValuesList();
        for (Rubro r : rubrosSeleccionados) {
            p.agregarRubro(r);
        }

        gestorProveedores.registrarProveedor(p);
        cargarTabla();
        limpiarFormulario();
        JOptionPane.showMessageDialog(this,
            "✅ Proveedor registrado correctamente.",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

private void cargarTabla() {
    modeloTabla.setRowCount(0);
    List<Proveedor> proveedores = gestorProveedores.getProveedores();
    for (Proveedor p : proveedores) {
        // Armar string de rubros
        StringBuilder rubros = new StringBuilder();
        p.getRubros().forEach(r -> {
            if (rubros.length() > 0) rubros.append(", ");
            rubros.append(r.getNombre());
        });

        modeloTabla.addRow(new Object[]{
            p.getCuit(),
            p.getRazonSocial(),
            p.getNombreFantasia(),
            p.getCondicionIVA(),
            p.getNumeroIngresosBrutos(),
            rubros.toString(),
            String.format("$%.2f", p.getTopeMaximoDeuda()),
            String.format("$%.2f", p.getDeudaVigente())
        });
    }
}

    private void limpiarFormulario() {
        txtCuit.setText("");
        txtRazonSocial.setText("");
        txtNombreFantasia.setText("");
        txtDomicilio.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtIngresosBrutos.setText("");
        txtTopeDeuda.setText("");
        cmbCondicionIVA.setSelectedIndex(0);
        listRubros.clearSelection();
    }
}