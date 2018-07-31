package org.reactome.server.config;

import org.reactome.server.Application;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:digester.properties"})
@ComponentScan(basePackageClasses = Application.class)
//@Import(value = CustomSecurityConfiguration.class)
class ApplicationConfig {

}