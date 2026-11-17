package feria.persistencia.dao;

import feria.modelo.*;
import java.sql.*;
import java.util.*;

public class StandDAO extends DAOBase {
    public List<Stand> listarTodos() {
        String sql = "SELECT id, largo, ancho, precio, estado, transferencia_id FROM stands";
        List<Stand> lista = new ArrayList<>();
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Stand x = new Stand();
                x.id = rs.getString("id");
                x.largo = rs.getDouble("largo");
                x.ancho = rs.getDouble("ancho");
                x.precio = rs.getDouble("precio");
                x.estado = EstadoStand.valueOf(rs.getString("estado"));
                int transferencia = rs.getInt("transferencia_id");
                x.transferenciaId = rs.wasNull() ? null : transferencia;
                lista.add(x);
            }
            return lista;
        } catch (SQLException e) { throw errorLectura(e); }
    }

    public Optional<Stand> buscarPorId(String id) {
        String sql = "SELECT id, largo, ancho, precio, estado, transferencia_id FROM stands WHERE id = ?";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Stand x = new Stand();
                x.id = rs.getString("id");
                x.largo = rs.getDouble("largo");
                x.ancho = rs.getDouble("ancho");
                x.precio = rs.getDouble("precio");
                x.estado = EstadoStand.valueOf(rs.getString("estado"));
                int transferencia = rs.getInt("transferencia_id");
                x.transferenciaId = rs.wasNull() ? null : transferencia;
                return Optional.of(x);
            }
        } catch (SQLException e) { throw errorLectura(e); }
    }
}
