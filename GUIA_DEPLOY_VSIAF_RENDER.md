# Render — actualización del proyecto VSIAF (Oficina DBF)

Esta guía NO ejecuta acciones en Render. Es una lista para cuando ya se haya probado el proyecto localmente.

**Precaución:** la nueva versión cambia las rutas de Oficina de `/{id}` a `/{codOfic}`. Aunque la forma literal de la URL se parece, el número ahora significa **CODOFIC** de `OFICINA.DBF`. Es un cambio incompatible para clientes antiguos que enviaban `id` interno. Las rutas de Responsables se dejaron sin cambios.

## Paso 1 — Respaldar la base de Render

Antes de actualizar, crea un respaldo con las opciones disponibles para tu instalación de PostgreSQL. No copies credenciales en documentos, GitHub ni Swagger.

## Paso 2 — Ejecutar migración de esquema en la base de Render

En una conexión administrativa a **la base PostgreSQL de Render** (no la base local) ejecuta `sql/03_migracion_oficina_unidad_opcional.sql`:

```sql
BEGIN;
ALTER TABLE public.oficina ALTER COLUMN unidad_id DROP NOT NULL;
COMMIT;
```

Esto elimina solo la obligatoriedad de una FK técnica; no elimina registros ni el campo `UNIDAD` del DBF.

Si todavía no existe la tabla `oficina` en una instalación completamente nueva, no ejecutes ese SQL; JPA generará la FK como opcional al crearla.

## Paso 3 — Verificar datos heredados

Ejecuta `sql/05_verificar_codigo_y_duplicados.sql` en la base destino, y resuelve manualmente cualquier duplicidad encontrada **antes de publicar**; el servicio devolverá 409 para CODOFIC duplicado en un mismo contexto.

El archivo `sql/04_normalizar_estados_anteriores_OPCIONAL.sql` contiene una opción manual para transformar **solo oficinas ficticias** de estado 0 a 3; no se ejecuta por sí solo.

## Paso 4 — Subir el proyecto probado

Una vez validado en IntelliJ con Java 21, subir una nueva revisión al repositorio de GitHub y desplegar desde Render siguiendo la configuración previa de Docker. Conservar `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` como variables del entorno y **no publicar** esos valores.

`SPRING_PROFILES_ACTIVE=demo` no es necesario: la entidad `10` se asigna como código de contexto sin requerir una fila de unidad ficticia. Dejar perfil `demo` desactivado en Render salvo una decisión expresa de mostrar datos de prueba.

## Paso 5 — Verificación final

Abrir `/swagger-ui/index.html`, registrar una oficina ficticia mediante `POST /api/oficinas` con únicamente `nomOfic` y `observ`, y usar su `codOfic` en `GET /api/oficinas/{codOfic}` y, si corresponde, PATCH. Verificar la persistencia en la base **de Render**.

No usar información sensible. No afirmar que todos los demás módulos han sido contrastados con los archivos DBF.
