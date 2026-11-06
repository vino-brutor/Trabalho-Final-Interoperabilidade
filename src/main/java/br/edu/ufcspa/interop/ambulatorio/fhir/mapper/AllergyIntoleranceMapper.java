package br.edu.ufcspa.interop.ambulatorio.fhir.mapper;

import br.edu.ufcspa.interop.ambulatorio.dominio.Alergia;
import br.edu.ufcspa.interop.ambulatorio.dominio.CategoriaAlergia;
import br.edu.ufcspa.interop.ambulatorio.dominio.CriticidadeAlergia;
import br.edu.ufcspa.interop.ambulatorio.dominio.Paciente;
import br.edu.ufcspa.interop.ambulatorio.dominio.SituacaoClinicaAlergia;
import br.edu.ufcspa.interop.ambulatorio.dominio.TipoAlergia;
import br.edu.ufcspa.interop.ambulatorio.dominio.VerificacaoAlergia;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.Codigos;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.ConversorDatas;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.ReferenciaPaciente;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.Sistemas;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import java.time.DateTimeException;
import java.time.LocalDate;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCategory;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCriticality;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceReactionComponent;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.springframework.stereotype.Component;

/**
 * Traduz Alergia (modelo do sistema) para AllergyIntolerance (FHIR R4) e vice-versa.
 *
 * Especificacao do recurso: https://hl7.org/fhir/R4/allergyintolerance.html
 * Regras de mapeamento: enunciado do trabalho, regras de AllergyIntolerance.
 *
 * Invariantes do recurso que o enunciado exige respeitar:
 *   ait-1: clinicalStatus SHALL be present if verificationStatus is not entered-in-error.
 *   ait-2: clinicalStatus SHALL NOT be present if verificationStatus is entered-in-error.
 */
@Component
public class AllergyIntoleranceMapper {

    private final ReferenciaPaciente referenciaPaciente;

    AllergyIntoleranceMapper(ReferenciaPaciente referenciaPaciente) {
        this.referenciaPaciente = referenciaPaciente;
    }

    /** Usado no GET (read): Alergia do banco para AllergyIntolerance. */
    public AllergyIntolerance paraFhir(Alergia alergia) {
        AllergyIntolerance recurso = new AllergyIntolerance();

        if (alergia.getId() != null) {
            recurso.setId(alergia.getId().toString());
        }

        if (alergia.getVerificacao() != null) {
            recurso.setVerificationStatus(Codigos.conceito(
                    Sistemas.ALLERGY_VERIFICATION,
                    codigoVerificacao(alergia.getVerificacao()),
                    null));
        }

        // ait-2: so declara clinicalStatus quando a verificacao nao for registrada-por-engano.
        if (alergia.getSituacaoClinica() != null
                && alergia.getVerificacao() != VerificacaoAlergia.REGISTRADA_POR_ENGANO) {
            recurso.setClinicalStatus(Codigos.conceito(
                    Sistemas.ALLERGY_CLINICAL,
                    codigoSituacaoClinica(alergia.getSituacaoClinica()),
                    null));
        }

        if (alergia.getTipo() != null) {
            recurso.setType(alergia.getTipo() == TipoAlergia.ALERGIA
                    ? AllergyIntoleranceType.ALLERGY
                    : AllergyIntoleranceType.INTOLERANCE);
        }

        if (alergia.getCategoria() != null) {
            recurso.addCategory(categoriaFhir(alergia.getCategoria()));
        }

        if (alergia.getCriticidade() != null) {
            recurso.setCriticality(criticidadeFhir(alergia.getCriticidade()));
        }

        if (alergia.getSubstancia() != null && !alergia.getSubstancia().isBlank()) {
            recurso.setCode(new CodeableConcept().setText(alergia.getSubstancia()));
        }

        if (alergia.getPaciente() != null) {
            recurso.setPatient(referenciaPaciente.para(alergia.getPaciente()));
        }

        if (alergia.getReacao() != null && !alergia.getReacao().isBlank()) {
            recurso.addReaction().addManifestation(
                    new CodeableConcept().setText(alergia.getReacao()));
        }

        if (alergia.getDataRegistro() != null) {
            recurso.setRecordedDateElement(ConversorDatas.paraDateTimeType(alergia.getDataRegistro()));
        }

        return recurso;
    }

