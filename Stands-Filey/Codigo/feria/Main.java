package feria;

import feria.modelo.*;
import feria.interfaces.*;
import feria.implementaciones.*;
import feria.servicios.*;
import feria.controladores.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        CanalNotificacion correo = new CanalCorreo();
        CanalNotificacion pagina = new CanalPagina();
        ServicioNotificaciones notificaciones = new ServicioNotificaciones(correo, pagina);

        ServicioAutenticacion autenticacion = new ServicioAutenticacion();
        ServicioReservas reservas = new ServicioReservas(notificaciones, new DescuentoProntoPago());
        ServicioPagos pagos = new ServicioPagos(new PasarelaPagoSimulada(), reservas);
        ServicioTransferencias transferencias = new ServicioTransferencias(pagos, notificaciones);

        ControladorAcceso acceso = new ControladorAcceso(autenticacion);
        Usuario usuario = acceso.registrar("Usuario Feria", "usuario@feria.com",
                "123456", "9991234567");

        List<Stand> stands = new ArrayList<>();
        stands.add(new Stand("A1", 3, 2, 10000, EstadoStand.DISPONIBLE));
        stands.add(new Stand("A2", 4, 3, 15000, EstadoStand.DISPONIBLE));
        stands.add(new Stand("B1", 2, 2, 8000, EstadoStand.BLOQUEADO));

        ControladorStands controladorStands = new ControladorStands(stands, reservas, pagos);

        System.out.println("STANDS DE LA FERIA");
        for (Stand s : controladorStands.verMapa()) {
            System.out.println(s.getId() + " | " + s.calcularMetrosCuadrados()
                    + " m2 | $" + s.getPrecio() + " | " + s.getEstado());
        }

        Reserva reserva = controladorStands.reservar(usuario, "A1");
        System.out.println("\nReserva creada. Pago inicial: $" + reserva.calcularMontoReserva());
        controladorStands.pagar(reserva.getId(), reserva.calcularMontoReserva());

        System.out.println("\nEstado actual del stand A1: "
                + controladorStands.verDetalle("A1").getEstado());

        ControladorTransferencia controladorTransferencia =
                new ControladorTransferencia(transferencias);
        TransferenciaBloqueados t = transferencias.enviarPermiso(stands.get(2), usuario);
        String codigo = controladorTransferencia.accederDesdeCorreo("B1");
        System.out.println("\nCódigo de transferencia: " + codigo);
    }
}
