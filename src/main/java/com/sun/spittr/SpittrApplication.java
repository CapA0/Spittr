package com.sun.spittr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpittrApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpittrApplication.class, args);
    }

}
