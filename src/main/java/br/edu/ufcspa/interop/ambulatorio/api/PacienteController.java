package br.edu.ufcspa.interop.ambulatorio.api;

import br.edu.ufcspa.interop.ambulatorio.api.dto.PacienteDTO;
import br.edu.ufcspa.interop.ambulatorio.servico.PacienteServico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** API proprietaria do ambulatorio (nao e FHIR). */
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteServico servico;

    public PacienteController(PacienteServico servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<PacienteDTO> listar() {
        return servico.listar().stream().map(PacienteDTO::de).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> buscar(@PathVariable Long id) {
        return servico.buscarPorId(id)
                .map(PacienteDTO::de)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
