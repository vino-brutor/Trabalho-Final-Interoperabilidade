package br.edu.ufcspa.interop.ambulatorio.repositorio;

import br.edu.ufcspa.interop.ambulatorio.dominio.SinalVital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SinalVitalRepositorio extends JpaRepository<SinalVital, Long> {

    List<SinalVital> findByPacienteId(Long pacienteId);
}
