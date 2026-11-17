package feria.interfaces;

import java.time.LocalDate;

public interface PoliticaDescuento {
    double calcular(double precio, LocalDate fechaPago);
}
