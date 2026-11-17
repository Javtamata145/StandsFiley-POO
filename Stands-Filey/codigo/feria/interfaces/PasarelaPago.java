package feria.interfaces;

public interface PasarelaPago {
    boolean cobrar(double monto);
    boolean consultarPago(String referencia);
}
