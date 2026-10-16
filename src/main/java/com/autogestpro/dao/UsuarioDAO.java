
package com.autogestpro.dao;

import com.autogestpro.config.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public boolean autenticar(String nombreUsuario, char[] clave)
            throws SQLException {

        if (nombreUsuario == null || nombreUsuario.isBlank()
                || clave == null || clave.length == 0) {
            return false;
        }

        String sql = """
                SELECT clave_hash
                FROM usuarios
                WHERE nombre_usuario = ?
                  AND activo = 1
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, nombreUsuario.trim());

            try (ResultSet resultado = consulta.executeQuery()) {
                if (!resultado.next()) {
                    return false;
                }

                String hashAlmacenado = resultado.getString("clave_hash");

                if (hashAlmacenado == null || hashAlmacenado.isBlank()) {
                    return false;
                }

                try {
                    return BCrypt.checkpw(
                            new String(clave), hashAlmacenado);
                } catch (IllegalArgumentException excepcion) {
                    return false;
                }
            }
        }
    }

    public boolean existeUsuarioActivo(String nombreUsuario)
            throws SQLException {

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return false;
        }

        String sql = """
                SELECT 1
                FROM usuarios
                WHERE nombre_usuario = ?
                  AND activo = 1
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, nombreUsuario.trim());

            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }
        }
    }
}
