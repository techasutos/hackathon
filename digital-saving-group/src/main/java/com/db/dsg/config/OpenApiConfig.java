package com.db.dsg.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "DSG Api", version = "v1", description = "Digital Saving Group Apis")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {


//    @Bean
//    public OpenAPI openAPI() {
//        return new OpenAPI()
//                .info(new io.swagger.v3.oas.models.info.Info().title("API Docs").version("v1"))
//                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
//                .components(new Components().addSecuritySchemes("bearerAuth",
//                        new io.swagger.v3.oas.models.security.SecurityScheme()
//                                .name("Authorization")
//                                .type(SecuritySchemeType.Type.HTTP)
//                                .scheme("bearer")
//                                .bearerFormat("JWT")
//                ));
//    }

}