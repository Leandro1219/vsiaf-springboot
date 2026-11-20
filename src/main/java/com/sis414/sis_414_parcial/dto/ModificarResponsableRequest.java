package com.sis414.sis_414_parcial.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Modificar no crea un responsable ni cambia su oficina, código o estado. */
@Schema(description = "Modificar + Guardar responsable. Conserva el código, oficina y estado.")
public record ModificarResponsableRequest(
        @Schema(description = "Nombre actualizado", example = "JUAN PEREZ") String nomResp,
        @Schema(description = "Cargo actualizado", example = "RESPONSABLE DE ALMACÉN") String cargo,
        @Schema(description = "Observación", example = "Actualización de cargo") String observ,
        @Schema(description = "CI: entre 8 y 20 dígitos", example = "12345678") String ci,
        @Schema(description = "Departamento de expedición válido", example = "1") Long departamentoExpId,
        @Schema(description = "Usuario de prueba opcional", example = "demo") String usuar
) {}
