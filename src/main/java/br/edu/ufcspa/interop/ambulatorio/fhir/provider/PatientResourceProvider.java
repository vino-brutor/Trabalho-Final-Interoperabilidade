package br.edu.ufcspa.interop.ambulatorio.fhir.provider;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import br.edu.ufcspa.interop.ambulatorio.dominio.Paciente;
import br.edu.ufcspa.interop.ambulatorio.fhir.mapper.PatientMapper;
import br.edu.ufcspa.interop.ambulatorio.servico.PacienteServico;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.ResourceVersionConflictException;

/**
 * Provider FHIR de Patient. Deve atender:
 *
 *   GET  /fhir/Patient/{id}   (read)
 *   POST /fhir/Patient        (create)
 *   PUT  /fhir/Patient/{id}   (update)
 * usando PacienteServico (ja pronto) e PatientMapper (a implementar).
 *
 * Lembre: o servidor so passa a enxergar este provider quando ele for um IResourceProvider
 * (veja como FhirConfig recebe os providers) e a LACUNA 3 estiver resolvida.
 */
@Component
public class PatientResourceProvider implements IResourceProvider {

    private final PacienteServico pacienteServico;
    private final PatientMapper patientMapper;

    PatientResourceProvider(PacienteServico pacienteServico, PatientMapper patientMapper) {
        this.pacienteServico = pacienteServico;
        this.patientMapper = patientMapper;
    }

    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }

    @Read 
    public Patient getPatient(@IdParam IdType id) {
        long idPaciente;

        try {
            idPaciente = Long.valueOf(id.getIdPart());
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("ID do paciente deve ser um número inteiro");
        }

        Paciente paciente = pacienteServico.buscarPorId(idPaciente)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Paciente não encontrado")
                    );

        return patientMapper.paraFhir(paciente);
    }
    
    @Create  
    public MethodOutcome postPatient(@ResourceParam Patient recurso) {
        Paciente paciente = new Paciente();

        patientMapper.paraDominio(recurso, paciente);

        if (pacienteServico.buscarPorCpf(paciente.getCpf()).isPresent()) {
            throw new ResourceVersionConflictException("Já existe um paciente cadastrado com esse CPF");
        }

        Paciente salvo = pacienteServico.salvar(paciente);

        MethodOutcome outcome = new MethodOutcome();
        outcome.setCreated(true);
        outcome.setId(new IdType("Patient", salvo.getId().toString()));
        return outcome;
    }
    
    @Update
    public MethodOutcome putPatient(
        @IdParam IdType id,
        @ResourceParam Patient recurso
    ) {
        long pacienteId;
        try {
            pacienteId = Long.valueOf(id.getIdPart());
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("ID do paciente deve ser um número inteiro");
        }

        if (!recurso.hasId()
                || !id.getIdPart().equals(recurso.getIdElement().getIdPart())) {
            throw new InvalidRequestException(
                    "O ID do corpo deve ser igual ao ID da URL");
        }

        Paciente paciente = pacienteServico.buscarPorId(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));

        patientMapper.paraDominio(recurso, paciente);

        Paciente pacienteComCpf = pacienteServico.buscarPorCpf(paciente.getCpf()).orElse(null);

        if (pacienteComCpf != null && !paciente.getId().equals(pacienteComCpf.getId())) {
            throw new ResourceVersionConflictException("Já existe um paciente cadastrado com esse CPF"); 
        }

        Paciente salvo = pacienteServico.salvar(paciente);

        MethodOutcome outcome = new MethodOutcome();
        outcome.setCreated(false);
        outcome.setId(new IdType("Patient", salvo.getId().toString()));
        return outcome;
    }
}
