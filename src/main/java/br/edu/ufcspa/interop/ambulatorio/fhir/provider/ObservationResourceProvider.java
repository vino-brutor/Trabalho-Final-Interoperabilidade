package br.edu.ufcspa.interop.ambulatorio.fhir.provider;

import org.springframework.stereotype.Component;

/**
 * Provider FHIR de Observation. Deve atender:
 *
 *   GET  /fhir/Observation/{id}   (read)
 *   POST /fhir/Observation        (create)
 *   PUT  /fhir/Observation/{id}   (update)
 *
 * TODO: transforme esta classe em um resource provider do HAPI FHIR e implemente as tres interacoes,
 * usando SinalVitalServico (ja pronto) e ObservationMapper (a implementar).
 */
@Component
public class ObservationResourceProvider {

    // TODO
}
