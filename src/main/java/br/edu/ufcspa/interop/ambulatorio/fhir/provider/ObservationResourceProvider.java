package br.edu.ufcspa.interop.ambulatorio.fhir.provider;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.stereotype.Component;

import br.edu.ufcspa.interop.ambulatorio.dominio.SinalVital;
import br.edu.ufcspa.interop.ambulatorio.fhir.mapper.ObservationMapper;
import br.edu.ufcspa.interop.ambulatorio.servico.SinalVitalServico;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

/**
 * Provider FHIR de Observation. Deve atender:
 *
 *   GET  /fhir/Observation/{id}   (read)
 *   POST /fhir/Observation        (create)
 *   PUT  /fhir/Observation/{id}   (update)
 *
 * Usa SinalVitalServico e ObservationMapper para as tres interacoes.
 */
@Component
public class ObservationResourceProvider implements IResourceProvider {

    private final SinalVitalServico sinalVitalServico;
    private final ObservationMapper observationMapper;

    ObservationResourceProvider(SinalVitalServico sinalVitalServico, ObservationMapper observationMapper) {
        this.sinalVitalServico = sinalVitalServico;
        this.observationMapper = observationMapper;
    }

    @Override
    public Class<Observation> getResourceType() {
        return Observation.class;
    }

    @Read
    public Observation getObservation(@IdParam IdType id) {
        long idSinalVital;

        try {
            idSinalVital = Long.valueOf(id.getIdPart());
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("ID do sinal vital deve ser um número inteiro");
        }

        SinalVital sinalVital = sinalVitalServico.buscarPorId(idSinalVital)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Sinal vital não encontrado")
                    );

        return observationMapper.paraFhir(sinalVital);
    }

    @Create
    public MethodOutcome postObservation(@ResourceParam Observation recurso) {
        SinalVital sinalVital = new SinalVital();

        observationMapper.paraDominio(recurso, sinalVital);

        SinalVital salvo = sinalVitalServico.salvar(sinalVital);

        MethodOutcome outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setId(new IdType("Observation", salvo.getId().toString()));
        return outcome;
    }

    @Update
    public MethodOutcome putObservation(
        @IdParam IdType id,
        @ResourceParam Observation recurso
    ) {
        long sinalVitalId;
        try {
            sinalVitalId = Long.valueOf(id.getIdPart());
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("ID do sinal vital deve ser um número inteiro");
        }

        if (!recurso.hasId()
                || !id.getIdPart().equals(recurso.getIdElement().getIdPart())) {
            throw new InvalidRequestException(
                    "O ID do corpo deve ser igual ao ID da URL");
        }

        SinalVital sinalVital = sinalVitalServico.buscarPorId(sinalVitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sinal vital não encontrado"));

        observationMapper.paraDominio(recurso, sinalVital);

        SinalVital salvo = sinalVitalServico.salvar(sinalVital);

        MethodOutcome outcome = new MethodOutcome();
        outcome.setCreated(false);
        outcome.setId(new IdType("Observation", salvo.getId().toString()));
        return outcome;
    }
}
