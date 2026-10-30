package br.edu.ufcspa.interop.ambulatorio.dominio;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Registro de vacina aplicada (ou nao aplicada) na sala de vacinas do ambulatorio.
 */
@Entity
@Table(name = "vacinacao")
public class Vacinacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    /**
     * Codigo da vacina na tabela CVX (Vaccine Administered), mantida pelo CDC.
     * O sistema guarda so o codigo, sem dizer de qual tabela ele veio.
     */
    @Column(nullable = false, length = 4)
    private String codigoVacina;

    /** Nome da vacina como aparece na carteira. Ex.: "Febre amarela". */
    @Column(nullable = false)
    private String nomeVacina;

    @Column(nullable = false)
    private LocalDate dataAplicacao;

    private String lote;

    /** Numero da dose no esquema (1, 2, 3...). Opcional. */
    private Integer dose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoVacinacao situacao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public String getCodigoVacina() { return codigoVacina; }
    public void setCodigoVacina(String codigoVacina) { this.codigoVacina = codigoVacina; }

    public String getNomeVacina() { return nomeVacina; }
    public void setNomeVacina(String nomeVacina) { this.nomeVacina = nomeVacina; }

    public LocalDate getDataAplicacao() { return dataAplicacao; }
    public void setDataAplicacao(LocalDate dataAplicacao) { this.dataAplicacao = dataAplicacao; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }

    public Integer getDose() { return dose; }
    public void setDose(Integer dose) { this.dose = dose; }

    public SituacaoVacinacao getSituacao() { return situacao; }
    public void setSituacao(SituacaoVacinacao situacao) { this.situacao = situacao; }
}
