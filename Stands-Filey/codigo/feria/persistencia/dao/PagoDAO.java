package feria.persistencia.dao;

import feria.modelo.Pago;
import feria.persistencia.ConsultasSQL.Pagos;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de la tabla pagos. CRUD completo. */
public class PagoDAO extends DAOBase {

    // ---------------- READ ----------------
    public List<Pago> listarTodos() {
        return consultarLista(Pagos.LISTAR_TODOS, SIN_PARAMETROS, this::mapear);
    }

    public List<Pago> listarPorReserva(int reservaId) {
        return consultarLista(Pagos.LISTAR_POR_RESERVA, ps -> ps.setInt(1, reservaId), this::mapear);
    }

    public Optional<Pago> buscarPorId(int id) {
        return consultarUno(Pagos.BUSCAR_POR_ID, ps -> ps.setInt(1, id), this::mapear);
    }

    /** Pagos realizados en el mes indicado (por ejemplo YearMonth.of(2026, 11)). */
    public List<Pago> listarPorMes(YearMonth mes) {
        return consultarLista(Pagos.LISTAR_POR_MES, ps -> {
            setFechaHora(ps, 1, mes.atDay(1).atStartOfDay());
            setFechaHora(ps, 2, mes.plusMonths(1).atDay(1).atStartOfDay());
        }, this::mapear);
    }

    /** Todos los pagos de las reservaciones de una editorial (usuario). */
    public List<Pago> listarPorEditorial(int usuarioId) {
        return consultarLista(Pagos.LISTAR_POR_EDITORIAL, ps -> ps.setInt(1, usuarioId), this::mapear);
    }

    /** Pagos de una reservación específica que pertenece a una editorial específica. */
    public List<Pago> listarPorEditorialYReserva(int usuarioId, int reservaId) {
        return consultarLista(Pagos.LISTAR_POR_EDITORIAL_Y_RESERVA, ps -> {
            ps.setInt(1, usuarioId);
            ps.setInt(2, reservaId);
        }, this::mapear);
    }

    /** Suma de los pagos confirmados de una reserva. */
    public double sumarConfirmados(int reservaId) {
        return consultarUno(Pagos.SUMAR_CONFIRMADOS, ps -> ps.setInt(1, reservaId),
                rs -> rs.getDouble("total")).orElse(0.0);
    }

    // ---------------- CREATE ----------------
    public Pago insertar(Pago p) {
        p.id = insertarYObtenerId(Pagos.INSERTAR, ps -> {
            ps.setInt(1, p.reservaId);
            ps.setDouble(2, p.monto);
            setFechaHora(ps, 3, p.fecha);
            ps.setString(4, p.tipo);
            ps.setBoolean(5, p.confirmado);
        });
        return p;
    }

    // ---------------- UPDATE ----------------
    public boolean actualizar(Pago p) {
        return ejecutar(Pagos.ACTUALIZAR, ps -> {
            ps.setInt(1, p.reservaId);
            ps.setDouble(2, p.monto);
            setFechaHora(ps, 3, p.fecha);
            ps.setString(4, p.tipo);
            ps.setBoolean(5, p.confirmado);
            ps.setInt(6, p.id);
        }) > 0;
    }

    public boolean confirmar(int id) {
        return ejecutar(Pagos.CONFIRMAR, ps -> ps.setInt(1, id)) > 0;
    }

    // ---------------- DELETE ----------------
    public boolean eliminar(int id) {
        return ejecutar(Pagos.ELIMINAR, ps -> ps.setInt(1, id)) > 0;
    }

    private Pago mapear(ResultSet rs) throws SQLException {
        Pago x = new Pago();
        x.id = rs.getInt("id");
        x.reservaId = rs.getInt("reserva_id");
        x.monto = rs.getDouble("monto");
        x.fecha = getFechaHora(rs, "fecha");
        x.tipo = rs.getString("tipo");
        x.confirmado = rs.getBoolean("confirmado");
        return x;
    }
}
