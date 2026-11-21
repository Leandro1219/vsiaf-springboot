# VSIAF 3.2 — Corrección final del módulo OFICINA

**Fecha:** 21 de noviembre de 2026. **Ámbito:** únicamente el módulo **Oficina**; Responsables y demás módulos conservan la versión anterior.

## 1. Fuente comprobada

El archivo original `dbsf.zip/OFICINA.DBF` define estos ocho campos:

| Campo DBF | FoxPro | PostgreSQL/JPA del proyecto | API REST Oficina |
|---|---|---|---|
| ENTIDAD | C(4) | `entidad VARCHAR(4)` | `entidad` |
| UNIDAD | C(5) | `unidad VARCHAR(5)` | `unidad` |
| CODOFIC | N(4) | `cod_ofic INTEGER` (validación 1–9999) | `codOfic` |
| NOMOFIC | C(65) | `nom_ofic VARCHAR(65)` | `nomOfic` |
| OBSERV | Memo | `observ TEXT` | `observ` |
| FEULT | D | `feult DATE` | `feult` |
| USUAR | C(8) | `usuar VARCHAR(8)` | `usuar` |
| API_ESTADO | N(1) | `api_estado INTEGER` | `apiEstado` |

`config.dbf` registra **ENTIDAD = "10" (Ministerio de Relaciones Exteriores)** y **UNIDAD = ""** (vacío). En el ejemplar examinado de `OFICINA.DBF` encontramos `API_ESTADO = 1` para activo y `3` para inactivo. No se extrapolan otras reglas de estado.

La base PostgreSQL conserva una columna **`id`** como clave técnica interna (no es un campo del DBF) y una FK técnica **`unidad_id`** opcional (tampoco aparece en el DBF). Ninguna se muestra en el JSON público de Oficina ni se pide en su POST.

## 2. Cambios realizados

1. `POST /api/oficinas` pide únicamente `nomOfic` y `observ`, como los datos editables de Nuevo Oficina.
2. `ENTIDAD`, `UNIDAD`, `CODOFIC`, `FEULT`, `USUAR`, `API_ESTADO` los asigna el servidor. El usuario `demo` es un identificador ficticio, no autenticación real.
3. Se eliminó la dependencia **obligatoria** de una fila en `unidad_admin`: con `UNIDAD=""`, no se crea ni utiliza una unidad inventada. La FK interna se guarda como `NULL` en las nuevas oficinas de ese contexto.
4. Para evitar que peticiones simultáneas generen el mismo `CODOFIC`, el servicio usa un bloqueo de transacción PostgreSQL por entidad y unidad, sin bloquear una unidad administrativa ficticia.
5. Los ocho campos del DTO `OficinaDto` son exactamente los del DBF en camelCase; **no contiene `id`**.
6. Las rutas de Oficina ahora utilizan **`codOfic`**, no el `id` de PostgreSQL. Esto es un cambio de contrato deliberado y hay que actualizar las llamadas anteriores.
7. Se rechaza un nombre en blanco o de más de 65 caracteres (400) y los nombres repetidos dentro de la entidad, ignorando mayúsculas/espacios extremos (409). El bloqueo garantiza la validación dentro de operaciones de esta API; los índices SQL opcionales refuerzan la unicidad ante otras escrituras.
8. POST crea oficinas INACTIVAS (`apiEstado=3`); PATCH activar coloca `1`, PATCH inactivar coloca `3`; PUT no cambia el estado ni código. Se conserva la regla previa que impide inactivar si hay activos asociados, sin exigir pruebas adicionales ajenas a Oficina.
9. Las oficinas antiguas guardadas con `api_estado=0` **no se modifican ni se muestran falsamente como 3**. Hay un SQL opcional y explícito para normalizar solo registros ficticios si se decide hacerlo.
10. `spring.profiles.active=demo` es opcional. Su inicializador crea únicamente una oficina ficticia, no entidades ni unidades administrativas ficticias.

## 3. Importante: migración necesaria para tu PostgreSQL actual

**Antes de iniciar esta nueva versión**, en pgAdmin conectado a `vsiaf_evaluacion`, con un respaldo previo, ejecuta `sql/03_migracion_oficina_unidad_opcional.sql`:

