package feria.servicios;

import feria.interfaces.PasarelaPago;
import feria.modelo.*;
import java.time.LocalDate;

public class ServicioPagos {
    private PasarelaPago pasarela;
    private ServicioReservas servicioReservas;
    private int siguienteId = 1;

    public ServicioPagos(PasarelaPago pasarela, ServicioReservas servicioReservas) {
        this.pasarela = pasarela;
        this.servicioReservas = servicioReservas;
    }

    public boolean registrarPago(Reserva reserva, double monto) {
        if (reserva == null || reserva.getEstado() != EstadoReserva.ACTIVA || monto <= 0)
            return false;

        double pendiente = reserva.getStand().getPrecio() - reserva.getMontoPagado();
        if (monto > pendiente) return false;

        double cobro = monto;
        if (reserva.getMontoPagado() == 0 && monto >= reserva.calcularMontoReserva()) {
            cobro = reserva.calcularMontoReserva();
        } else if (LocalDate.now().isBefore(reserva.getFechaLimite())) {
            cobro = Math.min(monto, pendiente);
        }

        if (!pasarela.cobrar(cobro)) return false;

        Pago pago = new Pago(siguienteId++, cobro, "NORMAL", true);
        reserva.registrarPago(pago);

        if (reserva.getMontoPagado() >= reserva.getStand().getPrecio()) {
            reserva.getStand().ocupar();
            reserva.setEstado(EstadoReserva.COMPLETADA);
        }
        return true;
    }

    public void verificarPagosRecibidos() {
        System.out.println("Verificación de pagos completada.");
    }
}
