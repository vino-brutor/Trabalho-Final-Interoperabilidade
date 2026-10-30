package br.edu.ufcspa.interop.ambulatorio.api.dto;

import br.edu.ufcspa.interop.ambulatorio.dominio.*;

import java.time.LocalDate;

/** Formato proprietario usado pela API /api/alergias. */
public record AlergiaDTO(
        Long id,
        Long pacienteId,
        String substancia,
        TipoAlergia tipo,
        CategoriaAlergia categoria,
        CriticidadeAlergia criticidade,
        SituacaoClinicaAlergia situacaoClinica,
        VerificacaoAlergia verificacao,
        String reacao,
        LocalDate dataRegistro) {

    public static AlergiaDTO de(Alergia a) {
        return new AlergiaDTO(
                a.getId(),
                a.getPaciente().getId(),
                a.getSubstancia(),
                a.getTipo(),
                a.getCategoria(),
                a.getCriticidade(),
                a.getSituacaoClinica(),
                a.getVerificacao(),
                a.getReacao(),
                a.getDataRegistro());
    }
}
