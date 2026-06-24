package vistas;

import controladores.GestorCatalogo;
import controladores.GestorProveedores;
import modelo.catalogo.PrecioAcordado;
import modelo.catalogo.Producto;
import modelo.enums.TipoIVA;
import modelo.enums.UnidadDeMedida;
import modelo.proveedores.Proveedor;
import modelo.proveedores.Rubro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class PanelCatalogo extends JPanel {

    private GestorCatalogo gestorCatalogo;
    private GestorProveedores gestorProveedores;
    private Runnable onVolver;

    // Componentes formulario producto
    private JTextField txtCodigo;
    private JTextField txtDescripcion;
    private JComboBox<UnidadDeMedida> cmbUnidad;
    private JComboBox<TipoIVA> cmbTipoIVA;
    private JComboBox<Rubro> cmbRubro;

    // Componentes precio acordado
    private JComboBox<Producto> cmbProducto;
    private JComboBox<Proveedor> cmbProveedor;
    private JTextField txtPrecio;

    // Tablas
    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;
    private JTable tablaPrecios;
    private DefaultTableModel modeloPrecios;

    // Botones
    private JButton btnAgregarProducto;
    private JButton btnLimpiarProducto;
    private JButton btnAgregarPrecio;
    private JButton btnVolver;

    public PanelCatalogo(Runnable onVolver) {
        this.gestorCatalogo = GestorCatalogo.getInstancia();
        this.gestorProveedores = GestorProveedores.getInstancia();
        this.onVolver = onVolver;
        inicializarComponentes();
        cargarTablas();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // ── Título y volver ─────────────────────────────────────────────
        JPanel panelTitulo = new JPanel(new BorderLayout());
        btnVolver = new JButton("← Volver al Menú");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel lblTitulo = new JLabel("Catálogo de Productos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelTitulo.add(btnVolver, BorderLayout.WEST);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        add(panelTitulo, BorderLayout.NORTH);

        // ── Panel izquierdo — formularios ───────────────────────────────
        JPanel panelIzquierdo = new JPanel(new GridLayout(2, 1, 0, 10));
        panelIzquierdo.setPreferredSize(new Dimension(320, 500));

        // Formulario nuevo producto
        JPanel panelProducto = new JPanel(new GridBagLayout());
        panelProducto.setBorder(BorderFactory.createTitledBorder("Nuevo Producto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelProducto.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        txtCodigo = new JTextField(15);
        panelProducto.add(txtCodigo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelProducto.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        txtDescripcion = new JTextField(15);
        panelProducto.add(txtDescripcion, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelProducto.add(new JLabel("Unidad de Medida:"), gbc);
        gbc.gridx = 1;
        cmbUnidad = new JComboBox<>(UnidadDeMedida.values());
        panelProducto.add(cmbUnidad, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelProducto.add(new JLabel("Tipo IVA:"), gbc);
        gbc.gridx = 1;
        cmbTipoIVA = new JComboBox<>(TipoIVA.values());
        panelProducto.add(cmbTipoIVA, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelProducto.add(new JLabel("Rubro:"), gbc);
        gbc.gridx = 1;
        cmbRubro = new JComboBox<>();
        for (Rubro r : gestorProveedores.getRubros()) {
            cmbRubro.addItem(r);
        }
        cmbRubro.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                if (value instanceof Rubro) setText(((Rubro) value).getNombre());
                return this;
            }
        });
        panelProducto.add(cmbRubro, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel pnlBtnProd = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnAgregarProducto = new JButton("Agregar Producto");
        btnAgregarProducto.setBackground(new Color(0, 150, 0));
        btnAgregarProducto.setForeground(Color.WHITE);
        btnLimpiarProducto = new JButton("Limpiar");
        pnlBtnProd.add(btnAgregarProducto);
        pnlBtnProd.add(btnLimpiarProducto);
        panelProducto.add(pnlBtnProd, gbc);

        panelIzquierdo.add(panelProducto);

        // Formulario precio acordado
        JPanel panelPrecio = new JPanel(new GridBagLayout());
        panelPrecio.setBorder(BorderFactory.createTitledBorder("Agregar Precio Acordado"));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(4, 8, 4, 8);
        gbc2.anchor = GridBagConstraints.WEST;

        gbc2.gridx = 0; gbc2.gridy = 0;
        panelPrecio.add(new JLabel("Producto:"), gbc2);
        gbc2.gridx = 1;
        cmbProducto = new JComboBox<>();
        cmbProducto.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                if (value instanceof Producto)
                    setText(((Producto) value).getDescripcion());
                return this;
            }
        });
        panelPrecio.add(cmbProducto, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 1;
        panelPrecio.add(new JLabel("Proveedor:"), gbc2);
        gbc2.gridx = 1;
        cmbProveedor = new JComboBox<>();
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
        for (Proveedor p : gestorProveedores.getProveedores()) {
            cmbProveedor.addItem(p);
        }
        panelPrecio.add(cmbProveedor, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 2;
        panelPrecio.add(new JLabel("Precio:"), gbc2);
        gbc2.gridx = 1;
        txtPrecio = new JTextField(15);
        txtPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.'
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        panelPrecio.add(txtPrecio, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 3;
        gbc2.gridwidth = 2;
        gbc2.anchor = GridBagConstraints.CENTER;
        btnAgregarPrecio = new JButton("Agregar Precio");
        btnAgregarPrecio.setBackground(new Color(0, 100, 180));
        btnAgregarPrecio.setForeground(Color.WHITE);
        panelPrecio.add(btnAgregarPrecio, gbc2);

        panelIzquierdo.add(panelPrecio);
        add(panelIzquierdo, BorderLayout.WEST);

        // ── Panel central — tablas ───────────────────────────────────────
        JPanel panelTablas = new JPanel(new GridLayout(2, 1, 0, 10));

        String[] colProductos = {"Código", "Descripción", "Unidad", "IVA", "Rubro"};
        modeloProductos = new DefaultTableModel(colProductos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaProductos = new JTable(modeloProductos);
        JScrollPane scrollProd = new JScrollPane(tablaProductos);
        scrollProd.setBorder(BorderFactory.createTitledBorder("Productos"));
        panelTablas.add(scrollProd);

        String[] colPrecios = {"Producto", "Proveedor", "Precio", "Fecha"};
        modeloPrecios = new DefaultTableModel(colPrecios, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPrecios = new JTable(modeloPrecios);
        JScrollPane scrollPrecios = new JScrollPane(tablaPrecios);
        scrollPrecios.setBorder(BorderFactory.createTitledBorder("Precios Acordados"));
        panelTablas.add(scrollPrecios);

        add(panelTablas, BorderLayout.CENTER);

        // ── Eventos ─────────────────────────────────────────────────────
        btnVolver.addActionListener(e -> onVolver.run());
        btnAgregarProducto.addActionListener(e -> agregarProducto());
        btnLimpiarProducto.addActionListener(e -> limpiarFormulario());
        btnAgregarPrecio.addActionListener(e -> agregarPrecioAcordado());
    }

    private void agregarProducto() {
        String codigo = txtCodigo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        UnidadDeMedida unidad = (UnidadDeMedida) cmbUnidad.getSelectedItem();
        TipoIVA tipoIVA = (TipoIVA) cmbTipoIVA.getSelectedItem();
        Rubro rubro = (Rubro) cmbRubro.getSelectedItem();

        if (codigo.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Código y descripción son obligatorios.",
                "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (gestorCatalogo.buscarProducto(codigo) != null) {
            JOptionPane.showMessageDialog(this,
                "Ya existe un producto con ese código.",
                "Código duplicado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Producto p = new Producto(codigo, descripcion, unidad, tipoIVA, rubro);
        gestorCatalogo.registrarProducto(p);
        cmbProducto.addItem(p);
        cargarTablas();
        limpiarFormulario();
        JOptionPane.showMessageDialog(this,
            "✅ Producto registrado correctamente.",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void agregarPrecioAcordado() {
        Producto producto = (Producto) cmbProducto.getSelectedItem();
        Proveedor proveedor = (Proveedor) cmbProveedor.getSelectedItem();
        String precioStr = txtPrecio.getText().trim();

        if (producto == null || proveedor == null || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Seleccioná producto, proveedor e ingresá el precio.",
                "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "El precio debe ser un valor numérico.",
                "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PrecioAcordado pa = new PrecioAcordado(precio, new Date(), proveedor, producto);
        producto.agregarPrecioAcordado(pa);
        cargarTablas();
        txtPrecio.setText("");
        JOptionPane.showMessageDialog(this,
            "✅ Precio acordado registrado correctamente.",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarTablas() {
        // Tabla productos
        modeloProductos.setRowCount(0);
        for (Producto p : gestorCatalogo.getProductos()) {
            modeloProductos.addRow(new Object[]{
                p.getCodigo(),
                p.getDescripcion(),
                p.getUnidadDeMedida(),
                p.getTipoIVA(),
                p.getRubro() != null ? p.getRubro().getNombre() : "-"
            });
        }

        // Tabla precios acordados
        modeloPrecios.setRowCount(0);
        for (Producto p : gestorCatalogo.getProductos()) {
            for (PrecioAcordado pa : p.getHistorialPrecios()) {
                modeloPrecios.addRow(new Object[]{
                    p.getDescripcion(),
                    pa.getProveedor().getRazonSocial(),
                    String.format("$%.2f", pa.getPrecio()),
                    pa.getFechaAcuerdo()
                });
            }
        }
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtDescripcion.setText("");
        cmbUnidad.setSelectedIndex(0);
        cmbTipoIVA.setSelectedIndex(0);
    }
}