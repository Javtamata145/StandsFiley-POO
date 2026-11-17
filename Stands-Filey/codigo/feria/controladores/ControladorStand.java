package feria.controladores;

import feria.modelo.Stand;
import feria.servicios.ServicioStand;
import java.util.List;
import java.util.Optional;

public class ControladorStand {
    private final ServicioStand servicio;
    public ControladorStand(ServicioStand servicio) { this.servicio = servicio; }
    public List<Stand> listar() { return servicio.listarStands(); }
    public Optional<Stand> consultar(String id) { return servicio.buscarStand(id); }
    public double consultarArea(Stand stand) { return servicio.calcularArea(stand); }
}
