package com.autogestpro.dao;

import com.autogestpro.model.Cliente;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteDAOTest {

    private static final String DOCUMENTO_PRUEBA = "9990000001";
    private final ClienteDAO dao = new ClienteDAO();

    private Cliente nuevoClientePrueba() {
        return new Cliente(0, "Cédula de ciudadanía", DOCUMENTO_PRUEBA,
                "Cliente", "De Prueba", "3000000000",
                "prueba@correo.com", "Calle de prueba");
    }

    private void borrarClientesDePrueba() throws SQLException {
        for (Cliente c : dao.buscarPorDocumento(DOCUMENTO_PRUEBA)) {
            dao.eliminar(c.getId());
        }
    }

    @BeforeEach
    void limpiarAntes() throws SQLException {
        borrarClientesDePrueba();
    }

    @AfterEach
    void limpiarDespues() throws SQLException {
        borrarClientesDePrueba();
    }

    @Test
    void insertaYBuscaUnClientePorDocumento() throws SQLException {
        long id = dao.insertar(nuevoClientePrueba());
        assertTrue(id > 0);
        List<Cliente> encontrados = dao.buscarPorDocumento(DOCUMENTO_PRUEBA);
        assertEquals(1, encontrados.size());
        assertEquals("Cliente", encontrados.get(0).getNombres());
    }

    @Test
    void buscarUnDocumentoQueNoExisteDevuelveListaVacia() throws SQLException {
        assertTrue(dao.buscarPorDocumento("9990000099").isEmpty());
    }

    @Test
    void unaComillaEnElBuscadorNoRompeLaConsulta() throws SQLException {
        assertTrue(dao.buscarPorDocumento("' OR '1'='1").isEmpty());
    }

    @Test
    void actualizaElTelefonoDeUnCliente() throws SQLException {
        long id = dao.insertar(nuevoClientePrueba());
        Cliente modificado = nuevoClientePrueba();
        modificado.setTelefono("3111111111");
        assertTrue(dao.actualizar(id, modificado));
        assertEquals("3111111111",
                dao.buscarPorDocumento(DOCUMENTO_PRUEBA).get(0).getTelefono());
    }

    @Test
    void eliminaUnCliente() throws SQLException {
        long id = dao.insertar(nuevoClientePrueba());
        assertTrue(dao.eliminar(id));
        assertTrue(dao.buscarPorDocumento(DOCUMENTO_PRUEBA).isEmpty());
    }

    @Test
    void noPermiteDosClientesConElMismoDocumento() throws SQLException {
        dao.insertar(nuevoClientePrueba());
        assertThrows(SQLException.class, () -> dao.insertar(nuevoClientePrueba()));
    }
}