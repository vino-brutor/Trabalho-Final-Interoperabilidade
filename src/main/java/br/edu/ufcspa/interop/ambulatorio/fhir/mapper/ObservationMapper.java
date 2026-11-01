package br.edu.ufcspa.interop.ambulatorio.fhir.mapper;

import br.edu.ufcspa.interop.ambulatorio.dominio.Paciente;
import br.edu.ufcspa.interop.ambulatorio.dominio.SinalVital;
import br.edu.ufcspa.interop.ambulatorio.dominio.SituacaoSinalVital;
import br.edu.ufcspa.interop.ambulatorio.dominio.TipoSinalVital;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.Codigos;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.ConversorDatas;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.ReferenciaPaciente;
import br.edu.ufcspa.interop.ambulatorio.fhir.util.Sistemas;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Quantity;
import org.springframework.stereotype.Component;

/**
 * Traduz SinalVital (modelo do sistema) para Observation (FHIR R4) e vice-versa.
 *
 * Especificacao do recurso: https://hl7.org/fhir/R4/observation.html
 * Perfil de sinais vitais: https://hl7.org/fhir/R4/observation-vitalsigns.html
 * Regras de mapeamento: enunciado do trabalho, regras de Observation.
 */
@Component
public class ObservationMapper {

    private final ReferenciaPaciente referenciaPaciente;

    ObservationMapper(ReferenciaPaciente referenciaPaciente) {
        this.referenciaPaciente = referenciaPaciente;
    }

    /** Usado no GET (read): SinalVital do banco para Observation. */
    public Observation paraFhir(SinalVital sinalVital) {
        Observation observation = new Observation();

        if (sinalVital.getId() != null) {
            observation.setId(sinalVital.getId().toString());
        }

        if (sinalVital.getSituacao() != null) {
            switch (sinalVital.getSituacao()) {
                case PRELIMINAR:
                    observation.setStatus(ObservationStatus.PRELIMINARY);
                    break;
                case FINAL:
                    observation.setStatus(ObservationStatus.FINAL);
                    break;
                case CORRIGIDO:
                    observation.setStatus(ObservationStatus.CORRECTED);
                    break;
                case CANCELADO:
                    observation.setStatus(ObservationStatus.CANCELLED);
                    break;
                case REGISTRADO_POR_ENGANO:
                    observation.setStatus(ObservationStatus.ENTEREDINERROR);
                    break;
            }
        }

        observation.addCategory(Codigos.conceito(
                Sistemas.OBSERVATION_CATEGORY, "vital-signs", "Vital Signs"));

        if (sinalVital.getTipo() != null) {
            observation.setCode(Codigos.conceito(Sistemas.LOINC,
                    codigoLoinc(sinalVital.getTipo()), sinalVital.getTipo().getDescricao()));
        }

        if (sinalVital.getPaciente() != null) {
            observation.setSubject(referenciaPaciente.para(sinalVital.getPaciente()));
        }

        if (sinalVital.getDataHoraMedicao() != null) {
            observation.setEffective(ConversorDatas.paraDateTimeType(sinalVital.getDataHoraMedicao()));
        }

        if (sinalVital.getValor() != null && sinalVital.getTipo() != null) {
            Quantity quantidade = new Quantity();
            quantidade.setValue(sinalVital.getValor());
            quantidade.setUnit(sinalVital.getUnidade());
            quantidade.setSystem(Sistemas.UCUM);
            quantidade.setCode(codigoUcum(sinalVital.getTipo()));
            observation.setValue(quantidade);
        }

        return observation;
    }

