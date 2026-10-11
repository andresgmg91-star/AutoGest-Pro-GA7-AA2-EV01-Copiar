package com.autogestpro.ui;

import com.autogestpro.dao.ClienteDAO;
import com.autogestpro.model.Cliente;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

/** Interfaz de escritorio para administrar los clientes del taller. */
public class VentanaClientes extends JFrame {
    private final ClienteDAO clienteDAO = new ClienteDAO();

    private final JComboBox<String> tipoDocumento = new JComboBox<>(new String[]{
            "Cédula de ciudadanía", "Cédula de extranjería", "NIT",
            "Pasaporte", "Permiso por Protección Temporal"
    });
    private final JTextField numeroDocumento = new JTextField();
    private final JTextField nombres = new JTextField();
    private final JTextField apellidos = new JTextField();
    private final JTextField telefono = new JTextField();
    private final JTextField correo = new JTextField();
    private final JTextField direccion = new JTextField();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Tipo documento", "Número", "Nombres", "Apellidos",
                    "Teléfono", "Correo", "Dirección"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tablaClientes = new JTable(modeloTabla);
    private Long clienteSeleccionadoId;

    public VentanaClientes() {
        super("AutoGest Pro | Gestión de clientes");
        crearInterfaz();
        consultarClientes();
    }

