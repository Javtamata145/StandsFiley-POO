package feria.persistencia.dao;

import feria.modelo.Notificacion;
import feria.persistencia.ConsultasSQL.Notificaciones;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de la tabla notificaciones. CRUD completo. */
public class NotificacionDAO extends DAOBase {

    // ---------------- READ ----------------
    public List<Notificacion> listarTodas() {
        return consultarLista(Notificaciones.LISTAR_TODAS, SIN_PARAMETROS, this::mapear);
    }

    public List<Notificacion> listarPorUsuario(int usuarioId) {
        return consultarLista(Notificaciones.LISTAR_POR_USUARIO, ps -> ps.setInt(1, usuarioId), this::mapear);
    }

    public List<Notificacion> listarNoLeidas(int usuarioId) {
        return consultarLista(Notificaciones.LISTAR_NO_LEIDAS, ps -> ps.setInt(1, usuarioId), this::mapear);
    }

    public Optional<Notificacion> buscarPorId(int id) {
        return consultarUno(Notificaciones.BUSCAR_POR_ID, ps -> ps.setInt(1, id), this::mapear);
    }

    // ---------------- CREATE ----------------
    public Notificacion insertar(Notificacion n) {
        n.id = insertarYObtenerId(Notificaciones.INSERTAR, ps -> {
            ps.setInt(1, n.usuarioId);
            ps.setString(2, n.tipo);
            ps.setString(3, n.asunto);
            ps.setString(4, n.mensaje);
            setFechaHora(ps, 5, n.fecha);
            ps.setBoolean(6, n.leida);
        });
        return n;
    }

    // ---------------- UPDATE ----------------
    public boolean actualizar(Notificacion n) {
        return ejecutar(Notificaciones.ACTUALIZAR, ps -> {
            ps.setInt(1, n.usuarioId);
            ps.setString(2, n.tipo);
            ps.setString(3, n.asunto);
            ps.setString(4, n.mensaje);
            setFechaHora(ps, 5, n.fecha);
            ps.setBoolean(6, n.leida);
            ps.setInt(7, n.id);
        }) > 0;
    }

    public boolean marcarLeida(int id) {
        return ejecutar(Notificaciones.MARCAR_LEIDA, ps -> ps.setInt(1, id)) > 0;
    }

    // ---------------- DELETE ----------------
    public boolean eliminar(int id) {
        return ejecutar(Notificaciones.ELIMINAR, ps -> ps.setInt(1, id)) > 0;
    }

    private Notificacion mapear(ResultSet rs) throws SQLException {
        Notificacion x = new Notificacion();
        x.id = rs.getInt("id");
        x.usuarioId = rs.getInt("usuario_id");
        x.tipo = rs.getString("tipo");
        x.asunto = rs.getString("asunto");
        x.mensaje = rs.getString("mensaje");
        x.fecha = getFechaHora(rs, "fecha");
        x.leida = rs.getBoolean("leida");
        return x;
    }
}
