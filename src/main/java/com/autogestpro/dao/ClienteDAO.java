package com.autogestpro.dao;

import com.autogestpro.config.DatabaseConnection;
import com.autogestpro.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Contiene las operaciones SQL para la entidad Cliente. */
public class ClienteDAO {
    private static final String CONSULTA_BASE = """
            SELECT id, tipo_documento, numero_documento, nombres, apellidos,
                   telefono, correo, direccion
            FROM clientes
            """;

    public long insertar(Cliente cliente) throws SQLException {
        String sql = """
                INSERT INTO clientes
                    (tipo_documento, numero_documento, nombres, apellidos,
                     telefono, correo, direccion)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            asignarParametros(statement, cliente);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        }
        return -1;
    }

    public List<Cliente> consultarTodos() throws SQLException {
        String sql = CONSULTA_BASE + " ORDER BY id DESC";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                clientes.add(mapearCliente(resultSet));
            }
        }
        return clientes;
    }

    public List<Cliente> buscarPorDocumento(String numeroDocumento) throws SQLException {
        String sql = CONSULTA_BASE + " WHERE numero_documento = ? ORDER BY id DESC";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, numeroDocumento);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    clientes.add(mapearCliente(resultSet));
                }
            }
        }
        return clientes;
    }

    public boolean actualizar(long id, Cliente cliente) throws SQLException {
        String sql = """
                UPDATE clientes
                SET tipo_documento = ?, numero_documento = ?, nombres = ?,
                    apellidos = ?, telefono = ?, correo = ?, direccion = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            asignarParametros(statement, cliente);
            statement.setLong(8, id);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean eliminar(long id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private void asignarParametros(PreparedStatement statement, Cliente cliente)
            throws SQLException {
        statement.setString(1, cliente.getTipoDocumento());
        statement.setString(2, cliente.getNumeroDocumento());
        statement.setString(3, cliente.getNombres());
        statement.setString(4, cliente.getApellidos());
        statement.setString(5, cliente.getTelefono());
        statement.setString(6, cliente.getCorreo());
        statement.setString(7, cliente.getDireccion());
    }

    private Cliente mapearCliente(ResultSet resultSet) throws SQLException {
        return new Cliente(
                resultSet.getLong("id"),
                resultSet.getString("tipo_documento"),
                resultSet.getString("numero_documento"),
                resultSet.getString("nombres"),
                resultSet.getString("apellidos"),
                resultSet.getString("telefono"),
                resultSet.getString("correo"),
                resultSet.getString("direccion")
        );
    }
}
