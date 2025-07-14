package com.franchise.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI franchiseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Franchise Management API")
                        .description("API Reactiva para gestión de franquicias, sucursales y productos")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Franchise API Team")
                                .email("wildsrincon.developer@gmail.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor de Desarrollo")
                ));
    }
}
