package org.reactome.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootApplication(scanBasePackages = "org.reactome.server")
public class ExperimentDigesterApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ExperimentDigesterApplication.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        return builder;
    }

    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter();
        converter.setWriteAcceptCharset(false);
        converter.setSupportedMediaTypes(List.of(
                new MediaType("text","plain", StandardCharsets.UTF_8),
                new MediaType("application","json", StandardCharsets.UTF_8)
        ));
        return converter;
    }

}
