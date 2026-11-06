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
            observation.setStatus(statusFhir(sinalVital.getSituacao()));
        }

        // A categoria vital-signs e exigida pelo perfil mesmo sem estar no banco.
        observation.addCategory(Codigos.conceito(
                Sistemas.OBSERVATION_CATEGORY, "vital-signs", "Vital Signs"));

        // Tipo em code, com o LOINC do perfil Vital Signs.
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

        // Valor e unidade em valueQuantity, com codigo UCUM padronizado do tipo.
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
        // Recusar sem status.
        if (!recurso.hasStatus()) {
            throw new UnprocessableEntityException("status é obrigatório");
        }
        SituacaoSinalVital situacao = situacaoDominio(recurso.getStatus());

        // A categoria vital-signs deve estar declarada.
        boolean temVitalSigns = recurso.getCategory().stream()
                .anyMatch(c -> Codigos.codigo(c, Sistemas.OBSERVATION_CATEGORY)
                        .filter("vital-signs"::equals).isPresent());
        if (!temVitalSigns) {
            throw new UnprocessableEntityException("categoria vital-signs é obrigatória");
        }

        // Recusar com LOINC fora dos seis tipos do sistema.
        String loinc = Codigos.codigo(recurso.getCode(), Sistemas.LOINC)
                .orElseThrow(() -> new UnprocessableEntityException("código LOINC é obrigatório"));
        TipoSinalVital tipo = tipoPorLoinc(loinc);

        // Valor numerico obrigatorio.
        if (!recurso.hasValueQuantity() || !recurso.getValueQuantity().hasValue()) {
            throw new UnprocessableEntityException("valor numérico é obrigatório em valueQuantity");
        }
        Quantity quantidade = recurso.getValueQuantity();

        // Recusar com unidade incompativel com o tipo (system UCUM + code esperado).
        if (!Sistemas.UCUM.equals(quantidade.getSystem())
                || !codigoUcum(tipo).equals(quantidade.getCode())) {
            throw new UnprocessableEntityException("unidade UCUM incompatível com o tipo de sinal vital");
        }
        if (quantidade.hasComparator()) {
            throw new UnprocessableEntityException("o sistema aceita somente valores exatos, sem comparador");
        }
        double valor = quantidade.getValue().doubleValue();
        if (!Double.isFinite(valor)) {
            throw new UnprocessableEntityException("valor fora do intervalo suportado pelo sistema");
        }

        // Data e hora em effectiveDateTime.
        if (!recurso.hasEffectiveDateTimeType()) {
            throw new UnprocessableEntityException("data da medição é obrigatória em effectiveDateTime");
        }
        LocalDateTime dataHora;
        try {
            dataHora = ConversorDatas.paraLocalDateTime(recurso.getEffectiveDateTimeType());
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new UnprocessableEntityException("data da medição inválida ou incompleta");
        }
        if (dataHora == null) {
            throw new UnprocessableEntityException("data da medição é obrigatória");
        }

        Paciente paciente = referenciaPaciente.resolver(recurso.getSubject(), "Observation.subject");

        destino.setPaciente(paciente);
        destino.setTipo(tipo);
        destino.setValor(valor);
        destino.setUnidade(tipo.getUnidadeLocal());
        destino.setDataHoraMedicao(dataHora);
        destino.setSituacao(situacao);
    }

    private ObservationStatus statusFhir(SituacaoSinalVital situacao) {
        switch (situacao) {
            case PRELIMINAR:
                return ObservationStatus.PRELIMINARY;
            case FINAL:
                return ObservationStatus.FINAL;
            case CORRIGIDO:
                return ObservationStatus.CORRECTED;
            case CANCELADO:
                return ObservationStatus.CANCELLED;
            case REGISTRADO_POR_ENGANO:
                return ObservationStatus.ENTEREDINERROR;
            default:
                throw new UnprocessableEntityException("situação não suportada");
        }
    }

    private SituacaoSinalVital situacaoDominio(ObservationStatus status) {
        switch (status) {
            case PRELIMINARY:
                return SituacaoSinalVital.PRELIMINAR;
            case FINAL:
                return SituacaoSinalVital.FINAL;
            case CORRECTED:
                return SituacaoSinalVital.CORRIGIDO;
            case CANCELLED:
                return SituacaoSinalVital.CANCELADO;
            case ENTEREDINERROR:
                return SituacaoSinalVital.REGISTRADO_POR_ENGANO;
            default:
                throw new UnprocessableEntityException("status não suportado pelo sistema");
        }
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
                throw new UnprocessableEntityException("tipo de sinal vital não suportado");
        }
    }

    private TipoSinalVital tipoPorLoinc(String loinc) {
        switch (loinc) {
            case "8867-4":
                return TipoSinalVital.FREQUENCIA_CARDIACA;
            case "9279-1":
                return TipoSinalVital.FREQUENCIA_RESPIRATORIA;
            case "8310-5":
                return TipoSinalVital.TEMPERATURA;
            case "29463-7":
                return TipoSinalVital.PESO;
            case "8302-2":
                return TipoSinalVital.ALTURA;
            case "2708-6":
                return TipoSinalVital.SATURACAO_O2;
            default:
                throw new UnprocessableEntityException("código LOINC não suportado pelo sistema");
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
                throw new UnprocessableEntityException("tipo de sinal vital não suportado");
        }
    }
}
