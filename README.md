# Sistema de Gestión de Stands de Feria

## Descripción
Sistema destinado a gestionar la disponibilidad, reservación, pago y transferencia de stands de la feria FILEY, facilitando la administración y el seguimiento de las operaciones realizadas por los usuarios.

## Funcionalidades principales

- **Gestión de stands:** consulta de identificadores, dimensiones, superficie, precio y disponibilidad.
- **Reservaciones:** reserva de stands disponibles mediante un pago inicial del 15%, con un plazo de un mes para completar el pago.
- **Gestión de pagos:** registro y verificación de pagos, aplicación de descuentos por pronto pago y actualización del estado de los stands.
- **Notificaciones:** envío de avisos por correo electrónico y dentro del sistema sobre reservaciones, fechas límite, vencimientos y descuentos.
- **Transferencia de stands:** transferencia de stands bloqueados mediante códigos únicos y validación de permisos.
- **Gestión de usuarios:** registro e inicio de sesión con validación de datos y acceso restringido a los módulos correspondientes.

## Requisitos no funcionales

El sistema contempla aspectos de seguridad, integridad de la información, concurrencia, accesibilidad, facilidad de uso, disponibilidad y rendimiento. También considera la protección de contraseñas y la automatización de tareas relacionadas con las reservaciones.

## Tecnologías y arquitectura

- Java
- Programación Orientada a Objetos (POO).
- Arquitectura basada en el patrón MVC.

## Consideraciones

Esta versión corresponde a una implementación académica simplificada. Utiliza almacenamiento en memoria y simulaciones para ciertos procesos, como los pagos y las notificaciones. Por lo tanto, algunos requisitos de producción se encuentran contemplados en el diseño, pero no están implementados completamente.