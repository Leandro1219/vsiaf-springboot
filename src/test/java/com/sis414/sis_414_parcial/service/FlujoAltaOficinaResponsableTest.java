package com.sis414.sis_414_parcial.service;

import com.sis414.sis_414_parcial.dto.*;
import com.sis414.sis_414_parcial.entity.*;
import com.sis414.sis_414_parcial.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FlujoAltaOficinaResponsableTest {
    private OficinaContextService contextoMock(UnidadAdmin unidad) {
        OficinaContextService contexto = mock(OficinaContextService.class);
        when(contexto.entidadCodigo()).thenReturn("10");
        when(contexto.unidadCodigo()).thenReturn("");
        when(contexto.usuarioPrueba()).thenReturn("demo");
        when(contexto.unidadExistenteOpcional()).thenReturn(unidad);
        return contexto;
    }

    @Test
    void nuevaOficinaSoloPideNombreYObservacionYRespetaCamposDBF() {
        OficinaRepository oficinas = mock(OficinaRepository.class);
        UnidadAdmin unidad = new UnidadAdmin(); unidad.setId(8L); unidad.setUnidad("");
        Entidad entidad = new Entidad(); entidad.setCodigo("10"); unidad.setEntidad(entidad);
        when(oficinas.mayorCodigo("10", "")).thenReturn(4);
        when(oficinas.saveAndFlush(any(Oficina.class))).thenAnswer(inv -> {
            Oficina o = inv.getArgument(0); o.setId(99L); return o;
        });
        OficinaService service = new OficinaService(oficinas, contextoMock(null));
        OficinaDto out = service.create(new NuevaOficinaRequest(" REGISTRO ", "Registra bienes"));
        assertEquals(8, out.getClass().getRecordComponents().length); // Ocho campos DBF; sin ID interno
        assertEquals("10", out.entidad());
        assertEquals("", out.unidad());
        assertEquals(5, out.codOfic()); // Distinto del id interno 99
        assertEquals("REGISTRO", out.nomOfic());
        assertEquals(OficinaService.ESTADO_INACTIVO_DBF, out.apiEstado());
        assertEquals("demo", out.usuar());
        verify(oficinas).saveAndFlush(any(Oficina.class));
    }

    @Test
    void nuevaOficinaRechazaNombreDuplicadoSinGuardar() {
        OficinaRepository oficinas = mock(OficinaRepository.class);
        UnidadAdmin unidad = new UnidadAdmin(); unidad.setId(1L);
        when(oficinas.existeNombre("10", "REGISTRO")).thenReturn(true);
        OficinaService service = new OficinaService(oficinas, contextoMock(unidad));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.create(new NuevaOficinaRequest("REGISTRO", "Observacion")));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(oficinas, never()).saveAndFlush(any(Oficina.class));
    }

    @Test
    void modificarOficinaConservaCodigoYEstadoYNoPideUnidadId() {
        OficinaRepository oficinas = mock(OficinaRepository.class);
        UnidadAdmin unidad = new UnidadAdmin(); unidad.setId(2L); unidad.setUnidad("");
        Oficina oficina = new Oficina(); oficina.setId(10L); oficina.setCodOfic(5);
        oficina.setEntidadCodigo("10"); oficina.setUnidadCodigo("");
        oficina.setApiEstado(OficinaService.ESTADO_INACTIVO_DBF); oficina.setUnidad(unidad);
        when(oficinas.findById(10L)).thenReturn(Optional.of(oficina));
        when(oficinas.save(any(Oficina.class))).thenAnswer(inv -> inv.getArgument(0));
        when(oficinas.buscarPorCodigo("10", "", 5)).thenReturn(List.of(oficina));
        OficinaService service = new OficinaService(oficinas, contextoMock(unidad));
        OficinaDto out = service.update(5, new ModificarOficinaRequest("NUEVO NOMBRE", "Otra observacion"));
        assertEquals("NUEVO NOMBRE", out.nomOfic());
        assertEquals(5, out.codOfic());
        assertEquals(3, out.apiEstado());
        assertEquals("10", out.entidad());
        verify(oficinas).save(oficina);
    }

    @Test
    void rechazaNombreVacioYLargoMayorQueDBF() {
        OficinaRepository oficinas = mock(OficinaRepository.class);
        OficinaService service = new OficinaService(oficinas, contextoMock(new UnidadAdmin()));
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> service.create(new NuevaOficinaRequest(" ", ""))).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> service.create(new NuevaOficinaRequest("X".repeat(66), ""))).getStatusCode());
        verify(oficinas, never()).saveAndFlush(any(Oficina.class));
    }


    @Test
    void oficinaConUnidadDbfVaciaNoNecesitaUnidadInventada() {
        OficinaRepository repo = mock(OficinaRepository.class);
        when(repo.mayorCodigo("10", "")).thenReturn(0);
        when(repo.saveAndFlush(any(Oficina.class))).thenAnswer(inv -> inv.getArgument(0));
        OficinaContextService contexto = contextoMock(null);
        OficinaDto dto = new OficinaService(repo, contexto)
                .create(new NuevaOficinaRequest("ARCHIVO", "Documentos ficticios"));
        assertEquals("10", dto.entidad());
        assertEquals("", dto.unidad());
        assertEquals(1, dto.codOfic());
        verify(contexto).bloquearEscritura();
        verify(repo).saveAndFlush(argThat(o -> o.getUnidad() == null));
    }

    @Test
    void codOficIdentificaOficinaSinIdEnJson() {
        OficinaRepository repo = mock(OficinaRepository.class);
        Oficina oficina = new Oficina(); oficina.setId(9L);
        oficina.setEntidadCodigo("10"); oficina.setUnidadCodigo(""); oficina.setCodOfic(4);
        when(repo.buscarPorCodigo("10", "", 4)).thenReturn(List.of(oficina));
        var dto = new OficinaService(repo, contextoMock(null)).findByCodigo(4);
        assertEquals(4, dto.codOfic());
        assertEquals(8, dto.getClass().getRecordComponents().length);
        assertTrue(java.util.Arrays.stream(dto.getClass().getRecordComponents())
                .noneMatch(c -> c.getName().equals("id")));
    }

    @Test
    void codigoDuplicadoHistoricoEsConflictoNoSeleccionArbitraria() {
        OficinaRepository repo = mock(OficinaRepository.class);
        when(repo.buscarPorCodigo("10", "", 4)).thenReturn(List.of(new Oficina(), new Oficina()));
        var e = assertThrows(ResponseStatusException.class,
                () -> new OficinaService(repo, contextoMock(null)).findByCodigo(4));
        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
    }

    @Test
    void nuevoResponsableSigueUsandoElFlujoPrevio() {
        ResponsableRepository responsables = mock(ResponsableRepository.class);
        DepartamentoRepository departamentos = mock(DepartamentoRepository.class);
        OficinaRepository oficinas = mock(OficinaRepository.class);
        Departamento d = new Departamento(); d.setId(8L); d.setDescripcion("Potosí");
        Oficina o = new Oficina(); o.setId(4L);
        when(departamentos.findById(8L)).thenReturn(Optional.of(d));
        when(oficinas.findById(4L)).thenReturn(Optional.of(o));
        when(responsables.saveAndFlush(any(Responsable.class))).thenAnswer(inv -> {
            Responsable r = inv.getArgument(0); r.setId(90L); return r;
        });
        when(responsables.save(any(Responsable.class))).thenAnswer(inv -> inv.getArgument(0));
        ResponsableDto out = new ResponsableService(responsables, departamentos, oficinas)
                .create(new NuevoResponsableRequest("PEDRO", "ENCARGADO", "Prueba", "12345678", 4L, 8L, "demo"));
        assertEquals(90, out.codResp());
        assertEquals(0, out.apiEstado()); // No se modifica logica de Responsables
    }

    @Test
    void ciCortoContinuaRechazado() {
        ResponsableRepository r = mock(ResponsableRepository.class);
        DepartamentoRepository d = mock(DepartamentoRepository.class);
        OficinaRepository o = mock(OficinaRepository.class);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                new ResponsableService(r, d, o).create(
                        new NuevoResponsableRequest("PERSONA", "CARGO", "", "123", 1L, 1L, "demo")));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void modificarResponsableConservaCodigoOficinaYEstado() {
        ResponsableRepository responsables = mock(ResponsableRepository.class);
        DepartamentoRepository departamentos = mock(DepartamentoRepository.class);
        OficinaRepository oficinas = mock(OficinaRepository.class);
        Responsable r = new Responsable(); r.setId(7L); r.setCodResp(300); r.setApiEstado(1);
        Oficina o = new Oficina(); o.setId(3L); r.setOficina(o);
        Departamento d = new Departamento(); d.setId(2L); d.setDescripcion("Cochabamba");
        when(responsables.findById(7L)).thenReturn(Optional.of(r));
        when(departamentos.findById(2L)).thenReturn(Optional.of(d));
        when(responsables.save(any(Responsable.class))).thenAnswer(inv -> inv.getArgument(0));
        ResponsableDto salida = new ResponsableService(responsables, departamentos, oficinas)
                .update(7L, new ModificarResponsableRequest("LUCIA", "JEFA", "Nueva", "87654321", 2L, null));
        assertEquals(300, salida.codResp());
        assertEquals(1, salida.apiEstado());
        assertEquals(3L, salida.oficinaId());
        assertEquals("LUCIA", salida.nomResp());
        verifyNoInteractions(oficinas);
    }

    @Test
    void departamentoFueraDeBoliviaSeRechaza() {
        ResponsableRepository responsables = mock(ResponsableRepository.class);
        DepartamentoRepository departamentos = mock(DepartamentoRepository.class);
        OficinaRepository oficinas = mock(OficinaRepository.class);
        Departamento noValido = new Departamento(); noValido.setDescripcion("Otro país");
        when(departamentos.findById(50L)).thenReturn(Optional.of(noValido));
        ResponsableService service = new ResponsableService(responsables, departamentos, oficinas);
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.create(
                new NuevoResponsableRequest("PERSONA", "CARGO", "", "12345678", 1L, 50L, "demo")));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        verify(responsables, never()).save(any());
    }

}
