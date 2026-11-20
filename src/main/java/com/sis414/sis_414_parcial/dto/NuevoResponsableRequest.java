package com.sis414.sis_414_parcial.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Datos que se capturan en la grilla antes de Guardar. */
@Schema(description = "Nuevo + Guardar un responsable. Código automático y estado INACTIVO.")
public record NuevoResponsableRequest(
        @Schema(description = "Nombre del responsable", example = "JUAN PEREZ") String nomResp,
        @Schema(description = "Cargo", example = "ENCARGADO") String cargo,
        @Schema(description = "Observación opcional", example = "Responsable del inventario") String observ,
        @Schema(description = "CI: entre 8 y 20 dígitos numéricos", example = "12345678") String ci,
        @Schema(description = "ID de la oficina existente", example = "1") Long oficinaId,
        @Schema(description = "ID de Expedido obtenido de GET /api/departamentos", example = "1") Long departamentoExpId,
        @Schema(description = "Usuario de prueba opcional", example = "demo") String usuar
) {}
