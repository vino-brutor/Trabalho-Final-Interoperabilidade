package br.edu.ufcspa.interop.ambulatorio.fhir.mapper;

import br.edu.ufcspa.interop.ambulatorio.dominio.Paciente;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.ConversorDatas;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.Sistemas;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.codesystems.AdministrativeGenderEnumFactory;
import org.springframework.stereotype.Component;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Patient.ContactComponent;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateType;

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
        Patient patient = new Patient();

        if (paciente.getId() != null) {
            patient.setId(String.valueOf(paciente.getId()));                        
        }

        if (paciente.getCpf() != null && !(paciente.getCpf().isBlank())) {
            Identifier identifierCpf = patient.addIdentifier();
            identifierCpf.setValue(paciente.getCpf());
            identifierCpf.setSystem(Sistemas.CPF);
        }

        if (paciente.getCns() != null && !(paciente.getCns().isBlank())) {
            Identifier identifierCns = patient.addIdentifier();
            identifierCns.setValue(paciente.getCns());
            identifierCns.setSystem(Sistemas.CNS);
        }

        boolean temNome = paciente.getNome() != null && !paciente.getNome().isBlank();
        boolean temSobrenome = paciente.getSobrenome() != null && !paciente.getSobrenome().isBlank();

        if (temNome || temSobrenome) {
            HumanName name = patient.addName();
            if (temNome) {
                name.addGiven(paciente.getNome());
            }
            if (temSobrenome) {
                name.setFamily(paciente.getSobrenome());
            }
        }

        DateType birthDate = ConversorDatas.paraDateType(paciente.getDataNascimento());
        patient.setBirthDateElement(birthDate);

        if (paciente.getSexo() != null) {
            switch (paciente.getSexo()) {
                case FEMININO:
                    patient.setGender(AdministrativeGender.FEMALE);
                    break;
                    
                case MASCULINO:
                    patient.setGender(AdministrativeGender.MALE);
                    break;
                        
                case OUTRO:
                    patient.setGender(AdministrativeGender.OTHER);
                    break;
                
                case NAO_INFORMADO:
                    patient.setGender(AdministrativeGender.UNKNOWN);
                    break;
                
                default:
                    patient.setGender(AdministrativeGender.UNKNOWN);
                    break;
            }
                
        }

        if ((paciente.getTelefone() != null) && !(paciente.getTelefone().isBlank())) {
            ContactPoint telephone = new ContactPoint();
            telephone.setSystem(ContactPointSystem.PHONE);
            telephone.setValue(paciente.getTelefone());
            patient.addTelecom(telephone);
        }

        if ((paciente.getEmail() != null) && !(paciente.getEmail().isBlank())) {
            ContactPoint email = new ContactPoint();
            email.setSystem(ContactPointSystem.EMAIL);
            email.setValue(paciente.getEmail());
            patient.addTelecom(email);
        }

        return patient;
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
