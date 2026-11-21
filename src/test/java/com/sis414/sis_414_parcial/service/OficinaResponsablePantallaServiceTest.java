package com.sis414.sis_414_parcial.service;

import com.sis414.sis_414_parcial.dto.OficinaDto;
import com.sis414.sis_414_parcial.entity.Oficina;
import com.sis414.sis_414_parcial.entity.Responsable;
import com.sis414.sis_414_parcial.repository.ActivoFijoRepository;
import com.sis414.sis_414_parcial.repository.OficinaRepository;
import com.sis414.sis_414_parcial.repository.ResponsableRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OficinaResponsablePantallaServiceTest {
    private OficinaRepository oficinas = mock(OficinaRepository.class);
    private ResponsableRepository responsables = mock(ResponsableRepository.class);
    private ActivoFijoRepository activos = mock(ActivoFijoRepository.class);
    private OficinaService oficinaService = mock(OficinaService.class);

    private OficinaResponsablePantallaService servicio() {
        return new OficinaResponsablePantallaService(oficinas, responsables, activos, oficinaService);
    }

    private Oficina oficina(Long id, int estado) {
        Oficina o = new Oficina(); o.setId(id); o.setEntidadCodigo("10"); o.setUnidadCodigo("");
        o.setCodOfic(5); o.setNomOfic("REGISTRO"); o.setApiEstado(estado);
        when(oficinaService.getPorCodigo(5)).thenReturn(o);
        when(oficinas.save(any(Oficina.class))).thenAnswer(inv -> inv.getArgument(0));
        when(oficinaService.toDto(any(Oficina.class))).thenAnswer(inv -> {
            Oficina x = inv.getArgument(0);
            return new OficinaDto(x.getEntidadCodigo(), x.getUnidadCodigo(), x.getCodOfic(),
                    x.getNomOfic(), x.getObserv(), x.getFeult(), x.getUsuar(), x.getApiEstado());
        });
        return o;
    }

    @Test
    void activarOficinaUsaEstado1YConservaCodigo() {
        Oficina o = oficina(5L, 3);
        OficinaDto out = servicio().cambiarEstadoOficina(5, 1);
        assertEquals(1, out.apiEstado());
        assertEquals(5, out.codOfic());
        assertEquals(1, o.getApiEstado());
    }

    @Test
    void inactivarOficinaUsa3ComoEnDBFEntregado() {
        Oficina o = oficina(7L, 1);
        OficinaDto out = servicio().cambiarEstadoOficina(5, 3);
        assertEquals(3, out.apiEstado());
        assertEquals(3, o.getApiEstado());
    }

    @Test
    void noInactivaOficinaConActivosFijos() {
        Oficina o = oficina(9L, 1);
        when(activos.existsByOficinaId(9L)).thenReturn(true);
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () ->
                servicio().cambiarEstadoOficina(5, 3));
        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        assertEquals(1, o.getApiEstado());
        verify(oficinas, never()).save(any(Oficina.class));
    }

    @Test
    void activarResponsableNoActivaLaOficina() {
        Oficina oficina = new Oficina(); oficina.setId(4L); oficina.setApiEstado(3);
        Responsable responsable = new Responsable(); responsable.setId(10L);
        responsable.setApiEstado(0); responsable.setOficina(oficina);
        when(responsables.findById(10L)).thenReturn(Optional.of(responsable));
        when(responsables.save(any(Responsable.class))).thenAnswer(inv -> inv.getArgument(0));
        var out = servicio().cambiarEstadoResponsable(10L, 1);
        assertEquals("ACTIVO", out.estado());
        assertEquals(3, oficina.getApiEstado());
        verify(oficinas, never()).save(any(Oficina.class));
    }
}
