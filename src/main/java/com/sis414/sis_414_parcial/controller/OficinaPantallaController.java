package com.sis414.sis_414_parcial.controller;

import com.sis414.sis_414_parcial.dto.OficinaDetalleDto;
import com.sis414.sis_414_parcial.dto.ResponsableFilaDto;
import com.sis414.sis_414_parcial.service.OficinaResponsablePantallaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oficinas")
@Tag(name = "Oficinas y Responsables")
public class OficinaPantallaController {
    private final OficinaResponsablePantallaService service;

    public OficinaPantallaController(OficinaResponsablePantallaService service) { this.service = service; }

    @GetMapping("/{id}/detalle")
    @Operation(summary = "OFICINA - Consultar pantalla con responsables", description = "Muestra nombre, observación, estado y grilla de responsables de la oficina.")
    public OficinaDetalleDto detalle(@PathVariable Long id) { return service.detalle(id); }

    @GetMapping("/{id}/responsables")
    @Operation(summary = "RESPONSABLES - Consultar grilla de la oficina", description = "Columnas Código, Responsable, Cargo, CI, Expedido y Estado.")
    public List<ResponsableFilaDto> responsables(@PathVariable Long id) { return service.responsables(id); }

    @PatchMapping("/{id}/activar")
    @Operation(summary = "OFICINA - Activar (PATCH) | endpoint elegido para la demostración",
            description = "Botón Activar en la zona superior de OFICINA de VSIAF. Cambia solo el estado de la oficina a ACTIVO, sin borrar el registro ni modificar a sus responsables. Este es el único endpoint que se propone ejecutar frente al docente.")
    public OficinaDetalleDto activar(@PathVariable Long id) { return service.cambiarEstadoOficina(id, 1); }

    @PatchMapping("/{id}/inactivar")
    @Operation(summary = "OFICINA - Inactivar (PATCH)",
            description = "Botón Inactivar de oficina: cambia el estado a INACTIVO sin eliminarla. Si ya está inactiva, mantiene ese estado.")
    public OficinaDetalleDto inactivar(@PathVariable Long id) { return service.cambiarEstadoOficina(id, 0); }
}
