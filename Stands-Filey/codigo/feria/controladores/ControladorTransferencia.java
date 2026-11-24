package feria.controladores;

import feria.modelo.EstadoTransferencia;
import feria.modelo.Reserva;
import feria.modelo.TransferenciaBloqueados;
import feria.servicios.ServicioTransferencia;
import java.util.List;
import java.util.Optional;

public class ControladorTransferencia {
    private final ServicioTransferencia servicio;
    public ControladorTransferencia(ServicioTransferencia servicio) { this.servicio = servicio; }

    // READ
    public List<TransferenciaBloqueados> listar() { return servicio.listarTransferencias(); }
    public Optional<TransferenciaBloqueados> consultar(int id) { return servicio.buscarPorId(id); }
    public Optional<TransferenciaBloqueados> consultarPorCodigo(String codigo) { return servicio.buscarPorCodigo(codigo); }
    public boolean validarCodigo(String standId, String codigo) { return servicio.validarCodigo(standId, codigo); }

    // CREATE
    public TransferenciaBloqueados crear(String standId, int usuarioPropietarioId) {
        return servicio.crear(standId, usuarioPropietarioId);
    }

    // UPDATE
    public String registrarAcceso(int transferenciaId) { return servicio.registrarAcceso(transferenciaId); }
    public Reserva completar(String standId, String codigo, int usuarioId) {
        return servicio.completar(standId, codigo, usuarioId);
    }
    public TransferenciaBloqueados actualizar(TransferenciaBloqueados t) { return servicio.actualizar(t); }
    public boolean cambiarEstado(int id, EstadoTransferencia estado) { return servicio.cambiarEstado(id, estado); }

    // DELETE
    public boolean eliminar(int id) { return servicio.eliminar(id); }
}
