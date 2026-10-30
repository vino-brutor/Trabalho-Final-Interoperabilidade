package br.edu.ufcspa.interop.ambulatorio.servico;

import br.edu.ufcspa.interop.ambulatorio.dominio.Paciente;
import br.edu.ufcspa.interop.ambulatorio.repositorio.PacienteRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteServico {

    private final PacienteRepositorio repositorio;

    public PacienteServico(PacienteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<Paciente> listar() {
        return repositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorCpf(String cpf) {
        return repositorio.findByCpf(cpf);
    }

    @Transactional
    public Paciente salvar(Paciente paciente) {
        return repositorio.save(paciente);
    }
}
