package com.sis414.sis_414_parcial.controller;

import com.sis414.sis_414_parcial.dto.ModificarOficinaRequest;
import com.sis414.sis_414_parcial.dto.NuevaOficinaRequest;
import com.sis414.sis_414_parcial.dto.OficinaDto;
import com.sis414.sis_414_parcial.service.OficinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "OFICINA - Consultar oficinas de la entidad y unidad configuradas",
            description = "Devuelve unicamente los ocho campos de OFICINA.DBF. No expone ID interno PostgreSQL.")
    public List<OficinaDto> findAll() { return service.findAll(); }

    @GetMapping("/{codOfic}")
    @Operation(summary = "OFICINA - Consultar por CODOFIC",
            description = "CODOFIC N(4) del DBF; la entidad y unidad se obtienen del contexto.")
    public OficinaDto findByCodigo(@Parameter(description = "Codigo de oficina CODOFIC, NO id interno")
                                    @PathVariable Integer codOfic) { return service.findByCodigo(codOfic); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "OFICINA - Nuevo + Guardar (POST)",
            description = "La ventana pide solo NOMOFIC y OBSERV; ENTIDAD=10, UNIDAD del contexto, "
                    + "CODOFIC automatico y API_ESTADO=3 INACTIVO. No crea unidades ficticias.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Oficina creada"),
            @ApiResponse(responseCode = "400", description = "Nombre vacio o de mas de 65 caracteres"),
            @ApiResponse(responseCode = "409", description = "Nombre repetido o codigo agotado")})
    public OficinaDto create(@RequestBody NuevaOficinaRequest request) { return service.create(request); }

    @PutMapping("/{codOfic}")
    @Operation(summary = "OFICINA - Modificar + Guardar (PUT)",
            description = "Cambia NOMOFIC/OBSERV; conserva CODOFIC, ENTIDAD, UNIDAD y estado.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Oficina actualizada"),
            @ApiResponse(responseCode = "400", description = "Nombre invalido"),
            @ApiResponse(responseCode = "404", description = "No existe la oficina"),
            @ApiResponse(responseCode = "409", description = "Nombre repetido")})
    public OficinaDto update(@Parameter(description = "CODOFIC del DBF, no el ID PostgreSQL")
                              @PathVariable Integer codOfic, @RequestBody ModificarOficinaRequest request) {
        return service.update(codOfic, request);
    }
}
