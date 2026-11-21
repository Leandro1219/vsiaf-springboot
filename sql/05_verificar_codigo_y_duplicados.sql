-- Solo consultas de control. Revisar antes de actualizar el servicio.
-- Nombres repetidos dentro de entidad, sin diferenciar mayusculas:
SELECT entidad, UPPER(BTRIM(nom_ofic)) AS nombre, COUNT(*) AS cantidad
FROM public.oficina
WHERE entidad='10' AND nom_ofic IS NOT NULL
GROUP BY entidad, UPPER(BTRIM(nom_ofic))
HAVING COUNT(*) > 1;

-- Codigo por ENTIDAD y UNIDAD, que son los campos DBF:
SELECT entidad, unidad, cod_ofic, COUNT(*) AS cantidad
FROM public.oficina
WHERE entidad='10'
GROUP BY entidad, unidad, cod_ofic
HAVING COUNT(*) > 1;

-- Solo para examinar oficina del contexto actual (Ministerio, unidad vacia):
SELECT entidad, unidad, cod_ofic, nom_ofic, api_estado, unidad_id
FROM public.oficina
WHERE entidad='10' AND unidad=''
ORDER BY cod_ofic;
