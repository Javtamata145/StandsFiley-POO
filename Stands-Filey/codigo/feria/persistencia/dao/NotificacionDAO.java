package feria.persistencia.dao;

import feria.modelo.Notificacion;
import java.sql.*;
import java.util.*;

public class NotificacionDAO extends DAOBase {
    public List<Notificacion> listarPorUsuario(int usuarioId) {
        String sql = "SELECT id, usuario_id, tipo, asunto, mensaje, fecha, leida FROM notificaciones WHERE usuario_id = ?";
        List<Notificacion> lista = new ArrayList<>();
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notificacion x = new Notificacion();
                    x.id = rs.getInt("id");
                    x.usuarioId = rs.getInt("usuario_id");
                    x.tipo = rs.getString("tipo");
                    x.asunto = rs.getString("asunto");
                    x.mensaje = rs.getString("mensaje");
                    Timestamp fecha = rs.getTimestamp("fecha");
                    x.fecha = fecha == null ? null : fecha.toLocalDateTime();
                    x.leida = rs.getBoolean("leida");
                    lista.add(x);
                }
            }
            return lista;
        } catch (SQLException e) { throw errorLectura(e); }
    }

    public Optional<Notificacion> buscarPorId(int id) {
        String sql = "SELECT id, usuario_id, tipo, asunto, mensaje, fecha, leida FROM notificaciones WHERE id = ?";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Notificacion x = new Notificacion();
                x.id = rs.getInt("id");
                x.usuarioId = rs.getInt("usuario_id");
                x.tipo = rs.getString("tipo");
                x.asunto = rs.getString("asunto");
                x.mensaje = rs.getString("mensaje");
                Timestamp fecha = rs.getTimestamp("fecha");
                x.fecha = fecha == null ? null : fecha.toLocalDateTime();
                x.leida = rs.getBoolean("leida");
                return Optional.of(x);
            }
        } catch (SQLException e) { throw errorLectura(e); }
    }
}
