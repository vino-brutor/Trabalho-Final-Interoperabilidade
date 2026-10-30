package br.edu.ufcspa.interop.ambulatorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sistema ambulatorial.
 *
 * API proprietaria (ja pronta): http://localhost:8080/api
 * Servidor FHIR (a implementar): http://localhost:8080/fhir
 * Console do banco H2: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:ambulatorio)
 */
@SpringBootApplication
public class AmbulatorioApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmbulatorioApplication.class, args);
    }
}
