package br.edu.ufcspa.interop.ambulatorio.fhir.util;

import br.edu.ufcspa.interop.ambulatorio.dominio.Paciente;
import br.edu.ufcspa.interop.ambulatorio.servico.PacienteServico;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;

/**
 * Converte entre o Paciente do dominio e uma Reference FHIR para Patient.
 *
 * Pronta para uso nos mappers de Observation e AllergyIntolerance.
 */
@Component
public class ReferenciaPaciente {

    private final PacienteServico pacientes;

    public ReferenciaPaciente(PacienteServico pacientes) {
        this.pacientes = pacientes;
    }

    public Reference para(Paciente paciente) {
        return new Reference("Patient/" + paciente.getId());
    }

    /**
     * Resolve "Patient/{id}" para o Paciente do banco.
     *
     * @param campo nome do elemento FHIR, usado na mensagem de erro (ex.: "Observation.subject")
     */
    public Paciente resolver(Reference referencia, String campo) {
        if (referencia == null || !referencia.hasReference()) {
            throw new UnprocessableEntityException(campo + " é obrigatório e deve referenciar um Patient (ex.: Patient/1)");
        }
        IIdType id = referencia.getReferenceElement();
        if (!"Patient".equals(id.getResourceType()) || !id.isIdPartValidLong()) {
            throw new UnprocessableEntityException(campo + " deve ter o formato Patient/{id}, recebido: " + referencia.getReference());
        }
        return pacientes.buscarPorId(id.getIdPartAsLong())
                .orElseThrow(() -> new UnprocessableEntityException(
                        campo + " referencia um Patient que não existe: " + referencia.getReference()));
    }
}
