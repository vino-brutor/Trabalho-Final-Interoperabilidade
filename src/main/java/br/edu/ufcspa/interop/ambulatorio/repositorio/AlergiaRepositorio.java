package br.edu.ufcspa.interop.ambulatorio.repositorio;

import br.edu.ufcspa.interop.ambulatorio.dominio.Alergia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlergiaRepositorio extends JpaRepository<Alergia, Long> {

    List<Alergia> findByPacienteId(Long pacienteId);
}
