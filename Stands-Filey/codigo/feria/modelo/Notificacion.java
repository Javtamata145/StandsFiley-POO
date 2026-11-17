package feria.modelo;

import java.time.LocalDateTime;

public class Notificacion {
    public int id;
    public int usuarioId;
    public String tipo;
    public String asunto;
    public String mensaje;
    public LocalDateTime fecha;
    public boolean leida;
}
