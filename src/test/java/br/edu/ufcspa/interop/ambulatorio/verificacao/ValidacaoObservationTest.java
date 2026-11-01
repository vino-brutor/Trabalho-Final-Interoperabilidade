package br.edu.ufcspa.interop.ambulatorio.verificacao;

import br.edu.ufcspa.interop.ambulatorio.suporte.FhirTesteBase;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import org.hl7.fhir.r4.model.OperationOutcome;
import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoObservationTest extends FhirTesteBase {

    @ParameterizedTest
    @CsvSource({
            "8867-4,/min,bpm,78",
            "9279-1,/min,irpm,18",
            "8310-5,Cel,°C,37.9",
            "29463-7,kg,kg,70.5",
            "8302-2,cm,cm,172",
            "2708-6,%,%,98"
    })
    void criarELerOsSeisTipos(String loinc, String ucum, String unidadeLocal, String valor) throws Exception {
        Observation recurso = exemploDaPasta(Observation.class, "observation-valida.json");
        recurso.getCode().getCodingFirstRep().setCode(loinc);
        recurso.getValueQuantity().setCode(ucum).setValue(new BigDecimal(valor));
        recurso.setEffective(new DateTimeType("2026-10-23T13:30:00Z"));

        MethodOutcome criado = cliente().create().resource(recurso).execute();
        assertEquals(Boolean.TRUE, criado.getCreated());
        Observation lida = cliente().read().resource(Observation.class).withId(criado.getId()).execute();

        assertEquals("http://loinc.org", lida.getCode().getCodingFirstRep().getSystem());
        assertEquals(loinc, lida.getCode().getCodingFirstRep().getCode());
        assertEquals("http://unitsofmeasure.org", lida.getValueQuantity().getSystem());
        assertEquals(ucum, lida.getValueQuantity().getCode());
        assertEquals(unidadeLocal, lida.getValueQuantity().getUnit());
        assertEquals(0, new BigDecimal(valor).compareTo(lida.getValueQuantity().getValue()));
        assertEquals("vital-signs", lida.getCategoryFirstRep().getCodingFirstRep().getCode());
        assertEquals("http://terminology.hl7.org/CodeSystem/observation-category",
                lida.getCategoryFirstRep().getCodingFirstRep().getSystem());
        assertEquals("Patient/2", lida.getSubject().getReference());
        assertEquals("2026-10-23T10:30:00-03:00", lida.getEffectiveDateTimeType().getValueAsString());

    }

    @ParameterizedTest
    @CsvSource({"preliminary", "final", "corrected", "cancelled", "entered-in-error"})
    void atualizarSituacao(String status) throws Exception {
        Observation recurso = exemploDaPasta(Observation.class, "observation-valida.json");
        MethodOutcome criado = cliente().create().resource(recurso).execute();
        recurso.setId(criado.getId());
        recurso.setStatus(ObservationStatus.fromCode(status));
        recurso.getSubject().setReference("Patient/1");
        recurso.getValueQuantity().setValue(new BigDecimal("38.2"));
        recurso.setEffective(new DateTimeType("2026-10-24T09:00:00-03:00"));

        cliente().update().resource(recurso).execute();
        Observation lida = cliente().read().resource(Observation.class).withId(criado.getId()).execute();
        assertEquals(status, lida.getStatus().toCode());
        assertEquals("Patient/1", lida.getSubject().getReference());
        assertEquals(0, new BigDecimal("38.2").compareTo(lida.getValueQuantity().getValue()));
        assertEquals("2026-10-24T09:00:00-03:00", lida.getEffectiveDateTimeType().getValueAsString());
    }

    static Stream<Consumer<Observation>> recursosInvalidos() {
        return Stream.of(
                o -> o.setStatus(null),
                o -> o.setStatus(ObservationStatus.UNKNOWN),
                o -> o.getCode().getCodingFirstRep().setCode("99999-9"),
                o -> o.getCode().getCodingFirstRep().setSystem("http://exemplo.org"),
                o -> o.setCategory(null),
                o -> o.getCategoryFirstRep().getCodingFirstRep().setSystem("http://exemplo.org"),
                o -> o.setValue(null),
                o -> o.getValueQuantity().setValue((BigDecimal) null),
                o -> o.getValueQuantity().setCode("kg"),
                o -> o.getValueQuantity().setSystem("http://exemplo.org"),
                o -> o.getValueQuantity().setComparator(Quantity.QuantityComparator.LESS_THAN),
                o -> o.setSubject(null),
                o -> o.getSubject().setReference("Patient/9999"),
                o -> o.getSubject().setReference("Observation/1"),
                o -> o.setEffective(null),
                o -> o.setEffective(new DateTimeType("2026-10")),
                o -> o.setEffective(new Period())
        );
    }

    @ParameterizedTest
    @MethodSource("recursosInvalidos")
    void recusarRecursosInvalidos(Consumer<Observation> alterar) throws Exception {
        Observation recurso = exemploDaPasta(Observation.class, "observation-valida.json");
        alterar.accept(recurso);
        BaseServerResponseException erro = assertThrows(BaseServerResponseException.class,
                () -> cliente().create().resource(recurso).execute());
        assertEquals(422, erro.getStatusCode());
        assertInstanceOf(OperationOutcome.class, erro.getOperationOutcome());
    }

    @Test
    void recusarExemploComFahrenheit() throws Exception {
        Observation recurso = exemploDaPasta(Observation.class, "observation-invalida-unidade.json");
        assertEquals(422, statusDoErro(() -> cliente().create().resource(recurso).execute()));
    }

    @Test
    void atualizarInexistente() throws Exception {
        Observation recurso = exemploDaPasta(Observation.class, "observation-valida.json");
        recurso.setId("9999");
        assertEquals(404, statusDoErro(() -> cliente().update().resource(recurso).execute()));
    }

    @Test
    void updateInvalidoNaoAlteraRegistro() throws Exception {
        Observation recurso = exemploDaPasta(Observation.class, "observation-valida.json");
        MethodOutcome criado = cliente().create().resource(recurso).execute();
        recurso.setId(criado.getId());
        recurso.getSubject().setReference("Patient/9999");
        recurso.getValueQuantity().setValue(new BigDecimal("99"));
        assertEquals(422, statusDoErro(() -> cliente().update().resource(recurso).execute()));

        Observation lida = cliente().read().resource(Observation.class).withId(criado.getId()).execute();
        assertEquals("Patient/2", lida.getSubject().getReference());
        assertEquals(0, new BigDecimal("37.9").compareTo(lida.getValueQuantity().getValue()));
    }
}
