package com.controledu;

import com.controledu.service.TipoGravedadService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ControlEduApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControlEduApplication.class, args);
	}

	// --- BLOQUE AÑADIDO ---
	// Este código se ejecutará automáticamente una vez que la aplicación inicie.
	@Bean
	CommandLineRunner init(TipoGravedadService tipoGravedadService) {
		return args -> {
			// Llama a tu método para crear los tipos de gravedad por defecto
			tipoGravedadService.initializeDefaultGravedades();

			// Mensaje de confirmación en la consola
			System.out.println(" Tipos de gravedad inicializados correctamente (si la tabla estaba vacía).");
		};
	}
	// ----------------------
}