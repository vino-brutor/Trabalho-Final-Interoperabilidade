package br.edu.ufcspa.interop.ambulatorio.repositorio;

import br.edu.ufcspa.interop.ambulatorio.dominio.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepositorio extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByCpf(String cpf);
}
