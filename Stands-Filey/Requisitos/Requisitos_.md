# Requisitos del Sistema de Stands de la Feria
 
## 1. Descripción general
 
Sistema web que representa la página de la feria. Permite visualizar los stands y su estado, reservarlos con un pago inicial, recibir notificaciones por correo y dentro de la página sobre fechas y pagos, y transferir stands previamente vendidos a sus propietarios originales mediante un código de acceso.
 
## 2. Estados de los stands
 
| Estado | Definición |
|---|---|
| **Disponible** | Stand que nadie ha reservado. |
| **Reservado** | Stand con un pago inicial (15%) pero que aún no se paga por completo. |
| **Ocupado** | Stand vendido y pagado por completo. |
| **Bloqueado** | Stand vendido con anterioridad a un propietario original; no está a la venta. |
 
---
 
## 3. Requisitos funcionales
 
### 3.1 Módulo de stands
 
| ID | Requisito |
|---|---|
| RF-001 | El sistema debe mostrar el conjunto de stands de la feria para que el usuario pueda elegir. |
| RF-002 | El sistema debe mostrar, por cada stand: ID, dimensiones y metros cuadrados. |
| RF-003 | El sistema debe manejar exactamente cuatro estados para un stand: Disponible, Reservado, Ocupado y Bloqueado. |
| RF-004 | El sistema debe mostrar el estado actual de cada stand de forma visible. |
| RF-005 | El sistema debe permitir al usuario seleccionar un stand. |
| RF-006 | El sistema debe permitir reservar únicamente stands en estado Disponible. |
| RF-007 | El sistema no debe permitir reservar ni comprar stands en estado Reservado, Ocupado o Bloqueado. |
| RF-008 | El sistema debe mostrar el precio de cada stand, definido según sus características. |
 
### 3.2 Reserva y pago
 
| ID | Requisito |
|---|---|
| RF-009 | El sistema debe permitir al usuario reservar un stand pagando el 15% de su precio. |
| RF-010 | El sistema debe cambiar el stand a estado Reservado una vez confirmado el pago de reserva. |
| RF-011 | El sistema debe asignar un plazo de 1 mes, contado desde la reserva, para cumplir con el pago. |
| RF-012 | El sistema debe liberar automáticamente el stand (volver a Disponible) si el usuario no paga dentro del plazo. |
| RF-013 | El sistema debe cambiar el stand a Ocupado cuando se complete el pago total. |
| RF-014 | El sistema debe aplicar descuento por pronto pago a quien pague antes de la fecha establecida. |
| RF-015 | El sistema debe verificar qué stands ya recibieron su pago para actualizar su estado en la página. |
 
### 3.3 Módulo de notificaciones (correo electrónico y página)
 
| ID | Requisito |
|---|---|
| RF-016 | El sistema debe enviar un correo de confirmación cuando el usuario realice una reserva. |
| RF-017 | El correo de confirmación debe indicar la fecha límite de pago. |
| RF-018 | El sistema debe enviar recordatorios de la fecha límite antes de que venza el plazo. |
| RF-019 | El sistema debe enviar una notificación cuando el pago esté a punto de vencer. |
| RF-020 | El sistema debe notificar sobre los descuentos por pronto pago disponibles. |
| RF-021 | El sistema debe notificar al usuario cuando su reserva expire y el stand sea liberado. |
| RF-022 | El sistema debe mostrar dentro de la página las mismas notificaciones que se envían por correo. |
| RF-023 | El sistema debe contar con una sección de notificaciones donde el usuario pueda consultarlas al ingresar a la página. |
 
### 3.4 Módulo de transferencia (stands bloqueados)
 
