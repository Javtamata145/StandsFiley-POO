package feria;

import feria.controladores.*;
import feria.persistencia.ConexionBD;
import feria.persistencia.dao.*;
import feria.servicios.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // Verificación rápida de conexión
        try (Connection conn = ConexionBD.obtenerConexion()) {
            System.out.println("Conexión exitosa a: " + conn.getCatalog());
        } catch (Exception e) {
            System.err.println("Error de conexión inicial: " + e.getMessage());
            return;
        }

        // Cada controlador depende de un servicio; cada servicio de su DAO JDBC.
        ControladorStand stands = new ControladorStand(new ServicioStand(new StandDAO()));
        ControladorUsuario usuarios = new ControladorUsuario(new ServicioUsuario(new UsuarioDAO()));
        ControladorReserva reservas = new ControladorReserva(new ServicioReserva(new ReservaDAO()));
        ControladorNotificacion notificaciones =
                new ControladorNotificacion(new ServicioNotificacion(new NotificacionDAO()));
        ControladorPago pagos = new ControladorPago(new ServicioPago(new PagoDAO()));
        ControladorTransferencia transferencias =
                new ControladorTransferencia(new ServicioTransferencia(new TransferenciaDAO()));

        try {
            System.out.println("Stands registrados: " + stands.listar().size());
            System.out.println("Usuarios registrados: " + usuarios.listar().size());
            System.out.println("Reservas registradas: " + reservas.listar().size());
            System.out.println("Transferencias registradas: " + transferencias.listar().size());
            System.out.println("Notificaciones usuario 1: " + notificaciones.listarDeUsuario(1).size());
            System.out.println("Pagos reserva 1: " + pagos.listarDeReserva(1).size());
        } catch (Exception e) {
            System.err.println("No se pudo consultar la BD: " + e.getMessage());
        }
    }
}
