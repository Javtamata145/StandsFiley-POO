package feria.servicios;

import feria.interfaces.CanalNotificacion;
import feria.modelo.Notificacion;
import feria.modelo.Usuario;
import java.util.ArrayList;
import java.util.List;

public class ServicioNotificaciones {
    private List<CanalNotificacion> canales = new ArrayList<>();
    private int siguienteId = 1;

    public ServicioNotificaciones(CanalNotificacion... canales) {
        this.canales.addAll(List.of(canales));
    }

    public void notificar(Usuario usuario, String tipo, String asunto, String mensaje) {
        Notificacion n = new Notificacion(siguienteId++, tipo, asunto, mensaje);
        usuario.agregarNotificacion(n);
        for (CanalNotificacion canal : canales) {
            canal.enviar(n, usuario.getCorreo());
        }
    }

    public List<Notificacion> listarDeUsuario(Usuario usuario) {
        return usuario.getNotificaciones();
    }
}
