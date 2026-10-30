package br.edu.ufcspa.interop.ambulatorio.api;

import br.edu.ufcspa.interop.ambulatorio.api.dto.SinalVitalDTO;
import br.edu.ufcspa.interop.ambulatorio.servico.SinalVitalServico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** API proprietaria do ambulatorio (nao e FHIR). */
@RestController
@RequestMapping("/api/sinais-vitais")
public class SinalVitalController {

    private final SinalVitalServico servico;

    public SinalVitalController(SinalVitalServico servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<SinalVitalDTO> listar(@RequestParam(required = false) Long pacienteId) {
        var lista = pacienteId == null ? servico.listar() : servico.listarPorPaciente(pacienteId);
        return lista.stream().map(SinalVitalDTO::de).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SinalVitalDTO> buscar(@PathVariable Long id) {
        return servico.buscarPorId(id)
                .map(SinalVitalDTO::de)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
