package br.edu.ufcspa.interop.ambulatorio.verificacao;

import br.edu.ufcspa.interop.ambulatorio.suporte.FhirTesteBase;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.r4.model.Observation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verificacao basica de Observation. Passar aqui NAO garante nota maxima:
 * a correcao tambem confere systems, codigos LOINC/UCUM e regras de validacao.
 */
class VerificacaoObservationTest extends FhirTesteBase {

    @Test
    @DisplayName("Lacuna 3: o CapabilityStatement declara read, create e update para Observation")
    void capability() {
        assertTrue(interacoesDeclaradas("Observation").containsAll(Set.of("read", "create", "update")),
                "Interacoes declaradas: " + interacoesDeclaradas("Observation"));
    }

    @Test
    @DisplayName("GET /Observation/1 devolve a frequencia cardiaca da Maria Clara")
    void lerExistente() {
        Observation o = cliente().read().resource(Observation.class).withId("1").execute();
        assertEquals("1", o.getIdElement().getIdPart());
        assertTrue(o.hasStatus(), "status e obrigatorio em Observation");
        assertFalse(o.getCode().getCoding().isEmpty(), "code deveria ter ao menos um Coding");
        assertEquals("Patient/1", o.getSubject().getReference());
        assertTrue(o.hasValueQuantity(), "o valor deveria vir em valueQuantity");
        assertEquals(0, new BigDecimal("78").compareTo(o.getValueQuantity().getValue()));
        assertTrue(o.hasEffectiveDateTimeType(), "a data/hora da medicao deveria vir em effectiveDateTime");
    }

    @Test
    @DisplayName("GET /Observation/9999 responde 404")
    void lerInexistente() {
        assertThrows(ResourceNotFoundException.class,
                () -> cliente().read().resource(Observation.class).withId("9999").execute());
    }

    @Test
    @DisplayName("POST e PUT com o exemplo do grupo (exemplos/observation-valida.json)")
    void criarEAtualizarComExemplo() throws Exception {
        Observation exemplo = exemploDaPasta(Observation.class, "observation-valida.json");

        MethodOutcome criado = cliente().create().resource(exemplo).execute();
        assertNotNull(criado.getId(), "O POST deveria devolver o id criado (cabecalho Location)");
        String id = criado.getId().getIdPart();

        Observation lida = cliente().read().resource(Observation.class).withId(id).execute();
        assertEquals(0, exemplo.getValueQuantity().getValue().compareTo(lida.getValueQuantity().getValue()));

        BigDecimal novoValor = exemplo.getValueQuantity().getValue().add(BigDecimal.ONE);
        lida.getValueQuantity().setValue(novoValor);
        lida.setId(id);
        cliente().update().resource(lida).withId(id).execute();

        Observation atualizada = cliente().read().resource(Observation.class).withId(id).execute();
        assertEquals(0, novoValor.compareTo(atualizada.getValueQuantity().getValue()),
                "O valor deveria ter sido atualizado pelo PUT");
    }
}
