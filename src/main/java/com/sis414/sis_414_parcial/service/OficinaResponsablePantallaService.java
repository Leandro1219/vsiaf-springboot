package com.sis414.sis_414_parcial.service;

import com.sis414.sis_414_parcial.dto.OficinaDetalleDto;
import com.sis414.sis_414_parcial.dto.OficinaDto;
import com.sis414.sis_414_parcial.dto.ResponsableFilaDto;
import com.sis414.sis_414_parcial.entity.Oficina;
import com.sis414.sis_414_parcial.entity.Responsable;
import com.sis414.sis_414_parcial.repository.ActivoFijoRepository;
import com.sis414.sis_414_parcial.repository.OficinaRepository;
import com.sis414.sis_414_parcial.repository.ResponsableRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/** Acciones de pantalla: oficina y responsable son operaciones independientes. */
@Service
public class OficinaResponsablePantallaService {
    private final OficinaRepository oficinas;
    private final ResponsableRepository responsables;
    private final ActivoFijoRepository activos;
    private final OficinaService oficinaService;

    public OficinaResponsablePantallaService(OficinaRepository oficinas,
            ResponsableRepository responsables, ActivoFijoRepository activos,
            OficinaService oficinaService) {
        this.oficinas = oficinas;
        this.responsables = responsables;
        this.activos = activos;
        this.oficinaService = oficinaService;
    }

    @Transactional(readOnly = true)
    public OficinaDetalleDto detalle(Integer codOfic) {
        Oficina o = oficina(codOfic);
        OficinaDto d = oficinaService.toDto(o);
        return new OficinaDetalleDto(d.entidad(), d.unidad(), d.codOfic(),
                d.nomOfic(), d.observ(), d.feult(), d.usuar(), d.apiEstado(), responsables(codOfic));
    }

    @Transactional(readOnly = true)
    public List<ResponsableFilaDto> responsables(Integer codOfic) {
        Oficina oficina = oficina(codOfic);
        return responsables.findByOficinaId(oficina.getId()).stream().map(this::fila).toList();
    }

    /** PATCH oficina devuelve exactamente los mismos nombres que GET/POST/PUT Oficina. */
    @Transactional
    public OficinaDto cambiarEstadoOficina(Integer codOfic, int nuevoEstado) {
        if (nuevoEstado != OficinaService.ESTADO_ACTIVO && nuevoEstado != OficinaService.ESTADO_INACTIVO_DBF)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de oficina no permitido");
        Oficina o = oficina(codOfic);
        if (nuevoEstado == OficinaService.ESTADO_INACTIVO_DBF && activos.existsByOficinaId(o.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede inactivar la oficina porque tiene activos fijos asociados");
        }
        o.setApiEstado(nuevoEstado);
        o.setFeult(LocalDate.now());
        return oficinaService.toDto(oficinas.save(o));
    }

    /** No se modifica la regla previa de estados independientes del responsable. */
    @Transactional
    public ResponsableFilaDto cambiarEstadoResponsable(Long id, int nuevoEstado) {
        if (nuevoEstado != 0 && nuevoEstado != 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de responsable no permitido");
        Responsable r = responsables.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Responsable no encontrado: " + id));
        r.setApiEstado(nuevoEstado);
        return fila(responsables.save(r));
    }

    private Oficina oficina(Integer codOfic) { return oficinaService.getPorCodigo(codOfic); }

    private ResponsableFilaDto fila(Responsable r) {
        return new ResponsableFilaDto(r.getId(), r.getCodResp(), r.getNomResp(), r.getCargo(), r.getCi(),
                r.getDepartamentoExp() == null ? null : r.getDepartamentoExp().getSigla(),
                Integer.valueOf(1).equals(r.getApiEstado()) ? "ACTIVO" : "INACTIVO");
    }
}
