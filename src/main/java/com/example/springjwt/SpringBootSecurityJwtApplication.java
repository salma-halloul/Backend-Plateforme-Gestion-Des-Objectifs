package com.example.springjwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
@EnableConfigurationProperties
@EntityScan(basePackages = {"com.example.springjwt.models"})
public class SpringBootSecurityJwtApplication {

    private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringBootSecurityJwtApplication.applicationContext = SpringApplication.run(SpringBootSecurityJwtApplication.class, args);
    }
}

