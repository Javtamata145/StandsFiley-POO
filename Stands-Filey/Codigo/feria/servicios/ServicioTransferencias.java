package feria.servicios;

import feria.modelo.*;
import java.util.HashMap;
import java.util.Map;

public class ServicioTransferencias {
    private ServicioPagos pagos;
    private ServicioNotificaciones notificaciones;
    private Map<String, TransferenciaBloqueados> transferencias = new HashMap<>();
    private int siguienteId = 1;
    private int siguienteReserva = 1000;

    public ServicioTransferencias(ServicioPagos pagos, ServicioNotificaciones notificaciones) {
        this.pagos = pagos;
        this.notificaciones = notificaciones;
    }

    public TransferenciaBloqueados enviarPermiso(Stand stand, Usuario propietario) {
        if (stand.getEstado() != EstadoStand.BLOQUEADO)
            throw new IllegalStateException("El stand debe estar bloqueado.");
        TransferenciaBloqueados t = new TransferenciaBloqueados(siguienteId++, stand, propietario);
        stand.habilitarTransferencia(t);
        transferencias.put(stand.getId(), t);
        notificaciones.notificar(propietario, "TRANSFERENCIA", "Permiso de transferencia",
                "Accede a la transferencia del stand " + stand.getId() + ".");
        return t;
    }

    public String accederTransferencia(String idStand) {
        TransferenciaBloqueados t = transferencias.get(idStand);
        if (t == null) throw new IllegalArgumentException("No existe la transferencia.");
        t.registrarAcceso();
        t.getStand().liberar();
        return t.getCodigoAcceso();
    }

    public Reserva habilitarPago(Stand stand, String codigo, double monto) {
        TransferenciaBloqueados t = transferencias.get(stand.getId());
        if (t == null || !t.validarCodigo(codigo) || stand.getEstado() != EstadoStand.DISPONIBLE)
            throw new IllegalArgumentException("Código inválido para este stand.");

        Reserva r = new Reserva(siguienteReserva++, t.getUsuario(), stand);
        if (!pagos.registrarPago(r, monto)) {
            stand.liberar();
            throw new IllegalStateException("No se pudo procesar el pago.");
        }
        t.setReserva(r);
        return r;
    }
}
