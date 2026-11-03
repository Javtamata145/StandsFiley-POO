package feria.servicios;

import feria.interfaces.PoliticaDescuento;
import feria.modelo.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicioReservas {
    private List<Reserva> reservas = new ArrayList<>();
    private ServicioNotificaciones notificaciones;
    private PoliticaDescuento descuento;
    private int siguienteId = 1;

    public ServicioReservas(ServicioNotificaciones notificaciones, PoliticaDescuento descuento) {
        this.notificaciones = notificaciones;
        this.descuento = descuento;
    }

    public synchronized Reserva reservarStand(Usuario usuario, Stand stand) {
        if (usuario == null || stand == null || !stand.estaDisponible())
            throw new IllegalStateException("No se puede reservar este stand.");
        stand.reservar();
        Reserva reserva = new Reserva(siguienteId++, usuario, stand);
        reservas.add(reserva);
        notificaciones.notificar(usuario, "RESERVA", "Reserva creada",
            "Tu reserva vence el " + reserva.getFechaLimite() + ".");
        return reserva;
    }

    public void liberarReservasVencidas() {
        for (Reserva r : reservas) {
            if (r.estaVencida()) {
                r.setEstado(EstadoReserva.VENCIDA);
                r.getStand().liberar();
                notificaciones.notificar(r.getUsuario(), "VENCIMIENTO", "Reserva vencida",
                    "Tu reserva expiró y el stand fue liberado.");
            }
        }
    }

    public List<Reserva> getReservas() { return reservas; }
    public double calcularPrecioConDescuento(Stand stand, LocalDate fecha) {
        return descuento.calcular(stand.getPrecio(), fecha);
    }
}
