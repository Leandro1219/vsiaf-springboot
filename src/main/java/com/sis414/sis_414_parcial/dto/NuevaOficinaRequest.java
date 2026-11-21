package com.sis414.sis_414_parcial.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Campos que el operador escribe en VSIAF: oficina y observacion. */
@Schema(description = "OFICINA.DBF: nombre (C, 65) y observacion (Memo). "
        + "Entidad=10 y unidad se obtienen del contexto aun si UNIDAD esta vacia; codigo, fecha, usuario y estado los asigna el sistema.")
public record NuevaOficinaRequest(
        @Schema(description = "Nombre no repetido de oficina (maximo 65 caracteres)", example = "REGISTRO")
        String nomOfic,
        @Schema(description = "Observacion acerca de la funcion de la oficina", example = "Se encarga del registro de bienes")
        String observ
) {}
