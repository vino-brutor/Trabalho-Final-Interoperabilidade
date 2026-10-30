package br.edu.ufcspa.interop.ambulatorio.fhir.mapper;

import br.edu.ufcspa.interop.ambulatorio.dominio.SinalVital;
import org.hl7.fhir.r4.model.Observation;
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

    /** Usado no GET (read): SinalVital do banco para Observation. */
    public Observation paraFhir(SinalVital sinalVital) {
        // TODO
        throw new UnsupportedOperationException("TODO: ObservationMapper.paraFhir");
    }

    /**
     * Usado no POST (create) e no PUT (update): copia o conteudo da Observation recebida para o SinalVital.
     * Se a Observation nao atender as regras do enunciado, recuse com o erro HTTP adequado.
     */
    public void paraDominio(Observation recurso, SinalVital destino) {
        // TODO
        throw new UnsupportedOperationException("TODO: ObservationMapper.paraDominio");
    }
}
