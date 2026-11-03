package feria.controladores;

import feria.modelo.*;
import feria.servicios.ServicioTransferencias;

public class ControladorTransferencia {
    private ServicioTransferencias servicio;
    public ControladorTransferencia(ServicioTransferencias servicio) { this.servicio = servicio; }
    public String accederDesdeCorreo(String idStand) { return servicio.accederTransferencia(idStand); }
    public Reserva ingresarCodigo(Stand stand, String codigo, double monto) {
        return servicio.habilitarPago(stand, codigo, monto);
    }
}
