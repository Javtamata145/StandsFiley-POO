package feria.controladores;

import feria.modelo.Usuario;
import feria.servicios.ServicioUsuario;
import java.util.List;
import java.util.Optional;

public class ControladorUsuario {
    private final ServicioUsuario servicio;
    public ControladorUsuario(ServicioUsuario servicio) { this.servicio = servicio; }

    // READ
    public List<Usuario> listar() { return servicio.listarUsuarios(); }
    public Optional<Usuario> consultar(int id) { return servicio.buscarPorId(id); }
    public Optional<Usuario> consultarPorCorreo(String correo) { return servicio.buscarPorCorreo(correo); }

    // CREATE
    public Usuario registrar(String nombre, String correo, String contrasena, String telefono) {
        return servicio.registrar(nombre, correo, contrasena, telefono);
    }
    public Optional<Usuario> iniciarSesion(String correo, String contrasena) {
        return servicio.iniciarSesion(correo, contrasena);
    }

    // UPDATE
    public Usuario actualizar(Usuario usuario) { return servicio.actualizar(usuario); }
    public void cambiarContrasena(int id, String actual, String nueva) { servicio.cambiarContrasena(id, actual, nueva); }

    // DELETE
    public boolean eliminar(int id) { return servicio.eliminar(id); }
}
