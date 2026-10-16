
package com.autogestpro.web;

import com.autogestpro.dao.UsuarioDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginWebController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @GetMapping("/login")
    public String mostrarLogin() {
        return "redirect:/login.html";
    }

    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam("usuario") String usuario,
            @RequestParam("clave") String clave,
            HttpSession sesion) {

        try {
            String nombreUsuario = usuario == null ? "" : usuario.trim();

            if (nombreUsuario.isBlank()
                    || clave == null || clave.isBlank()) {
                return "redirect:/login.html?error=usuario";
            }

            boolean autenticado = usuarioDAO.autenticar(
                    nombreUsuario,
                    clave.toCharArray()
            );

            if (autenticado) {
                sesion.setAttribute("usuario", nombreUsuario);
                return "redirect:/dashboard";
            }

            boolean existeUsuario =
                    usuarioDAO.existeUsuarioActivo(nombreUsuario);

            if (!existeUsuario) {
                return "redirect:/login.html?error=usuario";
            }

            return "redirect:/login.html?error=clave";

        } catch (Exception e) {
            return "redirect:/login.html?error=acceso";
        }
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession sesion) {
        if (sesion.getAttribute("usuario") == null) {
            return "redirect:/login.html";
        }

        return "dashboard";
    }

    @PostMapping("/logout")
    public String cerrarSesion(HttpSession sesion) {
        sesion.invalidate();
        return "redirect:/";
    }
}
