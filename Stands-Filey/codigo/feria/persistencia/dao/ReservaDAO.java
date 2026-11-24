package feria.persistencia.dao;

import feria.modelo.EstadoReserva;
import feria.modelo.Reserva;
import feria.persistencia.ConsultasSQL.Reservas;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de la tabla reservas. CRUD completo. */
public class ReservaDAO extends DAOBase {

    // ---------------- READ ----------------
    public List<Reserva> listarTodos() {
        return consultarLista(Reservas.LISTAR_TODOS, SIN_PARAMETROS, this::mapear);
    }

    public List<Reserva> listarPorUsuario(int usuarioId) {
        return consultarLista(Reservas.LISTAR_POR_USUARIO, ps -> ps.setInt(1, usuarioId), this::mapear);
    }

    public List<Reserva> listarPorStand(String standId) {
        return consultarLista(Reservas.LISTAR_POR_STAND, ps -> ps.setString(1, standId), this::mapear);
    }

    /** Reservas ACTIVAS con fecha límite anterior a la fecha indicada. */
    public List<Reserva> listarActivasVencidas(LocalDate hoy) {
        return consultarLista(Reservas.LISTAR_ACTIVAS_VENCIDAS, ps -> setFecha(ps, 1, hoy), this::mapear);
    }

    public Optional<Reserva> buscarPorId(int id) {
        return consultarUno(Reservas.BUSCAR_POR_ID, ps -> ps.setInt(1, id), this::mapear);
    }

    // ---------------- CREATE ----------------
    public Reserva insertar(Reserva r) {
        r.id = insertarYObtenerId(Reservas.INSERTAR, ps -> {
            ps.setInt(1, r.usuarioId);
            ps.setString(2, r.standId);
            setFecha(ps, 3, r.fechaReserva);
            setFecha(ps, 4, r.fechaLimite);
            ps.setDouble(5, r.montoPagado);
            ps.setString(6, r.estado.name());
        });
        return r;
    }

    // ---------------- UPDATE ----------------
    public boolean actualizar(Reserva r) {
        return ejecutar(Reservas.ACTUALIZAR, ps -> {
            ps.setInt(1, r.usuarioId);
            ps.setString(2, r.standId);
            setFecha(ps, 3, r.fechaReserva);
            setFecha(ps, 4, r.fechaLimite);
            ps.setDouble(5, r.montoPagado);
            ps.setString(6, r.estado.name());
            ps.setInt(7, r.id);
        }) > 0;
    }

    public boolean actualizarMonto(int id, double montoPagado) {
        return ejecutar(Reservas.ACTUALIZAR_MONTO, ps -> {
            ps.setDouble(1, montoPagado);
            ps.setInt(2, id);
        }) > 0;
    }

    public boolean cambiarEstado(int id, EstadoReserva nuevo) {
        return ejecutar(Reservas.CAMBIAR_ESTADO, ps -> {
            ps.setString(1, nuevo.name());
            ps.setInt(2, id);
        }) > 0;
    }

    // ---------------- DELETE ----------------
    /** Al borrar una reserva, la BD borra sus pagos (ON DELETE CASCADE). */
    public boolean eliminar(int id) {
        return ejecutar(Reservas.ELIMINAR, ps -> ps.setInt(1, id)) > 0;
    }

    private Reserva mapear(ResultSet rs) throws SQLException {
        Reserva x = new Reserva();
        x.id = rs.getInt("id");
        x.usuarioId = rs.getInt("usuario_id");
        x.standId = rs.getString("stand_id");
        x.fechaReserva = getFecha(rs, "fecha_reserva");
        x.fechaLimite = getFecha(rs, "fecha_limite");
        x.montoPagado = rs.getDouble("monto_pagado");
        x.estado = EstadoReserva.valueOf(rs.getString("estado"));
        return x;
    }
}
