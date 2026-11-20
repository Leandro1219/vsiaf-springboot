package com.sis414.sis_414_parcial.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Vista única de Swagger: los controladores se presentan por ventanas de VSIAF.
 * No se crean ni duplican endpoints para fines de exposición.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VSIAf")
                        .version("Segundo parcial")
                        .description("Aquí se encuentran los endpoints organizados según los diferentes módulos " +
                                "y funciones del programa VSIAF. Se utilizaron métodos HTTP como GET, POST, PUT " +
                                "y PATCH para realizar consultas, registrar información, modificar datos, activar " +
                                "e inactivar registros."))
                .tags(List.of(
                        new Tag().name("Activos Fijos")
                                .description("Registro y consulta de activos, ficha, movimientos y operaciones "
                                        + "existentes en el backend. No todas las reglas se han contrastado "
                                        + "con el programa VSIAF original."),
                        new Tag().name("Grupos y Auxiliares")
                                .description("Grupos contables, auxiliares y relaciones disponibles en el proyecto."),
                        new Tag().name("Oficinas y Responsables")
                                .description("OFICINAS: Nuevo + Guardar (POST), Modificar + Guardar (PUT), "
                                        + "Activar e Inactivar (PATCH). RESPONSABLES: Nuevo + Guardar (POST), "
                                        + "Modificar + Guardar (PUT), Activar e Inactivar (PATCH). "
                                        + "Código asignado automáticamente y estado inicial INACTIVO; "
                                        + "se consultan oficinas, responsables y los nueve departamentos de Expedido. "
                                        + "Un responsable puede activarse o inactivarse aunque su oficina "
                                        + "esté INACTIVA; ambos estados son independientes en este backend. "
                                        + "Deshacer y Salir no requieren endpoints REST."),
                        new Tag().name("Generar Reportes")
                                .description("Consultas y generación de datos para reportes. "
                                        + "Su alcance depende de las funciones implementadas en el backend."),
                        new Tag().name("Administrar Unidad")
                                .description("Entidades, unidades administrativas, selección y datos asociados."),
                        new Tag().name("Administradores")
                                .description("Gestión administrativa y seguridad. Algunas operaciones están "
                                        + "marcadas como pendientes y pueden devolver HTTP 501."),
                        new Tag().name("Respaldos BD")
                                .description("Operaciones de respaldo: algunas acciones están pendientes "
                                        + "y no realizan todavía un respaldo o restauración real."),
                        new Tag().name("Localización BD")
                                .description("Consulta y configuración de localización de archivos/datos."),
                        new Tag().name("Iniciar sesión")
                                .description("Endpoint de autenticación existente en el proyecto académico.")));
    }
}
