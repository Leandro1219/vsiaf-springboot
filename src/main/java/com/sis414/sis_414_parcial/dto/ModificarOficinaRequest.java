package com.sis414.sis_414_parcial.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Modificar+Guardar cambia solamente los campos editables en OFICINA. */
@Schema(description = "Modificar oficina: codigo, entidad, unidad, usuario y estado se conservan/asignan internamente.")
public record ModificarOficinaRequest(
        @Schema(description = "Nombre de oficina (maximo 65 caracteres, no duplicado)", example = "REGISTRO ACTUALIZADO")
        String nomOfic,
        @Schema(description = "Observacion", example = "Registro y control de bienes")
        String observ
) {}
