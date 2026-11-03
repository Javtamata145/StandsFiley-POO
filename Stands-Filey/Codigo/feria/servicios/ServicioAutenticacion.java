package feria.servicios;

import feria.modelo.Usuario;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

public class ServicioAutenticacion {
    private Map<String, Usuario> usuarios = new HashMap<>();
    private Usuario sesion;
    private int siguienteId = 1;

    public Usuario registrarUsuario(String nombre, String correo, String contrasena, String telefono) {
        if (nombre == null || correo == null || contrasena == null || telefono == null
                || nombre.isBlank() || correo.isBlank() || contrasena.isBlank() || telefono.isBlank())
            throw new IllegalArgumentException("Todos los datos son obligatorios.");
        if (correoRegistrado(correo)) throw new IllegalArgumentException("El correo ya está registrado.");

        String salt = UUID.randomUUID().toString();
        Usuario usuario = new Usuario(siguienteId++, nombre, correo,
                hash(contrasena, salt), salt, telefono);
        if (!usuario.validarDatos()) throw new IllegalArgumentException("Los datos no son válidos.");
        usuarios.put(correo.toLowerCase(), usuario);
        return usuario;
    }

    public Usuario iniciarSesion(String correo, String contrasena) {
        Usuario u = usuarios.get(correo.toLowerCase());
        if (u != null && u.getContrasenaHash().equals(hash(contrasena, u.getSalt()))) {
            sesion = u;
            return u;
        }
        return null;
    }

    public boolean validarSesion() { return sesion != null; }
    public boolean correoRegistrado(String correo) {
        return correo != null && usuarios.containsKey(correo.toLowerCase());
    }
    public Usuario getSesion() { return sesion; }
    public void cerrarSesion() { sesion = null; }

    private String hash(String texto, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(md.digest((salt + texto).getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
