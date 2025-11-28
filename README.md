## PROYECTO MyDAI: AgroManager

---

## Índice

1. Requisitos
2. Implementación BD
3. Casos de Uso
4. Diagrama (Entidades)

---

# 1. Requisitos
## 🌱 AgroManager

![LOGO](img/logo.png)

## 👥 Integrantes

| Nombre                             | DNI       | Foto                                   |
|------------------------------------|-----------|----------------------------------------|
| **Diego Durán Barroso**            | 20969286W | <img src="img/diego.png" width="120"/> |
| **Hugo Sánchez de la Roda Rivera** | 50489290N | <img src="img/hugo.png" width="120"/>  |



## 📝 Eslogan

> **“Sembramos organización, cosechamos resultados.”**



## 📖 Resumen

La aplicación web **AgroManager** debe permitir a los responsables de fincas gestionar trabajadores, controlar su
jornada laboral, generar nóminas y conocer en tiempo real la ubicación de los empleados y el estado de las fincas.



## 📌 Descripción

AgroManager es una aplicación web que busca simplificar la gestión agrícola centralizando en un solo sistema las
principales tareas de organización de trabajadores y fincas. El sistema permite dar de **alta y baja empleados**,
asignar contratos y relacionarlos con las fincas. Además, incorpora un **módulo de control de jornada** con fichajes,
horas trabajadas y tareas realizadas. El cálculo de **nóminas automáticas** se genera según salario base, horas trabajadas
y pluses correspondientes. La aplicación integra **geolocalización en tiempo real** para conocer dónde está cada trabajador y ofrece la posibilidad
de **actualizar el estado de las fincas** desde el terreno.



## ✅ Funcionalidades, Requisitos, “Pliego de condiciones”

- 📋 El responsable debe poder **gestionar los trabajadores** (altas, bajas, contratos, asignaciones a fincas).
- ⏱️ Los empleados deben poder **fichar su jornada laboral**, indicando inicio y fin de la misma.
- 🕒 El sistema debe **registrar horas trabajadas y tareas realizadas** durante la jornada.
- 💰 El sistema debe permitir **generar nóminas automáticas**, teniendo en cuenta salario base, horas trabajadas y
  pluses.
- 🌾 Los trabajadores podrán **cambiar el estado de la finca** en la que trabajan (ejemplo: sembrada, en mantenimiento,
  lista para cosecha, etc.).
- 📱 La aplicación debe ser **responsive**, funcionando en dispositivos móviles y PC.
- 📊 Los responsables podrán **visualizar resúmenes** en paneles de control con la situación actual de trabajadores,
  nóminas y fincas.



## 🌟 Funcionalidades opcionales, recomendables o futuribles

- 📑 Mostrar al responsable **informes automáticos** con horas trabajadas por finca, productividad de trabajadores y
  estados de las fincas.
- 📂 Posibilidad de **exportar datos** en formatos estándar (Excel, PDF).
- 🔗 Integración con **sistemas externos de facturación o RRHH**.
- 🌍 Posibilidad de elegir el **idioma de la interfaz**.
- 🔔 Inclusión de **notificaciones automáticas** (ejemplo: fin de contrato, incidencias en fichajes, cambios de estado de
  finca).

---

# 2. Implementación BD 🧩

La base de datos de **AgroManager** se implementa mediante **Docker** para asegurar un entorno reproducible, aislado y fácil de desplegar.
El contenedor principal utiliza **MySQL** como sistema gestor de base de datos y se comunica con la aplicación **Spring Boot** a través de la red interna definida en el archivo `docker-compose.yml`.


### ⚙️ Pasos de despliegue

1. Abrir una terminal en la raíz del proyecto.
2. Ejecutar el siguiente comando para levantar los contenedores:

   ```bash
   docker compose up --build
   ```

3. Una vez iniciados los servicios, acceder al contenedor de MySQL:

   ```bash
   docker exec -it agromanager-mysql mysql -u root -p
   ```
   Contraseña: `root`

