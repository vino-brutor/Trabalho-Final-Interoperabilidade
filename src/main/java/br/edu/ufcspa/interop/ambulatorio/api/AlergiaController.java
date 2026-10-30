package br.edu.ufcspa.interop.ambulatorio.api;

import br.edu.ufcspa.interop.ambulatorio.api.dto.AlergiaDTO;
import br.edu.ufcspa.interop.ambulatorio.servico.AlergiaServico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** API proprietaria do ambulatorio (nao e FHIR). */
@RestController
@RequestMapping("/api/alergias")
public class AlergiaController {

    private final AlergiaServico servico;

    public AlergiaController(AlergiaServico servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<AlergiaDTO> listar(@RequestParam(required = false) Long pacienteId) {
        var lista = pacienteId == null ? servico.listar() : servico.listarPorPaciente(pacienteId);
        return lista.stream().map(AlergiaDTO::de).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlergiaDTO> buscar(@PathVariable Long id) {
        return servico.buscarPorId(id)
                .map(AlergiaDTO::de)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
