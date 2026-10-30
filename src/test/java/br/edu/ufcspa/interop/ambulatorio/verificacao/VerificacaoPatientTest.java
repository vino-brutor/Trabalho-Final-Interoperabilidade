package br.edu.ufcspa.interop.ambulatorio.verificacao;

import br.edu.ufcspa.interop.ambulatorio.suporte.FhirTesteBase;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verificacao basica de Patient. Passar aqui NAO garante nota maxima:
 * a correcao tambem confere systems, codigos e regras de validacao.
 */
class VerificacaoPatientTest extends FhirTesteBase {

    @Test
    @DisplayName("Lacuna 3: o CapabilityStatement declara read, create e update para Patient")
    void capability() {
        assertTrue(interacoesDeclaradas("Patient").containsAll(java.util.Set.of("read", "create", "update")),
                "Interacoes declaradas: " + interacoesDeclaradas("Patient"));
    }

    @Test
    @DisplayName("GET /Patient/1 devolve a Maria Clara")
    void lerExistente() {
        Patient p = cliente().read().resource(Patient.class).withId("1").execute();
        assertEquals("1", p.getIdElement().getIdPart());
        assertTrue(p.hasIdentifier(), "Patient deveria ter identifier (CPF)");
        assertEquals("Souza Pereira", p.getNameFirstRep().getFamily());
        assertEquals(AdministrativeGender.FEMALE, p.getGender());
        assertEquals("1985-03-12", p.getBirthDateElement().getValueAsString());
    }

    @Test
    @DisplayName("GET /Patient/9999 responde 404")
    void lerInexistente() {
        assertThrows(ResourceNotFoundException.class,
                () -> cliente().read().resource(Patient.class).withId("9999").execute());
    }

    @Test
    @DisplayName("POST e PUT com o exemplo do grupo (exemplos/patient-valido.json)")
    void criarEAtualizarComExemplo() throws Exception {
        Patient exemplo = exemploDaPasta(Patient.class, "patient-valido.json");

        MethodOutcome criado = cliente().create().resource(exemplo).execute();
        assertNotNull(criado.getId(), "O POST deveria devolver o id criado (cabecalho Location)");
        String id = criado.getId().getIdPart();

        Patient lido = cliente().read().resource(Patient.class).withId(id).execute();
        assertEquals(exemplo.getNameFirstRep().getFamily(), lido.getNameFirstRep().getFamily());

        lido.getTelecom().removeIf(c -> c.getSystem() == ContactPoint.ContactPointSystem.PHONE);
        lido.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue("(51) 90000-0000");
        lido.setId(id);
        cliente().update().resource(lido).withId(id).execute();

        Patient atualizado = cliente().read().resource(Patient.class).withId(id).execute();
        assertTrue(atualizado.getTelecom().stream().anyMatch(c -> "(51) 90000-0000".equals(c.getValue())),
                "O telefone deveria ter sido atualizado pelo PUT");
    }
}
