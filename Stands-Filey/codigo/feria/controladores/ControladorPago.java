package feria.controladores;

import feria.modelo.Pago;
import feria.servicios.ServicioPago;
import java.util.List;
import java.util.Optional;

public class ControladorPago {
    private final ServicioPago servicio;
    public ControladorPago(ServicioPago servicio) { this.servicio = servicio; }
    public List<Pago> listarDeReserva(int reservaId) { return servicio.listarDeReserva(reservaId); }
    public Optional<Pago> consultar(int id) { return servicio.buscarPorId(id); }
    public double totalConfirmado(List<Pago> pagos) { return servicio.sumarPagosConfirmados(pagos); }
}
