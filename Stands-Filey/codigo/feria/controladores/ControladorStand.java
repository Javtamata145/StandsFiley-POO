package feria.controladores;

import feria.modelo.EstadoStand;
import feria.modelo.Stand;
import feria.servicios.ServicioStand;
import java.util.List;
import java.util.Optional;

public class ControladorStand {
    private final ServicioStand servicio;
    public ControladorStand(ServicioStand servicio) { this.servicio = servicio; }

    // READ
    public List<Stand> listar() { return servicio.listarStands(); }
    public List<Stand> listarPorEstado(EstadoStand estado) { return servicio.listarPorEstado(estado); }
    public List<Stand> listarDisponibles() { return servicio.listarDisponibles(); }
    public Optional<Stand> consultar(String id) { return servicio.buscarStand(id); }
    public double consultarArea(Stand stand) { return servicio.calcularArea(stand); }

    // CREATE
    public Stand crear(Stand stand) { return servicio.crear(stand); }

    // UPDATE
    public Stand actualizar(Stand stand) { return servicio.actualizar(stand); }
    public boolean cambiarEstado(String id, EstadoStand estado) { return servicio.cambiarEstado(id, estado); }

    // DELETE
    public boolean eliminar(String id) { return servicio.eliminar(id); }
}
