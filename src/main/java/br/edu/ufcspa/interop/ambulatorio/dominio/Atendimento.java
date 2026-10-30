package br.edu.ufcspa.interop.ambulatorio.dominio;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Atendimento (consulta, teleconsulta, visita domiciliar...) prestado ao paciente.
 */
@Entity
@Table(name = "atendimento")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAtendimento tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoAtendimento situacao;

    private LocalDateTime inicio;

    /** Vazio enquanto o atendimento nao terminou. */
    private LocalDateTime fim;

    /** Motivo do atendimento em texto livre. Ex.: "Dor de garganta há 3 dias". */
    private String motivo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public TipoAtendimento getTipo() { return tipo; }
    public void setTipo(TipoAtendimento tipo) { this.tipo = tipo; }

    public SituacaoAtendimento getSituacao() { return situacao; }
    public void setSituacao(SituacaoAtendimento situacao) { this.situacao = situacao; }

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }

    public LocalDateTime getFim() { return fim; }
    public void setFim(LocalDateTime fim) { this.fim = fim; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
