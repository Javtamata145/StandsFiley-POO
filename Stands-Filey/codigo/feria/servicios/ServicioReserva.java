package feria.servicios;

import feria.modelo.Reserva;
import feria.persistencia.dao.ReservaDAO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ServicioReserva {
    private final ReservaDAO dao;
    public ServicioReserva(ReservaDAO dao) { this.dao = dao; }
    public List<Reserva> listarReservas() { return dao.listarTodos(); }
    public Optional<Reserva> buscarPorId(int id) { return dao.buscarPorId(id); }
    public boolean estaVencida(Reserva reserva) {
        return reserva.estado != null && reserva.estado.name().equals("ACTIVA")
                && reserva.fechaLimite != null && LocalDate.now().isAfter(reserva.fechaLimite);
    }
}
