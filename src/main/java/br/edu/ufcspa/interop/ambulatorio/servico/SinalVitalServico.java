package br.edu.ufcspa.interop.ambulatorio.servico;

import br.edu.ufcspa.interop.ambulatorio.dominio.SinalVital;
import br.edu.ufcspa.interop.ambulatorio.repositorio.SinalVitalRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SinalVitalServico {

    private final SinalVitalRepositorio repositorio;

    public SinalVitalServico(SinalVitalRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<SinalVital> listar() {
        return repositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<SinalVital> listarPorPaciente(Long pacienteId) {
        return repositorio.findByPacienteId(pacienteId);
    }

    @Transactional(readOnly = true)
    public Optional<SinalVital> buscarPorId(Long id) {
        return repositorio.findById(id);
    }

    @Transactional
    public SinalVital salvar(SinalVital sinalVital) {
        return repositorio.save(sinalVital);
    }
}
