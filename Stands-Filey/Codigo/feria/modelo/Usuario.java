package feria.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Usuario {
    private int id;
    private String nombreUsuarioEditorial;
    private String correo;
    private String contrasenaHash;
    private String salt;
    private String telefono;
    private List<Notificacion> notificaciones = new ArrayList<>();

    public Usuario(int id, String nombreUsuarioEditorial, String correo,
                   String contrasenaHash, String salt, String telefono) {
        this.id = id;
        this.nombreUsuarioEditorial = nombreUsuarioEditorial;
        this.correo = correo;
        this.contrasenaHash = contrasenaHash;
        this.salt = salt;
        this.telefono = telefono;
    }

    public boolean validarDatos() {
        return nombreUsuarioEditorial != null && !nombreUsuarioEditorial.isBlank()
            && correo != null && Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$", correo)
            && telefono != null && Pattern.matches("^\\+?[0-9]{10,15}$", telefono)
            && contrasenaHash != null && !contrasenaHash.isBlank();
    }

    public void agregarNotificacion(Notificacion n) { notificaciones.add(n); }
    public int getId() { return id; }
    public String getNombreUsuarioEditorial() { return nombreUsuarioEditorial; }
    public String getCorreo() { return correo; }
    public String getContrasenaHash() { return contrasenaHash; }
    public String getSalt() { return salt; }
    public String getTelefono() { return telefono; }
    public List<Notificacion> getNotificaciones() { return notificaciones; }
}
