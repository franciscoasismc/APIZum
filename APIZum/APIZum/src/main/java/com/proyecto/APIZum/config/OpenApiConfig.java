package com.proyecto.APIZum.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "APIZum",
        version     = "1.0",
        description = "API REST de transferencias monetarias entre usuarios. " +
                      "Requiere autenticación JWT para la mayoría de endpoints. " +
                      "Documentación generada con springdoc-openapi.",
        contact     = @Contact(name = "Francisco", email = "franciscomzco@protonmail.com")
    ),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name   = "bearerAuth",
    type   = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in     = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // springdoc-openapi auto-configura el resto; esta clase solo centraliza metadatos.
}
