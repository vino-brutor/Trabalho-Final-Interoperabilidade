package br.edu.ufcspa.interop.ambulatorio.fhir.util;

/**
 * Identificadores (URIs) de sistemas de identificacao e de terminologia usados nos mappers.
 *
 * Esta classe esta INCOMPLETA: troque cada "???" pela URI correta.
 * Fontes: NamingSystems da RNDS (https://rnds-fhir.saude.gov.br/artifacts.html)
 * e especificacao FHIR R4 (https://hl7.org/fhir/R4/).
 */
public final class Sistemas {

    public static final String CPF = "http://rnds.saude.gov.br/fhir/r4/NamingSystem/cpf";
    public static final String CNS = "http://rnds.saude.gov.br/fhir/r4/NamingSystem/cns";

    public static final String LOINC = "http://loinc.org";
    public static final String UCUM = "http://unitsofmeasure.org";
    public static final String OBSERVATION_CATEGORY = "http://terminology.hl7.org/CodeSystem/observation-category";

    public static final String ALLERGY_CLINICAL = "???";
    public static final String ALLERGY_VERIFICATION = "???";

    private Sistemas() {
    }
}
