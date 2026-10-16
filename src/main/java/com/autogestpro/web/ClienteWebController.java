
package com.autogestpro.web;

import com.autogestpro.dao.ClienteDAO;
import com.autogestpro.model.Cliente;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

@Controller
public class ClienteWebController {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @GetMapping("/clientes")
    public String mostrarClientes(HttpSession sesion, Model modelo) {
        if (sesion.getAttribute("usuario") == null) {
            return "redirect:/login.html";
        }

        try {
            modelo.addAttribute("clientes", clienteDAO.consultarTodos());
            modelo.addAttribute("usuario", sesion.getAttribute("usuario"));
            return "clientes";
        } catch (SQLException e) {
            modelo.addAttribute(
                    "error",
                    "No fue posible consultar los clientes. Verifica la base de datos."
            );
            return "clientes";
        }
    }

    @PostMapping("/clientes/guardar")
    public String guardarCliente(
            @RequestParam String tipoDocumento,
            @RequestParam String numeroDocumento,
            @RequestParam String nombres,
            @RequestParam String apellidos,
            @RequestParam(required = false, defaultValue = "") String telefono,
            @RequestParam(required = false, defaultValue = "") String correo,
            @RequestParam(required = false, defaultValue = "") String direccion,
            HttpSession sesion,
            RedirectAttributes atributos) {

        if (sesion.getAttribute("usuario") == null) {
            return "redirect:/login.html";
        }

        try {
            Cliente cliente = new Cliente(
                    0,
                    tipoDocumento,
                    numeroDocumento.trim(),
                    nombres.trim(),
                    apellidos.trim(),
                    telefono.trim(),
                    correo.trim(),
                    direccion.trim()
            );

            clienteDAO.insertar(cliente);
            atributos.addFlashAttribute(
                    "mensaje",
                    "Cliente registrado correctamente."
            );

        } catch (SQLException e) {
            atributos.addFlashAttribute(
                    "error",
                    "No fue posible registrar el cliente. Revisa los datos e inténtalo nuevamente."
            );
        }

        return "redirect:/clientes";
    }
}
