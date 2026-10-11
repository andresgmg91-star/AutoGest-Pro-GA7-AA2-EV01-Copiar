# AutoGest Pro — Módulo de gestión de clientes

**Evidencia:** GA7-220501096-AA2-EV01 — Codificación de módulos del software  
**Aprendiz:** Andrés Mantilla  
**Tecnologías:** Java 17, JDBC, MariaDB/MySQL, Maven y Git.

## 1. ¿Qué contiene?

Este proyecto implementa el módulo de clientes de AutoGest Pro. Permite:

- **Insertar:** registrar nuevos clientes.
- **Consultar:** listar los clientes guardados.
- **Buscar:** localizar un cliente por su número de documento.
- **Actualizar:** modificar el registro seleccionado.
- **Eliminar:** borrar un registro, previa confirmación.

Se utiliza una interfaz gráfica sencilla de escritorio con Swing. Las consultas SQL se encuentran en `ClienteDAO`, los datos se representan mediante la clase `Cliente` y la conexión se administra en `DatabaseConnection`.

## 2. Requisitos

- JDK 17 o superior.
- Maven 3.8 o superior.
- Un servidor MariaDB o MySQL en funcionamiento.
- Git, para el control de versiones.

La dependencia JDBC se descarga desde Maven al construir el proyecto. El archivo `pom.xml` declara MariaDB Connector/J 3.5.10, que utiliza la API JDBC y permite conectarse a servidores MariaDB y MySQL.

## 3. Preparar la base de datos

1. Inicie MySQL/MariaDB. Si usa XAMPP, abra el panel de control e inicie el servicio de base de datos.
2. Abra phpMyAdmin o su administrador de base de datos.
3. Importe el archivo `database/autogest_pro.sql`.
4. Confirme que se crearon la base `autogest_pro` y la tabla `clientes`.

El script utiliza `CREATE DATABASE IF NOT EXISTS` y `CREATE TABLE IF NOT EXISTS`; no elimina las tablas existentes.

## 4. Configurar la conexión

La configuración predeterminada es para un entorno **local de práctica**:

- URL: `jdbc:mariadb://localhost:3306/autogest_pro`
- Usuario: `root`
- Contraseña: vacía

Esto puede coincidir con una instalación local de XAMPP sin contraseña, pero no es una configuración universal. Si su instalación utiliza otros datos, copie `src/main/resources/db.properties.example` como `src/main/resources/db.properties` y edite la URL, el usuario y la contraseña. El archivo real `db.properties` está excluido de Git para no publicar credenciales.

También se pueden definir estas variables de entorno, que tienen prioridad sobre el archivo:

- `AUTOGEST_DB_URL`
- `AUTOGEST_DB_USER`
- `AUTOGEST_DB_PASSWORD`

No use la cuenta `root` ni una contraseña vacía en un servidor productivo.

## 5. Ejecutar desde la terminal

Abra una terminal en la carpeta que contiene `pom.xml` y ejecute:

```bash
mvn clean compile
mvn exec:java
```

La primera ejecución necesita conexión a internet para que Maven descargue la dependencia JDBC. También se puede importar la carpeta como **proyecto Maven** en Visual Studio Code o en un IDE compatible con Java.

## 6. Probar las operaciones

1. Pulse **Consultar todos** para ver los registros.
2. Registre un cliente con tipo y número de documento, nombres y apellidos. Pulse **Guardar cliente**.
3. Escriba un número de documento y pulse **Buscar por documento**.
4. Seleccione una fila de la tabla, cambie un dato y pulse **Actualizar seleccionado**.
5. Seleccione una fila y pulse **Eliminar seleccionado**. Confirme la eliminación.
6. Vuelva a consultar para verificar el resultado.

La base de datos impide duplicar la combinación de tipo y número de documento. Los valores enviados a SQL se parametrizan con `PreparedStatement`.

## 7. Estructura del proyecto

```text
ANDRESMANTILLA_AA2_EV01/
├── database/
│   └── autogest_pro.sql
├── docs/
│   ├── DOCUMENTACION_MODULO.md
│   └── GUIA_EVIDENCIAS.md
├── src/main/java/com/autogestpro/
│   ├── Main.java
│   ├── config/DatabaseConnection.java
│   ├── dao/ClienteDAO.java
│   ├── model/Cliente.java
│   └── ui/VentanaClientes.java
├── src/main/resources/db.properties.example
├── .gitignore
├── pom.xml
├── ENLACE_REPOSITORIO.txt
└── README.md
```

## 8. Control de versiones con Git

Si la carpeta ya tiene un repositorio Git, abra la terminal en esta carpeta y revise `git status`. Para crear el repositorio local si todavía no existe:

```bash
git init
git add .
git commit -m "Primera versión del módulo de clientes de AutoGest Pro"
```

Después cree un repositorio vacío en su cuenta de GitHub. Copie su URL y ejecute los comandos sustituyendo la dirección de ejemplo:

```bash
git branch -M main
git remote add origin https://github.com/USUARIO/AutoGest-Pro-GA7-AA2-EV01.git
git push -u origin main
```

Si `origin` ya existe, no repita `git remote add origin`; use `git remote set-url origin URL_DEL_REPOSITORIO` si debe corregirlo. Al publicar el proyecto, copie la URL real en `ENLACE_REPOSITORIO.txt` y vuelva a incluir ese archivo en la entrega ZIP.

## 9. Alcance y compatibilidad

Este paquete desarrolla un módulo Java con JDBC porque la consigna de esta evidencia menciona expresamente JDBC. Si la versión principal de AutoGest Pro se mantiene en PHP, esta implementación debe presentarse como el módulo Java solicitado para la evidencia, no como código PHP que pueda ejecutar JDBC directamente.