    /**
     * Usado no POST (create) e no PUT (update): copia o conteudo do AllergyIntolerance recebido para a Alergia.
     * Se o recurso nao atender as regras do enunciado, recuse com o erro HTTP adequado.
     */
    public void paraDominio(AllergyIntolerance recurso, Alergia destino) {
        if (!recurso.hasVerificationStatus()) {
            throw new UnprocessableEntityException("verificationStatus é obrigatório");
        }

        String codVerificacao = Codigos.codigo(recurso.getVerificationStatus(), Sistemas.ALLERGY_VERIFICATION)
                .orElseThrow(() -> new UnprocessableEntityException(
                        "verificationStatus deve usar o code system " + Sistemas.ALLERGY_VERIFICATION));

        VerificacaoAlergia verificacao;
        switch (codVerificacao) {
            case "unconfirmed":
                verificacao = VerificacaoAlergia.NAO_CONFIRMADA;
                break;
            case "confirmed":
                verificacao = VerificacaoAlergia.CONFIRMADA;
                break;
            case "refuted":
                verificacao = VerificacaoAlergia.REFUTADA;
                break;
            case "entered-in-error":
                verificacao = VerificacaoAlergia.REGISTRADA_POR_ENGANO;
                break;
            default:
                throw new UnprocessableEntityException(
                        "verificationStatus não suportado pelo sistema: " + codVerificacao);
        }

        boolean engano = verificacao == VerificacaoAlergia.REGISTRADA_POR_ENGANO;

        // ait-2: clinicalStatus NAO pode estar presente quando verificationStatus e entered-in-error.
        if (engano && recurso.hasClinicalStatus()) {
            throw new UnprocessableEntityException(
                    "ait-2: clinicalStatus não pode estar presente quando verificationStatus é entered-in-error");
        }

        // ait-1: clinicalStatus DEVE estar presente quando verificationStatus nao e entered-in-error.
        if (!engano && !recurso.hasClinicalStatus()) {
            throw new UnprocessableEntityException(
                    "ait-1: clinicalStatus é obrigatório quando verificationStatus não é entered-in-error");
        }

        SituacaoClinicaAlergia situacaoClinica = null;
        if (recurso.hasClinicalStatus()) {
            String codClinico = Codigos.codigo(recurso.getClinicalStatus(), Sistemas.ALLERGY_CLINICAL)
                    .orElseThrow(() -> new UnprocessableEntityException(
                            "clinicalStatus deve usar o code system " + Sistemas.ALLERGY_CLINICAL));
            switch (codClinico) {
                case "active":
                    situacaoClinica = SituacaoClinicaAlergia.ATIVA;
                    break;
                case "inactive":
                    situacaoClinica = SituacaoClinicaAlergia.INATIVA;
                    break;
                case "resolved":
                    situacaoClinica = SituacaoClinicaAlergia.RESOLVIDA;
                    break;
                default:
                    throw new UnprocessableEntityException(
                            "clinicalStatus não suportado pelo sistema: " + codClinico);
            }
        }

        String substancia = Codigos.texto(recurso.getCode())
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new UnprocessableEntityException("code.text (substância) é obrigatório"));

        Paciente paciente = referenciaPaciente.resolver(recurso.getPatient(), "AllergyIntolerance.patient");

        TipoAlergia tipo = null;
        if (recurso.hasType()) {
            tipo = recurso.getType() == AllergyIntoleranceType.INTOLERANCE
                    ? TipoAlergia.INTOLERANCIA
                    : TipoAlergia.ALERGIA;
        }

        CategoriaAlergia categoria = null;
        if (!recurso.getCategory().isEmpty()) {
            categoria = categoriaDominio(recurso.getCategory().get(0).getValue());
        }

        CriticidadeAlergia criticidade = null;
        if (recurso.hasCriticality()) {
            criticidade = criticidadeDominio(recurso.getCriticality());
        }

