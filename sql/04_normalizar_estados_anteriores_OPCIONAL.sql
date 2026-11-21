-- OPCIONAL, solo si se desea adaptar OFICINAS ANTIGUAS de esta API.
-- OFICINA.DBF examinado: 1=ACTIVO, 3=INACTIVO.
-- Versiones previas de la API academica usaban 0=INACTIVO.
--
-- NO ejecutar sin revisar la lista y realizar respaldo. Solo usar sobre
-- registros ficticios de prueba de la entidad 10. NO es requisito para
-- registrar nuevas oficinas, que ya usan 3. No modifica otros modulos.
SELECT id, entidad, unidad, cod_ofic, nom_ofic, api_estado
FROM public.oficina
WHERE entidad = '10' AND api_estado = 0
ORDER BY id;

-- SI verificaste que esos registros son academicos y quieres migrarlos:
-- DESCOMENTA Y EJECUTA POR SEPARADO, despues de hacer un respaldo.
-- BEGIN;
-- UPDATE public.oficina SET api_estado=3
-- WHERE entidad = '10' AND api_estado = 0;
-- COMMIT;
