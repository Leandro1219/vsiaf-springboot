package com.sis414.sis_414_parcial.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Solo los datos que escribe el operador: el código y el estado los fija el servidor. */
@Schema(description = "Nuevo + Guardar de una oficina. El código es automático y comienza INACTIVA.")
public record NuevaOficinaRequest(
        @Schema(description = "Nombre de la oficina", example = "REGISTRO") String nomOfic,
        @Schema(description = "Descripción de las funciones de la oficina", example = "Registra los bienes asignados") String observ,
        @Schema(description = "ID de la unidad administrativa existente", example = "1") Long unidadId,
        @Schema(description = "Usuario de prueba (opcional; máximo 8 caracteres)", example = "demo") String usuar
) {}
