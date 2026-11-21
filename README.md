# VSIAF 3.2 — API académica de referencia (módulo Oficina ajustado al DBF)

Java 21 · Spring Boot 3.4.1 · PostgreSQL · Swagger UI · Gradle

**Primero leer [`GUIA_CORRECCIONES_OFICINAS_DBF.md`](GUIA_CORRECCIONES_OFICINAS_DBF.md).**

Esta versión ajusta el módulo **Oficina** a los ocho campos de `OFICINA.DBF`, con contexto institucional **Ministerio de Relaciones Exteriores, ENTIDAD=10, UNIDAD=""**. Nuevo pide únicamente nombre y observación. El identificador visible es `codOfic`, no el `id` técnico de PostgreSQL.

**Si ya existe la tabla `oficina` de una versión anterior:** ejecutar antes el script `sql/03_migracion_oficina_unidad_opcional.sql` en una base PostgreSQL del proyecto respaldada. No hay que crear unidades artificiales ni habilitar el perfil `demo`.

### Ejecutar localmente

1. Abrir el proyecto en IntelliJ con JDK 21.
2. Configurar PostgreSQL mediante `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. La contraseña no debe subirse a GitHub.
3. En base existente: ejecutar SQL 03 en pgAdmin y verificar con SQL 05.
4. Ejecutar Spring Boot, abrir `http://localhost:8080/swagger-ui/index.html`.
5. Probar `POST /api/oficinas`; tomar el valor **`codOfic`** de la respuesta para usarlo en PUT/PATCH/GET.

Los demás módulos se conservaron como estaban y se deben validar por separado. No se alteran los archivos reales del programa VSIAF.
