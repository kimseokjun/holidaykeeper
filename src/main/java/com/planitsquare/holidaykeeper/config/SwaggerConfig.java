package com.planitsquare.holidaykeeper.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Holiday Keeper API")
                        .description("전 세계 공휴일 관리 시스템 API")
                        .version("v1.0.0"));
    }
}
