package feria.servicios;

import feria.modelo.EstadoStand;
import feria.modelo.EstadoTransferencia;
import feria.modelo.Reserva;
import feria.modelo.Stand;
import feria.modelo.TransferenciaBloqueados;
import feria.persistencia.dao.StandDAO;
import feria.persistencia.dao.TransferenciaDAO;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Lógica de negocio de transferencia de stands bloqueados (RF-024 a RF-032) y CRUD. */
public class ServicioTransferencia {
    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LONGITUD_CODIGO = 12;

    private final TransferenciaDAO dao;
    private final StandDAO standDAO;
    private final ServicioReserva servicioReserva;
    private final SecureRandom aleatorio = new SecureRandom();

    public ServicioTransferencia(TransferenciaDAO dao, StandDAO standDAO, ServicioReserva servicioReserva) {
        this.dao = dao;
        this.standDAO = standDAO;
        this.servicioReserva = servicioReserva;
    }

    // ---------------- READ ----------------
    public List<TransferenciaBloqueados> listarTransferencias() { return dao.listarTodas(); }
    public Optional<TransferenciaBloqueados> buscarPorId(int id) { return dao.buscarPorId(id); }
    public Optional<TransferenciaBloqueados> buscarPorCodigo(String codigo) { return dao.buscarPorCodigo(codigo); }

    /** RF-031: el código es válido solo si existe, está PENDIENTE y corresponde a ese stand. */
    public boolean validarCodigo(String standId, String codigo) {
        if (codigo == null || standId == null) return false;
        return dao.buscarPorCodigo(codigo.trim())
                .filter(t -> t.estado == EstadoTransferencia.PENDIENTE)
                .filter(t -> t.standId.equals(standId))
                .isPresent();
    }

    // ---------------- CREATE ----------------
    /** RF-024: crea la transferencia de un stand BLOQUEADO con un código único y difícil de adivinar. */
    public TransferenciaBloqueados crear(String standId, int usuarioPropietarioId) {
        Stand stand = standDAO.buscarPorId(standId)
                .orElseThrow(() -> new IllegalArgumentException("No existe el stand " + standId + "."));
        if (stand.estado != EstadoStand.BLOQUEADO) {
            throw new IllegalStateException("Solo se crean transferencias para stands BLOQUEADOS.");
        }
        TransferenciaBloqueados t = new TransferenciaBloqueados();
        t.codigoAcceso = generarCodigoUnico();
        t.estado = EstadoTransferencia.PENDIENTE;
        t.usuarioId = usuarioPropietarioId;
        t.standId = standId;
        dao.insertar(t);
        standDAO.asignarTransferencia(standId, t.id);
        return t;
    }

    // ---------------- UPDATE ----------------
    /**
     * RF-027 a RF-029: el propietario original entra desde su correo.
     * Se registra el acceso, el stand pasa de BLOQUEADO a DISPONIBLE
     * y se le devuelve su código de acceso.
     */
    public String registrarAcceso(int transferenciaId) {
        TransferenciaBloqueados t = obtener(transferenciaId);
        if (t.estado != EstadoTransferencia.PENDIENTE) {
            throw new IllegalStateException("La transferencia ya no está pendiente (" + t.estado + ").");
        }
        dao.registrarAcceso(t.id, LocalDateTime.now());
        standDAO.cambiarEstadoSiEsta(t.standId, EstadoStand.BLOQUEADO, EstadoStand.DISPONIBLE);
        return t.codigoAcceso;
    }

    /**
     * RF-030 a RF-032: el propietario ingresa su código en el stand y paga el 15%.
     * La transferencia queda ACEPTADA y el stand RESERVADO.
     */
    public Reserva completar(String standId, String codigo, int usuarioId) {
        if (!validarCodigo(standId, codigo)) {
            throw new IllegalArgumentException("El código no es válido para el stand " + standId + ".");
        }
        TransferenciaBloqueados t = dao.buscarPorCodigo(codigo.trim()).orElseThrow();
        if (t.usuarioId != usuarioId) {
            throw new IllegalArgumentException("El código no pertenece a este usuario.");
        }
        if (t.fechaAcceso == null) {
            throw new IllegalStateException("El propietario debe acceder primero desde el correo de transferencia.");
        }
        Reserva reserva = servicioReserva.reservarInterno(usuarioId, standId, true);
        dao.cambiarEstado(t.id, EstadoTransferencia.ACEPTADA);
        dao.asignarReserva(t.id, reserva.id);
        standDAO.asignarTransferencia(standId, null);
        return reserva;
    }

    public TransferenciaBloqueados actualizar(TransferenciaBloqueados t) {
        obtener(t.id);
        if (t.codigoAcceso == null || t.codigoAcceso.isBlank() || t.estado == null) {
            throw new IllegalArgumentException("La transferencia debe tener código y estado.");
        }
        dao.actualizar(t);
        return t;
    }

    public boolean cambiarEstado(int id, EstadoTransferencia nuevo) { return dao.cambiarEstado(id, nuevo); }

    // ---------------- DELETE ----------------
    /** El stand queda sin transferencia asociada (ON DELETE SET NULL). */
    public boolean eliminar(int id) { return dao.eliminar(id); }

    // ---------------- Auxiliares ----------------
    private TransferenciaBloqueados obtener(int id) {
        return dao.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la transferencia " + id + "."));
    }

    /** RNF-001: código aleatorio criptográficamente seguro y sin repetir. */
    private String generarCodigoUnico() {
        String codigo;
        do {
            StringBuilder sb = new StringBuilder("TRF-");
            for (int i = 0; i < LONGITUD_CODIGO; i++) {
                sb.append(ALFABETO.charAt(aleatorio.nextInt(ALFABETO.length())));
            }
            codigo = sb.toString();
        } while (dao.buscarPorCodigo(codigo).isPresent());
        return codigo;
    }
}
