package feria.servicios;

public class TareasProgramadas {
    private ServicioReservas reservas;
    private ServicioPagos pagos;
    private ServicioNotificaciones notificaciones;

    public TareasProgramadas(ServicioReservas reservas, ServicioPagos pagos,
                             ServicioNotificaciones notificaciones) {
        this.reservas = reservas;
        this.pagos = pagos;
        this.notificaciones = notificaciones;
    }

    public void ejecutar() {
        reservas.liberarReservasVencidas();
        pagos.verificarPagosRecibidos();
        System.out.println("Tareas programadas ejecutadas.");
    }

    public void enviarRecordatorios() {
        for (var r : reservas.getReservas()) {
            if (r.getEstado() == feria.modelo.EstadoReserva.ACTIVA) {
                notificaciones.notificar(r.getUsuario(), "RECORDATORIO", "Fecha límite de pago",
                        "Tu fecha límite es " + r.getFechaLimite() + ".");
            }
        }
    }
}
