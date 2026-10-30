package br.edu.ufcspa.interop.ambulatorio.api.dto;

import br.edu.ufcspa.interop.ambulatorio.dominio.Paciente;
import br.edu.ufcspa.interop.ambulatorio.dominio.Sexo;

import java.time.LocalDate;

/** Formato proprietario usado pela API /api/pacientes. */
public record PacienteDTO(
        Long id,
        String nomeCompleto,
        String cpf,
        String cns,
        LocalDate dataNascimento,
        Sexo sexo,
        String telefone,
        String email,
        boolean ativo) {

    public static PacienteDTO de(Paciente p) {
        return new PacienteDTO(
                p.getId(),
                p.getNome() + " " + p.getSobrenome(),
                p.getCpf(),
                p.getCns(),
                p.getDataNascimento(),
                p.getSexo(),
                p.getTelefone(),
                p.getEmail(),
                p.isAtivo());
    }
}
