package com.sis414.sis_414_parcial.controller;

import com.sis414.sis_414_parcial.dto.ModificarOficinaRequest;
import com.sis414.sis_414_parcial.dto.NuevaOficinaRequest;
import com.sis414.sis_414_parcial.dto.OficinaDto;
import com.sis414.sis_414_parcial.service.OficinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oficinas")
@Tag(name = "Oficinas y Responsables")
public class OficinaController {
    private final OficinaService service;

    public OficinaController(OficinaService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "OFICINA - Consultar lista para seleccionar", description = "Consulta necesaria para mostrar las oficinas registradas.")
    public List<OficinaDto> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    @Operation(summary = "OFICINA - Consultar oficina", description = "Recupera los datos de una oficina existente.")
    public OficinaDto findById(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "OFICINA - Nuevo + Guardar (POST)",
            description = "Nuevo inicia la captura en VSIAF; Guardar equivale a este POST. Código autogenerado, estado inicial INACTIVO. Requiere unidad existente.")
    public OficinaDto create(@RequestBody NuevaOficinaRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    @Operation(summary = "OFICINA - Modificar + Guardar (PUT)",
            description = "Actualiza nombre y observación. Conserva código, unidad y estado; no activa la oficina.")
    public OficinaDto update(@PathVariable Long id, @RequestBody ModificarOficinaRequest request) {
        return service.update(id, request);
    }
}
