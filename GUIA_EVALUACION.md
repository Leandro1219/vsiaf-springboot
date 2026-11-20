# VSIAF 3.2 — Guía final para la evaluación con Swagger UI

## 1. Qué se va a mostrar

Se presenta un **backend académico** con **Java 21, Spring Boot, PostgreSQL y Swagger UI**, construido tomando como referencia las ventanas del programa boliviano VSIAF 3.2. **No se mostrará código fuente** ni se necesita una interfaz gráfica nueva.

La indicación del docente fue **desplegar, ejecutar y explicar UN endpoint** y después mostrar en el VSIAF original **dónde está la función, cómo se utiliza y por qué determinadas acciones no corresponden a endpoints**. No se debe repetir el mismo endpoint elegido por otro compañero.

- **Endpoint propuesto para ejecutar ante el docente:** `PATCH /api/oficinas/{id}/activar`.
- **Ventana VSIAF:** Oficinas y Responsables → botones superiores de **Oficina** → **Activar** (no confundir con Activar responsable, en la parte inferior).
- **Efecto:** estado de la oficina **INACTIVO → ACTIVO**. No elimina la oficina ni cambia automáticamente sus responsables.
- **Resultado esperado:** `200 OK`, `estado: "ACTIVO"`.
- Los demás endpoints del módulo están disponibles y fueron probados localmente, pero **no es necesario ejecutar cada uno ante el docente**.

**Despliegue:** en la conversación del docente se utilizó el verbo «deployar», pero no se especificó expresamente si se acepta una ejecución local durante una pantalla compartida o si se exige URL pública. Esta guía permite una demo **local**. Si el docente exige un enlace remoto, hay que desplegar el backend y conectar PostgreSQL en un entorno accesible (p. ej. Render); no se debe afirmar que ya se desplegó remotamente.

## 2. Qué cambió en esta versión

Swagger UI presenta **UNA SOLA documentación OpenAPI**, sin el selector artificial de los grupos `01`, `02`, `03`. Cada controlador conserva su ruta y se organiza por la ventana correspondiente:

1. **Oficinas y Responsables** (todas las operaciones juntas, incluida Activar oficina).
2. **Activos Fijos**.
3. **Grupos y Auxiliares**.
4. **Generar Reportes**.
5. **Administrar Unidad**.
6. **Administradores**.
7. **Respaldos BD**.
8. **Localización BD**.
9. **Iniciar sesión**.

No se duplicaron endpoints, no se crearon endpoints ficticios para botones de interfaz, y no se eliminó el código de los otros módulos. **Importante:** varios endpoints de otros módulos proceden del proyecto de referencia y **no han pasado las mismas pruebas funcionales que Oficinas y Responsables**; algunos están marcados como pendientes y pueden devolver HTTP `501 Not Implemented`.

El texto introductorio ahora distingue el avance comprobado de Oficina y Responsable del alcance parcial de los otros módulos. La API funciona con una base PostgreSQL **independiente** de la base del programa original VSIAF: no sincroniza cambios con la aplicación de escritorio.

## 3. Preparación local: IntelliJ y PostgreSQL

1. Mantén una copia de la carpeta anterior que ya funciona. Descomprime este ZIP en **otra carpeta** y ábrela en IntelliJ desde `build.gradle`.
2. Comprueba Java/JDK **21** y que PostgreSQL esté iniciado.
3. La base de pruebas es **`vsiaf_evaluacion`**. Si ya la creaste y probaste, puedes continuar utilizándola.
4. En `src/main/resources/application.properties` conserva tus parámetros de conexión funcionales. El valor predeterminado de URL es:

   ```properties
   spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/vsiaf_evaluacion}
   spring.datasource.username=${DB_USERNAME:postgres}
   spring.datasource.password=${DB_PASSWORD}
   ```

   La contraseña real debe configurarse localmente, **no subirla a GitHub ni incluirla en capturas**. Si en tu instalación anterior dejaste la URL directa, también puede utilizarse la misma URL sin `${...}`.

5. Para preparar datos **ficticios** (solo en una base de pruebas), activa temporalmente:

   ```properties
   spring.profiles.active=demo
   ```

   El inicializador `demo` reutiliza o crea una oficina ficticia y la restablece a **INACTIVO** cada vez que inicia la aplicación. No modifica las demás oficinas que hayas creado manualmente.