4. Conectarse a la base de datos y verificar las tablas generadas automáticamente por **JPA/Hibernate**:

   ```sql
   USE agromanager;
   SHOW TABLES;
   SELECT * FROM empleado;
   SELECT * FROM finca;
   ```


---

# 3. Casos de Uso 🧠

A continuación, se explican los **casos de uso** reflejados en el diagrama del sistema, diferenciando las acciones disponibles para **Gerente** y **Empleado**.

<img src="img/casoUso.png" width="762"/>

| Nº | Actor | Caso de Uso | Descripción breve |
|----|--------|--------------|------------------|
| **1** | **Usuario** | **Registrarse como gerente** | Permite crear un usuario con rol GERENTE para acceder por primera vez al sistema. |
| **2** | **Gerente** | **Registrar empleado** | Permite dar de alta a un nuevo trabajador en el sistema, introduciendo sus datos personales y laborales. |
| **3** | **Gerente** | **Actualizar datos de empleado** | Modifica la información existente de un empleado (nombre, DNI, activo, etc.). |
| **4** | **Gerente** | **Eliminar empleado** | Da de baja a un empleado y elimina en cascada sus asignaciones, fichajes, contratos, tareas y nóminas. |
| **5** | **Gerente** | **Asignar empleado a finca** | Asocia un trabajador a una finca con una fecha de inicio y fin opcional. |
| **6** | **Gerente** | **Cambiar estado de finca** | Actualiza el estado operativo de una finca (SEMBRADA, MANTENIMIENTO, BARBECHO…). |
| **7** | **Gerente** | **Crear contrato de trabajo** | Registra un contrato laboral con tipo, fechas, salario base y tarifa por hora. |
| **8** | **Gerente** | **Eliminar contrato de trabajo** | Elimina un contrato concreto y sus posibles dependencias. |
| **9** | **Gerente** | **Generar nómina mensual** | Calcula automáticamente la nómina del empleado en función del contrato y tareas realizadas. |
| **10** | **Gerente** | **Crear nuevas tareas** | Permite crear tareas directamente (sin que las registre el empleado). |
| **11** | **Gerente** | **Eliminar tareas** | Elimina tareas previamente registradas por el sistema o un empleado. |
| **12** | **Empleado** | **Registrar fichaje de inicio** | Registra el comienzo de la jornada laboral del empleado. |
| **13** | **Empleado** | **Registrar fichaje de fin** | Finaliza el fichaje iniciado, marcando el fin de la jornada laboral. |
| **14** | **Empleado** | **Registrar tarea realizada** | Permite registrar una tarea realizada con tipo y duración. |




# 4. Diagrama (Entidades) 🧭

El siguiente diagrama muestra el **modelo de datos principal** de AgroManager, donde se representan las entidades, relaciones y enumeraciones que conforman la base de datos.

<img src="img/dclases.png" width="1200"/>

### 📘 Descripción breve

- **Usuario**: clase base que almacena credenciales y el **rol** del sistema (`GERENTE` o `EMPLEADO`).
- **Gerente** y **Empleado**: extienden de `Usuario`.
  - El **Gerente** gestiona las fincas y empleados.
  - El **Empleado** realiza fichajes, tareas y genera nóminas.
- **Finca**: contiene información de cada terreno agrícola y su **estado** (`SEMBRADA`, `MANTENIMIENTO`, `LISTA_COSECHA`, `BARBECHO`).
- **Asignacion**: relaciona empleados con fincas y fechas de trabajo.
- **Contrato**: define el tipo (`TEMPORAL`, `INDEFINIDO`, `FIJO_DISCONTINUO`), fechas y salario base.
- **Fichaje**: registra el inicio y fin de jornada.
- **Tarea**: almacena actividades realizadas por el empleado.
- **Nomina**: se genera a partir del contrato y fichajes del empleado.