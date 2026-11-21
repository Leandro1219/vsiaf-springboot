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
                        .title("VSIAF 3.2 - API REST de referencia")
                        .version("Proyecto academico - presentacion")
                        .description("API REST desarrollada con Java 21, Spring Boot y PostgreSQL. "
                                + "Los endpoints estan agrupados por ventanas del VSIAF 3.2. "
                                + "El modulo Oficina se ajusta a los campos del archivo OFICINA.DBF "
                                + "y trabaja con la entidad Ministerio de Relaciones Exteriores (10). "
                                + "Los datos de demostracion son ficticios y no se sincronizan con "
                                + "la base de datos del programa VSIAF original."))
                .tags(List.of(
                        new Tag().name("Activos Fijos")
                                .description("Registro y consulta de activos, ficha, movimientos y operaciones "
                                        + "existentes en el backend. No todas las reglas se han contrastado "
                                        + "con el programa VSIAF original."),
                        new Tag().name("Grupos y Auxiliares")
                                .description("Grupos contables, auxiliares y relaciones disponibles en el proyecto."),
                        new Tag().name("Oficinas y Responsables")
                                .description("OFICINA: GET, Nuevo+Guardar (POST), Modificar+Guardar (PUT), "
                                        + "Activar/Inactivar (PATCH). La entidad es Ministerio de Relaciones Exteriores "
                                        + "(codigo 10). El alta pide solo nombre y observacion; CODOFIC y contexto "
                                        + "institucional se asignan automaticamente. UNIDAD vacia no exige una unidad ficticia. Los nombres de los campos "
                                        + "son iguales en las respuestas de Oficina. Se valida que el nombre no se "
                                        + "repita y que tenga maximo 65 caracteres. Los campos se contrastaron con "
                                        + "OFICINA.DBF; se consulta por CODOFIC sin exponer el id interno de PostgreSQL. Responsables conserva su "
                                        + "desarrollo previo y se revisa por separado. Guardar equivale a POST/PUT; "
                                        + "Deshacer y Salir son acciones de pantalla."),
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
