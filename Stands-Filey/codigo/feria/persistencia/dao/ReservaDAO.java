package feria.persistencia.dao;

import feria.modelo.*;
import java.sql.*;
import java.util.*;

public class ReservaDAO extends DAOBase {
    public List<Reserva> listarTodos() {
        String sql = "SELECT id, usuario_id, stand_id, fecha_reserva, fecha_limite, monto_pagado, estado FROM reservas";
        List<Reserva> lista = new ArrayList<>();
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) { throw errorLectura(e); }
    }

    public Optional<Reserva> buscarPorId(int id) {
        String sql = "SELECT id, usuario_id, stand_id, fecha_reserva, fecha_limite, monto_pagado, estado FROM reservas WHERE id = ?";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw errorLectura(e); }
    }

    private Reserva mapear(ResultSet rs) throws SQLException {
        Reserva x = new Reserva();
        x.id = rs.getInt("id");
        x.usuarioId = rs.getInt("usuario_id");
        x.standId = rs.getString("stand_id");
        java.sql.Date fecha = rs.getDate("fecha_reserva");
        java.sql.Date limite = rs.getDate("fecha_limite");
        x.fechaReserva = fecha == null ? null : fecha.toLocalDate();
        x.fechaLimite = limite == null ? null : limite.toLocalDate();
        x.montoPagado = rs.getDouble("monto_pagado");
        x.estado = EstadoReserva.valueOf(rs.getString("estado"));
        return x;
    }
}
