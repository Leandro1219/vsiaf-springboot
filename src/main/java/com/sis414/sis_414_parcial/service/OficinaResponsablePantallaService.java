package com.sis414.sis_414_parcial.service;

import com.sis414.sis_414_parcial.dto.OficinaDetalleDto;
import com.sis414.sis_414_parcial.dto.ResponsableFilaDto;
import com.sis414.sis_414_parcial.entity.Oficina;
import com.sis414.sis_414_parcial.entity.Responsable;
import com.sis414.sis_414_parcial.repository.OficinaRepository;
import com.sis414.sis_414_parcial.repository.ResponsableRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** Botones persistentes: Activar/Inactivar de oficina y de responsable, por separado. */
@Service
public class OficinaResponsablePantallaService {
    private final OficinaRepository oficinas;
    private final ResponsableRepository responsables;

    public OficinaResponsablePantallaService(OficinaRepository oficinas, ResponsableRepository responsables) {
        this.oficinas = oficinas;
        this.responsables = responsables;
    }

    @Transactional(readOnly = true)
    public OficinaDetalleDto detalle(Long id) {
        Oficina o = oficina(id);
        return new OficinaDetalleDto(o.getId(), o.getCodOfic(), o.getNomOfic(), estado(o.getApiEstado()),
                o.getObserv(), responsables(id));
    }

    @Transactional(readOnly = true)
    public List<ResponsableFilaDto> responsables(Long oficinaId) {
        oficina(oficinaId);
        return responsables.findByOficinaId(oficinaId).stream().map(this::fila).toList();
    }

    @Transactional
    public OficinaDetalleDto cambiarEstadoOficina(Long id, int nuevoEstado) {
        validarNuevoEstado(nuevoEstado);
        Oficina o = oficina(id);
        o.setApiEstado(nuevoEstado);
        oficinas.save(o);
        return detalle(id);
    }

    @Transactional
    public ResponsableFilaDto cambiarEstadoResponsable(Long id, int nuevoEstado) {
        validarNuevoEstado(nuevoEstado);
        Responsable r = responsables.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Responsable no encontrado: " + id));
        r.setApiEstado(nuevoEstado);
        return fila(responsables.save(r));
    }

    private Oficina oficina(Long id) {
        return oficinas.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Oficina no encontrada: " + id));
    }

    private ResponsableFilaDto fila(Responsable r) {
        return new ResponsableFilaDto(r.getId(), r.getCodResp(), r.getNomResp(), r.getCargo(), r.getCi(),
                r.getDepartamentoExp() == null ? null : r.getDepartamentoExp().getSigla(),
                estado(r.getApiEstado()));
    }

    private static void validarNuevoEstado(int e) {
        if (e != 0 && e != 1) throw new IllegalArgumentException("Estado no válido");
    }

    private static String estado(Integer e) { return Integer.valueOf(1).equals(e) ? "ACTIVO" : "INACTIVO"; }
}
