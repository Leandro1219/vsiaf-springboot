package com.sis414.sis_414_parcial.controller;

import com.sis414.sis_414_parcial.dto.OficinaDetalleDto;
import com.sis414.sis_414_parcial.dto.OficinaDto;
import com.sis414.sis_414_parcial.dto.ResponsableFilaDto;
import com.sis414.sis_414_parcial.service.OficinaResponsablePantallaService;
import com.sis414.sis_414_parcial.service.OficinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oficinas")
@Tag(name = "Oficinas y Responsables")
public class OficinaPantallaController {
    private final OficinaResponsablePantallaService service;
    public OficinaPantallaController(OficinaResponsablePantallaService service) { this.service = service; }

    @GetMapping("/{codOfic}/detalle")
    @Operation(summary = "OFICINA - Consultar oficina y grilla de responsables",
            description = "Ocho campos DBF de oficina y grilla visible en la misma pantalla VSIAF.")
    public OficinaDetalleDto detalle(@PathVariable Integer codOfic) { return service.detalle(codOfic); }

    @GetMapping("/{codOfic}/responsables")
    @Operation(summary = "RESPONSABLES - Consultar tabla asociada a oficina")
    public List<ResponsableFilaDto> responsables(@PathVariable Integer codOfic) {
        return service.responsables(codOfic);
    }

    @PatchMapping("/{codOfic}/activar")
    @Operation(summary = "OFICINA - Activar (PATCH) | endpoint de defensa",
            description = "Boton Activar del VSIAF: pone API_ESTADO=1. Se usa CODOFIC N(4), no id interno.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Oficina ACTIVA"),
            @ApiResponse(responseCode = "404", description = "Oficina no encontrada")})
    public OficinaDto activar(@Parameter(description = "CODOFIC del DBF") @PathVariable Integer codOfic) {
        return service.cambiarEstadoOficina(codOfic, OficinaService.ESTADO_ACTIVO);
    }

    @PatchMapping("/{codOfic}/inactivar")
    @Operation(summary = "OFICINA - Inactivar (PATCH)",
            description = "Pone API_ESTADO=3 (INACTIVO segun DBF), sin eliminar la oficina.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Oficina INACTIVA"),
            @ApiResponse(responseCode = "404", description = "Oficina no encontrada"),
            @ApiResponse(responseCode = "409", description = "Existen activos fijos vinculados")})
    public OficinaDto inactivar(@Parameter(description = "CODOFIC del DBF") @PathVariable Integer codOfic) {
        return service.cambiarEstadoOficina(codOfic, OficinaService.ESTADO_INACTIVO_DBF);
    }
}
