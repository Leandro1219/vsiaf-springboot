package com.sis414.sis_414_parcial.service;

import com.sis414.sis_414_parcial.dto.ModificarOficinaRequest;
import com.sis414.sis_414_parcial.dto.NuevaOficinaRequest;
import com.sis414.sis_414_parcial.dto.OficinaDto;
import com.sis414.sis_414_parcial.entity.Oficina;
import com.sis414.sis_414_parcial.repository.OficinaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/** Opera sobre CODOFIC, ENTIDAD y UNIDAD del DBF; ID PostgreSQL es invisible en la API Oficina. */
@Service
public class OficinaService {
    public static final int ESTADO_ACTIVO = 1;
    public static final int ESTADO_INACTIVO_DBF = 3;

    private final OficinaRepository repository;
    private final OficinaContextService contexto;

    public OficinaService(OficinaRepository repository, OficinaContextService contexto) {
        this.repository = repository;
        this.contexto = contexto;
    }

    @Transactional(readOnly = true)
    public List<OficinaDto> findAll() {
        return repository.findAll().stream().filter(contexto::pertenece).map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public OficinaDto findByCodigo(Integer codOfic) { return toDto(getPorCodigo(codOfic)); }

    /** Nuevo + Guardar: no se solicitan IDs que no figuran en la ventana original. */
    @Transactional
    public OficinaDto create(NuevaOficinaRequest request) {
        if (request == null) throw badRequest("Envie los datos de la oficina");
        String nombre = validarNombre(request.nomOfic());
        contexto.bloquearEscritura();
        comprobarNombreLibre(nombre, null);
        Integer max = repository.mayorCodigo(contexto.entidadCodigo(), contexto.unidadCodigo());
        int codigo = max == null ? 1 : max + 1;
        if (codigo > 9999) throw new ResponseStatusException(HttpStatus.CONFLICT,
                "CODOFIC excede los 4 digitos de OFICINA.DBF");

        Oficina oficina = new Oficina();
        oficina.setEntidadCodigo(contexto.entidadCodigo());
        oficina.setUnidadCodigo(contexto.unidadCodigo());
        oficina.setUnidad(contexto.unidadExistenteOpcional());
        oficina.setCodOfic(codigo);
        oficina.setNomOfic(nombre);
        oficina.setObserv(request.observ());
        oficina.setFeult(LocalDate.now());
        oficina.setUsuar(contexto.usuarioPrueba());
        oficina.setApiEstado(ESTADO_INACTIVO_DBF);
        return toDto(repository.saveAndFlush(oficina));
    }

    /** Modificar + Guardar: conserva CODOFIC, ENTIDAD, UNIDAD y API_ESTADO. */
    @Transactional
    public OficinaDto update(Integer codOfic, ModificarOficinaRequest request) {
        if (request == null) throw badRequest("Envie los datos modificados");
        String nombre = validarNombre(request.nomOfic());
        contexto.bloquearEscritura();
        Oficina oficina = getPorCodigo(codOfic);
        comprobarNombreLibre(nombre, oficina.getId());
        oficina.setNomOfic(nombre);
        oficina.setObserv(request.observ());
        oficina.setFeult(LocalDate.now());
        oficina.setUsuar(contexto.usuarioPrueba());
        return toDto(repository.save(oficina));
    }

    /** No cambia las relaciones internas de otros modulos; resuelve solo la ruta publica de Oficina. */
    @Transactional(readOnly = true)
    public Oficina getPorCodigo(Integer codOfic) {
        if (codOfic == null || codOfic < 1 || codOfic > 9999)
            throw badRequest("CODOFIC debe contener un valor entre 1 y 9999");
        List<Oficina> encontradas = repository.buscarPorCodigo(
                contexto.entidadCodigo(), contexto.unidadCodigo(), codOfic);
        if (encontradas.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Oficina no encontrada: CODOFIC=" + codOfic);
        if (encontradas.size() != 1)
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Existen varias oficinas con ese CODOFIC en el mismo contexto; revisar registros anteriores");
        Oficina oficina = encontradas.get(0);
        contexto.exigirEnContexto(oficina);
        return oficina;
    }

    public OficinaDto toDto(Oficina e) {
        String entidad = e.getEntidadCodigo() != null ? e.getEntidadCodigo()
                : e.getUnidad() != null && e.getUnidad().getEntidad() != null
                    ? e.getUnidad().getEntidad().getCodigo() : null;
        String unidad = e.getUnidadCodigo() != null ? e.getUnidadCodigo()
                : e.getUnidad() == null ? null : e.getUnidad().getUnidad();
        // Los estados 0 de la API anterior NO se transforman silenciosamente.
        // Ver sql/04_normalizar_estados_anteriores_OPCIONAL.sql.
        return new OficinaDto(entidad, unidad, e.getCodOfic(), e.getNomOfic(),
                e.getObserv(), e.getFeult(), e.getUsuar(), e.getApiEstado());
    }

    private void comprobarNombreLibre(String nombre, Long exceptoId) {
        boolean repetido = exceptoId == null
                ? repository.existeNombre(contexto.entidadCodigo(), nombre)
                : repository.existeNombreExcepto(contexto.entidadCodigo(), nombre, exceptoId);
        if (repetido) throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Ya existe una oficina con nombre '" + nombre + "' en esta entidad");
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw badRequest("El nombre de oficina es obligatorio");
        String normalizado = nombre.strip();
        if (normalizado.length() > 65) throw badRequest("NOMOFIC admite maximo 65 caracteres");
        return normalizado;
    }

    private static ResponseStatusException badRequest(String detalle) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, detalle);
    }
}
