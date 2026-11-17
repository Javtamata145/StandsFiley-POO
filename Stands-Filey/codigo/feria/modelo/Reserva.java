package feria.modelo;

import java.time.LocalDate;

public class Reserva {
    public int id;
    public int usuarioId;
    public String standId;
    public LocalDate fechaReserva;
    public LocalDate fechaLimite;
    public double montoPagado;
    public EstadoReserva estado;
}