        String reacao = null;
        for (AllergyIntoleranceReactionComponent r : recurso.getReaction()) {
            for (CodeableConcept manifestacao : r.getManifestation()) {
                String texto = Codigos.texto(manifestacao).filter(t -> !t.isBlank()).orElse(null);
                if (texto != null) {
                    reacao = texto;
                    break;
                }
            }
            if (reacao != null) {
                break;
            }
        }

        LocalDate dataRegistro;
        try {
            dataRegistro = ConversorDatas.paraLocalDate(recurso.getRecordedDateElement());
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new UnprocessableEntityException("recordedDate inválida ou incompleta");
        }
        if (dataRegistro == null) {
            throw new UnprocessableEntityException("recordedDate é obrigatória");
        }

        destino.setVerificacao(verificacao);
        destino.setSituacaoClinica(situacaoClinica);
        destino.setSubstancia(substancia);
        destino.setPaciente(paciente);
        destino.setTipo(tipo);
        destino.setCategoria(categoria);
        destino.setCriticidade(criticidade);
        destino.setReacao(reacao);
        destino.setDataRegistro(dataRegistro);
    }

    private String codigoVerificacao(VerificacaoAlergia verificacao) {
        switch (verificacao) {
            case NAO_CONFIRMADA:
                return "unconfirmed";
            case CONFIRMADA:
                return "confirmed";
            case REFUTADA:
                return "refuted";
            case REGISTRADA_POR_ENGANO:
                return "entered-in-error";
            default:
                throw new UnprocessableEntityException("Verificação não suportada");
        }
    }

    private String codigoSituacaoClinica(SituacaoClinicaAlergia situacao) {
        switch (situacao) {
            case ATIVA:
                return "active";
            case INATIVA:
                return "inactive";
            case RESOLVIDA:
                return "resolved";
            default:
                throw new UnprocessableEntityException("Situação clínica não suportada");
        }
    }

    private AllergyIntoleranceCategory categoriaFhir(CategoriaAlergia categoria) {
        switch (categoria) {
            case ALIMENTO:
                return AllergyIntoleranceCategory.FOOD;
            case MEDICAMENTO:
                return AllergyIntoleranceCategory.MEDICATION;
            case AMBIENTE:
                return AllergyIntoleranceCategory.ENVIRONMENT;
            case BIOLOGICO:
                return AllergyIntoleranceCategory.BIOLOGIC;
            default:
                throw new UnprocessableEntityException("Categoria de alergia não suportada");
        }
    }

    private CategoriaAlergia categoriaDominio(AllergyIntoleranceCategory categoria) {
        if (categoria == null) {
            return null;
        }
        switch (categoria) {
            case FOOD:
                return CategoriaAlergia.ALIMENTO;
            case MEDICATION:
                return CategoriaAlergia.MEDICAMENTO;
            case ENVIRONMENT:
                return CategoriaAlergia.AMBIENTE;
            case BIOLOGIC:
                return CategoriaAlergia.BIOLOGICO;
            default:
                throw new UnprocessableEntityException("Categoria de alergia não suportada pelo sistema");
        }
    }

    private AllergyIntoleranceCriticality criticidadeFhir(CriticidadeAlergia criticidade) {
        switch (criticidade) {
            case BAIXA:
                return AllergyIntoleranceCriticality.LOW;
            case ALTA:
                return AllergyIntoleranceCriticality.HIGH;
            case INDETERMINADA:
                return AllergyIntoleranceCriticality.UNABLETOASSESS;
            default:
                throw new UnprocessableEntityException("Criticidade não suportada");
        }
    }

    private CriticidadeAlergia criticidadeDominio(AllergyIntoleranceCriticality criticidade) {
        if (criticidade == null) {
            return null;
        }
        switch (criticidade) {
            case LOW:
                return CriticidadeAlergia.BAIXA;
            case HIGH:
                return CriticidadeAlergia.ALTA;
            case UNABLETOASSESS:
                return CriticidadeAlergia.INDETERMINADA;
            default:
                throw new UnprocessableEntityException("Criticidade não suportada pelo sistema");
        }
    }
}
