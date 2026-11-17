package feria.servicios;

import feria.modelo.TransferenciaBloqueados;
import feria.persistencia.dao.TransferenciaDAO;
import java.util.List;
import java.util.Optional;

public class ServicioTransferencia {
    private final TransferenciaDAO dao;
    public ServicioTransferencia(TransferenciaDAO dao) { this.dao = dao; }
    public List<TransferenciaBloqueados> listarTransferencias() { return dao.listarTodas(); }
    public Optional<TransferenciaBloqueados> buscarPorId(int id) { return dao.buscarPorId(id); }
}
