package com.autogestpro;

import com.autogestpro.config.DatabaseConnection;
import com.autogestpro.ui.VentanaClientes;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.SQLException;

/** Punto de entrada del módulo de clientes de AutoGest Pro. */
public class Main {
    public static void main(String[] args) {
        try (Connection ignored = DatabaseConnection.getConnection()) {
            // La conexión se abre y cierra para comprobar la configuración antes de abrir la ventana.
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar con la base de datos de AutoGest Pro.\n\n"
                            + "1. Inicie MySQL/MariaDB.\n"
                            + "2. Ejecute database/autogest_pro.sql.\n"
                            + "3. Revise src/main/resources/db.properties o las variables de entorno.\n\n"
                            + "Detalle: " + exception.getMessage(),
                    "Error de conexión JDBC", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> new VentanaClientes().setVisible(true));
    }
}
