package com.sis414.sis_414_parcial.dto;

import java.time.LocalDate;

/** Exactamente los ocho campos funcionales de OFICINA.DBF, en camelCase JSON. */
public record OficinaDto(
        String entidad,
        String unidad,
        Integer codOfic,
        String nomOfic,
        String observ,
        LocalDate feult,
        String usuar,
        Integer apiEstado
) {}
