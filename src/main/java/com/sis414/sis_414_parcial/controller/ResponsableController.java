package com.sis414.sis_414_parcial.controller;

import com.sis414.sis_414_parcial.dto.ModificarResponsableRequest;
import com.sis414.sis_414_parcial.dto.NuevoResponsableRequest;
import com.sis414.sis_414_parcial.dto.ResponsableDto;
import com.sis414.sis_414_parcial.service.ResponsableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responsables")
@Tag(name = "Oficinas y Responsables")
public class ResponsableController {
    private final ResponsableService service;

    public ResponsableController(ResponsableService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "RESPONSABLE - Consultar registros")
    public List<ResponsableDto> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    @Operation(summary = "RESPONSABLE - Consultar responsable seleccionado")
    public ResponsableDto findById(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "RESPONSABLE - Nuevo + Guardar (POST)",
            description = "Se crea con código automático y estado INACTIVO. Requiere oficina, nombre, cargo, CI de 8 a 20 dígitos y un departamento válido de Expedido.")
    public ResponsableDto create(@RequestBody NuevoResponsableRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    @Operation(summary = "RESPONSABLE - Modificar + Guardar (PUT)",
            description = "Actualiza nombre, cargo, observación, CI y Expedido. Conserva oficina, código y estado.")
    public ResponsableDto update(@PathVariable Long id, @RequestBody ModificarResponsableRequest request) {
        return service.update(id, request);
    }
}
