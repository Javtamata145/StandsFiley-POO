package feria.servicios;

import feria.modelo.Notificacion;
import feria.persistencia.dao.NotificacionDAO;
import java.util.List;
import java.util.Optional;

public class ServicioNotificacion {
    private final NotificacionDAO dao;
    public ServicioNotificacion(NotificacionDAO dao) { this.dao = dao; }
    public List<Notificacion> listarDeUsuario(int usuarioId) { return dao.listarPorUsuario(usuarioId); }
    public Optional<Notificacion> buscarPorId(int id) { return dao.buscarPorId(id); }
}
