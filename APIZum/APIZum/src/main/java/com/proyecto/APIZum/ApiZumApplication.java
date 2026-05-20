package com.proyecto.APIZum;

import com.proyecto.APIZum.security.RSAKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// Habilita la carga de RSAKeyProperties desde application.properties
@SpringBootApplication
@EnableConfigurationProperties(RSAKeyProperties.class)
public class ApiZumApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiZumApplication.class, args);
	}
}
