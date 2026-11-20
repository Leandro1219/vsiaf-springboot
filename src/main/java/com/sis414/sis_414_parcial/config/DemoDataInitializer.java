package com.sis414.sis_414_parcial.config;

import com.sis414.sis_414_parcial.entity.Entidad;
import com.sis414.sis_414_parcial.entity.Departamento;
import com.sis414.sis_414_parcial.entity.Oficina;
import com.sis414.sis_414_parcial.entity.Responsable;
import com.sis414.sis_414_parcial.entity.UnidadAdmin;
import com.sis414.sis_414_parcial.repository.EntidadRepository;
import com.sis414.sis_414_parcial.repository.DepartamentoRepository;
import com.sis414.sis_414_parcial.repository.OficinaRepository;
import com.sis414.sis_414_parcial.repository.ResponsableRepository;
import com.sis414.sis_414_parcial.repository.UnidadAdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Datos FICTICIOS y reutilizables, exclusivamente con el perfil "demo".
 * Nunca se ejecuta durante el arranque normal. La oficina se deja INACTIVA
 * para que pueda demostrarse una sola llamada a PATCH /activar.
 */
@Component
@Profile("demo")
public class DemoDataInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);
    private static final String ENTIDAD_DEMO = "ENTIDAD DEMO DE EVALUACION";
    private static final String UNIDAD_DEMO = "UNIDAD DEMO DE EVALUACION";
    private static final String OFICINA_DEMO = "OFICINA DEMO DE EVALUACION";
    private static final String RESPONSABLE_DEMO = "RESPONSABLE DEMO";

    private final EntidadRepository entidades;
    private final UnidadAdminRepository unidades;
    private final OficinaRepository oficinas;
    private final ResponsableRepository responsables;
    private final DepartamentoRepository departamentos;

    public DemoDataInitializer(EntidadRepository entidades, UnidadAdminRepository unidades,
                               OficinaRepository oficinas, ResponsableRepository responsables,
                               DepartamentoRepository departamentos) {
        this.entidades = entidades;
        this.unidades = unidades;
        this.oficinas = oficinas;
        this.responsables = responsables;
        this.departamentos = departamentos;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Entidad entidad = entidades.findAll().stream()
                .filter(e -> ENTIDAD_DEMO.equals(e.getDescEnt()))
                .findFirst()
                .orElseGet(() -> {
                    Entidad e = new Entidad();
                    e.setGestion(LocalDate.now().getYear());
                    e.setCodigo("D032");
                    e.setDescEnt(ENTIDAD_DEMO);
                    e.setSiglaEnt("EVAL");
                    return entidades.save(e);
                });

        UnidadAdmin unidad = unidades.findAll().stream()
                .filter(u -> UNIDAD_DEMO.equals(u.getDescrip()) && u.getEntidad() != null
                        && u.getEntidad().getId().equals(entidad.getId()))
                .findFirst()
                .orElseGet(() -> {
                    UnidadAdmin u = new UnidadAdmin();
                    u.setUnidad("D01");
                    u.setDescrip(UNIDAD_DEMO);
                    u.setCiudad("La Paz");
                    u.setEstadoUni(1);
                    u.setEntidad(entidad);
                    return unidades.save(u);
                });

        Oficina oficina = oficinas.findAll().stream()
                .filter(o -> OFICINA_DEMO.equals(o.getNomOfic()) && o.getUnidad() != null
                        && o.getUnidad().getId().equals(unidad.getId()))
                .findFirst()
                .orElseGet(() -> {
                    Oficina o = new Oficina();
                    o.setCodOfic(9321);
                    o.setNomOfic(OFICINA_DEMO);
                    o.setObserv("Datos ficticios para demostracion de Swagger UI");
                    o.setFeult(LocalDate.now());
                    o.setUsuar("demo");
                    o.setUnidad(unidad);
                    return oficinas.save(o);
                });

        // Rearmar solamente ESTE registro ficticio al reiniciar: demo repetible.
        oficina.setApiEstado(0);
        oficinas.save(oficina);

        // Catálogo de Expedido (nueve departamentos de Bolivia) solo en base demo.
        String[][] nombres = {
                {"La Paz", "LPZ"}, {"Cochabamba", "CBB"}, {"Santa Cruz", "SCZ"},
                {"Oruro", "ORU"}, {"Potosi", "PTS"}, {"Chuquisaca", "CHQ"},
                {"Tarija", "TJA"}, {"Beni", "BEN"}, {"Pando", "PND"}
        };
        for (int i = 0; i < nombres.length; i++) {
            final int codigo = i + 1;
            final String nombre = nombres[i][0];
            final String sigla = nombres[i][1];
            if (departamentos.findAll().stream().noneMatch(d -> nombre.equalsIgnoreCase(d.getDescripcion()))) {
                Departamento d = new Departamento();
                d.setCodigo(codigo);
                d.setDescripcion(nombre);
                d.setSigla(sigla);
                departamentos.save(d);
            }
        }
        Departamento expedidoDemo = departamentos.findAll().stream()
                .filter(d -> "La Paz".equalsIgnoreCase(d.getDescripcion()))
                .findFirst().orElseThrow();

        Responsable responsableDemo = responsables.findByOficinaId(oficina.getId()).stream()
                .filter(r -> RESPONSABLE_DEMO.equals(r.getNomResp()))
                .findFirst().orElseGet(() -> {
                    Responsable r = new Responsable();
                    r.setCodResp(9321);
                    r.setNomResp(RESPONSABLE_DEMO);
                    r.setCargo("Encargado de prueba");
                    r.setObserv("Datos inventados, no corresponden al VSIAF real");
                    r.setFeult(LocalDate.now());
                    r.setUsuar("demo");
                    r.setOficina(oficina);
                    return r;
                });
        // Actualiza únicamente el registro DEMO, incluso al reutilizar la base anterior.
        responsableDemo.setCi("12345678");
        responsableDemo.setApiEstado(0);
        responsableDemo.setDepartamentoExp(expedidoDemo);
        responsables.save(responsableDemo);

        log.info("====================================================");
        log.info("EVALUACION VSIAF (solo base de prueba y perfil demo)");
        log.info("Swagger: http://localhost:8080/swagger-ui.html");
        log.info("Swagger unificado: todos los endpoints agrupados por ventanas de VSIAF");
        log.info("Modulo: Oficinas y Responsables");
        log.info("METODO: PATCH /api/oficinas/{}/activar", oficina.getId());
        log.info("ID para Try it out: {}", oficina.getId());
        log.info("Estado inicial: INACTIVO; despues de PATCH: ACTIVO");
        log.info("====================================================");
    }
}
