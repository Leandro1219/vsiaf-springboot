package com.sis414.sis_414_parcial.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** No admite alterar código, estado ni unidad por medio de Modificar. */
@Schema(description = "Modificar + Guardar los datos de la oficina; conserva código y estado.")
public record ModificarOficinaRequest(
        @Schema(description = "Nuevo nombre", example = "REGISTRO ACTUALIZADO") String nomOfic,
        @Schema(description = "Nueva observación", example = "Registro y seguimiento de bienes") String observ,
        @Schema(description = "Usuario de prueba (opcional; máximo 8 caracteres)", example = "demo") String usuar
) {}
