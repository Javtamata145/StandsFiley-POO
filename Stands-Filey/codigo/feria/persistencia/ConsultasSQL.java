package feria.persistencia;

/**
 * ÚNICA clase del sistema que contiene las sentencias SQL.
 *
 * Todas las consultas (SELECT, INSERT, UPDATE y DELETE) de la base de datos
 * stands_filey están centralizadas aquí, agrupadas por tabla. Los DAO no
 * escriben SQL: solo toman la consulta de esta clase, asignan los parámetros
 * y la ejecutan mediante los métodos de DAOBase.
 *
 * Si cambia una tabla o una columna, solo hay que modificar este archivo.
 */
public final class ConsultasSQL {

    private ConsultasSQL() {}

    // =====================================================================
    // Tabla: usuarios
    // =====================================================================
    public static final class Usuarios {
        private Usuarios() {}

        private static final String COLUMNAS =
                "id, nombre_usuario_editorial, correo, contrasena_hash, salt, telefono";

        public static final String LISTAR_TODOS =
                "SELECT " + COLUMNAS + " FROM usuarios ORDER BY id";
        public static final String BUSCAR_POR_ID =
                "SELECT " + COLUMNAS + " FROM usuarios WHERE id = ?";
        public static final String BUSCAR_POR_CORREO =
                "SELECT " + COLUMNAS + " FROM usuarios WHERE correo = ?";

        public static final String INSERTAR =
                "INSERT INTO usuarios (nombre_usuario_editorial, correo, contrasena_hash, salt, telefono) "
                + "VALUES (?, ?, ?, ?, ?)";
        public static final String ACTUALIZAR =
                "UPDATE usuarios SET nombre_usuario_editorial = ?, correo = ?, telefono = ? WHERE id = ?";
        public static final String ACTUALIZAR_CONTRASENA =
                "UPDATE usuarios SET contrasena_hash = ?, salt = ? WHERE id = ?";
        public static final String ELIMINAR =
                "DELETE FROM usuarios WHERE id = ?";
    }

    // =====================================================================
    // Tabla: stands
    // =====================================================================
    public static final class Stands {
        private Stands() {}

        private static final String COLUMNAS =
                "id, largo, ancho, precio, estado, transferencia_id";

        public static final String LISTAR_TODOS =
                "SELECT " + COLUMNAS + " FROM stands ORDER BY id";
        public static final String LISTAR_POR_ESTADO =
                "SELECT " + COLUMNAS + " FROM stands WHERE estado = ? ORDER BY id";
        public static final String BUSCAR_POR_ID =
                "SELECT " + COLUMNAS + " FROM stands WHERE id = ?";

        public static final String INSERTAR =
                "INSERT INTO stands (id, largo, ancho, precio, estado, transferencia_id) VALUES (?, ?, ?, ?, ?, ?)";
        public static final String ACTUALIZAR =
                "UPDATE stands SET largo = ?, ancho = ?, precio = ?, estado = ?, transferencia_id = ? WHERE id = ?";
        public static final String CAMBIAR_ESTADO =
                "UPDATE stands SET estado = ? WHERE id = ?";
        /**
         * Cambia el estado solo si el stand sigue en el estado esperado.
         * Es atómico en la BD, así dos usuarios no pueden reservar el mismo
         * stand al mismo tiempo (RNF-003).
         */
        public static final String CAMBIAR_ESTADO_SI_ESTA =
                "UPDATE stands SET estado = ? WHERE id = ? AND estado = ?";
        public static final String ASIGNAR_TRANSFERENCIA =
                "UPDATE stands SET transferencia_id = ? WHERE id = ?";
        public static final String ELIMINAR =
                "DELETE FROM stands WHERE id = ?";
    }

    // =====================================================================
    // Tabla: reservas
    // =====================================================================
    public static final class Reservas {
        private Reservas() {}

        private static final String COLUMNAS =
                "id, usuario_id, stand_id, fecha_reserva, fecha_limite, monto_pagado, estado";

        public static final String LISTAR_TODOS =
                "SELECT " + COLUMNAS + " FROM reservas ORDER BY id";
        public static final String LISTAR_POR_USUARIO =
                "SELECT " + COLUMNAS + " FROM reservas WHERE usuario_id = ? ORDER BY id";
        public static final String LISTAR_POR_STAND =
                "SELECT " + COLUMNAS + " FROM reservas WHERE stand_id = ? ORDER BY id";
        /** Reservas ACTIVAS cuya fecha límite ya pasó (para liberar stands, RF-012). */
        public static final String LISTAR_ACTIVAS_VENCIDAS =
                "SELECT " + COLUMNAS + " FROM reservas WHERE estado = 'ACTIVA' AND fecha_limite < ? ORDER BY id";
        public static final String BUSCAR_POR_ID =
                "SELECT " + COLUMNAS + " FROM reservas WHERE id = ?";

