package com.sis414.sis_414_parcial.service;

import com.sis414.sis_414_parcial.dto.ModificarResponsableRequest;
import com.sis414.sis_414_parcial.dto.NuevoResponsableRequest;
import com.sis414.sis_414_parcial.dto.ResponsableDto;
import com.sis414.sis_414_parcial.entity.Departamento;
import com.sis414.sis_414_parcial.entity.Responsable;
import com.sis414.sis_414_parcial.repository.DepartamentoRepository;
import com.sis414.sis_414_parcial.repository.OficinaRepository;
import com.sis414.sis_414_parcial.repository.ResponsableRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ResponsableService {
    private final ResponsableRepository repository;
    private final DepartamentoRepository departamentos;
    private final OficinaRepository oficinas;

    public ResponsableService(ResponsableRepository repository,
                              DepartamentoRepository departamentos, OficinaRepository oficinas) {
        this.repository = repository;
        this.departamentos = departamentos;
        this.oficinas = oficinas;
    }

    @Transactional(readOnly = true)
    public List<ResponsableDto> findAll() { return repository.findAll().stream().map(this::toDto).toList(); }

    @Transactional(readOnly = true)
    public ResponsableDto findById(Long id) { return toDto(getOrThrow(id)); }

    /** Nuevo + Guardar: estado inicial INACTIVO y código sin intervención del cliente. */
    @Transactional
    public ResponsableDto create(NuevoResponsableRequest request) {
        if (request == null) throw badRequest("Envíe los datos del responsable");
        validarDatos(request.nomResp(), request.cargo(), request.ci(), request.usuar());
        if (request.oficinaId() == null) throw badRequest("Seleccione una oficina");
        Departamento expedido = obtenerExpedido(request.departamentoExpId());

        Responsable r = new Responsable();
        r.setOficina(oficinas.findById(request.oficinaId()).orElseThrow(() ->
                badRequest("No existe la oficina indicada: " + request.oficinaId())));
        aplicarDatos(r, request.nomResp(), request.cargo(), request.observ(), request.ci(), request.usuar());
        r.setDepartamentoExp(expedido);
        r.setApiEstado(0);
        repository.saveAndFlush(r);
        r.setCodResp(Math.toIntExact(r.getId()));
        return toDto(repository.save(r));
    }

    /** Modificar + Guardar: mantiene oficina, código y estado originales. */
    @Transactional
    public ResponsableDto update(Long id, ModificarResponsableRequest request) {
        Responsable r = getOrThrow(id);
        if (request == null) throw badRequest("Envíe los datos modificados");
        validarDatos(request.nomResp(), request.cargo(), request.ci(), request.usuar());
        Departamento expedido = obtenerExpedido(request.departamentoExpId());
        aplicarDatos(r, request.nomResp(), request.cargo(), request.observ(), request.ci(), request.usuar());
        r.setDepartamentoExp(expedido);
        return toDto(repository.save(r));
    }

    @Transactional(readOnly = true)
    public Responsable getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Responsable no encontrado: " + id));
    }

    public ResponsableDto toDto(Responsable e) {
        return new ResponsableDto(e.getId(), e.getCodResp(), e.getNomResp(), e.getCargo(),
                e.getObserv(), e.getCi(), e.getFeult(), e.getUsuar(), e.getApiEstado(),
                e.getOficina() == null ? null : e.getOficina().getId(),
                e.getDepartamentoExp() == null ? null : e.getDepartamentoExp().getId());
    }

    private Departamento obtenerExpedido(Long id) {
        if (id == null) throw badRequest("Seleccione un departamento en Expedido");
        Departamento departamento = departamentos.findById(id).orElseThrow(() ->
                badRequest("No existe el departamento indicado: " + id));
        if (!DepartamentosBolivia.valido(departamento.getDescripcion())) {
            throw badRequest("Expedido debe ser uno de los nueve departamentos de Bolivia");
        }
        return departamento;
    }

    private static void validarDatos(String nombre, String cargo, String ci, String usuario) {
        if (nombre == null || nombre.isBlank()) throw badRequest("El nombre del responsable es obligatorio");
        if (nombre.trim().length() > 35) throw badRequest("El nombre no puede superar 35 caracteres");
        if (cargo == null || cargo.isBlank()) throw badRequest("El cargo del responsable es obligatorio");
        if (cargo.trim().length() > 35) throw badRequest("El cargo no puede superar 35 caracteres");
        if (ci == null || !ci.trim().matches("\\d{8,20}")) {
            throw badRequest("El CI debe contener entre 8 y 20 dígitos numéricos");
        }
        if (usuario != null && usuario.length() > 8) throw badRequest("Usuario: máximo 8 caracteres");
    }

    private static void aplicarDatos(Responsable r, String nombre, String cargo,
                                    String observacion, String ci, String usuario) {
        r.setNomResp(nombre.trim());
        r.setCargo(cargo.trim());
        r.setCi(ci.trim());
        r.setObserv(observacion);
        r.setFeult(LocalDate.now());
        if (usuario != null) r.setUsuar(usuario);
    }

    private static ResponseStatusException badRequest(String detalle) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, detalle);
    }
}
