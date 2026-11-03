package feria.modelo;

public class Stand {
    private String id;
    private double largo;
    private double ancho;
    private double precio;
    private EstadoStand estado;
    private TransferenciaBloqueados transferencia;

    public Stand(String id, double largo, double ancho, double precio, EstadoStand estado) {
        this.id = id;
        this.largo = largo;
        this.ancho = ancho;
        this.precio = precio;
        this.estado = estado;
    }

    public double calcularMetrosCuadrados() { return largo * ancho; }
    public boolean estaDisponible() { return estado == EstadoStand.DISPONIBLE; }
    public void reservar() {
        if (!estaDisponible()) throw new IllegalStateException("El stand no está disponible.");
        estado = EstadoStand.RESERVADO;
    }
    public void ocupar() { estado = EstadoStand.OCUPADO; }
    public void liberar() { estado = EstadoStand.DISPONIBLE; }
    public void bloquear() { estado = EstadoStand.BLOQUEADO; }
    public void habilitarTransferencia(TransferenciaBloqueados t) {
        if (estado != EstadoStand.BLOQUEADO) throw new IllegalStateException("El stand no está bloqueado.");
        transferencia = t;
    }

    public String getId() { return id; }
    public double getLargo() { return largo; }
    public double getAncho() { return ancho; }
    public double getPrecio() { return precio; }
    public EstadoStand getEstado() { return estado; }
    public TransferenciaBloqueados getTransferencia() { return transferencia; }
}
