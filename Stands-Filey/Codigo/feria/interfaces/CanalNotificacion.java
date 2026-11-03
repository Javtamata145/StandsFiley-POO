package feria.interfaces;

import feria.modelo.Notificacion;

public interface CanalNotificacion {
    void enviar(Notificacion notificacion, String destino);
}
