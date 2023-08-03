package com.fullcycle.admin.catalog.infraestructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalog.infraestructure.configuration.json.Json;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return Json.mapper();
    }
}
