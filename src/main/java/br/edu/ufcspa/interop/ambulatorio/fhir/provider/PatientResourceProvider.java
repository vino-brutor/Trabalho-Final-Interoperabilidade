package br.edu.ufcspa.interop.ambulatorio.fhir.provider;

import org.springframework.stereotype.Component;

/**
 * Provider FHIR de Patient. Deve atender:
 *
 *   GET  /fhir/Patient/{id}   (read)
 *   POST /fhir/Patient        (create)
 *   PUT  /fhir/Patient/{id}   (update)
 *
 * TODO: transforme esta classe em um resource provider do HAPI FHIR e implemente as tres interacoes,
 * usando PacienteServico (ja pronto) e PatientMapper (a implementar).
 *
 * Lembre: o servidor so passa a enxergar este provider quando ele for um IResourceProvider
 * (veja como FhirConfig recebe os providers) e a LACUNA 3 estiver resolvida.
 */
@Component
public class PatientResourceProvider {

    // TODO
}
