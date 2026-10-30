package br.edu.ufcspa.interop.ambulatorio.verificacao;

import br.edu.ufcspa.interop.ambulatorio.suporte.FhirTesteBase;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verificacao basica de AllergyIntolerance. Passar aqui NAO garante nota maxima:
 * a correcao tambem confere systems, codigos e as invariantes do recurso.
 */
class VerificacaoAllergyIntoleranceTest extends FhirTesteBase {

    @Test
    @DisplayName("Lacuna 3: o CapabilityStatement declara read, create e update para AllergyIntolerance")
    void capability() {
        assertTrue(interacoesDeclaradas("AllergyIntolerance").containsAll(Set.of("read", "create", "update")),
                "Interacoes declaradas: " + interacoesDeclaradas("AllergyIntolerance"));
    }

    @Test
    @DisplayName("GET /AllergyIntolerance/1 devolve a alergia a dipirona da Maria Clara")
    void lerExistente() {
        AllergyIntolerance a = cliente().read().resource(AllergyIntolerance.class).withId("1").execute();
        assertEquals("1", a.getIdElement().getIdPart());
        assertEquals("Patient/1", a.getPatient().getReference());
        assertEquals("Dipirona", a.getCode().getText());
        assertTrue(a.hasVerificationStatus(), "verificationStatus deveria estar presente");
        assertTrue(a.hasClinicalStatus(), "clinicalStatus deveria estar presente");
    }

    @Test
    @DisplayName("GET /AllergyIntolerance/9999 responde 404")
    void lerInexistente() {
        assertThrows(ResourceNotFoundException.class,
                () -> cliente().read().resource(AllergyIntolerance.class).withId("9999").execute());
    }

    @Test
    @DisplayName("POST e PUT com o exemplo do grupo (exemplos/allergyintolerance-valida.json)")
    void criarEAtualizarComExemplo() throws Exception {
        AllergyIntolerance exemplo = exemploDaPasta(AllergyIntolerance.class, "allergyintolerance-valida.json");

        MethodOutcome criado = cliente().create().resource(exemplo).execute();
        assertNotNull(criado.getId(), "O POST deveria devolver o id criado (cabecalho Location)");
        String id = criado.getId().getIdPart();

        AllergyIntolerance lida = cliente().read().resource(AllergyIntolerance.class).withId(id).execute();
        assertEquals(exemplo.getCode().getText(), lida.getCode().getText());

        lida.getCode().setText("Amendoim e castanhas");
        lida.setId(id);
        cliente().update().resource(lida).withId(id).execute();

        AllergyIntolerance atualizada = cliente().read().resource(AllergyIntolerance.class).withId(id).execute();
        assertEquals("Amendoim e castanhas", atualizada.getCode().getText(),
                "A substancia deveria ter sido atualizada pelo PUT");
    }
}
