package feria.modelo;

import java.security.SecureRandom;
import java.util.UUID;

public class TransferenciaBloqueados {
    private int id;
    private String codigoAcceso;
    private String estado;
    private java.time.LocalDateTime fechaAcceso;
    private Usuario usuario;
    private Reserva reserva;
    private Stand stand;

    public TransferenciaBloqueados(int id, Stand stand, Usuario usuario) {
        this.id = id;
        this.stand = stand;
        this.usuario = usuario;
        this.estado = "PENDIENTE";
        generarCodigo();
    }

    public void generarCodigo() {
        codigoAcceso = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    public boolean validarCodigo(String codigo) {
        return codigo != null && codigoAcceso.equals(codigo) && estado.equals("ACCESO_HABILITADO");
    }
    public void registrarAcceso() {
        fechaAcceso = java.time.LocalDateTime.now();
        estado = "ACCESO_HABILITADO";
    }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public int getId() { return id; }
    public String getCodigoAcceso() { return codigoAcceso; }
    public String getEstado() { return estado; }
    public Usuario getUsuario() { return usuario; }
    public Reserva getReserva() { return reserva; }
    public Stand getStand() { return stand; }
}
