package feria.implementaciones;

import feria.interfaces.CanalNotificacion;
import feria.modelo.Notificacion;

public class CanalPagina implements CanalNotificacion {
    @Override
    public void enviar(Notificacion n, String destino) {
        System.out.println("[PÁGINA de usuario " + destino + "] " + n.getAsunto());
    }
}
