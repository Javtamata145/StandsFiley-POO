package feria.controladores;

import feria.modelo.TransferenciaBloqueados;
import feria.servicios.ServicioTransferencia;
import java.util.List;
import java.util.Optional;

public class ControladorTransferencia {
    private final ServicioTransferencia servicio;
    public ControladorTransferencia(ServicioTransferencia servicio) { this.servicio = servicio; }
    public List<TransferenciaBloqueados> listar() { return servicio.listarTransferencias(); }
    public Optional<TransferenciaBloqueados> consultar(int id) { return servicio.buscarPorId(id); }
}
