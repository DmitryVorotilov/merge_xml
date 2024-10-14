package com.vpolosov.trainee.mergexml.controller;

import com.vpolosov.trainee.mergexml.config.ConfigProperties;
import com.vpolosov.trainee.mergexml.repository.HistoryRepository;
import com.vpolosov.trainee.mergexml.utils.FileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(MergeControllerIntegrationTests.TestTimeConfig.class)
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Тестирование контроллера MergeController")
public class MergeControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private Clock clock;

    @Autowired
    private DateTimeFormatter totalTimeFormat;

    private static Path getFixturesPath() {
        return Paths.get("src/test/resources/test_fixtures/sourceXml/InvalidCurrCode")
            .toAbsolutePath().normalize();
    }

    @TestConfiguration
    static class TestTimeConfig {
        @Bean
        public Clock clock(DateTimeFormatter localDateFormat) {
            var fixedSameDate = LocalDate.parse("22.02.2024", localDateFormat)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);
            return Clock.fixed(fixedSameDate, ZoneOffset.UTC);
        }
    }

    @AfterEach
    void cleanUp() {
        historyRepository.deleteAll();
    }

    @DisplayName("Тест контроллера patchXml() когда переданы файлы с валидным кодом валюты")
    @Test
    void patchXml_whenValidCurrCode_thenReturnOk() throws Exception {
        String path = Paths.get("src/test/resources/test_fixtures/Ok")
            .toAbsolutePath().normalize().toString();

        mockMvc
            .perform(
                post("/xml")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content(path)
            )
            .andExpect(status().isOk());

        var fileName = fileUtil.fileNameWithTime(configProperties.getFileName(), clock, totalTimeFormat);
        var pathToTotalFile = Path.of(path, fileName);
        if (Files.exists(pathToTotalFile)) {
            Files.delete(pathToTotalFile);
        }
    }

    @DisplayName("Тест контроллера patchXml() когда переданы файлы с невалидным кодом валюты")
    @Test
    void patchXml_whenInvalidCurrCode_thenReturnInvalidCurrencyCodeValueException() throws Exception {
        String path = getFixturesPath().toString();

        MockHttpServletResponse responsePost = mockMvc
            .perform(
                post("/xml")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content(path)
            )
            .andReturn()
            .getResponse();

        assertThat(responsePost.getStatus()).isEqualTo(400);
        responsePost.setCharacterEncoding("UTF-8");
        assertThat(responsePost.getContentAsString()).contains("Допустимое значение кода валюты 810");
    }
}
