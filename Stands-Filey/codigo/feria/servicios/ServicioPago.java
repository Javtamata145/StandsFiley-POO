package feria.servicios;

import feria.modelo.Pago;
import feria.persistencia.dao.PagoDAO;
import java.util.List;
import java.util.Optional;

public class ServicioPago {
    private final PagoDAO dao;
    public ServicioPago(PagoDAO dao) { this.dao = dao; }
    public List<Pago> listarDeReserva(int reservaId) { return dao.listarPorReserva(reservaId); }
    public Optional<Pago> buscarPorId(int id) { return dao.buscarPorId(id); }
    public double sumarPagosConfirmados(List<Pago> pagos) {
        return pagos.stream().filter(p -> p.confirmado).mapToDouble(p -> p.monto).sum();
    }
}
