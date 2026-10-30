package br.edu.ufcspa.interop.ambulatorio.fhir.mapper;

import br.edu.ufcspa.interop.ambulatorio.dominio.Paciente;
import br.edu.ufcspa.interop.ambulatorio.dominio.Sexo;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.ConversorDatas;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.Sistemas;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

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
     * Usado no POST (create) e no PUT (update): copia o conteudo do Patient
     * recebido para o Paciente.
     * Se o Patient nao atender as regras do enunciado, recuse com o erro HTTP
     * adequado.
     */
    public void paraDominio(Patient recurso, Paciente destino) {
        destino.setCpf(null);
        destino.setCns(null);
        destino.setSexo(null);
        destino.setTelefone(null);
        destino.setEmail(null);

        for (Identifier identifier : recurso.getIdentifier()) {
            if ((Sistemas.CPF.equals(identifier.getSystem()))) {
                destino.setCpf(identifier.getValue());
            }

            if (Sistemas.CNS.equals(identifier.getSystem())) {
                destino.setCns(identifier.getValue());
            }
        }

        if (destino.getCpf() == null || !destino.getCpf().matches("[0-9]{11}")) {
            throw new UnprocessableEntityException(
                    "CPF é obrigatório e deve conter exatamente 11 dígitos");
        }

        if (!recurso.hasName()) {
            throw new UnprocessableEntityException(
                "Nome é obrigatório"
            );
        }

        HumanName name = recurso.getNameFirstRep();
        String prenome = name.getGivenAsSingleString();
        String sobrenome = name.getFamily();

        if ((prenome == null || sobrenome == null) || (prenome.isBlank() || sobrenome.isBlank())) {
            throw new UnprocessableEntityException(
                "Prenome e sobrenome são obrigatórios"
            );
        }

        destino.setNome(prenome);
        destino.setSobrenome(sobrenome);

        try {
            destino.setDataNascimento(ConversorDatas.paraLocalDate(recurso.getBirthDateElement()));
        } catch (IllegalArgumentException e) {
            throw new UnprocessableEntityException("Data de nascimento invalida");
        }       

        if (recurso.hasGender()) {
            switch (recurso.getGender()) {
            case FEMALE:
                destino.setSexo(Sexo.FEMININO);
                break;
            case MALE:
                destino.setSexo(Sexo.MASCULINO);
                break;
            case OTHER:
                destino.setSexo(Sexo.OUTRO);
                break;
            case UNKNOWN:
                destino.setSexo(Sexo.NAO_INFORMADO);
                break;
            default:
                break;
            }
        }

        for (ContactPoint contact : recurso.getTelecom()) {
            if (contact.hasSystem()) {
                switch (contact.getSystem()) {
                    case PHONE:
                        destino.setTelefone(contact.getValue());
                        break;
                    case EMAIL:
                        destino.setEmail(contact.getValue());
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
