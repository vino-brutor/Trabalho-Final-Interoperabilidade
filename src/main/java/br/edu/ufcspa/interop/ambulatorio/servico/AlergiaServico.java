package br.edu.ufcspa.interop.ambulatorio.servico;

import br.edu.ufcspa.interop.ambulatorio.dominio.Alergia;
import br.edu.ufcspa.interop.ambulatorio.repositorio.AlergiaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AlergiaServico {

    private final AlergiaRepositorio repositorio;

    public AlergiaServico(AlergiaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<Alergia> listar() {
        return repositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<Alergia> listarPorPaciente(Long pacienteId) {
        return repositorio.findByPacienteId(pacienteId);
    }

    @Transactional(readOnly = true)
    public Optional<Alergia> buscarPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public Alergia salvar(Alergia alergia) {
        return repositorio.save(alergia);
    }
}
