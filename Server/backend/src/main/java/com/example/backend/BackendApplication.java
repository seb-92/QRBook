package com.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication( scanBasePackages = "com.example.backend" )
@EnableJpaRepositories
@EntityScan( value = "com.example.backend" )
@EnableWebMvc
public class BackendApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run( BackendApplication.class, args );
    }

    @Bean
    @Primary
    public ObjectMapper geObjMapper()
    {
        return new ObjectMapper().findAndRegisterModules();
    }
}
