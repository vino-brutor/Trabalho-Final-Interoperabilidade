package br.edu.ufcspa.interop.ambulatorio.fhir.provider;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.stereotype.Component;

import br.edu.ufcspa.interop.ambulatorio.dominio.Alergia;
import br.edu.ufcspa.interop.ambulatorio.fhir.mapper.AllergyIntoleranceMapper;
import br.edu.ufcspa.interop.ambulatorio.servico.AlergiaServico;
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
 * Provider FHIR de AllergyIntolerance. Deve atender:
 *
 *   GET  /fhir/AllergyIntolerance/{id}   (read)
 *   POST /fhir/AllergyIntolerance        (create)
 *   PUT  /fhir/AllergyIntolerance/{id}   (update)
 *
 * Usa AlergiaServico e AllergyIntoleranceMapper para as tres interacoes.
 */
@Component
public class AllergyIntoleranceResourceProvider implements IResourceProvider {

    private final AlergiaServico alergiaServico;
    private final AllergyIntoleranceMapper allergyIntoleranceMapper;

    AllergyIntoleranceResourceProvider(AlergiaServico alergiaServico,
            AllergyIntoleranceMapper allergyIntoleranceMapper) {
        this.alergiaServico = alergiaServico;
        this.allergyIntoleranceMapper = allergyIntoleranceMapper;
    }

    @Override
    public Class<AllergyIntolerance> getResourceType() {
        return AllergyIntolerance.class;
    }

    @Read
    public AllergyIntolerance getAllergyIntolerance(@IdParam IdType id) {
        long idAlergia;

        try {
            idAlergia = Long.valueOf(id.getIdPart());
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("ID da alergia deve ser um número inteiro");
        }

        Alergia alergia = alergiaServico.buscarPorId(idAlergia)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Alergia não encontrada")
                    );

        return allergyIntoleranceMapper.paraFhir(alergia);
    }

    @Create
    public MethodOutcome postAllergyIntolerance(@ResourceParam AllergyIntolerance recurso) {
        Alergia alergia = new Alergia();

        allergyIntoleranceMapper.paraDominio(recurso, alergia);

        Alergia salvo = alergiaServico.salvar(alergia);

        MethodOutcome outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setId(new IdType("AllergyIntolerance", salvo.getId().toString()));
        return outcome;
    }

    @Update
    public MethodOutcome putAllergyIntolerance(
        @IdParam IdType id,
        @ResourceParam AllergyIntolerance recurso
    ) {
        long alergiaId;
        try {
            alergiaId = Long.valueOf(id.getIdPart());
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("ID da alergia deve ser um número inteiro");
        }

        if (!recurso.hasId()
                || !id.getIdPart().equals(recurso.getIdElement().getIdPart())) {
            throw new InvalidRequestException(
                    "O ID do corpo deve ser igual ao ID da URL");
        }

        Alergia alergia = alergiaServico.buscarPorId(alergiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Alergia não encontrada"));

        allergyIntoleranceMapper.paraDominio(recurso, alergia);

        Alergia salvo = alergiaServico.salvar(alergia);

        MethodOutcome outcome = new MethodOutcome();
        outcome.setCreated(false);
        outcome.setId(new IdType("AllergyIntolerance", salvo.getId().toString()));
        return outcome;
    }
}
