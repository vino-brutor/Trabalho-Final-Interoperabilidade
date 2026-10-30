package br.edu.ufcspa.interop.ambulatorio.dominio;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Medicao de sinal vital feita na triagem do ambulatorio.
 */
@Entity
@Table(name = "sinal_vital")
public class SinalVital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSinalVital tipo;

    @Column(nullable = false)
    private Double valor;

    /** Unidade como o sistema sempre gravou (ex.: "bpm", "°C"). Veja {@link TipoSinalVital}. */
    @Column(nullable = false)
    private String unidade;

    @Column(nullable = false)
    private LocalDateTime dataHoraMedicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoSinalVital situacao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public TipoSinalVital getTipo() { return tipo; }
    public void setTipo(TipoSinalVital tipo) { this.tipo = tipo; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public LocalDateTime getDataHoraMedicao() { return dataHoraMedicao; }
    public void setDataHoraMedicao(LocalDateTime dataHoraMedicao) { this.dataHoraMedicao = dataHoraMedicao; }

    public SituacaoSinalVital getSituacao() { return situacao; }
    public void setSituacao(SituacaoSinalVital situacao) { this.situacao = situacao; }
}
