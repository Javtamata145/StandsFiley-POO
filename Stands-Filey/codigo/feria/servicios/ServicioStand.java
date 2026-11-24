package feria.servicios;

import feria.modelo.EstadoStand;
import feria.modelo.Stand;
import feria.persistencia.dao.StandDAO;
import java.util.List;
import java.util.Optional;

/** Lógica de negocio de stands y CRUD. */
public class ServicioStand {
    private final StandDAO dao;
    public ServicioStand(StandDAO dao) { this.dao = dao; }

    // ---------------- READ ----------------
    public List<Stand> listarStands() { return dao.listarTodos(); }
    public List<Stand> listarPorEstado(EstadoStand estado) { return dao.listarPorEstado(estado); }
    public List<Stand> listarDisponibles() { return dao.listarPorEstado(EstadoStand.DISPONIBLE); }
    public Optional<Stand> buscarStand(String id) { return dao.buscarPorId(id); }
    public double calcularArea(Stand stand) { return stand.largo * stand.ancho; }

    /** RF-006 / RF-007: solo se reservan stands Disponibles. */
    public boolean puedeReservarse(Stand stand) { return stand.estado == EstadoStand.DISPONIBLE; }

    // ---------------- CREATE ----------------
    public Stand crear(Stand stand) {
        validar(stand);
        if (dao.buscarPorId(stand.id).isPresent()) {
            throw new IllegalArgumentException("Ya existe un stand con id " + stand.id + ".");
        }
        if (stand.estado == null) stand.estado = EstadoStand.DISPONIBLE;
        return dao.insertar(stand);
    }

    // ---------------- UPDATE ----------------
    public Stand actualizar(Stand stand) {
        validar(stand);
        Stand actual = dao.buscarPorId(stand.id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el stand " + stand.id + "."));
        if (stand.estado == null) stand.estado = actual.estado;
        dao.actualizar(stand);
        return stand;
    }

    public boolean cambiarEstado(String id, EstadoStand nuevo) {
        return dao.cambiarEstado(id, nuevo);
    }

    // ---------------- DELETE ----------------
    /** No se puede eliminar un stand Reservado u Ocupado. */
    public boolean eliminar(String id) {
        Optional<Stand> stand = dao.buscarPorId(id);
        if (stand.isEmpty()) return false;
        EstadoStand estado = stand.get().estado;
        if (estado == EstadoStand.RESERVADO || estado == EstadoStand.OCUPADO) {
            throw new IllegalStateException("No se puede eliminar el stand " + id + " porque está " + estado + ".");
        }
        return dao.eliminar(id);
    }

    private void validar(Stand s) {
        if (s == null || s.id == null || s.id.isBlank()) {
            throw new IllegalArgumentException("El stand debe tener un id.");
        }
        if (s.largo <= 0 || s.ancho <= 0) {
            throw new IllegalArgumentException("Las dimensiones del stand deben ser mayores a 0.");
        }
        if (s.precio <= 0) {
            throw new IllegalArgumentException("El precio del stand debe ser mayor a 0.");
        }
    }
}
