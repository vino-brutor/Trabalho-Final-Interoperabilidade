package br.edu.ufcspa.interop.ambulatorio.dominio;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Alergia ou intolerancia registrada no prontuario do paciente.
 */
@Entity
@Table(name = "alergia")
public class Alergia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    /** Substancia em texto livre, como digitada pelo profissional. Ex.: "Dipirona". */
    @Column(nullable = false)
    private String substancia;

    @Enumerated(EnumType.STRING)
    private TipoAlergia tipo;

    @Enumerated(EnumType.STRING)
    private CategoriaAlergia categoria;

    @Enumerated(EnumType.STRING)
    private CriticidadeAlergia criticidade;

    /** Pode ficar vazia quando o registro foi feito por engano (veja {@link VerificacaoAlergia}). */
    @Enumerated(EnumType.STRING)
    private SituacaoClinicaAlergia situacaoClinica;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificacaoAlergia verificacao;

    /** Reacao observada, em texto livre. Ex.: "Urticária". Opcional. */
    private String reacao;

    @Column(nullable = false)
    private LocalDate dataRegistro;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public String getSubstancia() { return substancia; }
    public void setSubstancia(String substancia) { this.substancia = substancia; }

    public TipoAlergia getTipo() { return tipo; }
    public void setTipo(TipoAlergia tipo) { this.tipo = tipo; }

    public CategoriaAlergia getCategoria() { return categoria; }
    public void setCategoria(CategoriaAlergia categoria) { this.categoria = categoria; }

    public CriticidadeAlergia getCriticidade() { return criticidade; }
    public void setCriticidade(CriticidadeAlergia criticidade) { this.criticidade = criticidade; }

    public SituacaoClinicaAlergia getSituacaoClinica() { return situacaoClinica; }
    public void setSituacaoClinica(SituacaoClinicaAlergia situacaoClinica) { this.situacaoClinica = situacaoClinica; }

    public VerificacaoAlergia getVerificacao() { return verificacao; }
    public void setVerificacao(VerificacaoAlergia verificacao) { this.verificacao = verificacao; }

    public String getReacao() { return reacao; }
    public void setReacao(String reacao) { this.reacao = reacao; }

    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }
}
