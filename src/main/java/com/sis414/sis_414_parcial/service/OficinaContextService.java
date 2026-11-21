package com.sis414.sis_414_parcial.service;

import com.sis414.sis_414_parcial.entity.Oficina;
import com.sis414.sis_414_parcial.entity.UnidadAdmin;
import com.sis414.sis_414_parcial.repository.UnidadAdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

/**
 * ENTIDAD C(4) y UNIDAD C(5) se obtienen del contexto (config.dbf), sin pedir
 * IDs internos en la ventana. UNIDAD="" es un valor valido y NO exige que
 * exista un registro artificial en UNIDADADMIN.
 */
@Service
public class OficinaContextService {
    private final UnidadAdminRepository unidades;
    private final JdbcTemplate jdbc;
    private final String entidadCodigo;
    private final String unidadCodigo;
    private final String usuarioPrueba;

    public OficinaContextService(UnidadAdminRepository unidades, JdbcTemplate jdbc,
            @Value("${vsiaf.entidad.codigo:10}") String entidadCodigo,
            @Value("${vsiaf.unidad.codigo:}") String unidadCodigo,
            @Value("${vsiaf.usuario:demo}") String usuarioPrueba) {
        this.unidades = unidades;
        this.jdbc = jdbc;
        this.entidadCodigo = entidadCodigo.strip();
        this.unidadCodigo = unidadCodigo.strip();
        this.usuarioPrueba = usuarioPrueba.strip();
        if (this.entidadCodigo.isBlank() || this.entidadCodigo.length() > 4)
            throw new IllegalArgumentException("ENTIDAD debe contener entre 1 y 4 caracteres");
        if (this.unidadCodigo.length() > 5)
            throw new IllegalArgumentException("UNIDAD no puede superar 5 caracteres");
        if (this.usuarioPrueba.isBlank() || this.usuarioPrueba.length() > 8)
            throw new IllegalArgumentException("USUAR debe contener entre 1 y 8 caracteres");
    }

    public String entidadCodigo() { return entidadCodigo; }
    public String unidadCodigo() { return unidadCodigo; }
    public String usuarioPrueba() { return usuarioPrueba; }

    public boolean pertenece(Oficina oficina) {
        String e = oficina.getEntidadCodigo() != null ? oficina.getEntidadCodigo()
                : oficina.getUnidad() != null && oficina.getUnidad().getEntidad() != null
                    ? oficina.getUnidad().getEntidad().getCodigo() : null;
        String u = oficina.getUnidadCodigo() != null ? oficina.getUnidadCodigo()
                : oficina.getUnidad() != null ? oficina.getUnidad().getUnidad() : null;
        return Objects.equals(entidadCodigo, e) && Objects.equals(unidadCodigo, u);
    }

    public void exigirEnContexto(Oficina oficina) {
        if (!pertenece(oficina))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Oficina fuera de la entidad y unidad configuradas");
    }

    /**
     * Serializa la asignacion de CODOFIC y la comprobacion de nombres entre
     * peticiones concurrentes, sin depender de una fila ficticia de UNIDADADMIN.
     * PostgreSQL libera este bloqueo automaticamente al finalizar la transaccion.
     */
    public void bloquearEscritura() {
        jdbc.execute("SELECT pg_advisory_xact_lock(hashtext(?))", (PreparedStatementCallback<Void>) ps -> {
            ps.setString(1, "VSIAF:OFICINA:" + entidadCodigo + ":" + unidadCodigo);
            ps.execute();
            return null;
        });
    }

    /**
     * Conserva una FK tecnica solamente cuando existe una unidad REAL con
     * codigo no vacio. No se crea una unidad administrativa para la cadena "".
     */
    public UnidadAdmin unidadExistenteOpcional() {
        if (unidadCodigo.isEmpty()) return null;
        List<UnidadAdmin> coincidencias = unidades.findAll().stream()
                .filter(u -> u.getEntidad() != null && entidadCodigo.equals(u.getEntidad().getCodigo()))
                .filter(u -> unidadCodigo.equals(u.getUnidad()))
                .toList();
        if (coincidencias.size() > 1)
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Hay varias unidades administrativas con el codigo configurado");
        return coincidencias.isEmpty() ? null : coincidencias.get(0);
    }
}
