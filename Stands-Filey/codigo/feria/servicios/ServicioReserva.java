package feria.servicios;

import feria.interfaces.PasarelaPago;
import feria.modelo.EstadoReserva;
import feria.modelo.EstadoStand;
import feria.modelo.Pago;
import feria.modelo.Reserva;
import feria.modelo.Stand;
import feria.persistencia.dao.PagoDAO;
import feria.persistencia.dao.ReservaDAO;
import feria.persistencia.dao.StandDAO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Lógica de negocio de reservas y CRUD. */
public class ServicioReserva {
    /** RN-001: el pago de reserva es el 15% del precio. */
    public static final double PORCENTAJE_RESERVA = 0.15;
    /** RN-002: el plazo de pago es de 1 mes. */
    public static final int PLAZO_MESES = 1;

    private final ReservaDAO dao;
    private final StandDAO standDAO;
    private final PagoDAO pagoDAO;
    private final PasarelaPago pasarela;

    public ServicioReserva(ReservaDAO dao, StandDAO standDAO, PagoDAO pagoDAO, PasarelaPago pasarela) {
        this.dao = dao;
        this.standDAO = standDAO;
        this.pagoDAO = pagoDAO;
        this.pasarela = pasarela;
    }

    // ---------------- READ ----------------
    public List<Reserva> listarReservas() { return dao.listarTodos(); }
    public List<Reserva> listarDeUsuario(int usuarioId) { return dao.listarPorUsuario(usuarioId); }
    public Optional<Reserva> buscarPorId(int id) { return dao.buscarPorId(id); }

    public boolean estaVencida(Reserva reserva) {
        return reserva.estado == EstadoReserva.ACTIVA
                && reserva.fechaLimite != null && LocalDate.now().isAfter(reserva.fechaLimite);
    }

    // ---------------- CREATE ----------------
    /**
     * Reserva un stand Disponible cobrando el 15% (RF-006, RF-009, RF-010, RF-011).
     * Guarda la reserva, el pago inicial y cambia el stand a RESERVADO.
     */
    public Reserva reservar(int usuarioId, String standId) {
        return reservarInterno(usuarioId, standId, false);
    }

    /**
     * Usado también por ServicioTransferencia: si el stand tiene una transferencia
     * pendiente, solo se permite reservarlo cuando el código ya fue validado.
     */
    Reserva reservarInterno(int usuarioId, String standId, boolean codigoValidado) {
        Stand stand = standDAO.buscarPorId(standId)
                .orElseThrow(() -> new IllegalArgumentException("No existe el stand " + standId + "."));
        if (stand.estado != EstadoStand.DISPONIBLE) {
            throw new IllegalStateException("El stand " + standId + " no está disponible (" + stand.estado + ").");
        }
        if (stand.transferenciaId != null && !codigoValidado) {
            throw new IllegalStateException("El stand " + standId
                    + " está en transferencia: se requiere el código de acceso del propietario original.");
        }

        double montoInicial = Math.round(stand.precio * PORCENTAJE_RESERVA * 100.0) / 100.0;
        if (!pasarela.cobrar(montoInicial)) {
            throw new IllegalStateException("La pasarela de pago rechazó el cobro de reserva.");
        }

        // Cambio atómico DISPONIBLE -> RESERVADO: si otro usuario se adelantó, falla (RNF-003).
        if (!standDAO.cambiarEstadoSiEsta(standId, EstadoStand.DISPONIBLE, EstadoStand.RESERVADO)) {
            throw new IllegalStateException("El stand " + standId + " acaba de ser reservado por otro usuario.");
        }

        try {
            Reserva r = new Reserva();
            r.usuarioId = usuarioId;
            r.standId = standId;
            r.fechaReserva = LocalDate.now();
            r.fechaLimite = r.fechaReserva.plusMonths(PLAZO_MESES);
            r.montoPagado = montoInicial;
            r.estado = EstadoReserva.ACTIVA;
            dao.insertar(r);

            Pago p = new Pago();
            p.reservaId = r.id;
            p.monto = montoInicial;
            p.fecha = LocalDateTime.now();
            p.tipo = "RESERVA";
            p.confirmado = true;
            pagoDAO.insertar(p);
            return r;
        } catch (RuntimeException e) {
            // Si algo falla después de apartar el stand, se libera de nuevo.
            standDAO.cambiarEstado(standId, EstadoStand.DISPONIBLE);
            throw e;
        }
    }

    // ---------------- UPDATE ----------------
    public Reserva actualizar(Reserva reserva) {
        dao.buscarPorId(reserva.id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva " + reserva.id + "."));
        if (reserva.fechaReserva == null || reserva.fechaLimite == null
                || reserva.fechaLimite.isBefore(reserva.fechaReserva)) {
            throw new IllegalArgumentException("Las fechas de la reserva no son válidas.");
        }
        if (reserva.montoPagado < 0) {
            throw new IllegalArgumentException("El monto pagado no puede ser negativo.");
        }
        dao.actualizar(reserva);
        return reserva;
    }

    /** RF-012 / RNF-008: marca como VENCIDAS las reservas fuera de plazo y libera sus stands. */
    public List<Reserva> liberarVencidas() {
        List<Reserva> vencidas = dao.listarActivasVencidas(LocalDate.now());
        for (Reserva r : vencidas) {
            dao.cambiarEstado(r.id, EstadoReserva.VENCIDA);
            standDAO.cambiarEstadoSiEsta(r.standId, EstadoStand.RESERVADO, EstadoStand.DISPONIBLE);
            r.estado = EstadoReserva.VENCIDA;
        }
        return vencidas;
    }

    // ---------------- DELETE ----------------
    /**
     * Elimina la reserva (sus pagos se borran en cascada).
     * Si estaba ACTIVA, el stand vuelve a Disponible.
     */
    public boolean eliminar(int id) {
        Optional<Reserva> reserva = dao.buscarPorId(id);
        if (reserva.isEmpty()) return false;
        boolean eliminada = dao.eliminar(id);
        if (eliminada && reserva.get().estado == EstadoReserva.ACTIVA) {
            standDAO.cambiarEstadoSiEsta(reserva.get().standId, EstadoStand.RESERVADO, EstadoStand.DISPONIBLE);
        }
        return eliminada;
    }
}
