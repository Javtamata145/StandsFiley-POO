package feria.persistencia.dao;

import feria.modelo.EstadoTransferencia;
import feria.modelo.TransferenciaBloqueados;
import feria.persistencia.ConsultasSQL.Transferencias;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de la tabla transferencias. CRUD completo. */
public class TransferenciaDAO extends DAOBase {

    // ---------------- READ ----------------
    public List<TransferenciaBloqueados> listarTodas() {
        return consultarLista(Transferencias.LISTAR_TODAS, SIN_PARAMETROS, this::mapear);
    }

    public Optional<TransferenciaBloqueados> buscarPorId(int id) {
        return consultarUno(Transferencias.BUSCAR_POR_ID, ps -> ps.setInt(1, id), this::mapear);
    }

    public Optional<TransferenciaBloqueados> buscarPorCodigo(String codigo) {
        return consultarUno(Transferencias.BUSCAR_POR_CODIGO, ps -> ps.setString(1, codigo), this::mapear);
    }

    public List<TransferenciaBloqueados> listarPorStand(String standId) {
        return consultarLista(Transferencias.BUSCAR_POR_STAND, ps -> ps.setString(1, standId), this::mapear);
    }

    // ---------------- CREATE ----------------
    public TransferenciaBloqueados insertar(TransferenciaBloqueados t) {
        t.id = insertarYObtenerId(Transferencias.INSERTAR, ps -> {
            ps.setString(1, t.codigoAcceso);
            ps.setString(2, t.estado.name());
            setFechaHora(ps, 3, t.fechaAcceso);
            ps.setInt(4, t.usuarioId);
            setEnteroONulo(ps, 5, t.reservaId);
            ps.setString(6, t.standId);
        });
        return t;
    }

    // ---------------- UPDATE ----------------
    public boolean actualizar(TransferenciaBloqueados t) {
        return ejecutar(Transferencias.ACTUALIZAR, ps -> {
            ps.setString(1, t.codigoAcceso);
            ps.setString(2, t.estado.name());
            setFechaHora(ps, 3, t.fechaAcceso);
            ps.setInt(4, t.usuarioId);
            setEnteroONulo(ps, 5, t.reservaId);
            ps.setString(6, t.standId);
            ps.setInt(7, t.id);
        }) > 0;
    }

    public boolean cambiarEstado(int id, EstadoTransferencia nuevo) {
        return ejecutar(Transferencias.CAMBIAR_ESTADO, ps -> {
            ps.setString(1, nuevo.name());
            ps.setInt(2, id);
        }) > 0;
    }

    public boolean registrarAcceso(int id, LocalDateTime fecha) {
        return ejecutar(Transferencias.REGISTRAR_ACCESO, ps -> {
            setFechaHora(ps, 1, fecha);
            ps.setInt(2, id);
        }) > 0;
    }

    public boolean asignarReserva(int id, Integer reservaId) {
        return ejecutar(Transferencias.ASIGNAR_RESERVA, ps -> {
            setEnteroONulo(ps, 1, reservaId);
            ps.setInt(2, id);
        }) > 0;
    }

    // ---------------- DELETE ----------------
    /** Al borrar, la BD pone stands.transferencia_id en NULL (ON DELETE SET NULL). */
    public boolean eliminar(int id) {
        return ejecutar(Transferencias.ELIMINAR, ps -> ps.setInt(1, id)) > 0;
    }

    private TransferenciaBloqueados mapear(ResultSet rs) throws SQLException {
        TransferenciaBloqueados x = new TransferenciaBloqueados();
        x.id = rs.getInt("id");
        x.codigoAcceso = rs.getString("codigo_acceso");
        x.estado = EstadoTransferencia.valueOf(rs.getString("estado"));
        x.fechaAcceso = getFechaHora(rs, "fecha_acceso");
        x.usuarioId = rs.getInt("usuario_id");
        x.reservaId = getEnteroONulo(rs, "reserva_id");
        x.standId = rs.getString("stand_id");
        return x;
    }
}