    private void crearInterfaz() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 650));
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("AutoGest Pro - Gestión de clientes", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(14, 8, 8, 8));

        JPanel formulario = new JPanel(new GridLayout(4, 4, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del cliente"));
        formulario.add(new JLabel("Tipo de documento *"));
        formulario.add(tipoDocumento);
        formulario.add(new JLabel("Número de documento *"));
        formulario.add(numeroDocumento);
        formulario.add(new JLabel("Nombres *"));
        formulario.add(nombres);
        formulario.add(new JLabel("Apellidos *"));
        formulario.add(apellidos);
        formulario.add(new JLabel("Teléfono"));
        formulario.add(telefono);
        formulario.add(new JLabel("Correo"));
        formulario.add(correo);
        formulario.add(new JLabel("Dirección"));
        formulario.add(direccion);
        formulario.add(new JLabel("* Campos obligatorios"));
        formulario.add(new JLabel(""));

        JButton botonGuardar = new JButton("Guardar cliente");
        JButton botonConsultar = new JButton("Consultar todos");
        JButton botonBuscar = new JButton("Buscar por documento");
        JButton botonActualizar = new JButton("Actualizar seleccionado");
        JButton botonEliminar = new JButton("Eliminar seleccionado");
        JButton botonLimpiar = new JButton("Limpiar formulario");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        botones.add(botonGuardar);
        botones.add(botonConsultar);
        botones.add(botonBuscar);
        botones.add(botonActualizar);
        botones.add(botonEliminar);
        botones.add(botonLimpiar);

        JPanel zonaSuperior = new JPanel(new BorderLayout(8, 8));
        zonaSuperior.add(titulo, BorderLayout.NORTH);
        zonaSuperior.add(formulario, BorderLayout.CENTER);
        zonaSuperior.add(botones, BorderLayout.SOUTH);

        tablaClientes.setAutoCreateRowSorter(true);
        tablaClientes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.setFillsViewportHeight(true);
        tablaClientes.getSelectionModel().addListSelectionListener(this::seleccionarFila);

        JScrollPane desplazamiento = new JScrollPane(tablaClientes);
        desplazamiento.setBorder(BorderFactory.createTitledBorder("Clientes registrados"));

        add(zonaSuperior, BorderLayout.NORTH);
        add(desplazamiento, BorderLayout.CENTER);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        botonGuardar.addActionListener(event -> guardarCliente());
        botonConsultar.addActionListener(event -> consultarClientes());
        botonBuscar.addActionListener(event -> buscarCliente());
        botonActualizar.addActionListener(event -> actualizarCliente());
        botonEliminar.addActionListener(event -> eliminarCliente());
        botonLimpiar.addActionListener(event -> limpiarFormulario());
    }

    private void seleccionarFila(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting()) {
            return;
        }
        int filaVista = tablaClientes.getSelectedRow();
        if (filaVista < 0) {
            clienteSeleccionadoId = null;
            return;
        }
        int filaModelo = tablaClientes.convertRowIndexToModel(filaVista);
        clienteSeleccionadoId = Long.parseLong(modeloTabla.getValueAt(filaModelo, 0).toString());
        tipoDocumento.setSelectedItem(textoCelda(filaModelo, 1));
        numeroDocumento.setText(textoCelda(filaModelo, 2));
        nombres.setText(textoCelda(filaModelo, 3));
        apellidos.setText(textoCelda(filaModelo, 4));
        telefono.setText(textoCelda(filaModelo, 5));
        correo.setText(textoCelda(filaModelo, 6));
        direccion.setText(textoCelda(filaModelo, 7));
    }

    private String textoCelda(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private void guardarCliente() {
        if (!formularioValido()) {
            return;
        }
        try {
            long id = clienteDAO.insertar(leerCliente());
            JOptionPane.showMessageDialog(this, "Cliente guardado correctamente. ID: " + id,
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            consultarClientes();
        } catch (SQLException exception) {
            mostrarError("No fue posible guardar el cliente.", exception);
        }
    }

    private void consultarClientes() {
        try {
            mostrarClientes(clienteDAO.consultarTodos());
        } catch (SQLException exception) {
            mostrarError("No fue posible consultar los clientes.", exception);
        }
    }

    private void buscarCliente() {
        String documento = numeroDocumento.getText().trim();
        if (documento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escriba el número de documento para buscar.",
                    "Dato requerido", JOptionPane.WARNING_MESSAGE);
            numeroDocumento.requestFocus();
            return;
        }
        try {
            List<Cliente> resultados = clienteDAO.buscarPorDocumento(documento);
            mostrarClientes(resultados);
            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron clientes con ese documento.",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException exception) {
            mostrarError("No fue posible realizar la búsqueda.", exception);
        }
    }

    private void actualizarCliente() {
        if (clienteSeleccionadoId == null) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un cliente de la tabla.",
                    "Seleccione un cliente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!formularioValido()) {
            return;
        }
        try {
            boolean actualizado = clienteDAO.actualizar(clienteSeleccionadoId, leerCliente());
            if (actualizado) {
                JOptionPane.showMessageDialog(this, "Los datos del cliente fueron actualizados.",
                        "Actualización exitosa", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                consultarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "El cliente ya no existe en la base de datos.",
                        "Registro no encontrado", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException exception) {
            mostrarError("No fue posible actualizar el cliente.", exception);
        }
    }

    private void eliminarCliente() {
        if (clienteSeleccionadoId == null) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un cliente de la tabla.",
                    "Seleccione un cliente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el cliente seleccionado?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            boolean eliminado = clienteDAO.eliminar(clienteSeleccionadoId);
            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.",
                        "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                consultarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el cliente para eliminar.",
                        "Registro no encontrado", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException exception) {
            mostrarError("No fue posible eliminar el cliente.", exception);
        }
    }

    private Cliente leerCliente() {
        Cliente cliente = new Cliente();
        cliente.setTipoDocumento(String.valueOf(tipoDocumento.getSelectedItem()));
        cliente.setNumeroDocumento(numeroDocumento.getText().trim());
        cliente.setNombres(nombres.getText().trim());
        cliente.setApellidos(apellidos.getText().trim());
        cliente.setTelefono(telefono.getText().trim());
        cliente.setCorreo(correo.getText().trim());
        cliente.setDireccion(direccion.getText().trim());
        return cliente;
    }

    private boolean formularioValido() {
        if (numeroDocumento.getText().trim().isEmpty()
                || nombres.getText().trim().isEmpty()
                || apellidos.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Complete los campos obligatorios: número de documento, nombres y apellidos.",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void mostrarClientes(List<Cliente> clientes) {
        modeloTabla.setRowCount(0);
        clienteSeleccionadoId = null;
        for (Cliente cliente : clientes) {
            modeloTabla.addRow(new Object[]{
                    cliente.getId(), cliente.getTipoDocumento(), cliente.getNumeroDocumento(),
                    cliente.getNombres(), cliente.getApellidos(), cliente.getTelefono(),
                    cliente.getCorreo(), cliente.getDireccion()
            });
        }
    }

    private void limpiarFormulario() {
        clienteSeleccionadoId = null;
        tablaClientes.clearSelection();
        tipoDocumento.setSelectedIndex(0);
        numeroDocumento.setText("");
        nombres.setText("");
        apellidos.setText("");
        telefono.setText("");
        correo.setText("");
        direccion.setText("");
        numeroDocumento.requestFocus();
    }

    private void mostrarError(String mensaje, SQLException exception) {
        JOptionPane.showMessageDialog(this,
                mensaje + "\nRevise que MySQL/MariaDB esté iniciado, que la base de datos exista "
                        + "y que los datos de conexión sean correctos.\nDetalle: "
                        + exception.getMessage(),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }
}
