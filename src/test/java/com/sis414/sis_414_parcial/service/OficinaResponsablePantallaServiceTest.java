package com.sis414.sis_414_parcial.service;

import com.sis414.sis_414_parcial.dto.OficinaDetalleDto;
import com.sis414.sis_414_parcial.entity.Oficina;
import com.sis414.sis_414_parcial.repository.OficinaRepository;
import com.sis414.sis_414_parcial.repository.ResponsableRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OficinaResponsablePantallaServiceTest {

    @Test
    void inactivarOficinaGuardaEstadoYLoDevuelve() {
        OficinaRepository oficinas = mock(OficinaRepository.class);
        ResponsableRepository responsables = mock(ResponsableRepository.class);
        Oficina oficina = new Oficina();
        oficina.setId(7L);
        oficina.setCodOfic(7);
        oficina.setNomOfic("OFICINA DE PRUEBA");
        oficina.setObserv("Prueba");
        oficina.setApiEstado(1);

        when(oficinas.findById(7L)).thenReturn(Optional.of(oficina));
        when(oficinas.save(any(Oficina.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(responsables.findByOficinaId(7L)).thenReturn(List.of());

        OficinaResponsablePantallaService service = new OficinaResponsablePantallaService(oficinas, responsables);
        OficinaDetalleDto resultado = service.cambiarEstadoOficina(7L, 0);

        assertEquals(0, oficina.getApiEstado());
        assertEquals("INACTIVO", resultado.estado());
        assertEquals(7L, resultado.id());
        assertTrue(resultado.responsables().isEmpty());
        verify(oficinas).save(oficina);
        verify(responsables).findByOficinaId(7L);
    }

    @Test
    void activarOficinaGuardaEstadoYLoDevuelve() {
        OficinaRepository oficinas = mock(OficinaRepository.class);
        ResponsableRepository responsables = mock(ResponsableRepository.class);
        Oficina oficina = new Oficina();
        oficina.setId(1L);
        oficina.setApiEstado(0);
        when(oficinas.findById(1L)).thenReturn(Optional.of(oficina));
        when(oficinas.save(any(Oficina.class))).thenAnswer(i -> i.getArgument(0));
        when(responsables.findByOficinaId(1L)).thenReturn(List.of());
        OficinaResponsablePantallaService service = new OficinaResponsablePantallaService(oficinas, responsables);
        OficinaDetalleDto resultado = service.cambiarEstadoOficina(1L, 1);
        assertEquals(1, oficina.getApiEstado());
        assertEquals("ACTIVO", resultado.estado());
    }

    @Test
    void inactivarOficinaInexistenteResponde404() {
        OficinaRepository oficinas = mock(OficinaRepository.class);
        ResponsableRepository responsables = mock(ResponsableRepository.class);
        when(oficinas.findById(999L)).thenReturn(Optional.empty());

        OficinaResponsablePantallaService service = new OficinaResponsablePantallaService(oficinas, responsables);
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.cambiarEstadoOficina(999L, 0));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verify(oficinas, never()).save(any());
    }

    @Test
    void activarOficinaYaActivaNoProduceError() {
        OficinaRepository oficinas = mock(OficinaRepository.class);
        ResponsableRepository responsables = mock(ResponsableRepository.class);
        Oficina oficina = new Oficina(); oficina.setId(5L); oficina.setApiEstado(1);
        when(oficinas.findById(5L)).thenReturn(Optional.of(oficina));
        OficinaResponsablePantallaService service = new OficinaResponsablePantallaService(oficinas, responsables);
        when(oficinas.save(any(Oficina.class))).thenAnswer(i -> i.getArgument(0));
        when(responsables.findByOficinaId(5L)).thenReturn(List.of());
        assertEquals("ACTIVO", service.cambiarEstadoOficina(5L, 1).estado());
        verify(oficinas).save(oficina);
    }

    @Test
    void activarResponsableNoActivaOficina() {
        OficinaRepository oficinas = mock(OficinaRepository.class);
        ResponsableRepository responsables = mock(ResponsableRepository.class);
        Oficina oficina = new Oficina(); oficina.setId(4L); oficina.setApiEstado(0);
        com.sis414.sis_414_parcial.entity.Responsable r =
                new com.sis414.sis_414_parcial.entity.Responsable();
        r.setId(10L); r.setApiEstado(0); r.setOficina(oficina);
        when(responsables.findById(10L)).thenReturn(Optional.of(r));
        when(responsables.save(any(com.sis414.sis_414_parcial.entity.Responsable.class)))
                .thenAnswer(i -> i.getArgument(0));
        OficinaResponsablePantallaService service = new OficinaResponsablePantallaService(oficinas, responsables);
        var resultado = service.cambiarEstadoResponsable(10L, 1);
        assertEquals("ACTIVO", resultado.estado());
        assertEquals(0, oficina.getApiEstado());
        verify(oficinas, never()).save(any());
    }

}
