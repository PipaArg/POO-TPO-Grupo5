package vistas;

import controladores.GestorCatalogo;
import controladores.GestorComprobantes;
import controladores.GestorOC;
import controladores.GestorProveedores;
import modelo.catalogo.Producto;
import modelo.comprobantes.Factura;
import modelo.comprobantes.ItemComprobante;
import modelo.comprobantes.NotaDeCredito;
import modelo.comprobantes.NotaDeDebito;
import modelo.comprobantes.ResultadoValidacion;
import modelo.enums.LetraComprobante;
import modelo.enums.Rol;
import modelo.ordenes.OrdenDeCompra;
import modelo.proveedores.Movimiento;
import modelo.proveedores.Proveedor;
import modelo.usuarios.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class PanelComprobantes extends JPanel {

    private GestorComprobantes gestorComprobantes;
    private GestorOC gestorOC;
    private GestorProveedores gestorProveedores;
    private Runnable onVolver;

    // Componentes
    private JTextField txtNumeroComprobante;
    private JTextField txtImporteNeto;
    private JTextField txtImporteIVA;
    private JComboBox<LetraComprobante> cmbLetra;
    private JComboBox<String> cmbTipo;
    private JComboBox<Proveedor> cmbProveedor;
    private JTextField txtNumeroOC;
    private JTextField txtCodigoProducto;
    private JTextField txtCantidad;
    private JTextField txtPrecioUnitario;
    private JTable tablaItems;
    private DefaultTableModel modeloItems;
    private JTable tablaComprobantes;
    private DefaultTableModel modeloComprobantes;
    private JButton btnAgregarItem;
    private JButton btnRegistrar;
    private JButton btnLimpiar;
    private JButton btnVolver;

    public PanelComprobantes(Runnable onVolver) {
        this.gestorComprobantes = GestorComprobantes.getInstancia();
        this.gestorOC           = GestorOC.getInstancia();
        this.gestorProveedores  = GestorProveedores.getInstancia();
        this.onVolver = onVolver;
        inicializarComponentes();
        cargarTablaComprobantes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // ── Título y volver ─────────────────────────────────────────────
        JPanel panelTitulo = new JPanel(new BorderLayout());
        btnVolver = new JButton("← Volver al Menú");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel lblTitulo = new JLabel("Registro de Comprobantes", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelTitulo.add(btnVolver, BorderLayout.WEST);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        add(panelTitulo, BorderLayout.NORTH);

        // ── Panel izquierdo — formulario ────────────────────────────────
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Nuevo Comprobante"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Tipo
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        cmbTipo = new JComboBox<>(new String[]{
            "Factura", "Nota de Crédito", "Nota de Débito"});
        panelForm.add(cmbTipo, gbc);

        // Número
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Número:"), gbc);
        gbc.gridx = 1;
        txtNumeroComprobante = new JTextField(15);
        panelForm.add(txtNumeroComprobante, gbc);

        // Letra
        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Letra:"), gbc);
        gbc.gridx = 1;
        cmbLetra = new JComboBox<>(LetraComprobante.values());
        panelForm.add(cmbLetra, gbc);

        // Proveedor
        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Proveedor:"), gbc);
        gbc.gridx = 1;
        cmbProveedor = new JComboBox<>();
        for (Proveedor p : gestorProveedores.getProveedores()) {
            cmbProveedor.addItem(p);
        }
        cmbProveedor.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                if (value instanceof Proveedor)
                    setText(((Proveedor) value).getRazonSocial());
                return this;
            }
        });
        panelForm.add(cmbProveedor, gbc);

        // Importe Neto
        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Importe Neto:"), gbc);
        gbc.gridx = 1;
        txtImporteNeto = new JTextField(15);
        txtImporteNeto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.'
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE)
                    e.consume();
            }
        });
        panelForm.add(txtImporteNeto, gbc);

        // Importe IVA
        gbc.gridx = 0; gbc.gridy = 5;
        panelForm.add(new JLabel("Importe IVA:"), gbc);
        gbc.gridx = 1;
        txtImporteIVA = new JTextField(15);
        txtImporteIVA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.'
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE)
                    e.consume();
            }
        });
        panelForm.add(txtImporteIVA, gbc);

        // Número OC
        gbc.gridx = 0; gbc.gridy = 6;
        panelForm.add(new JLabel("N° OC (opcional):"), gbc);
        gbc.gridx = 1;
        txtNumeroOC = new JTextField(15);
        panelForm.add(txtNumeroOC, gbc);

        // Separador items
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        panelForm.add(new JLabel("─────── Items ───────"), gbc);
        gbc.gridwidth = 1;

        // Código producto
        gbc.gridx = 0; gbc.gridy = 8;
        panelForm.add(new JLabel("Cód. Producto:"), gbc);
        gbc.gridx = 1;
        txtCodigoProducto = new JTextField(15);
        panelForm.add(txtCodigoProducto, gbc);

        // Cantidad
        gbc.gridx = 0; gbc.gridy = 9;
        panelForm.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1;
        txtCantidad = new JTextField(15);
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE)
                    e.consume();
            }
        });
        panelForm.add(txtCantidad, gbc);

        // Precio unitario
        gbc.gridx = 0; gbc.gridy = 10;
        panelForm.add(new JLabel("Precio Unit.:"), gbc);
        gbc.gridx = 1;
        txtPrecioUnitario = new JTextField(15);
        txtPrecioUnitario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.'
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE)
                    e.consume();
            }
        });
        panelForm.add(txtPrecioUnitario, gbc);

        // Botón agregar item
        gbc.gridx = 0; gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnAgregarItem = new JButton("+ Agregar Item");
        btnAgregarItem.setBackground(new Color(0, 100, 180));
        btnAgregarItem.setForeground(Color.WHITE);
        panelForm.add(btnAgregarItem, gbc);

        // Tabla items
        gbc.gridx = 0; gbc.gridy = 12;
        gbc.gridwidth = 2;
        String[] colItems = {"Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        modeloItems = new DefaultTableModel(colItems, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaItems = new JTable(modeloItems);
        JScrollPane scrollItems = new JScrollPane(tablaItems);
        scrollItems.setPreferredSize(new Dimension(300, 120));
        panelForm.add(scrollItems, gbc);

        // Botones registrar y limpiar
        gbc.gridx = 0; gbc.gridy = 13;
        gbc.gridwidth = 2;
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new Color(0, 150, 0));
        btnRegistrar.setForeground(Color.WHITE);
        btnLimpiar = new JButton("Limpiar");
        pnlBotones.add(btnRegistrar);
        pnlBotones.add(btnLimpiar);
        panelForm.add(pnlBotones, gbc);

        JScrollPane scrollForm = new JScrollPane(panelForm);
        scrollForm.setPreferredSize(new Dimension(350, 0));
        scrollForm.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollForm, BorderLayout.WEST);

        // ── Tabla comprobantes ──────────────────────────────────────────
        String[] colComp = {"Número", "Tipo", "Letra", "Proveedor",
                            "Importe Neto", "IVA", "Total"};
        modeloComprobantes = new DefaultTableModel(colComp, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaComprobantes = new JTable(modeloComprobantes);
        JScrollPane scrollComp = new JScrollPane(tablaComprobantes);
        scrollComp.setBorder(
            BorderFactory.createTitledBorder("Comprobantes registrados"));
        add(scrollComp, BorderLayout.CENTER);

        // ── Eventos ─────────────────────────────────────────────────────
        btnVolver.addActionListener(e -> onVolver.run());
        btnAgregarItem.addActionListener(e -> agregarItem());
        btnRegistrar.addActionListener(e -> registrarComprobante());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    // ── Agregar item a la tabla ──────────────────────────────────────────
    private void agregarItem() {
        String codigo   = txtCodigoProducto.getText().trim();
        String cantStr  = txtCantidad.getText().trim();
        String precioStr = txtPrecioUnitario.getText().trim();

        if (codigo.isEmpty() || cantStr.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Completá todos los campos del item.",
                "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Producto producto = GestorCatalogo.getInstancia().buscarProducto(codigo);
        if (producto == null) {
            JOptionPane.showMessageDialog(this,
                "Producto no encontrado: " + codigo,
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int cantidad;
        double precio;
        try {
            cantidad = Integer.parseInt(cantStr);
            precio   = Double.parseDouble(precioStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Cantidad y precio deben ser numéricos.",
                "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        modeloItems.addRow(new Object[]{
            producto.getDescripcion(),
            cantidad,
            String.format("$%.2f", precio),
            String.format("$%.2f", cantidad * precio)
        });

        txtCodigoProducto.setText("");
        txtCantidad.setText("");
        txtPrecioUnitario.setText("");
    }

    // ── Registrar comprobante ────────────────────────────────────────────
    private void registrarComprobante() {
        String numero   = txtNumeroComprobante.getText().trim();
        String netoStr  = txtImporteNeto.getText().trim();
        String ivaStr   = txtImporteIVA.getText().trim();
        String tipo     = (String) cmbTipo.getSelectedItem();
        LetraComprobante letra = (LetraComprobante) cmbLetra.getSelectedItem();
        Proveedor proveedor   = (Proveedor) cmbProveedor.getSelectedItem();

        if (numero.isEmpty() || netoStr.isEmpty() || ivaStr.isEmpty()
                || proveedor == null) {
            JOptionPane.showMessageDialog(this,
                "Completá todos los campos obligatorios.",
                "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double neto, iva;
        try {
            neto = Double.parseDouble(netoStr);
            iva  = Double.parseDouble(ivaStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Los importes deben ser numéricos.",
                "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("Factura".equals(tipo)) {
            registrarFactura(numero, neto, iva, letra, proveedor);

        } else if ("Nota de Crédito".equals(tipo)) {
            NotaDeCredito nc = new NotaDeCredito(
                numero, new Date(), neto, iva, letra, proveedor, null);
            gestorComprobantes.getComprobantes().add(nc);
            proveedor.getCuentaCorriente().asociarComprobante(nc);
            proveedor.getCuentaCorriente().registrarMovimiento(
                new Movimiento(new Date(), "Nota de Crédito", nc.getNumero(),
                    0, nc.getImporteTotal(),
                    proveedor.getCuentaCorriente().getSaldoActual()
                    - nc.getImporteTotal()));
            JOptionPane.showMessageDialog(this,
                "✅ Nota de Crédito registrada correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } else if ("Nota de Débito".equals(tipo)) {
            NotaDeDebito nd = new NotaDeDebito(
                numero, new Date(), neto, iva, letra, proveedor);
            gestorComprobantes.getComprobantes().add(nd);
            proveedor.getCuentaCorriente().asociarComprobante(nd);
            proveedor.getCuentaCorriente().registrarMovimiento(
                new Movimiento(new Date(), "Nota de Débito", nd.getNumero(),
                    nd.getImporteTotal(), 0,
                    proveedor.getCuentaCorriente().getSaldoActual()
                    + nd.getImporteTotal()));
            JOptionPane.showMessageDialog(this,
                "✅ Nota de Débito registrada correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }

        cargarTablaComprobantes();
        limpiarFormulario();
    }

    // ── Lógica específica de factura ────────────────────────────────────
    private void registrarFactura(String numero, double neto, double iva,
                                   LetraComprobante letra, Proveedor proveedor) {
        Factura factura = new Factura(numero, new Date(), neto, iva, letra, proveedor);

        // Agregar items desde la tabla
        for (int i = 0; i < modeloItems.getRowCount(); i++) {
            String desc  = (String) modeloItems.getValueAt(i, 0);
            int cant     = Integer.parseInt(modeloItems.getValueAt(i, 1).toString());
            double precio = Double.parseDouble(
                ((String) modeloItems.getValueAt(i, 2)).replace("$", ""));
            Producto prod = buscarProductoPorDescripcion(desc);
            if (prod != null) {
                factura.agregarItem(new ItemComprobante(prod, cant, precio));
            }
        }

        String numeroOC = txtNumeroOC.getText().trim();

        if (!numeroOC.isEmpty()) {
            // Con OC — validar precios
            OrdenDeCompra oc = gestorOC.buscarOrdenDeCompra(numeroOC);
            if (oc == null) {
                JOptionPane.showMessageDialog(this,
                    "No se encontró la OC: " + numeroOC,
                    "OC no encontrada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ResultadoValidacion resultado = factura.validarContraOC(oc);

            if (resultado.isValido()) {
                // Flujo exitoso
                factura.vincularOC(oc);
                agregarFacturaAlSistema(factura, proveedor);
                JOptionPane.showMessageDialog(this,
                    "✅ Factura registrada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Requiere autorización
                int decision = JOptionPane.showConfirmDialog(this,
                    "⚠️ " + resultado.getMensaje()
                    + "\n¿Solicitar autorización de supervisor?",
                    "Requiere Autorización",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (decision == JOptionPane.YES_OPTION) {
                    String nombreSup = JOptionPane.showInputDialog(this,
                        "Nombre del Supervisor:");
                    if (nombreSup != null && !nombreSup.trim().isEmpty()) {
                        Usuario sup = new Usuario(
                            nombreSup, nombreSup, "", Rol.SUPERVISOR);
                        factura.marcarAutorizada(sup, resultado.getMensaje());
                        factura.vincularOC(oc);
                        agregarFacturaAlSistema(factura, proveedor);
                        JOptionPane.showMessageDialog(this,
                            "✅ Factura registrada con autorización.",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

        } else {
            // Sin OC — siempre requiere autorización
            int decision = JOptionPane.showConfirmDialog(this,
                "⚠️ Esta factura no tiene OC asociada.\n"
                + "¿Solicitar autorización de supervisor?",
                "Requiere Autorización",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (decision == JOptionPane.YES_OPTION) {
                String nombreSup = JOptionPane.showInputDialog(this,
                    "Nombre del Supervisor:");
                if (nombreSup != null && !nombreSup.trim().isEmpty()) {
                    Usuario sup = new Usuario(
                        nombreSup, nombreSup, "", Rol.SUPERVISOR);
                    factura.marcarAutorizada(sup, "Sin OC asociada");
                    agregarFacturaAlSistema(factura, proveedor);
                    JOptionPane.showMessageDialog(this,
                        "✅ Factura registrada con autorización.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    // ── Método auxiliar para agregar factura al sistema ─────────────────
    private void agregarFacturaAlSistema(Factura factura, Proveedor proveedor) {
        gestorComprobantes.getComprobantes().add(factura);
        proveedor.getCuentaCorriente().asociarComprobante(factura);
        proveedor.getCuentaCorriente().registrarMovimiento(
            new Movimiento(new Date(), "Factura", factura.getNumero(),
                factura.getImporteTotal(), 0,
                proveedor.getCuentaCorriente().getSaldoActual()
                + factura.getImporteTotal()));
    }

    // ── Buscar producto por descripción ─────────────────────────────────
    private Producto buscarProductoPorDescripcion(String descripcion) {
        for (Producto p : GestorCatalogo.getInstancia().getProductos()) {
            if (p.getDescripcion().equals(descripcion)) return p;
        }
        return null;
    }

    // ── Cargar tabla comprobantes ────────────────────────────────────────
    private void cargarTablaComprobantes() {
        modeloComprobantes.setRowCount(0);
        for (modelo.comprobantes.Comprobante c :
                gestorComprobantes.getComprobantes()) {
            modeloComprobantes.addRow(new Object[]{
                c.getNumero(),
                c.getDescripcionTipo(),
                c.getLetra(),
                c.getProveedor().getRazonSocial(),
                String.format("$%.2f", c.getImporteNeto()),
                String.format("$%.2f", c.getImporteIVA()),
                String.format("$%.2f", c.getImporteTotal())
            });
        }
    }

    // ── Limpiar formulario ───────────────────────────────────────────────
    private void limpiarFormulario() {
        txtNumeroComprobante.setText("");
        txtImporteNeto.setText("");
        txtImporteIVA.setText("");
        txtNumeroOC.setText("");
        txtCodigoProducto.setText("");
        txtCantidad.setText("");
        txtPrecioUnitario.setText("");
        modeloItems.setRowCount(0);
        cmbTipo.setSelectedIndex(0);
        cmbLetra.setSelectedIndex(0);
    }
}