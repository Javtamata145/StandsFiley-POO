package feria.modelo;

import java.time.LocalDateTime;

public class Pago {
    public int id;
    public int reservaId;
    public double monto;
    public LocalDateTime fecha;
    public String tipo;
    public boolean confirmado;
}