| ID | Requisito |
|---|---|
| RF-024 | El sistema debe generar un código de acceso único y específico por cada stand bloqueado. |
| RF-025 | El sistema debe enviar el código de acceso al propietario original. |
| RF-026 | El sistema debe enviar un correo al propietario original que le otorgue permiso para acceder a la transferencia del stand que le corresponde. |
| RF-027 | El sistema debe permitir al propietario original acceder desde ese correo para completar la transferencia de su stand. |
| RF-028 | El sistema debe proporcionar el código de acceso al propietario original una vez que acceda a completar la transferencia. |
| RF-029 | El sistema debe cambiar el stand Bloqueado a estado Disponible en el mapa de stands cuando el propietario original acceda a completar la transferencia. |
| RF-030 | El sistema debe permitir ingresar el código de acceso en el stand ahora Disponible para poder realizar la transferencia. |
| RF-031 | El sistema debe habilitar el pago del stand únicamente si el código ingresado es válido y corresponde a ese stand. |
| RF-032 | El sistema debe cambiar el stand a estado Reservado una vez que el propietario original realice el pago. |
 
### 3.5 Módulo de registro e inicio de sesión
 
| ID | Requisito |
|---|---|
| RF-033 | El sistema debe permitir al usuario registrarse con nombre de usuario/editorial, correo electrónico, contraseña y número telefónico. |
| RF-034 | El sistema debe exigir que todos los datos del registro estén completos para crear la cuenta. |
| RF-035 | El sistema debe validar que el correo y el número telefónico tengan un formato válido. |
| RF-036 | El sistema no debe permitir registrar una cuenta con un correo que ya esté registrado. |
| RF-037 | El sistema debe solicitar el inicio de sesión cada vez que el usuario ingrese a la página. |
| RF-038 | El sistema debe permitir iniciar sesión con las credenciales registradas por el usuario. |
| RF-039 | El sistema debe permitir el acceso a los módulos de stands, reservas, notificaciones y transferencia únicamente a usuarios con sesión iniciada. |
| RF-040 | El sistema debe enviar las notificaciones al correo registrado por el usuario. |
 
---
 
## 4. Requisitos no funcionales
 
| ID | Categoría | Requisito |
|---|---|---|
| RNF-001 | Seguridad | Los códigos de acceso deben ser únicos, difíciles de adivinar y válidos solo para su stand. |
| RNF-002 | Seguridad | Los pagos deben procesarse mediante una pasarela de pago segura; el sistema no debe almacenar datos de tarjetas. |
| RNF-003 | Integridad | El sistema debe evitar que dos usuarios reserven el mismo stand al mismo tiempo (control de concurrencia). |
| RNF-004 | Integridad | El estado de un stand debe actualizarse de forma inmediata tras cada reserva, pago, expiración o transferencia. |
| RNF-005 | Rendimiento | La vista de stands debe cargar en menos de **3 segundos** con el total de stands de la feria. |
| RNF-006 | Disponibilidad | El sistema debe estar disponible al menos **99%** del tiempo, sobre todo cerca de las fechas límite. |
| RNF-007 | Confiabilidad | Los correos deben enviarse en menos de **5 minutos** tras el evento que los dispara. |
| RNF-008 | Confiabilidad | La liberación de reservas vencidas debe ejecutarse de forma automática (tarea programada). |
| RNF-009 | Usabilidad | El estado de cada stand debe distinguirse sin depender solo del color (ej. también con etiqueta o ícono). |
| RNF-010 | Usabilidad | El proceso de reserva debe completarse en un máximo de **5 pasos**. |
| RNF-011 | Compatibilidad | La interfaz debe ser responsiva y funcionar en los navegadores modernos de escritorio y móvil. |
| RNF-012 | Auditoría | El sistema debe registrar los cambios de estado de los stands, los pagos y los accesos al módulo de transferencia. |
| RNF-013 | Seguridad | Las contraseñas de los usuarios deben almacenarse cifradas con hash y salt, nunca en texto plano. |
 
---
 
## 5. Restricciones y reglas de negocio
 
| ID | Regla |
|---|---|
| RN-001 | El pago para reservar un stand es el 15% de su precio. |
| RN-002 | El plazo para cumplir con el pago tras reservar es de 1 mes. |
| RN-003 | Los stands Bloqueados fueron vendidos previamente y no están a la venta. |
| RN-004 | Las notificaciones se envían por correo electrónico y también se muestran dentro de la página. |
 
