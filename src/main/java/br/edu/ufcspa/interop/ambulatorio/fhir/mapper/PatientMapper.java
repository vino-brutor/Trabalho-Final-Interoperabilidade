package br.edu.ufcspa.interop.ambulatorio.fhir.mapper;

import br.edu.ufcspa.interop.ambulatorio.dominio.Paciente;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

/**
 * Traduz Paciente (modelo do sistema) para Patient (FHIR R4) e vice-versa.
 *
 * Especificacao do recurso: https://hl7.org/fhir/R4/patient.html
 * Regras de mapeamento: enunciado do trabalho, regras de Patient.
 */
@Component
public class PatientMapper {

    /** Usado no GET (read): Paciente do banco para Patient. */
    public Patient paraFhir(Paciente paciente) {
        // TODO
        throw new UnsupportedOperationException("TODO: PatientMapper.paraFhir");
    }

    /**
     * Usado no POST (create) e no PUT (update): copia o conteudo do Patient recebido para o Paciente.
     * Se o Patient nao atender as regras do enunciado, recuse com o erro HTTP adequado.
     */
    public void paraDominio(Patient recurso, Paciente destino) {
        // TODO
        throw new UnsupportedOperationException("TODO: PatientMapper.paraDominio");
    }
}
