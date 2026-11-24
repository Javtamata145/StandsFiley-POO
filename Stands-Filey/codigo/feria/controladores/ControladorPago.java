package feria.controladores;

import feria.modelo.Pago;
import feria.servicios.ServicioPago;
import java.util.List;
import java.util.Optional;

public class ControladorPago {
    private final ServicioPago servicio;
    public ControladorPago(ServicioPago servicio) { this.servicio = servicio; }

    // READ
    public List<Pago> listar() { return servicio.listarTodos(); }
    public List<Pago> listarDeReserva(int reservaId) { return servicio.listarDeReserva(reservaId); }
    public Optional<Pago> consultar(int id) { return servicio.buscarPorId(id); }
    public double totalConfirmado(List<Pago> pagos) { return servicio.sumarPagosConfirmados(pagos); }
    public double saldoPendiente(int reservaId) { return servicio.calcularSaldoPendiente(reservaId); }

    // CREATE
    public Pago registrarPago(int reservaId, double monto, String tipo) {
        return servicio.registrarPago(reservaId, monto, tipo);
    }

    // UPDATE
    public Pago actualizar(Pago pago) { return servicio.actualizar(pago); }
    public boolean confirmar(int id) { return servicio.confirmar(id); }

    // DELETE
    public boolean eliminar(int id) { return servicio.eliminar(id); }
}
