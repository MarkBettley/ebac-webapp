package com.ebac.modulo45;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.ebac.modulo45.entity")
@EnableJpaRepositories(basePackages = "com.ebac.modulo45.repository")
public class EbacWebAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbacWebAppApplication.class, args);
    }
}
