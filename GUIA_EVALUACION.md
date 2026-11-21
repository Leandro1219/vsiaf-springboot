# Guía de evaluación — Oficina VSIAF 3.2

**No exponer datos reales ni contraseñas.** La demostración se centra en la ventana **Oficina**, dentro de Oficinas y Responsables. El ingeniero solicitó mostrar **un endpoint** y explicar su correspondencia con VSIAF; no ejecutar una cadena de endpoints durante la defensa salvo que lo pida.

## Verificaciones antes de presentar

* En la instalación nueva, `POST /api/oficinas` debe funcionar con `nomOfic` y `observ`, sin perfil `demo` ni unidad ficticia, tras ejecutar la migración SQL 03 si la BD era anterior.
* Nombre duplicado — 409; nombre de 66 caracteres — 400; nuevo nombre válido — 201.
* POST empieza con estado 3; PATCH activar pasa a 1; PATCH inactivar pasa a 3; PUT conserva código y estado.
* Las respuestas de Oficina contienen 8 campos correspondientes a `OFICINA.DBF`.
* Las operaciones por oficina reciben **CODOFIC**, no `id` interno de PostgreSQL.
* Si existían `api_estado=0` antiguos, revisarlos antes de usarlos para la defensa; ver SQL 04 opcional.

## Endpoint de demostración recomendado

`PATCH /api/oficinas/{codOfic}/activar`: colocar un **CODOFIC de una oficina ficticia previamente INACTIVA (3)**. Pulsar Try it out > Execute. Mostrar `200 OK`, `apiEstado=1`, código intacto. En el programa VSIAF original, abrir **Oficinas y Responsables > Oficina > Activar** y explicar la coincidencia funcional. El `id` técnico de PostgreSQL no es un campo del DBF.

## Diferencias que debes poder explicar

* La API es académica y no está sincronizada con las tablas FoxPro del VSIAF original.
* La entidad `10` y el código de unidad vacío se obtienen del `config.dbf` examinado. El campo `UNIDAD` existe en `OFICINA.DBF`, aunque el operador no lo escribe en Nuevo Oficina.
* Guardar equivale a POST cuando se crea o PUT cuando se modifica; Deshacer y Salir pertenecen a la interfaz.
* La parte Responsables conserva su código anterior, se revisa por separado y no debe presentarse como totalmente adaptada a RESP.DBF.

**Antes de publicar en Render** hacer una prueba nueva en PostgreSQL local y después configurar por separado la base Render, que no comparte tablas ni registros con la local.
