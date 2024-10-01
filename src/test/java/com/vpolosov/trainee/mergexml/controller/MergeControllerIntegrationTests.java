package com.vpolosov.trainee.mergexml.controller;

import com.vpolosov.trainee.mergexml.config.ConfigProperties;
import com.vpolosov.trainee.mergexml.model.ValidationFileHistory;
import com.vpolosov.trainee.mergexml.model.ValidationProcess;
import com.vpolosov.trainee.mergexml.repository.HistoryRepository;
import com.vpolosov.trainee.mergexml.repository.ValidationFileHistoryRepository;
import com.vpolosov.trainee.mergexml.repository.ValidationProcessRepository;
import com.vpolosov.trainee.mergexml.utils.ErrorTestFixturesParameters;
import com.vpolosov.trainee.mergexml.utils.FileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(MergeControllerIntegrationTests.TestTimeConfig.class)
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Тестирование контроллера MergeController")
class MergeControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private ValidationProcessRepository validationProcessRepository;

    @Autowired
    private ValidationFileHistoryRepository validationFileHistoryRepository;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private Clock clock;

    @Autowired
    private DateTimeFormatter totalTimeFormat;

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

    @BeforeEach
    void cleanUp() {
        historyRepository.deleteAll();
        validationFileHistoryRepository.deleteAll();
        validationProcessRepository.deleteAll();
    }

    @DisplayName("Тест контроллера patchXml() когда переданы валидные файлы")
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

        List<ValidationProcess> processes = validationProcessRepository.findAll();
        assertThat(processes).hasSize(1);

        ValidationProcess validationProcess = processes.get(0);
        assertThat(validationProcess.getDirRef()).isEqualTo(path);
        assertThat(validationProcess.getIsSuccess()).isTrue();
        assertThat(validationProcess.getTotalDocRef()).contains(fileName);
        assertThat(validationProcess.getValidationProcessDate()).isNotNull();

        List<ValidationFileHistory> histories = validationFileHistoryRepository.findAll();
        assertThat(histories).isNotEmpty();

        assertThat(histories).hasSize(10);

        for (ValidationFileHistory history : histories) {
            assertThat(history.getIsSuccess()).isTrue();
            assertThat(history.getFailureReason()).isNull();
            assertThat(history.getValidationProcess().getId()).isEqualTo(validationProcess.getId());
        }
    }

    @DisplayName("Тестирование ошибок при объединении файлов")
    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("com.vpolosov.trainee.mergexml.utils.ErrorTestFixturesParameters#errorTestParameters")
    void patchXml_errorTests(ErrorTestFixturesParameters params) throws Exception {
        String absolutePath = Paths.get(params.getPath()).toAbsolutePath().normalize().toString();

        MockHttpServletResponse responsePost = mockMvc
                .perform(
                        post("/xml")
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(absolutePath)
                )
                .andReturn()
                .getResponse();

        assertThat(responsePost.getStatus()).isEqualTo(params.getExpectedStatus());
        responsePost.setCharacterEncoding("UTF-8");
        assertThat(responsePost.getContentAsString()).contains(params.getExpectedErrorMessage());

        List<ValidationProcess> processes = validationProcessRepository.findAll();
        assertThat(processes).hasSize(1);

        ValidationProcess validationProcess = processes.get(0);
        assertThat(validationProcess.getDirRef()).isEqualTo(absolutePath);
        assertThat(validationProcess.getIsSuccess()).isFalse();
        assertThat(validationProcess.getTotalDocRef()).isNull();
        assertThat(validationProcess.getValidationProcessDate()).isNotNull();

        List<ValidationFileHistory> histories = validationFileHistoryRepository.findAll();
        assertThat(histories).hasSize(params.getExpectedHistoryCount());

        if (params.getExpectedHistoryCount() > 0 && params.isCheckFailureReason()) {
            ValidationFileHistory history = histories.get(histories.size() - 1); // Последний файл, где произошла ошибка
            assertThat(history.getIsSuccess()).isFalse();
            assertThat(history.getFailureReason()).contains(params.getExpectedFailureReason());
            assertThat(history.getValidationProcess().getId()).isEqualTo(validationProcess.getId());
        }
    }
}
