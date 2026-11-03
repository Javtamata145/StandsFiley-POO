package feria.modelo;

import java.time.LocalDateTime;

public class Notificacion {
    private int id;
    private String tipo;
    private String asunto;
    private String mensaje;
    private LocalDateTime fecha;
    private boolean leida;

    public Notificacion(int id, String tipo, String asunto, String mensaje) {
        this.id = id;
        this.tipo = tipo;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.fecha = LocalDateTime.now();
    }

    public void marcarLeida() { leida = true; }
    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getAsunto() { return asunto; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFecha() { return fecha; }
    public boolean isLeida() { return leida; }
}
