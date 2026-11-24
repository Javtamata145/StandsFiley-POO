package feria.servicios;

import feria.modelo.Usuario;
import feria.persistencia.dao.UsuarioDAO;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Lógica de negocio de usuarios: registro, inicio de sesión y CRUD.
 * Las contraseñas se guardan con hash PBKDF2 + salt (RNF-013).
 */
public class ServicioUsuario {
    private static final Pattern FORMATO_CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern FORMATO_TELEFONO = Pattern.compile("^\\+?[0-9]{10,15}$");
    private static final int ITERACIONES = 65_536;
    private static final int LONGITUD_HASH = 256;

    private final UsuarioDAO dao;
    private final SecureRandom aleatorio = new SecureRandom();

    public ServicioUsuario(UsuarioDAO dao) { this.dao = dao; }

    // ---------------- READ ----------------
    public List<Usuario> listarUsuarios() { return dao.listarTodos(); }
    public Optional<Usuario> buscarPorId(int id) { return dao.buscarPorId(id); }
    public Optional<Usuario> buscarPorCorreo(String correo) { return dao.buscarPorCorreo(correo); }

    // ---------------- CREATE ----------------
    /** Registra un usuario nuevo (RF-033 a RF-036). */
    public Usuario registrar(String nombre, String correo, String contrasena, String telefono) {
        validarDatos(nombre, correo, telefono);
        if (contrasena == null || contrasena.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        String correoNormalizado = correo.trim().toLowerCase();
        if (dao.buscarPorCorreo(correoNormalizado).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con el correo " + correoNormalizado + ".");
        }
        Usuario u = new Usuario();
        u.nombreUsuarioEditorial = nombre.trim();
        u.correo = correoNormalizado;
        u.telefono = telefono.trim();
        u.salt = generarSalt();
        u.contrasenaHash = calcularHash(contrasena, u.salt);
        return dao.insertar(u);
    }

    /** Inicio de sesión (RF-038). Devuelve el usuario si las credenciales son correctas. */
    public Optional<Usuario> iniciarSesion(String correo, String contrasena) {
        if (correo == null || contrasena == null) return Optional.empty();
        Optional<Usuario> usuario = dao.buscarPorCorreo(correo.trim().toLowerCase());
        if (usuario.isEmpty()) return Optional.empty();
        String hash = calcularHash(contrasena, usuario.get().salt);
        boolean coincide = MessageDigest.isEqual(hash.getBytes(), usuario.get().contrasenaHash.getBytes());
        return coincide ? usuario : Optional.empty();
    }

    // ---------------- UPDATE ----------------
    /** Actualiza nombre, correo y teléfono. */
    public Usuario actualizar(Usuario cambios) {
        Usuario actual = dao.buscarPorId(cambios.id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario " + cambios.id + "."));
        validarDatos(cambios.nombreUsuarioEditorial, cambios.correo, cambios.telefono);
        String correoNuevo = cambios.correo.trim().toLowerCase();
        Optional<Usuario> otro = dao.buscarPorCorreo(correoNuevo);
        if (otro.isPresent() && otro.get().id != actual.id) {
            throw new IllegalArgumentException("El correo " + correoNuevo + " ya pertenece a otra cuenta.");
        }
        actual.nombreUsuarioEditorial = cambios.nombreUsuarioEditorial.trim();
        actual.correo = correoNuevo;
        actual.telefono = cambios.telefono.trim();
        dao.actualizar(actual);
        return actual;
    }

    public void cambiarContrasena(int id, String contrasenaActual, String contrasenaNueva) {
        Usuario u = dao.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario " + id + "."));
        if (iniciarSesion(u.correo, contrasenaActual).isEmpty()) {
            throw new IllegalArgumentException("La contraseña actual no es correcta.");
        }
        if (contrasenaNueva == null || contrasenaNueva.length() < 6) {
            throw new IllegalArgumentException("La contraseña nueva debe tener al menos 6 caracteres.");
        }
        String salt = generarSalt();
        dao.actualizarContrasena(id, calcularHash(contrasenaNueva, salt), salt);
    }

    // ---------------- DELETE ----------------
    /**
     * Elimina la cuenta. Sus notificaciones se borran en cascada; si tiene
     * reservas o transferencias la BD rechaza la operación.
     */
    public boolean eliminar(int id) {
        return dao.eliminar(id);
    }

    // ---------------- Auxiliares ----------------
    private void validarDatos(String nombre, String correo, String telefono) {
        if (estaVacio(nombre) || estaVacio(correo) || estaVacio(telefono)) {
            throw new IllegalArgumentException("Todos los datos del registro son obligatorios.");
        }
        if (!FORMATO_CORREO.matcher(correo.trim()).matches()) {
            throw new IllegalArgumentException("El correo no tiene un formato válido.");
        }
        if (!FORMATO_TELEFONO.matcher(telefono.trim()).matches()) {
            throw new IllegalArgumentException("El teléfono debe tener de 10 a 15 dígitos.");
        }
    }

    private static boolean estaVacio(String s) { return s == null || s.isBlank(); }

    private String generarSalt() {
        byte[] salt = new byte[16];
        aleatorio.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String calcularHash(String contrasena, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(),
                    Base64.getDecoder().decode(salt), ITERACIONES, LONGITUD_HASH);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña.", e);
        }
    }
}
