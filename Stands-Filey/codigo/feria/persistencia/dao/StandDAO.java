package feria.persistencia.dao;

import feria.modelo.EstadoStand;
import feria.modelo.Stand;
import feria.persistencia.ConsultasSQL.Stands;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de la tabla stands. CRUD completo. */
public class StandDAO extends DAOBase {

    // ---------------- READ ----------------
    public List<Stand> listarTodos() {
        return consultarLista(Stands.LISTAR_TODOS, SIN_PARAMETROS, this::mapear);
    }

    public List<Stand> listarPorEstado(EstadoStand estado) {
        return consultarLista(Stands.LISTAR_POR_ESTADO, ps -> ps.setString(1, estado.name()), this::mapear);
    }

    public Optional<Stand> buscarPorId(String id) {
        return consultarUno(Stands.BUSCAR_POR_ID, ps -> ps.setString(1, id), this::mapear);
    }

    // ---------------- CREATE ----------------
    /** El id del stand lo define el usuario (ej. "ST-A01"), no es AUTO_INCREMENT. */
    public Stand insertar(Stand s) {
        ejecutar(Stands.INSERTAR, ps -> {
            ps.setString(1, s.id);
            ps.setDouble(2, s.largo);
            ps.setDouble(3, s.ancho);
            ps.setDouble(4, s.precio);
            ps.setString(5, s.estado.name());
            setEnteroONulo(ps, 6, s.transferenciaId);
        });
        return s;
    }

    // ---------------- UPDATE ----------------
    public boolean actualizar(Stand s) {
        return ejecutar(Stands.ACTUALIZAR, ps -> {
            ps.setDouble(1, s.largo);
            ps.setDouble(2, s.ancho);
            ps.setDouble(3, s.precio);
            ps.setString(4, s.estado.name());
            setEnteroONulo(ps, 5, s.transferenciaId);
            ps.setString(6, s.id);
        }) > 0;
    }

    public boolean cambiarEstado(String id, EstadoStand nuevo) {
        return ejecutar(Stands.CAMBIAR_ESTADO, ps -> {
            ps.setString(1, nuevo.name());
            ps.setString(2, id);
        }) > 0;
    }

    /**
     * Cambia el estado SOLO si el stand está en el estado esperado.
     * Devuelve false si otro usuario ya lo cambió (control de concurrencia, RNF-003).
     */
    public boolean cambiarEstadoSiEsta(String id, EstadoStand esperado, EstadoStand nuevo) {
        return ejecutar(Stands.CAMBIAR_ESTADO_SI_ESTA, ps -> {
            ps.setString(1, nuevo.name());
            ps.setString(2, id);
            ps.setString(3, esperado.name());
        }) > 0;
    }

    public boolean asignarTransferencia(String standId, Integer transferenciaId) {
        return ejecutar(Stands.ASIGNAR_TRANSFERENCIA, ps -> {
            setEnteroONulo(ps, 1, transferenciaId);
            ps.setString(2, standId);
        }) > 0;
    }

    // ---------------- DELETE ----------------
    public boolean eliminar(String id) {
        return ejecutar(Stands.ELIMINAR, ps -> ps.setString(1, id)) > 0;
    }

    private Stand mapear(ResultSet rs) throws SQLException {
        Stand x = new Stand();
        x.id = rs.getString("id");
        x.largo = rs.getDouble("largo");
        x.ancho = rs.getDouble("ancho");
        x.precio = rs.getDouble("precio");
        x.estado = EstadoStand.valueOf(rs.getString("estado"));
        x.transferenciaId = getEnteroONulo(rs, "transferencia_id");
        return x;
    }
}
