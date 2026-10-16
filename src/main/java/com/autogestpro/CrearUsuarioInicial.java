
package com.autogestpro;

import com.autogestpro.config.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

public class CrearUsuarioInicial {

    public static void main(String[] args) {
        Console consola = System.console();
        Scanner teclado = consola == null ? new Scanner(System.in) : null;

        try (Connection conexion = DatabaseConnection.getConnection()) {

            try (PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT COUNT(*) FROM usuarios");
                 ResultSet resultado = consulta.executeQuery()) {

                resultado.next();

                if (resultado.getLong(1) > 0) {
                    System.out.println(
                            "Ya existen usuarios. No se creará otro usuario inicial.");
                    return;
                }
            }

            System.out.print("Nombre de usuario: ");
            String nombre = leerTexto(consola, teclado).trim();

            if (nombre.isBlank() || nombre.length() > 50) {
                System.out.println(
                        "El nombre debe tener entre 1 y 50 caracteres.");
                return;
            }

            char[] clave = leerClave(
                    consola, teclado, "Contraseña (mínimo 10 caracteres): ");

            char[] confirmacion = leerClave(
                    consola, teclado, "Confirma la contraseña: ");

            try {
                if (clave.length < 10) {
                    System.out.println(
                            "La contraseña debe tener al menos 10 caracteres.");
                    return;
                }

                if (!Arrays.equals(clave, confirmacion)) {
                    System.out.println("Las contraseñas no coinciden.");
                    return;
                }

                String hash = BCrypt.hashpw(
                        new String(clave), BCrypt.gensalt(12));

                String sql = """
                        INSERT INTO usuarios
                            (nombre_usuario, clave_hash, activo)
                        VALUES (?, ?, 1)
                        """;

                try (PreparedStatement insercion =
                             conexion.prepareStatement(sql)) {
                    insercion.setString(1, nombre);
                    insercion.setString(2, hash);
                    insercion.executeUpdate();
                }

                System.out.println(
                        "Usuario inicial creado correctamente.");
                System.out.println(
                        "Guarda el nombre de usuario y la contraseña.");

            } finally {
                Arrays.fill(clave, '\0');
                Arrays.fill(confirmacion, '\0');
            }

        } catch (SQLException excepcion) {
            System.out.println("No se pudo crear el usuario inicial.");
            System.out.println("Detalle: " + excepcion.getMessage());
        } finally {
            if (teclado != null) {
                teclado.close();
            }
        }
    }

    private static String leerTexto(
            Console consola, Scanner teclado) {
        return consola != null
                ? consola.readLine()
                : teclado.nextLine();
    }

    private static char[] leerClave(
            Console consola, Scanner teclado, String mensaje) {
        if (consola != null) {
            char[] clave = consola.readPassword("%s", mensaje);
            return clave == null ? new char[0] : clave;
        }

        System.out.println(
                "Aviso: la contraseña será visible al escribirla.");
        System.out.print(mensaje);
        return teclado.nextLine().toCharArray();
    }
}
