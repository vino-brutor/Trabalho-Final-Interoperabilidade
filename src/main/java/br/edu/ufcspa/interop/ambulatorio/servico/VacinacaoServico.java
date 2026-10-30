package br.edu.ufcspa.interop.ambulatorio.servico;

import br.edu.ufcspa.interop.ambulatorio.dominio.Vacinacao;
import br.edu.ufcspa.interop.ambulatorio.repositorio.VacinacaoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VacinacaoServico {

    private final VacinacaoRepositorio repositorio;

    public VacinacaoServico(VacinacaoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<Vacinacao> listar() {
        return repositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<Vacinacao> listarPorPaciente(Long pacienteId) {
        return repositorio.findByPacienteId(pacienteId);
    }

    @Transactional(readOnly = true)
    public Optional<Vacinacao> buscarPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public Vacinacao salvar(Vacinacao vacinacao) {
        return repositorio.save(vacinacao);
    }
}