        public static final String INSERTAR =
                "INSERT INTO reservas (usuario_id, stand_id, fecha_reserva, fecha_limite, monto_pagado, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        public static final String ACTUALIZAR =
                "UPDATE reservas SET usuario_id = ?, stand_id = ?, fecha_reserva = ?, fecha_limite = ?, "
                + "monto_pagado = ?, estado = ? WHERE id = ?";
        public static final String ACTUALIZAR_MONTO =
                "UPDATE reservas SET monto_pagado = ? WHERE id = ?";
        public static final String CAMBIAR_ESTADO =
                "UPDATE reservas SET estado = ? WHERE id = ?";
        public static final String ELIMINAR =
                "DELETE FROM reservas WHERE id = ?";
    }

    // =====================================================================
    // Tabla: pagos
    // =====================================================================
    public static final class Pagos {
        private Pagos() {}

        private static final String COLUMNAS = "id, reserva_id, monto, fecha, tipo, confirmado";

        public static final String LISTAR_TODOS =
                "SELECT " + COLUMNAS + " FROM pagos ORDER BY id";
        public static final String LISTAR_POR_RESERVA =
                "SELECT " + COLUMNAS + " FROM pagos WHERE reserva_id = ? ORDER BY fecha";
        public static final String BUSCAR_POR_ID =
                "SELECT " + COLUMNAS + " FROM pagos WHERE id = ?";
        public static final String SUMAR_CONFIRMADOS =
                "SELECT COALESCE(SUM(monto), 0) AS total FROM pagos WHERE reserva_id = ? AND confirmado = 1";

        public static final String INSERTAR =
                "INSERT INTO pagos (reserva_id, monto, fecha, tipo, confirmado) VALUES (?, ?, ?, ?, ?)";
        public static final String ACTUALIZAR =
                "UPDATE pagos SET reserva_id = ?, monto = ?, fecha = ?, tipo = ?, confirmado = ? WHERE id = ?";
        public static final String CONFIRMAR =
                "UPDATE pagos SET confirmado = 1 WHERE id = ?";
        public static final String ELIMINAR =
                "DELETE FROM pagos WHERE id = ?";
    }

    // =====================================================================
    // Tabla: notificaciones
    // =====================================================================
    public static final class Notificaciones {
        private Notificaciones() {}

        private static final String COLUMNAS = "id, usuario_id, tipo, asunto, mensaje, fecha, leida";

        public static final String LISTAR_TODAS =
                "SELECT " + COLUMNAS + " FROM notificaciones ORDER BY fecha DESC";
        public static final String LISTAR_POR_USUARIO =
                "SELECT " + COLUMNAS + " FROM notificaciones WHERE usuario_id = ? ORDER BY fecha DESC";
        public static final String LISTAR_NO_LEIDAS =
                "SELECT " + COLUMNAS + " FROM notificaciones WHERE usuario_id = ? AND leida = 0 ORDER BY fecha DESC";
        public static final String BUSCAR_POR_ID =
                "SELECT " + COLUMNAS + " FROM notificaciones WHERE id = ?";

        public static final String INSERTAR =
                "INSERT INTO notificaciones (usuario_id, tipo, asunto, mensaje, fecha, leida) VALUES (?, ?, ?, ?, ?, ?)";
        public static final String ACTUALIZAR =
                "UPDATE notificaciones SET usuario_id = ?, tipo = ?, asunto = ?, mensaje = ?, fecha = ?, leida = ? "
                + "WHERE id = ?";
        public static final String MARCAR_LEIDA =
                "UPDATE notificaciones SET leida = 1 WHERE id = ?";
        public static final String ELIMINAR =
                "DELETE FROM notificaciones WHERE id = ?";
    }

    // =====================================================================
    // Tabla: transferencias
    // =====================================================================
    public static final class Transferencias {
        private Transferencias() {}

        private static final String COLUMNAS =
                "id, codigo_acceso, estado, fecha_acceso, usuario_id, reserva_id, stand_id";

        public static final String LISTAR_TODAS =
                "SELECT " + COLUMNAS + " FROM transferencias ORDER BY id";
        public static final String BUSCAR_POR_ID =
                "SELECT " + COLUMNAS + " FROM transferencias WHERE id = ?";
        public static final String BUSCAR_POR_CODIGO =
                "SELECT " + COLUMNAS + " FROM transferencias WHERE codigo_acceso = ?";
        public static final String BUSCAR_POR_STAND =
                "SELECT " + COLUMNAS + " FROM transferencias WHERE stand_id = ? ORDER BY id DESC";

        public static final String INSERTAR =
                "INSERT INTO transferencias (codigo_acceso, estado, fecha_acceso, usuario_id, reserva_id, stand_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        public static final String ACTUALIZAR =
                "UPDATE transferencias SET codigo_acceso = ?, estado = ?, fecha_acceso = ?, usuario_id = ?, "
                + "reserva_id = ?, stand_id = ? WHERE id = ?";
        public static final String CAMBIAR_ESTADO =
                "UPDATE transferencias SET estado = ? WHERE id = ?";
        public static final String REGISTRAR_ACCESO =
                "UPDATE transferencias SET fecha_acceso = ? WHERE id = ?";
        public static final String ASIGNAR_RESERVA =
                "UPDATE transferencias SET reserva_id = ? WHERE id = ?";
        public static final String ELIMINAR =
                "DELETE FROM transferencias WHERE id = ?";
    }
}
