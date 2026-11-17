package feria.persistencia.dao;

import feria.modelo.EstadoTransferencia;
import feria.modelo.TransferenciaBloqueados;
import java.sql.*;
import java.util.*;

public class TransferenciaDAO extends DAOBase {
    public List<TransferenciaBloqueados> listarTodas() {
        String sql = "SELECT id, codigo_acceso, estado, fecha_acceso, usuario_id, reserva_id, stand_id FROM transferencias";
        List<TransferenciaBloqueados> lista = new ArrayList<>();
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) { throw errorLectura(e); }
    }

    public Optional<TransferenciaBloqueados> buscarPorId(int id) {
        String sql = "SELECT id, codigo_acceso, estado, fecha_acceso, usuario_id, reserva_id, stand_id FROM transferencias WHERE id = ?";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw errorLectura(e); }
    }

    private TransferenciaBloqueados mapear(ResultSet rs) throws SQLException {
        TransferenciaBloqueados x = new TransferenciaBloqueados();
        x.id = rs.getInt("id");
        x.codigoAcceso = rs.getString("codigo_acceso");
        x.estado = EstadoTransferencia.valueOf(rs.getString("estado"));
        Timestamp fecha = rs.getTimestamp("fecha_acceso");
        x.fechaAcceso = fecha == null ? null : fecha.toLocalDateTime();
        x.usuarioId = rs.getInt("usuario_id");
        int reserva = rs.getInt("reserva_id");
        x.reservaId = rs.wasNull() ? null : reserva;
        x.standId = rs.getString("stand_id");
        return x;
    }
}
