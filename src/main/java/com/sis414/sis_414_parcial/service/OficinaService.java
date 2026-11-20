package com.sis414.sis_414_parcial.service;

import com.sis414.sis_414_parcial.dto.ModificarOficinaRequest;
import com.sis414.sis_414_parcial.dto.NuevaOficinaRequest;
import com.sis414.sis_414_parcial.dto.OficinaDto;
import com.sis414.sis_414_parcial.entity.Oficina;
import com.sis414.sis_414_parcial.repository.OficinaRepository;
import com.sis414.sis_414_parcial.repository.UnidadAdminRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class OficinaService {
    private final OficinaRepository repository;
    private final UnidadAdminRepository unidades;

    public OficinaService(OficinaRepository repository, UnidadAdminRepository unidades) {
        this.repository = repository;
        this.unidades = unidades;
    }

    @Transactional(readOnly = true)
    public List<OficinaDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public OficinaDto findById(Long id) { return toDto(getOrThrow(id)); }

    /** Botón Nuevo: el usuario captura; POST significa que pulsó Guardar. */
    @Transactional
    public OficinaDto create(NuevaOficinaRequest request) {
        if (request == null) throw badRequest("Envíe los datos de la oficina");
        validarNombre(request.nomOfic());
        validarUsuario(request.usuar());
        if (request.unidadId() == null) throw badRequest("Seleccione una unidad administrativa");

        Oficina oficina = new Oficina();
        oficina.setNomOfic(request.nomOfic().trim());
        oficina.setObserv(request.observ());
        oficina.setFeult(LocalDate.now());
        oficina.setUsuar(request.usuar());
        oficina.setUnidad(unidades.findById(request.unidadId()).orElseThrow(() ->
                badRequest("No existe la unidad administrativa indicada: " + request.unidadId())));
        oficina.setApiEstado(0); // VSIAF: oficina nueva INACTIVA

        // Simplificación del sistema académico: código = ID autogenerado de PostgreSQL.
        repository.saveAndFlush(oficina);
        oficina.setCodOfic(Math.toIntExact(oficina.getId()));
        return toDto(repository.save(oficina));
    }

    /** Botón Modificar + Guardar: preserva estado, código y unidad. */
    @Transactional
    public OficinaDto update(Long id, ModificarOficinaRequest request) {
        Oficina oficina = getOrThrow(id);
        if (request == null) throw badRequest("Envíe los datos modificados");
        validarNombre(request.nomOfic());
        validarUsuario(request.usuar());
        oficina.setNomOfic(request.nomOfic().trim());
        oficina.setObserv(request.observ());
        oficina.setFeult(LocalDate.now());
        if (request.usuar() != null) oficina.setUsuar(request.usuar());
        return toDto(repository.save(oficina));
    }

    @Transactional(readOnly = true)
    public Oficina getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Oficina no encontrada: " + id));
    }

    public OficinaDto toDto(Oficina e) {
        return new OficinaDto(e.getId(), e.getCodOfic(), e.getNomOfic(), e.getObserv(),
                e.getFeult(), e.getUsuar(), e.getApiEstado(),
                e.getUnidad() == null ? null : e.getUnidad().getId());
    }

    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw badRequest("El nombre de la oficina es obligatorio");
        if (nombre.trim().length() > 65) throw badRequest("El nombre no puede superar 65 caracteres");
    }

    private static void validarUsuario(String usuario) {
        if (usuario != null && usuario.length() > 8) throw badRequest("Usuario: máximo 8 caracteres");
    }

    private static ResponseStatusException badRequest(String detalle) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, detalle);
    }
}
