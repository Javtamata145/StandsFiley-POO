package feria.implementaciones;

import feria.interfaces.PasarelaPago;

public class PasarelaPagoSimulada implements PasarelaPago {
    @Override
    public boolean cobrar(double monto) {
        return monto > 0;
    }

    @Override
    public boolean consultarPago(String referencia) {
        return referencia != null && !referencia.isBlank();
    }
}
