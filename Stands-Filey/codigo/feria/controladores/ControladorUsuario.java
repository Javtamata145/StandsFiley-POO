package feria.controladores;

import feria.modelo.Usuario;
import feria.servicios.ServicioUsuario;
import java.util.List;
import java.util.Optional;

public class ControladorUsuario {
    private final ServicioUsuario servicio;
    public ControladorUsuario(ServicioUsuario servicio) { this.servicio = servicio; }
    public List<Usuario> listar() { return servicio.listarUsuarios(); }
    public Optional<Usuario> consultar(int id) { return servicio.buscarPorId(id); }
    public Optional<Usuario> consultarPorCorreo(String correo) { return servicio.buscarPorCorreo(correo); }
}
