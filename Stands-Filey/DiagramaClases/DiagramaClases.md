# Diagrama de clases del sistema de stands de la feria

Este documento contiene el diagrama de clases del sistema, organizado por capas para representar su arquitectura actual. Se utiliza una estructura basada en MVC, complementada con servicios, interfaces y una capa de persistencia mediante JDBC.

Los diagramas están escritos en [Mermaid](https://mermaid.js.org/), que GitHub muestra automáticamente.

## Índice

1. Cómo leer el diagrama
2. Parte 1: Modelos
3. Parte 2: Servicios, interfaces e implementaciones
4. Parte 3: Controladores
5. Parte 4: Persistencia y acceso a datos
6. Parte 5: Punto de entrada

---

## 1. Cómo leer el diagrama

### Tipos de flecha

| Símbolo | Nombre | Significado |
|---|---|---|
| `-->` | Asociación / dependencia | Una clase utiliza o depende de otra. |
| `--|>` | Herencia | Una clase hereda de otra. |
| `<|..` | Implementación | Una clase implementa una interfaz. |

### Visibilidad

| Símbolo | Significado |
|---|---|
| `+` | Público |
| `-` | Privado |

---

## 2. Parte 1: Modelos

Los modelos representan los datos principales del sistema. Actualmente, estas clases contienen atributos y no incluyen métodos de lógica de negocio.

```mermaid
classDiagram
direction TB

class Usuario {
  +int id
  +String nombreUsuarioEditorial
  +String correo
  +String contrasenaHash
  +String salt
  +String telefono
}

class Stand {
  +String id
  +double largo
  +double ancho
  +double precio
  +EstadoStand estado
  +Integer transferenciaId
}

class Reserva {
  +int id
  +int usuarioId
  +String standId
  +LocalDate fechaReserva
  +LocalDate fechaLimite
  +double montoPagado
  +EstadoReserva estado
}

class Pago {
  +int id
  +int reservaId
  +double monto
  +LocalDateTime fecha
  +String tipo
  +boolean confirmado
}

class TransferenciaBloqueados {
  +int id
  +String codigoAcceso
  +EstadoTransferencia estado
  +LocalDateTime fechaAcceso
  +int usuarioId
  +Integer reservaId
  +String standId
}

class Notificacion {
  +int id
  +int usuarioId
  +String tipo
  +String asunto
  +String mensaje
  +LocalDateTime fecha
  +boolean leida
}

class EstadoStand {
  <<enumeration>>
  DISPONIBLE
  RESERVADO
  OCUPADO
  BLOQUEADO
}

class EstadoReserva {
  <<enumeration>>
  ACTIVA
  COMPLETADA
  VENCIDA
}

class EstadoTransferencia {
  <<enumeration>>
  PENDIENTE
  ACEPTADA
  RECHAZADA
  EXPIRADA
}

Stand --> EstadoStand
Reserva --> EstadoReserva
TransferenciaBloqueados --> EstadoTransferencia
```

### Características de los modelos

| Clase | Descripción |
|---|---|
| `Usuario` | Representa a una persona registrada en el sistema. |
| `Stand` | Representa un espacio disponible dentro de la feria. |
| `Reserva` | Almacena la información de una reservación. |
| `Pago` | Representa un pago asociado a una reserva. |
| `TransferenciaBloqueados` | Almacena la información de una transferencia de stands bloqueados. |
| `Notificacion` | Representa una notificación destinada a un usuario. |
| `EstadoStand` | Define los posibles estados de un stand. |
| `EstadoReserva` | Define los posibles estados de una reserva. |
| `EstadoTransferencia` | Define los posibles estados de una transferencia. |

**Nota:** Las relaciones entre entidades se representan mediante identificadores, como `usuarioId`, `standId` y `reservaId`. Esto permite relacionar los registros al recuperarlos desde la base de datos.

---

## 3. Parte 2: Servicios, interfaces e implementaciones

Los servicios contienen las operaciones y reglas del negocio. Se encargan de coordinar las operaciones que necesitan los controladores y de comunicarse con los DAO para recuperar información.

### Diagrama de servicios

```mermaid
classDiagram
direction TB

class ServicioStand {
  +listarStands()
  +listarPorEstado(estado)
  +listarDisponibles()
  +buscarStand(id)
  +calcularArea(stand)
  +puedeReservarse(stand)
  +crear(stand)
  +actualizar(stand)
  +cambiarEstado(id, estado)
  +eliminar(id)
}

class ServicioUsuario {
  +listarUsuarios()
  +buscarPorId(id)
  +buscarPorCorreo(correo)
  +registrar(nombre, correo, contrasena, telefono)
  +iniciarSesion(correo, contrasena)
  +actualizar(usuario)
  +cambiarContrasena(id, actual, nueva)
  +eliminar(id)
}

class ServicioReserva {
  +listarReservas()
  +listarDeUsuario(usuarioId)
  +buscarPorId(id)
  +estaVencida(reserva)
  +reservar(usuarioId, standId)
  +actualizar(reserva)
  +liberarVencidas()
  +eliminar(id)
}

class ServicioPago {
  +listarTodos()
  +listarDeReserva(reservaId)
  +buscarPorId(id)
  +calcularSaldoPendiente(reservaId)
  +registrarPago(reservaId, monto, tipo)
  +actualizar(pago)
  +confirmar(id)
  +eliminar(id)
}

class ServicioNotificacion {
  +listarTodas()
  +listarDeUsuario(usuarioId)
  +listarNoLeidas(usuarioId)
  +buscarPorId(id)
  +crear(usuarioId, tipo, asunto, mensaje)
  +actualizar(notificacion)
  +marcarLeida(id)
  +eliminar(id)
}

class ServicioTransferencia {
  +listarTransferencias()
  +buscarPorId(id)
  +buscarPorCodigo(codigo)
  +validarCodigo(standId, codigo)
  +crear(standId, usuarioId)
  +registrarAcceso(transferenciaId)
  +completar(standId, codigo, usuarioId)
  +actualizar(transferencia)
  +eliminar(id)
}

class TareasProgramadas {
}

ServicioReserva --> PasarelaPago
ServicioPago --> PasarelaPago
ServicioTransferencia --> ServicioReserva
```

### Interfaces

Las interfaces definen contratos que deben cumplir las clases que las implementan. Esto permite mantener separadas las reglas generales de sus implementaciones concretas.

```mermaid
classDiagram
direction TB

class CanalNotificacion {
  <<interface>>
  +enviar(notificacion)
}

class PasarelaPago {
  <<interface>>
  +cobrar(monto)
  +consultarPago(referencia)
}

class PoliticaDescuento {
  <<interface>>
  +calcular(precio, fechaPago)
}
```

### Implementaciones

```mermaid
classDiagram
direction TB

class DescuentoProntoPago

class CanalCorreo

class CanalPagina

class PasarelaPagoSimulada

PoliticaDescuento <|.. DescuentoProntoPago
CanalNotificacion <|.. CanalCorreo
CanalNotificacion <|.. CanalPagina
PasarelaPago <|.. PasarelaPagoSimulada
```

### Descripción de las clases

| Clase | Responsabilidad |
|---|---|
| `ServicioStand` | CRUD de stands con validación de dimensiones, precio y estado. |
| `ServicioUsuario` | Registro con validaciones, inicio de sesión y CRUD de usuarios. Guarda contraseñas con hash PBKDF2 + salt. |
| `ServicioReserva` | Reserva con pago del 15%, plazo de 1 mes, control de concurrencia, liberación de vencidas y CRUD. |
| `ServicioNotificacion` | CRUD de notificaciones y consulta de no leídas. |
| `ServicioPago` | Registro de abonos, cálculo de saldo y actualización automática de reserva y stand al liquidar. |
| `ServicioTransferencia` | Genera códigos únicos, registra el acceso del propietario, valida el código y completa la transferencia. |
| `TareasProgramadas` | Agrupa tareas que pueden ejecutarse automáticamente. |
| `CanalNotificacion` | Define el contrato para los canales de notificación. |
| `PasarelaPago` | Define el contrato para el procesamiento de pagos. |
| `PoliticaDescuento` | Define el contrato para calcular descuentos. |
| `CanalCorreo` | Implementa las notificaciones mediante correo. |
| `CanalPagina` | Implementa las notificaciones dentro de la página. |
| `DescuentoProntoPago` | Implementa una política de descuento por pronto pago. |
| `PasarelaPagoSimulada` | Simula el procesamiento de pagos. |

---

## 4. Parte 3: Controladores

Los controladores reciben las solicitudes y las delegan a los servicios correspondientes. No deben contener directamente las consultas SQL ni concentrar la lógica del negocio.

```mermaid
classDiagram
direction TB

class ControladorStand {
  +listar()
  +listarDisponibles()
  +consultar(id)
  +crear(stand)
  +actualizar(stand)
  +cambiarEstado(id, estado)
  +eliminar(id)
}

class ControladorUsuario {
  +listar()
  +consultar(id)
  +registrar(...)
  +iniciarSesion(correo, contrasena)
  +actualizar(usuario)
  +cambiarContrasena(id, actual, nueva)
  +eliminar(id)
}

class ControladorReserva {
  +listar()
  +consultar(id)
  +reservar(usuarioId, standId)
  +actualizar(reserva)
  +liberarVencidas()
  +eliminar(id)
}

class ControladorNotificacion {
  +listar()
  +listarDeUsuario(usuarioId)
  +crear(...)
  +actualizar(notificacion)
  +marcarLeida(id)
  +eliminar(id)
}

class ControladorPago {
  +listar()
  +listarDeReserva(reservaId)
  +saldoPendiente(reservaId)
  +registrarPago(reservaId, monto, tipo)
  +actualizar(pago)
  +confirmar(id)
  +eliminar(id)
}

class ControladorTransferencia {
  +listar()
  +consultar(id)
  +crear(standId, usuarioId)
  +registrarAcceso(id)
  +completar(standId, codigo, usuarioId)
  +actualizar(transferencia)
  +eliminar(id)
}

ControladorStand --> ServicioStand
ControladorUsuario --> ServicioUsuario
ControladorReserva --> ServicioReserva
ControladorNotificacion --> ServicioNotificacion
ControladorPago --> ServicioPago
ControladorTransferencia --> ServicioTransferencia
```

### Dependencias

| Controlador | Servicio utilizado | Responsabilidad |
|---|---|---|
| `ControladorStand` | `ServicioStand` | Gestionar solicitudes relacionadas con stands. |
| `ControladorUsuario` | `ServicioUsuario` | Gestionar solicitudes relacionadas con usuarios. |
| `ControladorReserva` | `ServicioReserva` | Gestionar solicitudes relacionadas con reservas. |
| `ControladorNotificacion` | `ServicioNotificacion` | Gestionar solicitudes relacionadas con notificaciones. |
| `ControladorPago` | `ServicioPago` | Gestionar solicitudes relacionadas con pagos. |
| `ControladorTransferencia` | `ServicioTransferencia` | Gestionar solicitudes relacionadas con transferencias. |

---

## 5. Parte 4: Persistencia y acceso a datos

Esta capa se encarga de la comunicación con la base de datos mediante JDBC. Los DAO contienen las consultas SQL y los servicios los utilizan para recuperar la información.

### Diagrama de persistencia

```mermaid
classDiagram
direction TB

class ConexionBD {
  +obtenerConexion() Connection
}

class ConsultasSQL {
  <<final>>
  Usuarios
  Stands
  Reservas
  Pagos
  Notificaciones
  Transferencias
}

class DAOBase {
  <<abstract>>
  #consultarLista(sql, parametros, mapeador)
  #consultarUno(sql, parametros, mapeador)
  #insertarYObtenerId(sql, parametros)
  #ejecutar(sql, parametros)
}

class StandDAO {
  +listarTodos()
  +listarPorEstado(estado)
  +buscarPorId(id)
  +insertar(stand)
  +actualizar(stand)
  +cambiarEstado(id, estado)
  +cambiarEstadoSiEsta(id, esperado, nuevo)
  +asignarTransferencia(standId, transferenciaId)
  +eliminar(id)
}

class UsuarioDAO {
  +listarTodos()
  +buscarPorId(id)
  +buscarPorCorreo(correo)
  +insertar(usuario)
  +actualizar(usuario)
  +actualizarContrasena(id, hash, salt)
  +eliminar(id)
}

class ReservaDAO {
  +listarTodos()
  +listarPorUsuario(usuarioId)
  +listarPorStand(standId)
  +listarActivasVencidas(hoy)
  +buscarPorId(id)
  +insertar(reserva)
  +actualizar(reserva)
  +actualizarMonto(id, monto)
  +cambiarEstado(id, estado)
  +eliminar(id)
}

class NotificacionDAO {
  +listarTodas()
  +listarPorUsuario(usuarioId)
  +listarNoLeidas(usuarioId)
  +buscarPorId(id)
  +insertar(notificacion)
  +actualizar(notificacion)
  +marcarLeida(id)
  +eliminar(id)
}

class PagoDAO {
  +listarTodos()
  +listarPorReserva(reservaId)
  +buscarPorId(id)
  +sumarConfirmados(reservaId)
  +insertar(pago)
  +actualizar(pago)
  +confirmar(id)
  +eliminar(id)
}

class TransferenciaDAO {
  +listarTodas()
  +buscarPorId(id)
  +buscarPorCodigo(codigo)
  +listarPorStand(standId)
  +insertar(transferencia)
  +actualizar(transferencia)
  +cambiarEstado(id, estado)
  +registrarAcceso(id, fecha)
  +asignarReserva(id, reservaId)
  +eliminar(id)
}

DAOBase <|-- StandDAO
DAOBase <|-- UsuarioDAO
DAOBase <|-- ReservaDAO
DAOBase <|-- NotificacionDAO
DAOBase <|-- PagoDAO
DAOBase <|-- TransferenciaDAO

DAOBase --> ConexionBD
StandDAO --> ConsultasSQL
UsuarioDAO --> ConsultasSQL
ReservaDAO --> ConsultasSQL
NotificacionDAO --> ConsultasSQL
PagoDAO --> ConsultasSQL
TransferenciaDAO --> ConsultasSQL
```

### Dependencias entre servicios y DAO

```mermaid
classDiagram
direction TB

ServicioStand --> StandDAO
ServicioUsuario --> UsuarioDAO
ServicioReserva --> ReservaDAO
ServicioReserva --> StandDAO
ServicioReserva --> PagoDAO
ServicioNotificacion --> NotificacionDAO
ServicioPago --> PagoDAO
ServicioPago --> ReservaDAO
ServicioPago --> StandDAO
ServicioTransferencia --> TransferenciaDAO
ServicioTransferencia --> StandDAO
```

### Responsabilidades

| Clase | Responsabilidad |
|---|---|
| `ConexionBD` | Establece y proporciona conexiones con la base de datos. |
| `ConsultasSQL` | **Única clase que contiene las sentencias SQL** (SELECT, INSERT, UPDATE y DELETE), agrupadas por tabla. |
| `DAOBase` | Clase padre que ejecuta las consultas con JDBC: abre la conexión, asigna parámetros, recorre resultados y traduce errores. |
| `StandDAO` | CRUD de la tabla `stands`. |
| `UsuarioDAO` | CRUD de la tabla `usuarios`. |
| `ReservaDAO` | CRUD de la tabla `reservas`. |
| `NotificacionDAO` | CRUD de la tabla `notificaciones`. |
| `PagoDAO` | CRUD de la tabla `pagos`. |
| `TransferenciaDAO` | CRUD de la tabla `transferencias`. |

**Importante:** En esta versión los DAO implementan el CRUD completo (`SELECT`, `INSERT`, `UPDATE` y `DELETE`). Ningún DAO escribe SQL directamente: todas las sentencias se toman de `ConsultasSQL`, y la ejecución JDBC repetida vive en `DAOBase`.

---

## 6. Parte 5: Punto de entrada

`Main.java` es el punto de entrada de la aplicación. Se encarga de inicializar los componentes necesarios para comenzar la ejecución del sistema.

```mermaid
classDiagram
direction TB

class Main

Main --> ControladorStand
Main --> ControladorUsuario
Main --> ControladorReserva
Main --> ControladorNotificacion
Main --> ControladorPago
Main --> ControladorTransferencia
```

`Main` verifica la conexión, arma las capas y ejecuta una demostración del CRUD completo con datos temporales que borra al terminar, por lo que la base de datos queda igual que antes.

---

## 7. Resumen de la arquitectura

| Capa | Responsabilidad principal |
|---|---|
| Modelo | Representar los datos del sistema. |
| Controladores | Recibir y coordinar solicitudes. |
| Servicios | Contener la lógica del negocio. |
| Interfaces | Definir contratos de comportamiento. |
| Implementaciones | Proporcionar implementaciones concretas. |
| Persistencia | Gestionar la conexión y el acceso a la base de datos. |
| DAO | Ejecutar consultas SQL. |

La comunicación principal del sistema sigue este flujo:

```mermaid
flowchart TD
    A[Main] --> B[Controlador]
    B --> C[Servicio]
    C --> D[DAO]
    D --> Q[ConsultasSQL]
    D --> E[ConexionBD]
    E --> F[(Base de datos)]
    F --> E
    E --> D
    D --> C
    C --> B
```

Esta organización permite separar responsabilidades y facilita el mantenimiento y la futura ampliación del sistema.