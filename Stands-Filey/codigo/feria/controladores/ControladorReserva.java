package feria.controladores;

import feria.modelo.Reserva;
import feria.servicios.ServicioReserva;
import java.util.List;
import java.util.Optional;

public class ControladorReserva {
    private final ServicioReserva servicio;
    public ControladorReserva(ServicioReserva servicio) { this.servicio = servicio; }
    public List<Reserva> listar() { return servicio.listarReservas(); }
    public Optional<Reserva> consultar(int id) { return servicio.buscarPorId(id); }
    public boolean estaVencida(Reserva reserva) { return servicio.estaVencida(reserva); }
}