    /**
     * Usado no POST (create) e no PUT (update): copia o conteudo da Observation recebida para o SinalVital.
     * Se a Observation nao atender as regras do enunciado, recuse com o erro HTTP adequado.
     */
    public void paraDominio(Observation recurso, SinalVital destino) {
        if (!recurso.hasStatus()) {
            throw new UnprocessableEntityException("Status é obrigatório");
        }

        SituacaoSinalVital situacao;
        switch (recurso.getStatus()) {
            case PRELIMINARY:
                situacao = SituacaoSinalVital.PRELIMINAR;
                break;
            case FINAL:
                situacao = SituacaoSinalVital.FINAL;
                break;
            case CORRECTED:
                situacao = SituacaoSinalVital.CORRIGIDO;
                break;
            case CANCELLED:
                situacao = SituacaoSinalVital.CANCELADO;
                break;
            case ENTEREDINERROR:
                situacao = SituacaoSinalVital.REGISTRADO_POR_ENGANO;
                break;
            default:
                throw new UnprocessableEntityException("Status não suportado pelo sistema");
        }

        boolean temCategoria = false;
        for (CodeableConcept categoria : recurso.getCategory()) {
            if (Codigos.codigo(categoria, Sistemas.OBSERVATION_CATEGORY)
                    .filter("vital-signs"::equals).isPresent()) {
                temCategoria = true;
                break;
            }
        }

        if (!temCategoria) {
            throw new UnprocessableEntityException("Categoria vital-signs é obrigatória");
        }

        String loinc = Codigos.codigo(recurso.getCode(), Sistemas.LOINC)
                .orElseThrow(() -> new UnprocessableEntityException("Código LOINC é obrigatório"));

        TipoSinalVital tipo;
        switch (loinc) {
            case "8867-4":
                tipo = TipoSinalVital.FREQUENCIA_CARDIACA;
                break;
            case "9279-1":
                tipo = TipoSinalVital.FREQUENCIA_RESPIRATORIA;
                break;
            case "8310-5":
                tipo = TipoSinalVital.TEMPERATURA;
                break;
            case "29463-7":
                tipo = TipoSinalVital.PESO;
                break;
            case "8302-2":
                tipo = TipoSinalVital.ALTURA;
                break;
            case "2708-6":
                tipo = TipoSinalVital.SATURACAO_O2;
                break;
            default:
                throw new UnprocessableEntityException("Código LOINC não suportado pelo sistema");
        }

        if (!recurso.hasValueQuantity() || !recurso.getValueQuantity().hasValue()) {
            throw new UnprocessableEntityException("Valor numérico é obrigatório em valueQuantity");
        }

        Quantity quantidade = recurso.getValueQuantity();
        if (!Sistemas.UCUM.equals(quantidade.getSystem())
                || !codigoUcum(tipo).equals(quantidade.getCode())) {
            throw new UnprocessableEntityException("Unidade UCUM incompatível com o tipo de sinal vital");
        }

        if (quantidade.hasComparator()) {
            throw new UnprocessableEntityException("O sistema aceita somente valores exatos, sem comparador");
        }

        double valor = quantidade.getValue().doubleValue();
        if (!Double.isFinite(valor)) {
            throw new UnprocessableEntityException("Valor fora do intervalo suportado pelo sistema");
        }

        if (!recurso.hasEffectiveDateTimeType()) {
            throw new UnprocessableEntityException("Data da medição é obrigatória em effectiveDateTime");
        }

        LocalDateTime dataHoraMedicao;
        try {
            dataHoraMedicao = ConversorDatas.paraLocalDateTime(recurso.getEffectiveDateTimeType());
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new UnprocessableEntityException("Data da medição inválida ou incompleta");
        }

        if (dataHoraMedicao == null) {
            throw new UnprocessableEntityException("Data da medição é obrigatória");
        }

        Paciente paciente = referenciaPaciente.resolver(recurso.getSubject(), "Observation.subject");

        destino.setPaciente(paciente);
        destino.setTipo(tipo);
        destino.setValor(valor);
        destino.setUnidade(tipo.getUnidadeLocal());
        destino.setDataHoraMedicao(dataHoraMedicao);
        destino.setSituacao(situacao);
    }

    private String codigoLoinc(TipoSinalVital tipo) {
        switch (tipo) {
            case FREQUENCIA_CARDIACA:
                return "8867-4";
            case FREQUENCIA_RESPIRATORIA:
                return "9279-1";
            case TEMPERATURA:
                return "8310-5";
            case PESO:
                return "29463-7";
            case ALTURA:
                return "8302-2";
            case SATURACAO_O2:
                return "2708-6";
            default:
                throw new UnprocessableEntityException("Tipo de sinal vital não suportado");
        }
    }

    private String codigoUcum(TipoSinalVital tipo) {
        switch (tipo) {
            case FREQUENCIA_CARDIACA:
            case FREQUENCIA_RESPIRATORIA:
                return "/min";
            case TEMPERATURA:
                return "Cel";
            case PESO:
                return "kg";
            case ALTURA:
                return "cm";
            case SATURACAO_O2:
                return "%";
            default:
                throw new UnprocessableEntityException("Tipo de sinal vital não suportado");
        }
    }
}
