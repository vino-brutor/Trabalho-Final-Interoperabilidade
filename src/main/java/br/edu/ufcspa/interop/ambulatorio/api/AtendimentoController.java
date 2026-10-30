package br.edu.ufcspa.interop.ambulatorio.api;

import br.edu.ufcspa.interop.ambulatorio.api.dto.AtendimentoDTO;
import br.edu.ufcspa.interop.ambulatorio.servico.AtendimentoServico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** API proprietaria do ambulatorio (nao e FHIR). */
@RestController
@RequestMapping("/api/atendimentos")
public class AtendimentoController {

    private final AtendimentoServico servico;

    public AtendimentoController(AtendimentoServico servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<AtendimentoDTO> listar(@RequestParam(required = false) Long pacienteId) {
        var lista = pacienteId == null ? servico.listar() : servico.listarPorPaciente(pacienteId);
        return lista.stream().map(AtendimentoDTO::de).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoDTO> buscar(@PathVariable Long id) {
        return servico.buscarPorId(id)
                .map(AtendimentoDTO::de)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
