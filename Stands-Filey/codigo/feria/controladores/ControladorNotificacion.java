package feria.controladores;

import feria.modelo.Notificacion;
import feria.servicios.ServicioNotificacion;
import java.util.List;
import java.util.Optional;

public class ControladorNotificacion {
    private final ServicioNotificacion servicio;
    public ControladorNotificacion(ServicioNotificacion servicio) { this.servicio = servicio; }

    // READ
    public List<Notificacion> listar() { return servicio.listarTodas(); }
    public List<Notificacion> listarDeUsuario(int usuarioId) { return servicio.listarDeUsuario(usuarioId); }
    public List<Notificacion> listarNoLeidas(int usuarioId) { return servicio.listarNoLeidas(usuarioId); }
    public Optional<Notificacion> consultar(int id) { return servicio.buscarPorId(id); }

    // CREATE
    public Notificacion crear(int usuarioId, String tipo, String asunto, String mensaje) {
        return servicio.crear(usuarioId, tipo, asunto, mensaje);
    }

    // UPDATE
    public Notificacion actualizar(Notificacion notificacion) { return servicio.actualizar(notificacion); }
    public boolean marcarLeida(int id) { return servicio.marcarLeida(id); }

    // DELETE
    public boolean eliminar(int id) { return servicio.eliminar(id); }
}
