package br.edu.ufcspa.interop.ambulatorio.repositorio;

import br.edu.ufcspa.interop.ambulatorio.dominio.Vacinacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacinacaoRepositorio extends JpaRepository<Vacinacao, Long> {

    List<Vacinacao> findByPacienteId(Long pacienteId);
}
