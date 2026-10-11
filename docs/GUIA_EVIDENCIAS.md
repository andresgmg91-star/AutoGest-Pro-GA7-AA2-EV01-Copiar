# Guía para tomar las capturas de la evidencia

Las capturas deben realizarse después de ejecutar el proyecto en su equipo. No se incluyen capturas simuladas.

1. **Estructura del proyecto:** mostrar el proyecto abierto en Visual Studio Code o en otro IDE, con paquetes, clases, `pom.xml` y el SQL.
2. **Conexión JDBC:** mostrar `DatabaseConnection.java` y la configuración local (oculte cualquier contraseña si configuró una).
3. **Operaciones SQL:** mostrar `ClienteDAO.java`, donde aparecen INSERT, SELECT, UPDATE y DELETE con `PreparedStatement`.
4. **Inserción y consulta:** abrir la aplicación, registrar un cliente de prueba y mostrarlo en la tabla.
5. **Actualización:** seleccionar el cliente, cambiar un dato, guardar los cambios y mostrar el resultado.
6. **Eliminación:** seleccionar un registro de prueba, confirmar la eliminación y verificar que ya no aparece.
7. **Versionamiento:** mostrar el repositorio de GitHub con los archivos publicados y al menos un commit.

Use datos ficticios en las pruebas y evite mostrar documentos o datos personales reales.
