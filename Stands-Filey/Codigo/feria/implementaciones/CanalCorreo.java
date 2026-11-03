package feria.implementaciones;

import feria.interfaces.CanalNotificacion;
import feria.modelo.Notificacion;

public class CanalCorreo implements CanalNotificacion {
    @Override
    public void enviar(Notificacion n, String destino) {
        System.out.println("[CORREO a " + destino + "] " + n.getAsunto() + ": " + n.getMensaje());
    }
}
