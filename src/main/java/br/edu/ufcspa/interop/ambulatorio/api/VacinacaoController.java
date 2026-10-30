package br.edu.ufcspa.interop.ambulatorio.api;

import br.edu.ufcspa.interop.ambulatorio.api.dto.VacinacaoDTO;
import br.edu.ufcspa.interop.ambulatorio.servico.VacinacaoServico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** API proprietaria do ambulatorio (nao e FHIR). */
@RestController
@RequestMapping("/api/vacinacoes")
public class VacinacaoController {

    private final VacinacaoServico servico;

    public VacinacaoController(VacinacaoServico servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<VacinacaoDTO> listar(@RequestParam(required = false) Long pacienteId) {
        var lista = pacienteId == null ? servico.listar() : servico.listarPorPaciente(pacienteId);
        return lista.stream().map(VacinacaoDTO::de).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacinacaoDTO> buscar(@PathVariable Long id) {
        return servico.buscarPorId(id)
                .map(VacinacaoDTO::de)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
