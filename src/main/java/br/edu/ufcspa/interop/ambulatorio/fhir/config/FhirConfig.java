package br.edu.ufcspa.interop.ambulatorio.fhir.config;

import java.util.List;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.openapi.OpenApiInterceptor;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;

/**
 * Configuracao do servidor FHIR (HAPI FHIR Plain Server) dentro da aplicacao Spring Boot.
 *
 * Esta classe esta INCOMPLETA: resolva as LACUNAS 1 a 5.
 */
@Configuration
public class FhirConfig {

    /**
     * LACUNA 1
     * O FhirContext e o "ponto de partida" do HAPI: ele sabe parsear e serializar
     * recursos de UMA versao especifica do FHIR. Crie e devolva o contexto da versao
     * usada neste projeto (confira quais estruturas do HAPI estao declaradas no pom.xml).
     * Dica: criar um FhirContext e caro; existe uma forma de reaproveitar uma instancia ja criada.
     */
    @Bean
    public FhirContext fhirContext() {
        throw new IllegalStateException("LACUNA 1 nao resolvida: FhirConfig.fhirContext()");
    }

    /** Todos os beans do Spring que implementam IResourceProvider chegam em providers. */
    @Bean
    public RestfulServer restfulServer(FhirContext fhirContext, List<IResourceProvider> providers) {

        RestfulServer server = new RestfulServer(fhirContext);

        server.setServerName("Servidor FHIR (UFCSPA)");
        server.setServerVersion("1.0.0");

        // LACUNA 3
        // Os providers chegam como parametro, mas o servidor ainda nao sabe que eles existem.
        // Sem isso, o servidor sobe e responde o /metadata, mas nao declara nem atende nenhum recurso.

        server.setDefaultResponseEncoding(EncodingEnum.JSON);

        // LACUNA 4
        // As respostas devem vir indentadas (legiveis por humanos) mesmo quando o cliente
        // nao pede ?_pretty=true.

        server.registerInterceptor(new ResponseHighlighterInterceptor());

        server.registerInterceptor(new OpenApiInterceptor());

        // LACUNA 5
        // Na aula de SMART on FHIR vimos aplicacoes que rodam no navegador e chamam o servidor FHIR
        // a partir de OUTRA origem (outro dominio/porta). O navegador bloqueia essas chamadas se o
        // servidor nao responder com os cabecalhos de CORS. Habilite CORS neste servidor usando o
        // mecanismo que o proprio HAPI FHIR oferece para isso.

        return server;
    }

    @Bean
    public ServletRegistrationBean<RestfulServer> fhirServlet(RestfulServer server) {

        // LACUNA 2
        // O servidor FHIR deve responder sob http://localhost:8080/fhir
        // (ex.: http://localhost:8080/fhir/metadata), SEM atrapalhar a API proprietaria em /api.
        // Enquanto esta lacuna nao for resolvida, observe o que acontece com GET /api/pacientes.
        ServletRegistrationBean<RestfulServer> registro = new ServletRegistrationBean<>(server);
        registro.setName("FhirServlet");

        return registro;
    }

}