6. Ejecuta `Sis414ParcialApplication` y espera `Started Sis414ParcialApplication`. En la consola aparecerá un mensaje con `METODO: PATCH /api/oficinas/{id}/activar` y el **ID real** de esa oficina ficticia.
7. Abre **http://localhost:8080/swagger-ui.html**. Debes ver **una documentación única** y todos los módulos listados en la sección 2.

**Si en tu IntelliJ no funciona Run → Edit Configurations:** puedes configurar `spring.profiles.active=demo` como en el paso 5, conservando tu conexión local que ya funcionaba. El perfil demo no debe activarse en una base real o remota.

## 4. Relación de todos los botones de Oficinas y Responsables

| Parte en VSIAF | Acción visible | Qué se usa en la API | Observación |
|---|---|---|---|
| Oficina (arriba) | **Nuevo** y luego **Guardar** | `POST /api/oficinas` | Se envía nombre, observación y unidad; el código se asigna automáticamente y comienza INACTIVA |
| Oficina (arriba) | **Modificar** y luego **Guardar** | `PUT /api/oficinas/{id}` | Actualiza nombre y observación sin activar ni inactivar por sí solo |
| Oficina (arriba) | **Activar** | `PATCH /api/oficinas/{id}/activar` | **Único endpoint elegido para la demostración** |
| Oficina (arriba) | **Inactivar** | `PATCH /api/oficinas/{id}/inactivar` | Cambia estado sin borrar el registro |
| Oficina (selectores) | Ver oficinas y detalles | `GET /api/oficinas`, `GET /api/oficinas/{id}`, `GET /api/oficinas/{id}/detalle` | Consultas necesarias para presentar datos |
| Responsables (abajo) | **Nuevo** y luego **Guardar** | `POST /api/responsables` | Código automático; nombre, cargo, CI, Expedido y oficina; estado inicial INACTIVO |
| Responsables (abajo) | **Modificar** y luego **Guardar** | `PUT /api/responsables/{id}` | Cambia datos sin cambiar estado |
| Responsables (abajo) | **Activar** | `PATCH /api/responsables/{id}/activar` | Activa solo al responsable |
| Responsables (abajo) | **Inactivar** | `PATCH /api/responsables/{id}/inactivar` | Inactiva solo al responsable |
| Responsables (tabla) | Ver responsables | `GET /api/responsables`, `GET /api/responsables/{id}`, `GET /api/oficinas/{id}/responsables` | Consultas para las filas de la ventana |
| Responsables (Expedido) | Elegir un departamento | `GET /api/departamentos`, `GET /api/departamentos/{id}` | Nueve departamentos de Bolivia como opciones de catálogo |
| Ambas zonas | **Deshacer** | **No requiere endpoint** | Cancela cambios de la edición actual que todavía no se guardaron |
| Ventana | **Salir** | **No requiere endpoint** | Cierra la ventana, no modifica registros de negocio |
| Ambas zonas | Bloquear botones en gris durante una edición | **No requiere endpoint adicional** | Es comportamiento de la interfaz original, no una operación REST |

### Reglas que ya conocemos

- La ventana inicial puede aparecer vacía; se inicia alta con **Nuevo**.
- Un registro nuevo comienza **INACTIVO**. Se activa posteriormente con el botón respectivo.
- Los códigos los genera el backend automáticamente. En este trabajo se derivan del ID de la base, **sin afirmar que VSIAF use internamente el mismo algoritmo**.
- El CI del responsable exige al menos **8 dígitos numéricos**; nuestro backend limita el valor a **8–20 dígitos** por el modelo actual.
- **Expedido** no es texto libre: se selecciona un departamento de los nueve disponibles.
- **Comprobado por el usuario en Swagger:** puede activarse o inactivarse un **responsable** aunque su **oficina esté inactiva**. Los dos estados son independientes en esta API.

## 5. Cómo mostrar UN endpoint ante el ingeniero

**Preparación:** con el perfil `demo` activo, reinicia Spring Boot para dejar **INACTIVA solo la oficina de demostración**. Anota el ID que aparece en la consola; no asumas que siempre será 1.

**Demostración en Swagger UI:**

1. En `http://localhost:8080/swagger-ui.html`, abre **Oficinas y Responsables**.
2. Localiza **`PATCH /api/oficinas/{id}/activar`** (se distingue por su descripción de demostración).
3. Pulsa **Try it out**, introduce el ID ficticio mostrado en la consola y pulsa **Execute**.
4. Señala el `Response code: 200` y el `Response body` con `estado: "ACTIVO"`.
5. Explica que `PATCH` **modifica parcialmente un recurso existente**: cambia su estado sin borrar la oficina ni sustituir todos sus campos.
6. Abre VSIAF original → **Oficinas y Responsables** → zona superior **OFICINA** → botón **Activar**; relaciona las dos acciones y explica brevemente las otras opciones de oficina y responsable.