```sql
BEGIN;
ALTER TABLE public.oficina ALTER COLUMN unidad_id DROP NOT NULL;
COMMIT;
```

La instrucción es idempotente, no borra tablas, registros ni otras relaciones: únicamente permite `NULL` en la FK técnica de la tabla `oficina`. Si no se hace, PostgreSQL puede seguir rechazando un POST aunque JPA haya cambiado la anotación.

**No ejecutar SQL sobre el programa FoxPro VSIAF ni su directorio de DBF.** Si se despliega en Render, esta misma migración debe realizarse **en la base PostgreSQL de Render**, por separado, antes del cambio de versión; la base local no se sincroniza con la nube.

## 4. Cómo utilizar ahora Swagger

* `GET /api/oficinas` — lista de los ocho campos DBF.
* `GET /api/oficinas/{codOfic}` — consulta por el código del DBF.
* `POST /api/oficinas` — Nuevo + Guardar.
* `PUT /api/oficinas/{codOfic}` — Modificar + Guardar.
* `PATCH /api/oficinas/{codOfic}/activar` — Activar.
* `PATCH /api/oficinas/{codOfic}/inactivar` — Inactivar.
* `GET /api/oficinas/{codOfic}/detalle` — datos DBF + tabla visible de responsables.
* `GET /api/oficinas/{codOfic}/responsables` — tabla de responsables asociada a la oficina.

**Ejemplo con tus datos anteriores:** la oficina de la prueba tenía `id=9` y `codOfic=4`. En la nueva versión, la URL correcta es `PATCH /api/oficinas/4/activar` (no `/9/activar`). La búsqueda se limita a ENTIDAD=10, UNIDAD="". Si se detectan códigos duplicados en ese contexto por registros históricos, responde 409 en vez de escoger uno arbitrariamente.

POST:

```json
{"nomOfic":"ARCHIVO DE PRUEBA", "observ":"Registro de documentos ficticios"}
```

Respuesta esperada (código ilustrativo):

```json
{
  "entidad":"10", "unidad":"", "codOfic":5,
  "nomOfic":"ARCHIVO DE PRUEBA",
  "observ":"Registro de documentos ficticios",
  "feult":"2026-11-21", "usuar":"demo", "apiEstado":3
}
```

No hay endpoint DELETE de Oficina porque no aparece la opción Eliminar en la ventana examinada. Guardar se representa por POST/PUT; Deshacer y Salir son acciones de interfaz y no endpoints independientes.

## 5. SQL complementario

* `sql/01_comprobar_oficinas.sql`: campos, registros y entidad.
* `sql/02_indices_opcionales_para_bd_de_prueba.sql`: índices de unicidad, **solo después** de revisar duplicados y respaldar.
* `sql/03_migracion_oficina_unidad_opcional.sql`: **migración necesaria en una base previa**.
* `sql/04_normalizar_estados_anteriores_OPCIONAL.sql`: SQL comentado de actualización de valores 0 anteriores para registros ficticios de ENTIDAD=10; no se ejecuta automáticamente.
* `sql/05_verificar_codigo_y_duplicados.sql`: verificar datos heredados antes de actualizar.

## 6. Alcance y limitaciones

* **Solo Oficina** fue corregida. `RESP.DBF`, `cla_depts.dbf`, activos fijos y módulos restantes requieren su propia revisión; no afirmar que todo el Swagger ya es equivalente al VSIAF original.
* Los datos DBF originales no se importan ni se modifican; todas las pruebas son ficticias.
* Los códigos funcionales son distintos del ID técnico. Cambió el significado del parámetro de ruta de las operaciones de Oficina; los endpoints de Responsables siguen con el esquema previo.
* El DBF describe la estructura y ejemplos de estado, pero no demuestra todos los algoritmos internos del VSIAF. El uso de códigos incrementales es una implementación académica con la longitud correcta, no una afirmación sobre el algoritmo exacto de FoxPro.
* No se ha certificado aquí un test de integración con PostgreSQL real: Gradle no se pudo descargar por bloqueo de red. Validar en IntelliJ antes de reemplazar el proyecto en Render.
