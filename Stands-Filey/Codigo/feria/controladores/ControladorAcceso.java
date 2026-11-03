package feria.controladores;

import feria.modelo.Usuario;
import feria.servicios.ServicioAutenticacion;

public class ControladorAcceso {
    private ServicioAutenticacion servicio;

    public ControladorAcceso(ServicioAutenticacion servicio) { this.servicio = servicio; }
    public Usuario registrar(String nombre, String correo, String contrasena, String telefono) {
        return servicio.registrarUsuario(nombre, correo, contrasena, telefono);
    }
    public Usuario iniciarSesion(String correo, String contrasena) {
        return servicio.iniciarSesion(correo, contrasena);
    }
}
