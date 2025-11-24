package com.petstore.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Habilita las tareas programadas
public class PetstoreFeature5BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetstoreFeature5BackendApplication.class, args);
	}

}