**Try it out y Cancel:** no es necesario pulsar **Cancel** antes de usar otro endpoint. `Cancel` sale del modo edición de esa operación, **pero no deshace** una petición ya ejecutada ni revierte lo guardado en PostgreSQL. Para presentar ordenadamente, se recomienda cerrar los controles abiertos antes de empezar.

### Guion oral breve

> Ingeniero, utilizamos una API REST creada con Spring Boot y PostgreSQL para representar funcionalidades de VSIAF 3.2. En Swagger están agrupadas por módulo. Elegimos el endpoint `PATCH /api/oficinas/{id}/activar`, porque corresponde al botón Activar de la sección Oficina. Una oficina nueva se registra inicialmente INACTIVA; para activarla, indicamos su ID y ejecutamos la petición PATCH. La respuesta 200 confirma que se procesó correctamente y podemos ver el estado ACTIVO. PATCH es apropiado porque modifica únicamente el estado del registro. Ahora, en VSIAF original, la función está en Oficinas y Responsables, en los botones superiores. Nuevo y Modificar preparan los datos y Guardar confirma; en nuestra API se representan por POST y PUT. Deshacer, Salir y los botones grises pertenecen a la interfaz y no necesitan endpoints propios. El backend académico usa una base independiente y no cambia automáticamente los datos del VSIAF de escritorio.

### Preguntas habituales

- **¿Por qué PATCH?** Porque se modifica un campo específico de una oficina existente, su estado.
- **¿Por qué no DELETE?** Inactivar no significa eliminar; conserva los datos del registro.
- **¿Qué hacen POST y PUT?** POST crea y guarda un registro nuevo; PUT actualiza y guarda un registro existente.
- **¿Por qué no hay endpoint para Salir o Deshacer?** Son acciones de la interfaz de escritorio; no corresponden a operaciones de negocio persistentes en el backend.
- **¿Puede activarse un responsable de una oficina inactiva?** Sí, así funcionó en la prueba del backend; activar un responsable no activa la oficina y viceversa.
- **¿Por qué no se muestra el código Java?** El requisito de la exposición es demostrar una petición en Swagger y su correspondencia en VSIAF, no revisar el código fuente.

## 6. Funcionalidad ya probada y lo que no debe afirmarse

El usuario ejecutó en su equipo y confirmó las siguientes pruebas de esta familia de código: POST nueva oficina (`201`), PUT modificar oficina (`200`), PATCH activar/inactivar oficina (`200`), POST nuevo responsable (`201`), PUT modificar responsable (`200`), PATCH activar/inactivar responsable (`200`), GET de oficinas, responsables, grilla y nueve departamentos (`200`), y rechazo de CI de siete dígitos (`400`). También verificó anteriormente una transición de estado de oficina en PostgreSQL. **Esas pruebas se hicieron sobre la versión anterior**, que conservamos como base; esta versión reorganiza principalmente Swagger y las descripciones, pero debe arrancarse en el equipo para confirmar que la documentación unificada se muestra correctamente.

No se debe afirmar que:

- Se implementaron o verificaron todas las reglas exactas de todos los módulos de VSIAF.
- La API está sincronizada con el programa VSIAF original.
- Los endpoints `501` de respaldos/administración realizan ya funciones reales.
- El proyecto está desplegado en Render u otra nube si solamente se ejecutó en localhost.

## 7. Verificación rápida de esta nueva copia

1. Inicio correcto: en consola, `Started Sis414ParcialApplication` y perfil `demo` activo.
2. Swagger: **una sola vista**, sin el selector de los grupos 01/02/03.
3. Bajo **Oficinas y Responsables** aparecen tanto `POST /api/oficinas`, `PUT /api/oficinas/{id}`, ambos `PATCH` de oficina, como `POST`, `PUT` y los dos `PATCH` de responsables, más sus consultas GET.
4. En **Activos Fijos**, **Grupos y Auxiliares**, **Generar Reportes** y otras secciones deben permanecer los endpoints que ya estaban en el proyecto base.
5. En el endpoint de muestra, `PATCH /api/oficinas/{id}/activar`, el **ID real de la consola** debe devolver `200` con estado `ACTIVO`.
6. Tras la prueba, usa `Cancel` solo para dejar limpia la pantalla si deseas; no sirve para revertir cambios.

El código original no se ha sobrescrito: esta versión es una copia independiente.
