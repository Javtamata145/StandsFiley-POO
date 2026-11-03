package feria.controladores;

import feria.modelo.*;
import feria.servicios.ServicioNotificaciones;
import java.util.List;

public class ControladorNotificaciones {
    private ServicioNotificaciones servicio;
    public ControladorNotificaciones(ServicioNotificaciones servicio) { this.servicio = servicio; }
    public List<Notificacion> verBandeja(Usuario usuario) { return servicio.listarDeUsuario(usuario); }
    public void marcarLeida(Usuario usuario, int id) {
        for (Notificacion n : verBandeja(usuario))
            if (n.getId() == id) n.marcarLeida();
    }
}
