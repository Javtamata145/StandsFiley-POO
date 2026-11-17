package feria.modelo;

import java.time.LocalDateTime;

public class TransferenciaBloqueados {
    public int id;
    public String codigoAcceso;
    public EstadoTransferencia estado;
    public LocalDateTime fechaAcceso;
    public int usuarioId;
    public Integer reservaId;
    public String standId;
}
