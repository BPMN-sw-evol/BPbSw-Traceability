package com.example.api.apitraceability;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import Interfaces.Traceability;
import java.io.File;

@SpringBootApplication
public class ApiTraceabilityApplication implements CommandLineRunner{

	public static void main(String[] args) {
		//SpringApplication.run(ApiTraceabilityApplication.class, args);
		SpringApplication app = new SpringApplication(ApiTraceabilityApplication.class);
		app.setHeadless(false);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		File carpetaSalida = new File(System.getProperty("user.dir") + File.separator + "output");
		if (!carpetaSalida.exists()) {
            carpetaSalida.mkdirs(); // Crea la carpeta y cualquier directorio padre que falte
        }

		Traceability.main(args);
	}
}
