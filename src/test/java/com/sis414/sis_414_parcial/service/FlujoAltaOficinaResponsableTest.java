package com.sis414.sis_414_parcial.service;

import com.sis414.sis_414_parcial.dto.*;
import com.sis414.sis_414_parcial.entity.*;
import com.sis414.sis_414_parcial.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FlujoAltaOficinaResponsableTest {

    @Test
    void nuevaOficinaGuardaInactivaYGeneraCodigoSinRecibirlo() {
        OficinaRepository oficinas = mock(OficinaRepository.class);
        UnidadAdminRepository unidades = mock(UnidadAdminRepository.class);
        UnidadAdmin unidad = new UnidadAdmin();
        unidad.setId(3L);
        when(unidades.findById(3L)).thenReturn(Optional.of(unidad));
        when(oficinas.saveAndFlush(any(Oficina.class))).thenAnswer(inv -> {
            Oficina o = inv.getArgument(0); o.setId(45L); return o;
        });
        when(oficinas.save(any(Oficina.class))).thenAnswer(inv -> inv.getArgument(0));
        OficinaDto out = new OficinaService(oficinas, unidades)
                .create(new NuevaOficinaRequest("REGISTRO", "Registros", 3L, "demo"));
        assertEquals(45, out.codOfic());
        assertEquals(0, out.apiEstado());
        assertEquals(3L, out.unidadId());
        verify(oficinas).saveAndFlush(any(Oficina.class));
    }

    @Test
    void modificarOficinaNoCambiaCodigoUnidadNiEstado() {
        OficinaRepository oficinas = mock(OficinaRepository.class);
        UnidadAdminRepository unidades = mock(UnidadAdminRepository.class);
        Oficina oficina = new Oficina();
        oficina.setId(10L); oficina.setCodOfic(9321); oficina.setApiEstado(1);
        UnidadAdmin unidad = new UnidadAdmin(); unidad.setId(2L); oficina.setUnidad(unidad);
        when(oficinas.findById(10L)).thenReturn(Optional.of(oficina));
        when(oficinas.save(any(Oficina.class))).thenAnswer(inv -> inv.getArgument(0));
        OficinaDto result = new OficinaService(oficinas, unidades)
                .update(10L, new ModificarOficinaRequest("OTRO NOMBRE", "Nueva descripción", null));
        assertEquals("OTRO NOMBRE", result.nomOfic());
        assertEquals(9321, result.codOfic());
        assertEquals(1, result.apiEstado());
        assertEquals(2L, result.unidadId());
        verifyNoInteractions(unidades);
    }

    @Test
    void nuevoResponsableGuardaInactivoCodigoAutomaticoYDepartamentoValido() {
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

        ResponsableDto resultado = new ResponsableService(responsables, departamentos, oficinas)
                .create(new NuevoResponsableRequest("PEDRO", "ENCARGADO", "Observación", "12345678", 4L, 8L, "demo"));
        assertEquals(90, resultado.codResp());
        assertEquals(0, resultado.apiEstado());
        assertEquals(4L, resultado.oficinaId());
        assertEquals(8L, resultado.departamentoExpId());
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
    void ciCortoSeRechazaSinGuardar() {
        ResponsableRepository responsables = mock(ResponsableRepository.class);
        DepartamentoRepository departamentos = mock(DepartamentoRepository.class);
        OficinaRepository oficinas = mock(OficinaRepository.class);
        ResponsableService service = new ResponsableService(responsables, departamentos, oficinas);
        NuevoResponsableRequest request = new NuevoResponsableRequest("PERSONA", "CARGO", "", "123", 1L, 1L, "demo");
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.create(request));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        verify(responsables, never()).save(any());
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

    @Test
    void oficinaSinUnidadEsRechazada() {
        OficinaService service = new OficinaService(mock(OficinaRepository.class), mock(UnidadAdminRepository.class));
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () ->
                service.create(new NuevaOficinaRequest("REGISTRO", "", null, "demo")));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    }
}
