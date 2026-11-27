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
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/** Lógica de negocio de pagos y CRUD. */
public class ServicioPago {
    private static final double TOLERANCIA = 0.01;

    private final PagoDAO dao;
    private final ReservaDAO reservaDAO;
    private final StandDAO standDAO;
    private final PasarelaPago pasarela;

    public ServicioPago(PagoDAO dao, ReservaDAO reservaDAO, StandDAO standDAO, PasarelaPago pasarela) {
        this.dao = dao;
        this.reservaDAO = reservaDAO;
        this.standDAO = standDAO;
        this.pasarela = pasarela;
    }

    // ---------------- READ ----------------
    public List<Pago> listarTodos() { return dao.listarTodos(); }
    public List<Pago> listarDeReserva(int reservaId) { return dao.listarPorReserva(reservaId); }
    public Optional<Pago> buscarPorId(int id) { return dao.buscarPorId(id); }

    /** Lista de pagos realizados en un mes específico (mes 1-12). */
    public List<Pago> listarPorMes(int anio, int mes) {
        YearMonth periodo;
        try {
            periodo = YearMonth.of(anio, mes);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Mes no válido: " + mes + "/" + anio + " (el mes va de 1 a 12).", e);
        }
        return dao.listarPorMes(periodo);
    }

    /** Todos los pagos de todas las reservaciones de una editorial. */
    public List<Pago> listarDeEditorial(int usuarioId) {
        return dao.listarPorEditorial(usuarioId);
    }

    /**
     * Lista de pagos realizados para una reservación de una Editorial en específico.
     * Valida que la reservación exista y que pertenezca a esa editorial.
     */
    public List<Pago> listarDeReservaDeEditorial(int usuarioId, int reservaId) {
        validarPropietario(usuarioId, obtenerReserva(reservaId));
        return dao.listarPorEditorialYReserva(usuarioId, reservaId);
    }

    public double sumarPagosConfirmados(List<Pago> pagos) {
        return pagos.stream().filter(p -> p.confirmado).mapToDouble(p -> p.monto).sum();
    }

    /** Cuánto falta por pagar de una reserva. */
    public double calcularSaldoPendiente(int reservaId) {
        Reserva r = obtenerReserva(reservaId);
        Stand s = obtenerStand(r.standId);
        return Math.max(0, s.precio - dao.sumarConfirmados(reservaId));
    }

    // ---------------- CREATE ----------------
    /**
     * Registro de un pago (abono) de una editorial para una de SUS reservaciones.
     * Valida que la reservación pertenezca a la editorial y luego registra el pago.
     */
    public Pago registrarPago(int usuarioId, int reservaId, double monto, String tipo) {
        validarPropietario(usuarioId, obtenerReserva(reservaId));
        return registrarPago(reservaId, monto, tipo);
    }

    /**
     * Registra un abono o liquidación de una reserva ACTIVA.
     * Si con este pago se cubre el precio total, la reserva pasa a COMPLETADA
     * y el stand a OCUPADO (RF-013, RF-015).
     */
    public Pago registrarPago(int reservaId, double monto, String tipo) {
        Reserva r = obtenerReserva(reservaId);
        if (r.estado != EstadoReserva.ACTIVA) {
            throw new IllegalStateException("La reserva " + reservaId + " no está activa (" + r.estado + ").");
        }
        if (r.fechaLimite != null && LocalDate.now().isAfter(r.fechaLimite)) {
            throw new IllegalStateException("La fecha límite de la reserva " + reservaId + " ("
                    + r.fechaLimite + ") ya pasó: no se aceptan más pagos.");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0.");
        }
        double saldo = calcularSaldoPendiente(reservaId);
        if (monto > saldo + TOLERANCIA) {
            throw new IllegalArgumentException("El monto excede el saldo pendiente ($" + saldo + ").");
        }
        if (!pasarela.cobrar(monto)) {
            throw new IllegalStateException("La pasarela de pago rechazó el cobro.");
        }
        Pago p = new Pago();
        p.reservaId = reservaId;
        p.monto = monto;
        p.fecha = LocalDateTime.now();
        p.tipo = (tipo == null || tipo.isBlank()) ? "ABONO" : tipo.trim().toUpperCase();
        p.confirmado = true;
        dao.insertar(p);
        recalcularReserva(reservaId);
        return p;
    }

    // ---------------- UPDATE ----------------
    public Pago actualizar(Pago pago) {
        dao.buscarPorId(pago.id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el pago " + pago.id + "."));
        if (pago.monto <= 0) throw new IllegalArgumentException("El monto debe ser mayor a 0.");
        obtenerReserva(pago.reservaId);
        dao.actualizar(pago);
        recalcularReserva(pago.reservaId);
        return pago;
    }

    public boolean confirmar(int id) {
        Pago p = dao.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el pago " + id + "."));
        boolean ok = dao.confirmar(id);
        recalcularReserva(p.reservaId);
        return ok;
    }

    // ---------------- DELETE ----------------
    public boolean eliminar(int id) {
        Optional<Pago> p = dao.buscarPorId(id);
        if (p.isEmpty()) return false;
        boolean ok = dao.eliminar(id);
        recalcularReserva(p.get().reservaId);
        return ok;
    }

    // ---------------- Auxiliares ----------------
    /**
     * Sincroniza monto_pagado de la reserva con la suma de pagos confirmados
     * y actualiza los estados de reserva y stand (RNF-004).
     */
    private void recalcularReserva(int reservaId) {
        Reserva r = obtenerReserva(reservaId);
        Stand s = obtenerStand(r.standId);
        double total = dao.sumarConfirmados(reservaId);
        reservaDAO.actualizarMonto(reservaId, total);

        boolean liquidada = total + TOLERANCIA >= s.precio;
        if (liquidada && r.estado == EstadoReserva.ACTIVA) {
            reservaDAO.cambiarEstado(reservaId, EstadoReserva.COMPLETADA);
            standDAO.cambiarEstado(s.id, EstadoStand.OCUPADO);
        } else if (!liquidada && r.estado == EstadoReserva.COMPLETADA) {
            reservaDAO.cambiarEstado(reservaId, EstadoReserva.ACTIVA);
            standDAO.cambiarEstado(s.id, EstadoStand.RESERVADO);
        }
    }

    private void validarPropietario(int usuarioId, Reserva r) {
        if (r.usuarioId != usuarioId) {
            throw new IllegalArgumentException("La reserva " + r.id + " no pertenece a la editorial " + usuarioId + ".");
        }
    }

    private Reserva obtenerReserva(int id) {
        return reservaDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva " + id + "."));
    }

    private Stand obtenerStand(String id) {
        return standDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el stand " + id + "."));
    }
}
