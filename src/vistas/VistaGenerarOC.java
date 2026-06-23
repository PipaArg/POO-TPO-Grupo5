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

public class VistaGenerarOC extends JFrame {

    private Controlador controlador;
    private OrdenDeCompra ocActual;

    // Componentes de la pantalla
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

    public VistaGenerarOC() {
        this.controlador = Controlador.getInstancia();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Configuración de la ventana
        setTitle("FarmaRed - Generar Orden de Compra");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ── Panel superior — búsqueda de proveedor ──────────────────────
        JPanel panelProveedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelProveedor.setBorder(BorderFactory.createTitledBorder("Proveedor"));

        panelProveedor.add(new JLabel("CUIT:"));
        txtCuit = new JTextField(15);
        panelProveedor.add(txtCuit);

        btnBuscarProveedor = new JButton("Buscar");
        panelProveedor.add(btnBuscarProveedor);

        lblProveedor = new JLabel("Proveedor: (no seleccionado)");
        panelProveedor.add(lblProveedor);

        add(panelProveedor, BorderLayout.NORTH);

        // ── Panel central — agregar items ───────────────────────────────
        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));

        JPanel panelItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelItem.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));

        panelItem.add(new JLabel("Código Producto:"));
        txtCodigoProducto = new JTextField(10);
        panelItem.add(txtCodigoProducto);

        panelItem.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField(5);
        panelItem.add(txtCantidad);

        panelItem.add(new JLabel("Precio Unitario:"));
        txtPrecio = new JTextField(8);
        panelItem.add(txtPrecio);

        btnAgregarItem = new JButton("Agregar Item");
        btnAgregarItem.setEnabled(false);
        panelItem.add(btnAgregarItem);

        panelCentro.add(panelItem, BorderLayout.NORTH);

        // Tabla de items
        String[] columnas = {"Código", "Descripción", "Cantidad", "Precio Unit.", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaItems = new JTable(modeloTabla);
        tablaItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        // Proveedor encontrado — crear la OC
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

        int cantidad = Integer.parseInt(cantidadStr);
        double precio = Double.parseDouble(precioStr);
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
            // Flujo exitoso — dentro del tope
            controlador.confirmarOC(ocActual);
            JOptionPane.showMessageDialog(this,
                "✅ Orden de Compra generada exitosamente.\nNúmero: " + ocActual.getNumero(),
                "OC Confirmada", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        } else {
            // Supera el tope — requiere autorización del supervisor
            int decision = JOptionPane.showConfirmDialog(this,
                "⚠️ La OC supera el tope de deuda del proveedor.\n"
                + "¿Desea solicitar autorización de un Supervisor?",
                "Requiere Autorización", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (decision == JOptionPane.YES_OPTION) {
                // Simulamos la autorización del supervisor
                String nombreSupervisor = JOptionPane.showInputDialog(this,
                    "Ingresá el nombre de usuario del Supervisor:");

                if (nombreSupervisor != null && !nombreSupervisor.trim().isEmpty()) {
                    Usuario supervisor = new Usuario(
                        nombreSupervisor, nombreSupervisor, "", Rol.SUPERVISOR);
                    controlador.confirmarOCConAutorizacion(ocActual, supervisor);
                    JOptionPane.showMessageDialog(this,
                        "✅ OC generada con autorización del Supervisor: " + nombreSupervisor
                        + "\nNúmero: " + ocActual.getNumero(),
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
