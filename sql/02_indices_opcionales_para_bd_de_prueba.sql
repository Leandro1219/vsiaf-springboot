-- Indices de defensa: ejecutar solo tras verificar duplicados con 05_...sql.
-- No se crean automaticamente: se aplican conscientemente en PostgreSQL.
-- NO ejecutar sobre DBF originales.

-- Nombres unicos dentro de la ENTIDAD: observacion del docente.
CREATE UNIQUE INDEX IF NOT EXISTS uq_oficina_entidad_nombre_normalizado
ON public.oficina (entidad, upper(btrim(nom_ofic)))
WHERE entidad IS NOT NULL AND nom_ofic IS NOT NULL AND btrim(nom_ofic) <> '';

-- CODOFIC por ENTIDAD+UNIDAD es el identificador funcional del DBF.
CREATE UNIQUE INDEX IF NOT EXISTS uq_oficina_entidad_unidad_codigo
ON public.oficina (entidad, unidad, cod_ofic)
WHERE entidad IS NOT NULL AND unidad IS NOT NULL AND cod_ofic IS NOT NULL;
