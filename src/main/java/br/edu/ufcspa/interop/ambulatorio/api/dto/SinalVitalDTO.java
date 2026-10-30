package br.edu.ufcspa.interop.ambulatorio.api.dto;

import br.edu.ufcspa.interop.ambulatorio.dominio.SinalVital;

import java.time.LocalDateTime;

/** Formato proprietario usado pela API /api/sinais-vitais. */
public record SinalVitalDTO(
        Long id,
        Long pacienteId,
        String tipo,
        String descricao,
        Double valor,
        String unidade,
        LocalDateTime dataHoraMedicao,
        String situacao) {

    public static SinalVitalDTO de(SinalVital s) {
        return new SinalVitalDTO(
                s.getId(),
                s.getPaciente().getId(),
                s.getTipo().name(),
                s.getTipo().getDescricao(),
                s.getValor(),
                s.getUnidade(),
                s.getDataHoraMedicao(),
                s.getSituacao().name());
    }
}
