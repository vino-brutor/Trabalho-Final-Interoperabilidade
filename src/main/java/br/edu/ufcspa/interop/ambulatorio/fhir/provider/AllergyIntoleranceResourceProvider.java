package br.edu.ufcspa.interop.ambulatorio.fhir.provider;

import org.springframework.stereotype.Component;

/**
 * Provider FHIR de AllergyIntolerance. Deve atender:
 *
 *   GET  /fhir/AllergyIntolerance/{id}   (read)
 *   POST /fhir/AllergyIntolerance        (create)
 *   PUT  /fhir/AllergyIntolerance/{id}   (update)
 *
 * TODO: transforme esta classe em um resource provider do HAPI FHIR e implemente as tres interacoes,
 * usando AlergiaServico (ja pronto) e AllergyIntoleranceMapper (a implementar).
 */
@Component
public class AllergyIntoleranceResourceProvider {

    // TODO
}
