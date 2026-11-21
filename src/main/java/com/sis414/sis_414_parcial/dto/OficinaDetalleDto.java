package com.sis414.sis_414_parcial.dto;

import java.time.LocalDate;
import java.util.List;

/** Los ocho campos de OFICINA.DBF y la tabla de Responsables visible en la pantalla. */
public record OficinaDetalleDto(
        String entidad,
        String unidad,
        Integer codOfic,
        String nomOfic,
        String observ,
        LocalDate feult,
        String usuar,
        Integer apiEstado,
        List<ResponsableFilaDto> responsables
) {}
