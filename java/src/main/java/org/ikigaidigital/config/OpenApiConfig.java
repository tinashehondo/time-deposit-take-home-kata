package org.ikigaidigital.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI timeDepositOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("XA Bank Time Deposit API")
                        .description("RESTful API for managing time deposit accounts and calculating interest")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("XA Bank")
                                .email("support@xabank.com")));
    }
}
