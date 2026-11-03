package feria.implementaciones;

import feria.interfaces.PoliticaDescuento;
import java.time.LocalDate;

public class DescuentoProntoPago implements PoliticaDescuento {
    private double porcentaje = 0.10;

    @Override
    public double calcular(double precio, LocalDate fechaPago) {
        return fechaPago.isBefore(LocalDate.now().plusDays(7))
            ? precio * (1 - porcentaje) : precio;
    }
}
