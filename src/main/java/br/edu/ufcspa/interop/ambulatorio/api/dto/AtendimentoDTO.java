package br.edu.ufcspa.interop.ambulatorio.api.dto;

import br.edu.ufcspa.interop.ambulatorio.dominio.Atendimento;
import br.edu.ufcspa.interop.ambulatorio.dominio.SituacaoAtendimento;
import br.edu.ufcspa.interop.ambulatorio.dominio.TipoAtendimento;

import java.time.LocalDateTime;

/** Formato proprietario usado pela API /api/atendimentos. */
public record AtendimentoDTO(
        Long id,
        Long pacienteId,
        TipoAtendimento tipo,
        SituacaoAtendimento situacao,
        LocalDateTime inicio,
        LocalDateTime fim,
        String motivo) {

    public static AtendimentoDTO de(Atendimento a) {
        return new AtendimentoDTO(
                a.getId(),
                a.getPaciente().getId(),
                a.getTipo(),
                a.getSituacao(),
                a.getInicio(),
                a.getFim(),
                a.getMotivo());
    }
}
