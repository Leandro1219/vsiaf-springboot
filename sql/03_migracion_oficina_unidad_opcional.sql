-- VSIAF 3.2 - OFICINA.DBF: UNIDAD C(5) puede estar en blanco, y
-- UNIDADADMIN.DBF puede no contener filas. No crear unidades ficticias.
--
-- SOLO PostgreSQL del proyecto (local o Render con respaldo previo).
-- Ejecutar UNA VEZ ANTES de iniciar la nueva version, en pgAdmin Query Tool.
-- NO modifica los .DBF de VSIAF ni borra oficinas/unidades.
--
-- Permite que la FK tecnica oficina.unidad_id sea NULL. Las columnas DBF
-- oficina.entidad y oficina.unidad siguen almacenandose como C(4) y C(5).
BEGIN;
ALTER TABLE public.oficina ALTER COLUMN unidad_id DROP NOT NULL;
COMMIT;

-- Verificacion (is_nullable debe mostrar YES para unidad_id):
SELECT column_name, is_nullable
FROM information_schema.columns
WHERE table_schema='public' AND table_name='oficina'
  AND column_name IN ('entidad','unidad','unidad_id');
