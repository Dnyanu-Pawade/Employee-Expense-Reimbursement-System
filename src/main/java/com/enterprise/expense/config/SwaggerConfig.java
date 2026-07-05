package com.enterprise.expense.config;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;
@Configuration
public class SwaggerConfig {
    @Bean public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info().title("Enterprise Expense Management Platform API").version("2.0.0")
                .description("60+ REST APIs for enterprise expense management"))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Auth"))
            .components(new Components().addSecuritySchemes("Bearer Auth",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
