package feria.servicios;

import feria.modelo.Notificacion;
import feria.persistencia.dao.NotificacionDAO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Lógica de negocio de notificaciones (sección de notificaciones, RF-022 / RF-023) y CRUD. */
public class ServicioNotificacion {
    private final NotificacionDAO dao;
    public ServicioNotificacion(NotificacionDAO dao) { this.dao = dao; }

    // ---------------- READ ----------------
    public List<Notificacion> listarTodas() { return dao.listarTodas(); }
    public List<Notificacion> listarDeUsuario(int usuarioId) { return dao.listarPorUsuario(usuarioId); }
    public List<Notificacion> listarNoLeidas(int usuarioId) { return dao.listarNoLeidas(usuarioId); }
    public Optional<Notificacion> buscarPorId(int id) { return dao.buscarPorId(id); }

    // ---------------- CREATE ----------------
    public Notificacion crear(int usuarioId, String tipo, String asunto, String mensaje) {
        if (tipo == null || tipo.isBlank() || asunto == null || asunto.isBlank()
                || mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException("La notificación debe tener tipo, asunto y mensaje.");
        }
        Notificacion n = new Notificacion();
        n.usuarioId = usuarioId;
        n.tipo = tipo.trim().toUpperCase();
        n.asunto = asunto.trim();
        n.mensaje = mensaje.trim();
        n.fecha = LocalDateTime.now();
        n.leida = false;
        return dao.insertar(n);
    }

    // ---------------- UPDATE ----------------
    public Notificacion actualizar(Notificacion n) {
        dao.buscarPorId(n.id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la notificación " + n.id + "."));
        if (n.fecha == null) n.fecha = LocalDateTime.now();
        dao.actualizar(n);
        return n;
    }

    public boolean marcarLeida(int id) { return dao.marcarLeida(id); }

    // ---------------- DELETE ----------------
    public boolean eliminar(int id) { return dao.eliminar(id); }
}
