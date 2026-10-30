package br.edu.ufcspa.interop.ambulatorio.verificacao;

import br.edu.ufcspa.interop.ambulatorio.suporte.FhirTesteBase;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica as lacunas de configuracao do servidor FHIR (1, 2, 4 e 5).
 * A lacuna 3 e verificada no teste "capability" de cada recurso.
 */
class ConfiguracaoServidorTest extends FhirTesteBase {

    @Test
    @DisplayName("Lacunas 1 e 2: /fhir/metadata responde com um CapabilityStatement FHIR 4.0.1")
    void metadataResponde() {
        CapabilityStatement cs = cliente().capabilities().ofType(CapabilityStatement.class).execute();
        assertNotNull(cs);
        assertEquals("4.0.1", cs.getFhirVersion().toCode(), "O servidor deve declarar FHIR R4 (4.0.1)");
    }

    @Test
    @DisplayName("Lacuna 2: a API proprietaria continua respondendo em /api")
    void apiProprietariaContinuaFuncionando() throws Exception {
        HttpResponse<String> resposta = getHttp("/api/pacientes");
        assertEquals(200, resposta.statusCode(),
                "GET /api/pacientes deveria responder 200; o servlet FHIR nao pode capturar todas as URLs");
        assertTrue(resposta.body().contains("52601815906"));
    }

    @Test
    @DisplayName("Lacuna 4: a resposta vem indentada (pretty print) mesmo sem pedir _pretty")
    void respostaIndentada() throws Exception {
        HttpResponse<String> resposta = getHttp("/fhir/metadata");
        assertEquals(200, resposta.statusCode());
        assertTrue(resposta.body().contains("\n  \""), "O JSON deveria vir indentado, com quebras de linha");
    }

    @Test
    @DisplayName("Lacuna 5: CORS liberado para aplicacoes web de outra origem")
    void corsLiberado() throws Exception {
        HttpRequest requisicao = HttpRequest.newBuilder(URI.create("http://localhost:" + porta + "/fhir/metadata"))
                .header("Accept", "application/fhir+json")
                .header("Origin", "http://app-smart.exemplo.org")
                .GET()
                .build();
        HttpResponse<String> resposta = HttpClient.newHttpClient().send(requisicao, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resposta.statusCode());
        assertTrue(resposta.headers().firstValue("Access-Control-Allow-Origin").isPresent(),
                "A resposta deveria trazer o cabecalho Access-Control-Allow-Origin");
    }
}
