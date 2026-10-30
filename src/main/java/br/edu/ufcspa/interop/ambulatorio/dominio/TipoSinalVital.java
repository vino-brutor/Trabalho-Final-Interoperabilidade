package br.edu.ufcspa.interop.ambulatorio.dominio;

/**
 * Tipos de sinal vital medidos na triagem, com a unidade que o sistema
 * usa internamente (unidade local, nao necessariamente padronizada).
 */
public enum TipoSinalVital {

    FREQUENCIA_CARDIACA("Frequência cardíaca", "bpm"),
    FREQUENCIA_RESPIRATORIA("Frequência respiratória", "irpm"),
    TEMPERATURA("Temperatura corporal", "°C"),
    PESO("Peso", "kg"),
    ALTURA("Altura", "cm"),
    SATURACAO_O2("Saturação de oxigênio", "%");

    private final String descricao;
    private final String unidadeLocal;

    TipoSinalVital(String descricao, String unidadeLocal) {
        this.descricao = descricao;
        this.unidadeLocal = unidadeLocal;
    }

    public String getDescricao() { return descricao; }

    public String getUnidadeLocal() { return unidadeLocal; }
}
