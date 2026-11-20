package com.sis414.sis_414_parcial.controller;

import com.sis414.sis_414_parcial.dto.DepartamentoDto;
import com.sis414.sis_414_parcial.service.DepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@Tag(name = "Oficinas y Responsables")
public class DepartamentoController {
    private final DepartamentoService service;
    public DepartamentoController(DepartamentoService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "EXPEDIDO - Mostrar los nueve departamentos (GET)",
            description = "Consulta de los departamentos disponibles para seleccionar Expedido de un responsable. No crea ni edita departamentos.")
    public List<DepartamentoDto> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    @Operation(summary = "EXPEDIDO - Consultar departamento seleccionado (GET)")
    public DepartamentoDto findById(@PathVariable Long id) { return service.findById(id); }
}
