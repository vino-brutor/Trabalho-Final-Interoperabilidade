package br.edu.ufcspa.interop.ambulatorio.fhir.mapper;

import br.edu.ufcspa.interop.ambulatorio.dominio.Paciente;
import br.edu.ufcspa.interop.ambulatorio.dominio.Sexo;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.ConversorDatas;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.Sistemas;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
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
        Patient patient = new Patient();

        if (paciente.getId() != null) {
            patient.setId(paciente.getId().toString());
        }

        // CPF e CNS em identifier, cada um com o system da RNDS. Campo vazio nao vira elemento.
        if (paciente.getCpf() != null && !paciente.getCpf().isBlank()) {
            Identifier cpf = patient.addIdentifier();
            cpf.setSystem(Sistemas.CPF);
            cpf.setValue(paciente.getCpf());
        }

        if (paciente.getCns() != null && !paciente.getCns().isBlank()) {
            Identifier cns = patient.addIdentifier();
            cns.setSystem(Sistemas.CNS);
            cns.setValue(paciente.getCns());
        }

        // Nome e sobrenome em um unico HumanName: given = nome, family = sobrenome.
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

        if (paciente.getDataNascimento() != null) {
            patient.setBirthDateElement(ConversorDatas.paraDateType(paciente.getDataNascimento()));
        }

        if (paciente.getSexo() != null) {
            patient.setGender(generoFhir(paciente.getSexo()));
        }

        // Telefone e e-mail em telecom, distinguidos pelo system.
        if (paciente.getTelefone() != null && !paciente.getTelefone().isBlank()) {
            patient.addTelecom(new ContactPoint()
                    .setSystem(ContactPointSystem.PHONE)
                    .setValue(paciente.getTelefone()));
        }

        if (paciente.getEmail() != null && !paciente.getEmail().isBlank()) {
            patient.addTelecom(new ContactPoint()
                    .setSystem(ContactPointSystem.EMAIL)
                    .setValue(paciente.getEmail()));
        }

        patient.setActive(paciente.isAtivo());

        return patient;
    }

    /**
     * Usado no POST (create) e no PUT (update): copia o conteudo do Patient recebido para o Paciente.
     * Se o Patient nao atender as regras do enunciado, recuse com o erro HTTP adequado.
     */
    public void paraDominio(Patient recurso, Paciente destino) {
        // Limpa os campos opcionais: no PUT, o recurso substitui o anterior por inteiro.
        destino.setCpf(null);
        destino.setCns(null);
        destino.setSexo(null);
        destino.setTelefone(null);
        destino.setEmail(null);

        for (Identifier identifier : recurso.getIdentifier()) {
            if (Sistemas.CPF.equals(identifier.getSystem())) {
                destino.setCpf(identifier.getValue());
            }
            if (Sistemas.CNS.equals(identifier.getSystem())) {
                destino.setCns(identifier.getValue());
            }
        }

        // Recusar sem CPF ou com CPF que nao tenha exatamente 11 digitos.
        if (destino.getCpf() == null || !destino.getCpf().matches("[0-9]{11}")) {
            throw new UnprocessableEntityException("CPF é obrigatório e deve conter exatamente 11 dígitos");
        }

        // Recusar sem nome ou sem sobrenome.
        if (!recurso.hasName()) {
            throw new UnprocessableEntityException("Nome é obrigatório");
        }
        HumanName name = recurso.getNameFirstRep();
        String nome = name.getGivenAsSingleString();
        String sobrenome = name.getFamily();
        if (nome == null || nome.isBlank() || sobrenome == null || sobrenome.isBlank()) {
            throw new UnprocessableEntityException("Nome e sobrenome são obrigatórios");
        }
        destino.setNome(nome);
        destino.setSobrenome(sobrenome);

        try {
            destino.setDataNascimento(ConversorDatas.paraLocalDate(recurso.getBirthDateElement()));
        } catch (IllegalArgumentException e) {
            throw new UnprocessableEntityException("Data de nascimento inválida ou incompleta");
        }

        if (recurso.hasGender()) {
            destino.setSexo(sexoDominio(recurso.getGender()));
        }

        for (ContactPoint contato : recurso.getTelecom()) {
            if (!contato.hasSystem()) {
                continue;
            }
            switch (contato.getSystem()) {
                case PHONE:
                    destino.setTelefone(contato.getValue());
                    break;
                case EMAIL:
                    destino.setEmail(contato.getValue());
                    break;
                default:
                    break;
            }
        }

        destino.setAtivo(recurso.hasActive() ? recurso.getActive() : true);
    }

    private AdministrativeGender generoFhir(Sexo sexo) {
        switch (sexo) {
            case MASCULINO:
                return AdministrativeGender.MALE;
            case FEMININO:
                return AdministrativeGender.FEMALE;
            case OUTRO:
                return AdministrativeGender.OTHER;
            case NAO_INFORMADO:
            default:
                return AdministrativeGender.UNKNOWN;
        }
    }

    private Sexo sexoDominio(AdministrativeGender genero) {
        switch (genero) {
            case MALE:
                return Sexo.MASCULINO;
            case FEMALE:
                return Sexo.FEMININO;
            case OTHER:
                return Sexo.OUTRO;
            case UNKNOWN:
            default:
                return Sexo.NAO_INFORMADO;
        }
    }
}
