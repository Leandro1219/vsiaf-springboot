-- Consultas de lectura: comparar campos funcionales OFICINA.DBF con PostgreSQL.
SELECT column_name, data_type, character_maximum_length, is_nullable
FROM information_schema.columns
WHERE table_schema='public' AND table_name='oficina'
ORDER BY ordinal_position;

-- Campos DBF (8), junto al id tecnico solo para diagnostico de PostgreSQL.
SELECT id, entidad, unidad, cod_ofic, nom_ofic, observ, feult, usuar, api_estado
FROM public.oficina
WHERE entidad='10'
ORDER BY cod_ofic;

-- Contexto institucional: sirve para verificar si ya existe entidad=10,
-- pero NO es obligatorio crear una fila ficticia en unidad_admin.
SELECT id, codigo, desc_ent FROM public.entidades WHERE codigo='10';
