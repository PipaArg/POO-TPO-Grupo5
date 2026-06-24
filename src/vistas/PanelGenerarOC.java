package vistas;

import controladores.Controlador;
import modelo.catalogo.Producto;
import modelo.comprobantes.ResultadoValidacion;
import modelo.ordenes.OrdenDeCompra;
import modelo.proveedores.Proveedor;
import modelo.usuarios.Usuario;
import modelo.enums.Rol;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelGenerarOC extends JPanel {

    private Controlador controlador;
    private OrdenDeCompra ocActual;
    private Runnable onVolver;

    // Componentes
    private JTextField txtCuit;
    private JLabel lblProveedor;
    private JTextField txtCodigoProducto;
    private JTextField txtCantidad;
    private JTextField txtPrecio;
    private JTable tablaItems;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private JButton btnBuscarProveedor;
    private JButton btnAgregarItem;
    private JButton btnConfirmar;
    private JButton btnLimpiar;
    private JButton btnVolver;

    public PanelGenerarOC(Runnable onVolver) {
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
        JLabel lblTitulo = new JLabel("Generar Orden de Compra", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelTitulo.add(btnVolver, BorderLayout.WEST);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);

        // ── Panel proveedor ─────────────────────────────────────────────
        JPanel panelProveedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelProveedor.setBorder(BorderFactory.createTitledBorder("Proveedor"));
        panelProveedor.add(new JLabel("CUIT:"));
        txtCuit = new JTextField(15);
    txtCuit.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent e) {
        char c = e.getKeyChar();
        if (!Character.isDigit(c) && c != '-' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
            e.consume();
        }
    }
});
        panelProveedor.add(txtCuit);
        btnBuscarProveedor = new JButton("Buscar");
        panelProveedor.add(btnBuscarProveedor);
        lblProveedor = new JLabel("Proveedor: (no seleccionado)");
        panelProveedor.add(lblProveedor);

        JPanel panelNorth = new JPanel(new GridLayout(2, 1));
        panelNorth.add(panelTitulo);
        panelNorth.add(panelProveedor);
        add(panelNorth, BorderLayout.NORTH);

        // ── Panel central — items ───────────────────────────────────────
        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));

        JPanel panelItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelItem.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));
        panelItem.add(new JLabel("Código:"));
        txtCodigoProducto = new JTextField(10);
        panelItem.add(txtCodigoProducto);
        panelItem.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField(5);
    txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent e) {
        char c = e.getKeyChar();
        if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
            e.consume();
        }
    }
});
        panelItem.add(txtCantidad);
        panelItem.add(new JLabel("Precio:"));
        txtPrecio = new JTextField(8);
    txtPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent e) {
        char c = e.getKeyChar();
        if (!Character.isDigit(c) && c != '.' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
            e.consume();
        }
    }
});
        panelItem.add(txtPrecio);
        btnAgregarItem = new JButton("Agregar Item");
        btnAgregarItem.setEnabled(false);
        panelItem.add(btnAgregarItem);
        panelCentro.add(panelItem, BorderLayout.NORTH);

        String[] columnas = {"Código", "Descripción", "Cantidad", "Precio Unit.", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaItems = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaItems);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Items de la Orden"));
        panelCentro.add(scrollTabla, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

        // ── Panel inferior — total y botones ────────────────────────────
        JPanel panelInferior = new JPanel(new BorderLayout());
        lblTotal = new JLabel("Total OC: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelInferior.add(lblTotal, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnLimpiar = new JButton("Limpiar");
        btnConfirmar = new JButton("Confirmar OC");
        btnConfirmar.setEnabled(false);
        btnConfirmar.setBackground(new Color(0, 150, 0));
        btnConfirmar.setForeground(Color.WHITE);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnConfirmar);
        panelInferior.add(panelBotones, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);

        // ── Eventos ─────────────────────────────────────────────────────
        btnVolver.addActionListener(e -> {
            limpiarFormulario();
            onVolver.run();
        });
        btnBuscarProveedor.addActionListener(e -> buscarProveedor());
        btnAgregarItem.addActionListener(e -> agregarItem());
        btnConfirmar.addActionListener(e -> confirmarOC());
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
        Proveedor proveedor = controlador.buscarProveedor(cuit);
        if (proveedor == null) {
            JOptionPane.showMessageDialog(this,
                "No se encontró un proveedor con el CUIT: " + cuit,
                "Proveedor no encontrado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ocActual = controlador.crearOrdenDeCompra(proveedor);
        lblProveedor.setText("Proveedor: " + proveedor.getRazonSocial()
                + " | Tope: $" + proveedor.getTopeMaximoDeuda());
        btnAgregarItem.setEnabled(true);
        btnConfirmar.setEnabled(true);
        JOptionPane.showMessageDialog(this,
            "Proveedor encontrado: " + proveedor.getRazonSocial(),
            "Proveedor seleccionado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void agregarItem() {
        String codigo = txtCodigoProducto.getText().trim();
        String cantidadStr = txtCantidad.getText().trim();
        String precioStr = txtPrecio.getText().trim();

        if (codigo.isEmpty() || cantidadStr.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Completá todos los campos del producto.",
                "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Producto producto = controlador.buscarProducto(codigo);
        if (producto == null) {
            JOptionPane.showMessageDialog(this,
                "No se encontró el producto con código: " + codigo,
                "Producto no encontrado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int cantidad;
        double precio;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "La cantidad y el precio deben ser valores numéricos válidos.",
                "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double subtotal = cantidad * precio;
        controlador.agregarItemAOC(ocActual, producto, cantidad, precio);
        modeloTabla.addRow(new Object[]{
            producto.getCodigo(),
            producto.getDescripcion(),
            cantidad,
            String.format("$%.2f", precio),
            String.format("$%.2f", subtotal)
        });
        actualizarTotal();
        limpiarCamposItem();
    }

    private void confirmarOC() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Agregá al menos un producto a la orden.",
                "OC vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ResultadoValidacion resultado = controlador.validarTopeDeDeuda(ocActual);

        if (resultado.isValido()) {
            controlador.confirmarOC(ocActual);
            JOptionPane.showMessageDialog(this,
                "✅ Orden de Compra generada exitosamente.\nNúmero: "
                + ocActual.getNumero(),
                "OC Confirmada", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        } else {
            int decision = JOptionPane.showConfirmDialog(this,
                "⚠️ La OC supera el tope de deuda del proveedor.\n"
                + "¿Desea solicitar autorización de un Supervisor?",
                "Requiere Autorización",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (decision == JOptionPane.YES_OPTION) {
                String nombreSupervisor = JOptionPane.showInputDialog(this,
                    "Ingresá el nombre de usuario del Supervisor:");
                if (nombreSupervisor != null && !nombreSupervisor.trim().isEmpty()) {
                    Usuario supervisor = new Usuario(
                        nombreSupervisor, nombreSupervisor, "", Rol.SUPERVISOR);
                    controlador.confirmarOCConAutorizacion(ocActual, supervisor);
                    JOptionPane.showMessageDialog(this,
                        "✅ OC generada con autorización del Supervisor: "
                        + nombreSupervisor + "\nNúmero: " + ocActual.getNumero(),
                        "OC Autorizada", JOptionPane.INFORMATION_MESSAGE);
                    limpiarFormulario();
                }
            } else {
                controlador.cancelarOC(ocActual);
                JOptionPane.showMessageDialog(this,
                    "❌ Operación cancelada.",
                    "OC Cancelada", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
            }
        }
    }

    private void actualizarTotal() {
        double total = ocActual.calcularTotalOC();
        lblTotal.setText(String.format("Total OC: $%.2f", total));
    }

    private void limpiarCamposItem() {
        txtCodigoProducto.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
    }

    private void limpiarFormulario() {
        txtCuit.setText("");
        lblProveedor.setText("Proveedor: (no seleccionado)");
        modeloTabla.setRowCount(0);
        lblTotal.setText("Total OC: $0.00");
        btnAgregarItem.setEnabled(false);
        btnConfirmar.setEnabled(false);
        ocActual = null;
        limpiarCamposItem();
    }
}