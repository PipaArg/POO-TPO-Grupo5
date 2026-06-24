package vistas;

import controladores.GestorUsuarios;
import modelo.enums.Rol;
import modelo.usuarios.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelGestionUsuarios extends JPanel {

    private GestorUsuarios gestorUsuarios;
    private Runnable onVolver;

    // Componentes
    private JTextField txtNombreCompleto;
    private JTextField txtNombreUsuario;
    private JTextField txtEmail;
    private JComboBox<Rol> cmbRol;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar;
    private JButton btnLimpiar;
    private JButton btnVolver;

    public PanelGestionUsuarios(Runnable onVolver) {
        this.gestorUsuarios = GestorUsuarios.getInstancia();
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
        JLabel lblTitulo = new JLabel("Gestión de Usuarios", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelTitulo.add(btnVolver, BorderLayout.WEST);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        add(panelTitulo, BorderLayout.NORTH);

        // ── Formulario ──────────────────────────────────────────────────
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Nuevo Usuario"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1;
        txtNombreCompleto = new JTextField(20);
        panelFormulario.add(txtNombreCompleto, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Nombre de Usuario:"), gbc);
        gbc.gridx = 1;
        txtNombreUsuario = new JTextField(20);
        panelFormulario.add(txtNombreUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        panelFormulario.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1;
        cmbRol = new JComboBox<>(Rol.values());
        panelFormulario.add(cmbRol, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnAgregar = new JButton("Agregar Usuario");
        btnAgregar.setBackground(new Color(0, 150, 0));
        btnAgregar.setForeground(Color.WHITE);
        btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnAgregar);
        panelBotones.add(btnLimpiar);
        panelFormulario.add(panelBotones, gbc);

        add(panelFormulario, BorderLayout.WEST);

        // ── Tabla ───────────────────────────────────────────────────────
        String[] columnas = {"Nombre Completo", "Usuario", "Email", "Rol", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        scroll.setBorder(BorderFactory.createTitledBorder("Usuarios registrados"));
        add(scroll, BorderLayout.CENTER);

        // ── Eventos ─────────────────────────────────────────────────────
        btnVolver.addActionListener(e -> onVolver.run());
        btnAgregar.addActionListener(e -> agregarUsuario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    private void agregarUsuario() {
        String nombreCompleto = txtNombreCompleto.getText().trim();
        String nombreUsuario  = txtNombreUsuario.getText().trim();
        String email          = txtEmail.getText().trim();
        Rol rol               = (Rol) cmbRol.getSelectedItem();

        if (nombreCompleto.isEmpty() || nombreUsuario.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre completo y el nombre de usuario son obligatorios.",
                "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (gestorUsuarios.buscarUsuario(nombreUsuario) != null) {
            JOptionPane.showMessageDialog(this,
                "Ya existe un usuario con ese nombre de usuario.",
                "Usuario duplicado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario u = new Usuario(nombreCompleto, nombreUsuario, email, rol);
        gestorUsuarios.registrarUsuario(u);
        cargarTabla();
        limpiarFormulario();
        JOptionPane.showMessageDialog(this,
            "✅ Usuario registrado correctamente.",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Usuario> usuarios = gestorUsuarios.getUsuarios();
        for (Usuario u : usuarios) {
            modeloTabla.addRow(new Object[]{
                u.getNombreCompleto(),
                u.getNombreUsuario(),
                u.getEmail(),
                u.getRol(),
                u.estaActivo() ? "✅" : "❌"
            });
        }
    }

    private void limpiarFormulario() {
        txtNombreCompleto.setText("");
        txtNombreUsuario.setText("");
        txtEmail.setText("");
        cmbRol.setSelectedIndex(0);
    }
}