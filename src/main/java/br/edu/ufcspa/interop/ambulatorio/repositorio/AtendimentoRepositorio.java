package br.edu.ufcspa.interop.ambulatorio.repositorio;

import br.edu.ufcspa.interop.ambulatorio.dominio.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtendimentoRepositorio extends JpaRepository<Atendimento, Long> {

    List<Atendimento> findByPacienteId(Long pacienteId);
}
