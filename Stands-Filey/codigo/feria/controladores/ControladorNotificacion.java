package feria.controladores;

import feria.modelo.Notificacion;
import feria.servicios.ServicioNotificacion;
import java.util.List;
import java.util.Optional;

public class ControladorNotificacion {
    private final ServicioNotificacion servicio;
    public ControladorNotificacion(ServicioNotificacion servicio) { this.servicio = servicio; }
    public List<Notificacion> listarDeUsuario(int usuarioId) { return servicio.listarDeUsuario(usuarioId); }
    public Optional<Notificacion> consultar(int id) { return servicio.buscarPorId(id); }
}
