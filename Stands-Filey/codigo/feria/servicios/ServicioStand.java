package feria.servicios;

import feria.modelo.Stand;
import feria.persistencia.dao.StandDAO;
import java.util.List;
import java.util.Optional;

public class ServicioStand {
    private final StandDAO dao;
    public ServicioStand(StandDAO dao) { this.dao = dao; }
    public List<Stand> listarStands() { return dao.listarTodos(); }
    public Optional<Stand> buscarStand(String id) { return dao.buscarPorId(id); }
    public double calcularArea(Stand stand) { return stand.largo * stand.ancho; }
}
