package feria.persistencia.dao;

import feria.modelo.Pago;
import java.sql.*;
import java.util.*;

public class PagoDAO extends DAOBase {
    public List<Pago> listarPorReserva(int reservaId) {
        String sql = "SELECT id, reserva_id, monto, fecha, tipo, confirmado FROM pagos WHERE reserva_id = ?";
        List<Pago> lista = new ArrayList<>();
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, reservaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pago x = new Pago();
                    x.id = rs.getInt("id");
                    x.reservaId = rs.getInt("reserva_id");
                    x.monto = rs.getDouble("monto");
                    Timestamp fecha = rs.getTimestamp("fecha");
                    x.fecha = fecha == null ? null : fecha.toLocalDateTime();
                    x.tipo = rs.getString("tipo");
                    x.confirmado = rs.getBoolean("confirmado");
                    lista.add(x);
                }
            }
            return lista;
        } catch (SQLException e) { throw errorLectura(e); }
    }

    public Optional<Pago> buscarPorId(int id) {
        String sql = "SELECT id, reserva_id, monto, fecha, tipo, confirmado FROM pagos WHERE id = ?";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Pago x = new Pago();
                x.id = rs.getInt("id");
                x.reservaId = rs.getInt("reserva_id");
                x.monto = rs.getDouble("monto");
                Timestamp fecha = rs.getTimestamp("fecha");
                x.fecha = fecha == null ? null : fecha.toLocalDateTime();
                x.tipo = rs.getString("tipo");
                x.confirmado = rs.getBoolean("confirmado");
                return Optional.of(x);
            }
        } catch (SQLException e) { throw errorLectura(e); }
    }
}
