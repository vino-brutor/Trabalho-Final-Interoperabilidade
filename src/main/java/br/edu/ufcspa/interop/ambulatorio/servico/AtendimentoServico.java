package br.edu.ufcspa.interop.ambulatorio.servico;

import br.edu.ufcspa.interop.ambulatorio.dominio.Atendimento;
import br.edu.ufcspa.interop.ambulatorio.repositorio.AtendimentoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AtendimentoServico {

    private final AtendimentoRepositorio repositorio;

    public AtendimentoServico(AtendimentoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<Atendimento> listar() {
        return repositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<Atendimento> listarPorPaciente(Long pacienteId) {
        return repositorio.findByPacienteId(pacienteId);
    }

    @Transactional(readOnly = true)
    public Optional<Atendimento> buscarPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public Atendimento salvar(Atendimento atendimento) {
        return repositorio.save(atendimento);
    }
}
