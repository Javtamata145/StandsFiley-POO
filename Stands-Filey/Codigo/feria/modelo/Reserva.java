package feria.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reserva {
    private int id;
    private Usuario usuario;
    private Stand stand;
    private LocalDate fechaReserva;
    private LocalDate fechaLimite;
    private double montoPagado;
    private EstadoReserva estado;
    private List<Pago> pagos = new ArrayList<>();

    public Reserva(int id, Usuario usuario, Stand stand) {
        this.id = id;
        this.usuario = usuario;
        this.stand = stand;
        this.fechaReserva = LocalDate.now();
        this.fechaLimite = fechaReserva.plusMonths(1);
        this.estado = EstadoReserva.ACTIVA;
    }

    public double calcularMontoReserva() { return stand.getPrecio() * 0.15; }
    public boolean estaVencida() {
        return estado == EstadoReserva.ACTIVA && LocalDate.now().isAfter(fechaLimite);
    }
    public void registrarPago(Pago pago) {
        if (pago.isConfirmado()) {
            pagos.add(pago);
            montoPagado += pago.getMonto();
        }
    }
    public int getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Stand getStand() { return stand; }
    public LocalDate getFechaReserva() { return fechaReserva; }
    public LocalDate getFechaLimite() { return fechaLimite; }
    public double getMontoPagado() { return montoPagado; }
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
    public List<Pago> getPagos() { return pagos; }
}
