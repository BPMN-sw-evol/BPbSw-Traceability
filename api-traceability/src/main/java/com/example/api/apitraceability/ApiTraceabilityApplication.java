package com.example.api.apitraceability;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import Interfaces.Traceability;

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
		Traceability.main(args);
	}
}
