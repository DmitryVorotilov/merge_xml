package com.vpolosov.trainee.mergexml.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Конфигурация времени.
 *
 * @author Maksim Litvinenko
 */
@Configuration
public class TimeConfig {

    /**
     * Создаёт бин для формата даты.
     *
     * @return возвращает парсер для даты.
     */
    @Bean
    public DateTimeFormatter localDateFormat() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy");
    }

    /**
     * Создаёт бин для формата даты результирующего файла {@code Total}.
     *
     * @return возвращает парсер для результирующего файла.
     */
    @Bean
    public DateTimeFormatter totalTimeFormat() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss");
    }

    /**
     * Создаёт бин определяющий Часы, обеспечивающие доступ к текущему моменту,
     * дате и времени с использованием часового пояса.
     *
     * @return системные часы во временной зоне {@code UTC}.
     */
    @Bean
    public Clock clock() {
        var fixedSameDate = LocalDate.parse("22.02.2024", localDateFormat())
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC);
        return Clock.fixed(fixedSameDate, ZoneOffset.UTC);
    }
}
