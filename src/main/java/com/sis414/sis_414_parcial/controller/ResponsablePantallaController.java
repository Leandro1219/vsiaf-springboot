package com.sis414.sis_414_parcial.controller;

import com.sis414.sis_414_parcial.dto.ResponsableFilaDto;
import com.sis414.sis_414_parcial.service.OficinaResponsablePantallaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/responsables")
@Tag(name = "Oficinas y Responsables")
public class ResponsablePantallaController {
    private final OficinaResponsablePantallaService service;

    public ResponsablePantallaController(OficinaResponsablePantallaService service) { this.service = service; }

    @PatchMapping("/{id}/activar")
    @Operation(summary = "RESPONSABLE - Activar (PATCH)",
            description = "Activa solamente al responsable seleccionado. Es independiente del estado de su oficina: también permite activarlo si la oficina está INACTIVA (comportamiento comprobado en la prueba local).")
    public ResponsableFilaDto activar(@PathVariable Long id) { return service.cambiarEstadoResponsable(id, 1); }

    @PatchMapping("/{id}/inactivar")
    @Operation(summary = "RESPONSABLE - Inactivar (PATCH)",
            description = "Inactiva al responsable sin borrar sus datos y sin modificar el estado de la oficina a la que está asociado.")
    public ResponsableFilaDto inactivar(@PathVariable Long id) { return service.cambiarEstadoResponsable(id, 0); }
}
