package br.edu.ufcspa.interop.ambulatorio.fhir.util;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import org.hl7.fhir.r4.model.BaseDateTimeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

/**
 * Conversoes entre as datas do Java (java.time) e os tipos de data do FHIR.
 * Ja esta pronto: use a vontade nos seus mappers.
 *
 * O sistema do ambulatorio grava datas e horas sem fuso. Consideramos sempre
 * o horario de Brasilia (America/Sao_Paulo).
 */
public final class ConversorDatas {

    public static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private ConversorDatas() {
    }

    /** LocalDate para o tipo FHIR date (ex.: 1985-03-12). */
    public static DateType paraDateType(LocalDate data) {
        return data == null ? null : new DateType(data.toString());
    }

    /** LocalDate para o tipo FHIR dateTime com precisao de dia (ex.: 2023-04-10). */
    public static DateTimeType paraDateTimeType(LocalDate data) {
        return data == null ? null : new DateTimeType(data.toString());
    }

    /** LocalDateTime para o tipo FHIR dateTime com fuso (ex.: 2026-10-01T09:15:00-03:00). */
    public static DateTimeType paraDateTimeType(LocalDateTime dataHora) {
        if (dataHora == null) {
            return null;
        }
        String texto = dataHora.atZone(FUSO).toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return new DateTimeType(texto);
    }

    /**
     * Tipo FHIR date ou dateTime para LocalDate.
     *
     * @throws IllegalArgumentException se a data vier incompleta (so ano, ou ano e mes)
     */
    public static LocalDate paraLocalDate(BaseDateTimeType data) {
        if (data == null || !data.hasValue()) {
            return null;
        }
        if (data.getPrecision().ordinal() < TemporalPrecisionEnum.DAY.ordinal()) {
            throw new IllegalArgumentException("Data incompleta: " + data.getValueAsString());
        }
        return LocalDate.parse(data.getValueAsString().substring(0, 10));
    }

    /**
     * Tipo FHIR dateTime para LocalDateTime no horario de Brasilia.
     * Uma data sem hora (ex.: 2026-10-01) vira meia-noite daquele dia.
     *
     * @throws IllegalArgumentException se a data vier incompleta (so ano, ou ano e mes)
     */
    public static LocalDateTime paraLocalDateTime(BaseDateTimeType data) {
        if (data == null || !data.hasValue()) {
            return null;
        }
        if (data.getPrecision().ordinal() <= TemporalPrecisionEnum.DAY.ordinal()) {
            return paraLocalDate(data).atStartOfDay();
        }
        return LocalDateTime.ofInstant(data.getValue().toInstant(), FUSO);
    }
}
