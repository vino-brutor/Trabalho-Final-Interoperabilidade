package br.edu.ufcspa.interop.ambulatorio.fhir.util;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import java.util.Optional;

/** Utilitarios para ler e montar CodeableConcept. Pronta para uso nos mappers. */
public final class Codigos {

    private Codigos() {
    }

    /** Devolve o codigo do primeiro Coding do sistema informado, se houver. */
    public static Optional<String> codigo(CodeableConcept conceito, String sistema) {
        if (conceito == null) {
            return Optional.empty();
        }
        return conceito.getCoding().stream()
                .filter(c -> sistema.equals(c.getSystem()))
                .map(Coding::getCode)
                .filter(c -> c != null && !c.isBlank())
                .findFirst();
    }

    /** Texto legivel do conceito: o text, ou o display do primeiro Coding. */
    public static Optional<String> texto(CodeableConcept conceito) {
        if (conceito == null) {
            return Optional.empty();
        }
        if (conceito.hasText()) {
            return Optional.of(conceito.getText());
        }
        return conceito.getCoding().stream()
                .map(Coding::getDisplay)
                .filter(d -> d != null && !d.isBlank())
                .findFirst();
    }

    public static CodeableConcept conceito(String sistema, String codigo, String display) {
        return new CodeableConcept().addCoding(new Coding(sistema, codigo, display));
    }
}
