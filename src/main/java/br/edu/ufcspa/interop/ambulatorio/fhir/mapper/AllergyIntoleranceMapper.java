package br.edu.ufcspa.interop.ambulatorio.fhir.mapper;

import br.edu.ufcspa.interop.ambulatorio.dominio.Alergia;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.springframework.stereotype.Component;

/**
 * Traduz Alergia (modelo do sistema) para AllergyIntolerance (FHIR R4) e vice-versa.
 *
 * Especificacao do recurso: https://hl7.org/fhir/R4/allergyintolerance.html
 * Regras de mapeamento: enunciado do trabalho, regras de AllergyIntolerance.
 */
@Component
public class AllergyIntoleranceMapper {

    /** Usado no GET (read): Alergia do banco para AllergyIntolerance. */
    public AllergyIntolerance paraFhir(Alergia alergia) {
        // TODO
        throw new UnsupportedOperationException("TODO: AllergyIntoleranceMapper.paraFhir");
    }

    /**
     * Usado no POST (create) e no PUT (update): copia o conteudo do AllergyIntolerance recebido para a Alergia.
     * Se o recurso nao atender as regras do enunciado, recuse com o erro HTTP adequado.
     */
    public void paraDominio(AllergyIntolerance recurso, Alergia destino) {
        // TODO
        throw new UnsupportedOperationException("TODO: AllergyIntoleranceMapper.paraDominio");
    }
}
