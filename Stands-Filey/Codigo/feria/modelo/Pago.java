package feria.modelo;

import java.time.LocalDateTime;

public class Pago {
    private int id;
    private double monto;
    private LocalDateTime fecha;
    private String tipo;
    private boolean confirmado;

    public Pago(int id, double monto, String tipo, boolean confirmado) {
        this.id = id;
        this.monto = monto;
        this.tipo = tipo;
        this.confirmado = confirmado;
        this.fecha = LocalDateTime.now();
    }

    public int getId() { return id; }
    public double getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public String getTipo() { return tipo; }
    public boolean isConfirmado() { return confirmado; }
}
