package feria.controladores;

import feria.modelo.*;
import feria.servicios.*;
import java.util.List;

public class ControladorStands {
    private List<Stand> stands;
    private ServicioReservas reservas;
    private ServicioPagos pagos;

    public ControladorStands(List<Stand> stands, ServicioReservas reservas, ServicioPagos pagos) {
        this.stands = stands;
        this.reservas = reservas;
        this.pagos = pagos;
    }

    public List<Stand> verMapa() { return stands; }
    public Stand verDetalle(String idStand) {
        return stands.stream().filter(s -> s.getId().equals(idStand)).findFirst().orElse(null);
    }
    public Reserva reservar(Usuario usuario, String idStand) {
        return reservas.reservarStand(usuario, verDetalle(idStand));
    }
    public boolean pagar(int idReserva, double monto) {
        Reserva r = reservas.getReservas().stream()
                .filter(x -> x.getId() == idReserva).findFirst().orElse(null);
        return r != null && pagos.registrarPago(r, monto);
    }
}
