package feria;

import feria.controladores.*;
import feria.implementaciones.PasarelaPagoSimulada;
import feria.interfaces.PasarelaPago;
import feria.modelo.*;
import feria.persistencia.ConexionBD;
import feria.persistencia.dao.*;
import feria.servicios.*;
import java.sql.Connection;
import java.time.YearMonth;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Verificación rápida de conexión
        try (Connection conn = ConexionBD.obtenerConexion()) {
            System.out.println("Conexión exitosa a: " + conn.getCatalog());
        } catch (Exception e) {
            System.err.println("Error de conexión inicial: " + e.getMessage());
            return;
        }

        // ---------- Armado de capas: Controlador -> Servicio -> DAO -> ConsultasSQL ----------
        StandDAO standDAO = new StandDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ReservaDAO reservaDAO = new ReservaDAO();
        PagoDAO pagoDAO = new PagoDAO();
        NotificacionDAO notificacionDAO = new NotificacionDAO();
        TransferenciaDAO transferenciaDAO = new TransferenciaDAO();
        PasarelaPago pasarela = new PasarelaPagoSimulada();

        ServicioReserva servicioReserva = new ServicioReserva(reservaDAO, standDAO, pagoDAO, pasarela);

        ControladorStand stands = new ControladorStand(new ServicioStand(standDAO));
        ControladorUsuario usuarios = new ControladorUsuario(new ServicioUsuario(usuarioDAO));
        ControladorReserva reservas = new ControladorReserva(servicioReserva);
        ControladorPago pagos = new ControladorPago(new ServicioPago(pagoDAO, reservaDAO, standDAO, pasarela));
        ControladorNotificacion notificaciones =
                new ControladorNotificacion(new ServicioNotificacion(notificacionDAO));
        ControladorTransferencia transferencias = new ControladorTransferencia(
                new ServicioTransferencia(transferenciaDAO, standDAO, servicioReserva));

        try {
            System.out.println("\n===== READ: datos actuales =====");
            System.out.println("Stands registrados: " + stands.listar().size());
            System.out.println("Usuarios registrados: " + usuarios.listar().size());
            System.out.println("Reservas registradas: " + reservas.listar().size());
            System.out.println("Pagos registrados: " + pagos.listar().size());
            System.out.println("Transferencias registradas: " + transferencias.listar().size());
            System.out.println("Notificaciones registradas: " + notificaciones.listar().size());
            YearMonth mes = YearMonth.now();
            System.out.println("Pagos realizados en " + mes + ": "
                    + pagos.listarPorMes(mes.getYear(), mes.getMonthValue()).size());

            demostrarCrud(stands, usuarios, reservas, pagos, notificaciones, transferencias);
        } catch (Exception e) {
            System.err.println("Error durante la ejecución: " + e.getMessage());
        }
    }

    /**
     * Recorre el CRUD completo con datos temporales y al final los borra,
     * para que la base de datos quede igual que antes de ejecutar.
     */
    private static void demostrarCrud(ControladorStand stands, ControladorUsuario usuarios,
                                      ControladorReserva reservas, ControladorPago pagos,
                                      ControladorNotificacion notificaciones,
                                      ControladorTransferencia transferencias) {
        String sufijo = String.valueOf(System.currentTimeMillis() % 100000);
        String standDemo = "DEMO-" + sufijo;
        String standBloqueado = "DEMO-B" + sufijo;
        Usuario usuario = null;
        Usuario propietario = null;

        try {
            // ---------------- CREATE ----------------
            System.out.println("\n===== CREATE =====");
            usuario = usuarios.registrar("Editorial Demo", "demo" + sufijo + "@feria.mx", "secreta123", "9991112233");
            System.out.println("Usuario creado con id " + usuario.id + " (contraseña guardada con hash + salt)");
            System.out.println("Login correcto: " + usuarios.iniciarSesion(usuario.correo, "secreta123").isPresent());
            System.out.println("Login incorrecto: " + usuarios.iniciarSesion(usuario.correo, "otra").isPresent());

            Stand s = new Stand();
            s.id = standDemo;
            s.largo = 3;
            s.ancho = 2;
            s.precio = 10000;
            stands.crear(s);
            System.out.println("Stand creado: " + s.id + " estado " + s.estado);

            Reserva r = reservas.reservar(usuario.id, standDemo);
            System.out.println("Reserva " + r.id + " creada, pagado 15% = $" + r.montoPagado
                    + ", fecha límite " + r.fechaLimite);
            System.out.println("Estado del stand: " + stands.consultar(standDemo).orElseThrow().estado);

            Notificacion n = notificaciones.crear(usuario.id, "RESERVA", "Reserva confirmada",
                    "Tu reserva del stand " + standDemo + " vence el " + r.fechaLimite + ".");
            System.out.println("Notificación creada con id " + n.id);

            // ---------------- UPDATE ----------------
            System.out.println("\n===== UPDATE =====");
            usuario.telefono = "9998887766";
            usuarios.actualizar(usuario);
            System.out.println("Teléfono actualizado: " + usuarios.consultar(usuario.id).orElseThrow().telefono);

            notificaciones.marcarLeida(n.id);
            System.out.println("Notificación leída: " + notificaciones.consultar(n.id).orElseThrow().leida);

            // ---------------- PAGOS (abonos a la reservación) ----------------
            System.out.println("\n===== PAGOS: abonos de la reservación " + r.id + " =====");
            System.out.println("Costo total del stand: $" + s.precio + " | fecha límite: " + r.fechaLimite
                    + " | saldo pendiente: $" + pagos.saldoPendiente(r.id));

            // CREATE: registro de pagos parciales de la editorial para SU reservación
            Pago abono1 = pagos.registrarPago(usuario.id, r.id, 2000, "ABONO");
            System.out.println("Abono registrado id " + abono1.id + " por $" + abono1.monto
                    + " | saldo pendiente: $" + pagos.saldoPendiente(r.id));
            Pago abono2 = pagos.registrarPago(usuario.id, r.id, 1500, "ABONO");
            System.out.println("Abono registrado id " + abono2.id + " por $" + abono2.monto
                    + " | saldo pendiente: $" + pagos.saldoPendiente(r.id));

            // UPDATE: corregir el monto de un abono (la reserva se recalcula)
            abono2.monto = 2500;
            pagos.actualizar(abono2);
            System.out.println("Abono " + abono2.id + " actualizado a $" + abono2.monto
                    + " | monto pagado en reserva: $" + reservas.consultar(r.id).orElseThrow().montoPagado);

            // READ: lista de pagos de una reservación de una editorial en específico
            System.out.println("Pagos de la reservación " + r.id + " de la editorial '"
                    + usuario.nombreUsuarioEditorial + "':");
            imprimirPagos(pagos.listarDeReservaDeEditorial(usuario.id, r.id));

            // READ: lista de pagos realizados en un mes específico
            YearMonth mesActual = YearMonth.now();
            System.out.println("Pagos realizados en " + mesActual + ":");
            imprimirPagos(pagos.listarPorMes(mesActual.getYear(), mesActual.getMonthValue()));

            // Casos que disparan excepciones a propósito
            try {
                pagos.registrarPago(usuario.id, r.id, 999999, "ABONO");
            } catch (IllegalArgumentException e) {
                System.out.println("Excepción esperada (monto excede saldo): " + e.getMessage());
            }
            try {
                pagos.registrarPago(usuario.id + 100000, r.id, 100, "ABONO");
            } catch (IllegalArgumentException e) {
                System.out.println("Excepción esperada (reserva de otra editorial): " + e.getMessage());
            }
            try {
                pagos.listarPorMes(mesActual.getYear(), 13);
            } catch (IllegalArgumentException e) {
                System.out.println("Excepción esperada (mes inválido): " + e.getMessage());
            }

            // Reserva de los datos de prueba (editorial 1, reserva 1, fecha límite 2026-11-25)
            try {
                System.out.println("Pagos de la reservación 1 de la editorial 1 (datos de prueba):");
                imprimirPagos(pagos.listarDeReservaDeEditorial(1, 1));
                pagos.registrarPago(1, 1, 100, "ABONO");
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("Excepción esperada (fecha límite vencida / sin datos de prueba): "
                        + e.getMessage());
            }

            // Liquidación: se cubre el total antes de la fecha límite
            double saldo = pagos.saldoPendiente(r.id);
            System.out.println("Saldo pendiente: $" + saldo);
            pagos.registrarPago(usuario.id, r.id, saldo, "LIQUIDACION");
            System.out.println("Reserva tras liquidar: " + reservas.consultar(r.id).orElseThrow().estado
                    + " | Stand: " + stands.consultar(standDemo).orElseThrow().estado
                    + " | total pagado: $" + pagos.totalConfirmado(pagos.listarDeReserva(r.id)));
            try {
                pagos.registrarPago(usuario.id, r.id, 100, "ABONO");
            } catch (IllegalStateException e) {
                System.out.println("Excepción esperada (reserva ya liquidada): " + e.getMessage());
            }

            // Flujo de transferencia de stand bloqueado
            propietario = usuarios.registrar("Propietario Original", "prop" + sufijo + "@feria.mx",
                    "secreta123", "9994445566");
            Stand b = new Stand();
            b.id = standBloqueado;
            b.largo = 4;
            b.ancho = 4;
            b.precio = 20000;
            b.estado = EstadoStand.BLOQUEADO;
            stands.crear(b);
            TransferenciaBloqueados t = transferencias.crear(standBloqueado, propietario.id);
            System.out.println("Transferencia creada con código " + t.codigoAcceso);
            String codigo = transferencias.registrarAcceso(t.id);
            System.out.println("Acceso registrado, stand ahora: " + stands.consultar(standBloqueado).orElseThrow().estado);
            try {
                reservas.reservar(usuario.id, standBloqueado);
            } catch (IllegalStateException e) {
                System.out.println("Otro usuario no puede reservarlo sin código: OK");
            }
            Reserva rt = transferencias.completar(standBloqueado, codigo, propietario.id);
            System.out.println("Transferencia completada, reserva " + rt.id + " | stand: "
                    + stands.consultar(standBloqueado).orElseThrow().estado
                    + " | transferencia: " + transferencias.consultar(t.id).orElseThrow().estado);

            // ---------------- DELETE ----------------
            System.out.println("\n===== DELETE =====");
            System.out.println("Transferencia eliminada: " + transferencias.eliminar(t.id));
            System.out.println("Reserva de transferencia eliminada: " + reservas.eliminar(rt.id));
            System.out.println("Notificación eliminada: " + notificaciones.eliminar(n.id));
            System.out.println("Pago (abono) " + abono1.id + " eliminado: " + pagos.eliminar(abono1.id)
                    + " | reserva vuelve a: " + reservas.consultar(r.id).orElseThrow().estado
                    + " | saldo pendiente: $" + pagos.saldoPendiente(r.id));
            System.out.println("Reserva eliminada (y sus pagos): " + reservas.eliminar(r.id));
        } catch (Exception e) {
            System.err.println("Error en la demostración: " + e.getMessage());
        } finally {
            limpiar(stands, usuarios, reservas, standDemo, standBloqueado, usuario, propietario);
        }
    }

    private static void imprimirPagos(List<Pago> lista) {
        if (lista.isEmpty()) {
            System.out.println("   (sin pagos)");
            return;
        }
        for (Pago p : lista) {
            System.out.println("   - Pago " + p.id + " | reserva " + p.reservaId + " | $" + p.monto
                    + " | " + p.fecha.toLocalDate() + " | " + p.tipo + (p.confirmado ? " | confirmado" : ""));
        }
    }

    private static void limpiar(ControladorStand stands, ControladorUsuario usuarios, ControladorReserva reservas,
                                String standDemo, String standBloqueado, Usuario usuario, Usuario propietario) {
        for (Usuario u : new Usuario[] {usuario, propietario}) {
            if (u == null) continue;
            for (Reserva r : reservas.listarDeUsuario(u.id)) reservas.eliminar(r.id);
        }
        for (String id : new String[] {standDemo, standBloqueado}) {
            stands.consultar(id).ifPresent(st -> {
                stands.cambiarEstado(id, EstadoStand.DISPONIBLE);
                System.out.println("Stand " + id + " eliminado: " + stands.eliminar(id));
            });
        }
        if (usuario != null) System.out.println("Usuario demo eliminado: " + usuarios.eliminar(usuario.id));
        if (propietario != null) System.out.println("Propietario demo eliminado: " + usuarios.eliminar(propietario.id));
    }
}
