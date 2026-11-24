package feria.controladores;

import feria.modelo.Reserva;
import feria.servicios.ServicioReserva;
import java.util.List;
import java.util.Optional;

public class ControladorReserva {
    private final ServicioReserva servicio;
    public ControladorReserva(ServicioReserva servicio) { this.servicio = servicio; }

    // READ
    public List<Reserva> listar() { return servicio.listarReservas(); }
    public List<Reserva> listarDeUsuario(int usuarioId) { return servicio.listarDeUsuario(usuarioId); }
    public Optional<Reserva> consultar(int id) { return servicio.buscarPorId(id); }
    public boolean estaVencida(Reserva reserva) { return servicio.estaVencida(reserva); }

    // CREATE
    public Reserva reservar(int usuarioId, String standId) { return servicio.reservar(usuarioId, standId); }

    // UPDATE
    public Reserva actualizar(Reserva reserva) { return servicio.actualizar(reserva); }
    public List<Reserva> liberarVencidas() { return servicio.liberarVencidas(); }

    // DELETE
    public boolean eliminar(int id) { return servicio.eliminar(id); }
}
