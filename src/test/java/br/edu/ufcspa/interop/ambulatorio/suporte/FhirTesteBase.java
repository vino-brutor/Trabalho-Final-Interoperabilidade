package br.edu.ufcspa.interop.ambulatorio.suporte;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Base dos testes: sobe a aplicacao inteira numa porta aleatoria e oferece
 * um cliente FHIR (HAPI) apontado para /fhir. Nao precisa alterar.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class FhirTesteBase {

    protected static final FhirContext CTX = FhirContext.forR4Cached();

    static {
        // O cliente nao consulta o /metadata antes de cada chamada
        CTX.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
    }

    @LocalServerPort
    protected int porta;

    protected IGenericClient cliente() {
        return CTX.newRestfulGenericClient("http://localhost:" + porta + "/fhir");
    }

    /** GET "cru" (sem o cliente FHIR), aceitando qualquer formato de resposta. */
    protected HttpResponse<String> getHttp(String caminho) throws IOException, InterruptedException {
        HttpRequest requisicao = HttpRequest.newBuilder(URI.create("http://localhost:" + porta + caminho))
                .header("Accept", "*/*")
                .GET()
                .build();
        return HttpClient.newHttpClient().send(requisicao, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    /** Interacoes (read, create, update...) que o CapabilityStatement declara para o tipo de recurso. */
    protected Set<String> interacoesDeclaradas(String tipoRecurso) {
        CapabilityStatement cs = cliente().capabilities().ofType(CapabilityStatement.class).execute();
        return cs.getRest().stream()
                .flatMap(rest -> rest.getResource().stream())
                .filter(recurso -> tipoRecurso.equals(recurso.getType()))
                .flatMap(recurso -> recurso.getInteraction().stream())
                .map(interacao -> interacao.getCode().toCode())
                .collect(Collectors.toSet());
    }

    /** Le um JSON da pasta exemplos/ na raiz do projeto. */
    protected <T extends IBaseResource> T exemploDaPasta(Class<T> tipo, String arquivo) throws IOException {
        String json = Files.readString(Path.of("exemplos", arquivo), StandardCharsets.UTF_8);
        return CTX.newJsonParser().parseResource(tipo, json);
    }

    /** Le um JSON de src/test/resources. */
    protected <T extends IBaseResource> T exemploDoClasspath(Class<T> tipo, String caminho) throws IOException {
        try (InputStream entrada = getClass().getResourceAsStream("/" + caminho)) {
            assertNotNull(entrada, "Arquivo de teste nao encontrado: " + caminho);
            String json = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
            return CTX.newJsonParser().parseResource(tipo, json);
        }
    }

    /** Executa a acao, exige que o servidor responda com erro e devolve o status HTTP. */
    protected int statusDoErro(Executable acao) {
        BaseServerResponseException erro = assertThrows(BaseServerResponseException.class, acao,
                "O servidor deveria ter recusado a requisicao");
        return erro.getStatusCode();
    }

    /** CPF ficticio aleatorio (11 digitos), para nao colidir com os ja cadastrados. */
    protected static String cpfAleatorio() {
        StringBuilder cpf = new StringBuilder("9");
        for (int i = 0; i < 10; i++) {
            cpf.append(ThreadLocalRandom.current().nextInt(10));
        }
        return cpf.toString();
    }
}
