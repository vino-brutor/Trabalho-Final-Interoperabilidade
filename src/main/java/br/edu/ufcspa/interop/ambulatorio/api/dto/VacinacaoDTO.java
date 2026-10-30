package br.edu.ufcspa.interop.ambulatorio.api.dto;

import br.edu.ufcspa.interop.ambulatorio.dominio.SituacaoVacinacao;
import br.edu.ufcspa.interop.ambulatorio.dominio.Vacinacao;

import java.time.LocalDate;

/** Formato proprietario usado pela API /api/vacinacoes. */
public record VacinacaoDTO(
        Long id,
        Long pacienteId,
        String codigoVacina,
        String nomeVacina,
        LocalDate dataAplicacao,
        String lote,
        Integer dose,
        SituacaoVacinacao situacao) {

    public static VacinacaoDTO de(Vacinacao v) {
        return new VacinacaoDTO(
                v.getId(),
                v.getPaciente().getId(),
                v.getCodigoVacina(),
                v.getNomeVacina(),
                v.getDataAplicacao(),
                v.getLote(),
                v.getDose(),
                v.getSituacao());
    }
}
