package com.sis414.sis_414_parcial.config;

import com.sis414.sis_414_parcial.dto.NuevaOficinaRequest;
import com.sis414.sis_414_parcial.repository.OficinaRepository;
import com.sis414.sis_414_parcial.service.OficinaContextService;
import com.sis414.sis_414_parcial.service.OficinaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** En modo demo crea SOLO una oficina ficticia, nunca una unidad ficticia. */
@Component
@Profile("demo")
public class DemoDataInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);
    private static final String NOMBRE = "OFICINA DEMO DE EVALUACION";
    private final OficinaRepository oficinas;
    private final OficinaService oficinaService;
    private final OficinaContextService contexto;

    public DemoDataInitializer(OficinaRepository oficinas, OficinaService oficinaService,
                               OficinaContextService contexto) {
        this.oficinas = oficinas;
        this.oficinaService = oficinaService;
        this.contexto = contexto;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!"10".equals(contexto.entidadCodigo()) || !contexto.unidadCodigo().isEmpty()) {
            log.info("Perfil demo: contexto distinto al de config.dbf; no se generan oficinas de ejemplo");
            return;
        }
        var existente = oficinas.findAll().stream()
                .filter(contexto::pertenece)
                .filter(o -> NOMBRE.equalsIgnoreCase(o.getNomOfic()))
                .findFirst();
        if (existente.isEmpty()) {
            // No violar la unicidad por ENTIDAD si ese nombre ya existe en otra unidad.
            if (oficinas.existeNombre(contexto.entidadCodigo(), NOMBRE)) {
                log.warn("La oficina de demostracion ya existe en otra unidad de esta entidad; no se crea otra");
                return;
            }
            var creada = oficinaService.create(new NuevaOficinaRequest(NOMBRE,
                    "Registro ficticio para pruebas del modulo Oficina"));
            log.info("Oficina demo creada: CODOFIC={}", creada.codOfic());
        } else {
            log.info("Oficina demo existente: CODOFIC={} | estado={}",
                    existente.get().getCodOfic(), existente.get().getApiEstado());
        }
        log.info("Para activar una oficina ficticia INACTIVA, usar PATCH /api/oficinas/{codOfic}/activar");
    }
}
