package br.edu.ufcspa.interop.ambulatorio.dominio;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Paciente cadastrado no ambulatorio (modelo proprietario do sistema).
 */
@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Prenome(s). Ex.: "Maria Clara". */
    @Column(nullable = false)
    private String nome;

    /** Sobrenome(s). Ex.: "Souza Pereira". */
    @Column(nullable = false)
    private String sobrenome;

    /** CPF somente com digitos (11 caracteres). */
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    /** Cartao Nacional de Saude somente com digitos (15 caracteres). Opcional. */
    @Column(length = 15)
    private String cns;

    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    private String telefone;

    private String email;

    @Column(nullable = false)
    private boolean ativo = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCns() { return cns; }
    public void setCns(String cns) { this.cns = cns; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public Sexo getSexo() { return sexo; }
    public void setSexo(Sexo sexo) { this.sexo = sexo; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
