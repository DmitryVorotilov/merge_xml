package com.vpolosov.trainee.mergexml.utils;

import lombok.Getter;

import java.util.stream.Stream;

/**
 * Класс для хранения параметров тестовых данных с ожидаемыми ошибками при тестировании контроллера MergeController.
 * Содержит информацию о тестовом сценарии.
 * @author Artyom Bogaichuk
 */
@Getter
public class ErrorTestFixturesParameters {
    /**
     * Имя теста для отображения.
     */
    String testName;

    /**
     * Путь к директории с тестовыми данными.
     */
    String path;

    /**
     * Ожидаемый HTTP статус ответа.
     */
    int expectedStatus;

    /**
     * Ожидаемое сообщение об ошибке в ответе.
     */
    String expectedErrorMessage;

    /**
     * Флаг, указывающий, необходимо ли проверять причину ошибки в истории валидации.
     */
    boolean checkFailureReason;

    /**
     * Ожидаемое количество записей в истории валидации файлов.
     */
    int expectedHistoryCount;

    /**
     * Ожидаемая причина ошибки в истории валидации.
     */
    String expectedFailureReason;

    public ErrorTestFixturesParameters(String testName, String path, int expectedStatus, String expectedErrorMessage,
                               boolean checkFailureReason, int expectedHistoryCount, String expectedFailureReason) {
        this.testName = testName;
        this.path = path;
        this.expectedStatus = expectedStatus;
        this.expectedErrorMessage = expectedErrorMessage;
        this.checkFailureReason = checkFailureReason;
        this.expectedHistoryCount = expectedHistoryCount;
        this.expectedFailureReason = expectedFailureReason;
    }

    @Override
    public String toString() {
        return testName;
    }

    public static Stream<ErrorTestFixturesParameters> errorTestParameters() {
        return Stream.of(
                new ErrorTestFixturesParameters(
                        "Файлы с невалидным кодом валюты",
                        "src/test/resources/test_fixtures/sourceXml/InvalidCurrCode",
                        400,
                        "Допустимое значение кода валюты 810",
                        true,
                        1,
                        "Допустимое значение кода валюты 810"
                ),
                new ErrorTestFixturesParameters(
                        "Нет XSD файла",
                        "src/test/resources/test_fixtures/sourceXml/NoXsd",
                        400,
                        "There are not exactly 1 xsd files",
                        false,
                        0,
                        null
                ),
                new ErrorTestFixturesParameters(
                        "Несколько XSD файлов",
                        "src/test/resources/test_fixtures/sourceXml/TwoXsd",
                        400,
                        "There are not exactly 1 xsd files",
                        false,
                        0,
                        null
                ),
                new ErrorTestFixturesParameters(
                        "Разные плательщики",
                        "src/test/resources/test_fixtures/sourceXml/DifferentPayer",
                        400,
                        "Данные файлы не могут быть объединены, т.к. обнаружены разные плательщики",
                        true,
                        2,
                        "Данные файлы не могут быть объединены, т.к. обнаружены разные плательщики"
                ),
                new ErrorTestFixturesParameters(
                        "Некорректный код бюджетной классификации",
                        "src/test/resources/test_fixtures/sourceXml/DependencyPayInfoIncorrect",
                        400,
                        "Код программы доходов бюджетов не найден в графе зависимостей",
                        true,
                        1,
                        "Код программы доходов бюджетов не найден в графе зависимостей"
                ),
                new ErrorTestFixturesParameters(
                        "Превышена максимальная сумма платежа",
                        "src/test/resources/test_fixtures/sourceXml/MaxAmount",
                        400,
                        "сумма платежа не соответствует максимальной",
                        true,
                        3,
                        "сумма платежа не соответствует максимальной"
                ),
                new ErrorTestFixturesParameters(
                        "Сумма платежа ниже минимальной",
                        "src/test/resources/test_fixtures/sourceXml/MinAmount",
                        400,
                        "сумма платежа не соответствует минимальной",
                        true,
                        3,
                        "сумма платежа не соответствует минимальной"
                ),
                new ErrorTestFixturesParameters(
                        "Некорректная сумма платежа",
                        "src/test/resources/test_fixtures/sourceXml/NotCorrectAmount",
                        400,
                        "не найдена сумма платежа или сумма некорректна",
                        true,
                        1,
                        "не найдена сумма платежа или сумма некорректна"
                ),
                new ErrorTestFixturesParameters(
                        "Превышено максимальное количество файлов",
                        "src/test/resources/test_fixtures/sourceXml/More10",
                        400,
                        "There are more than 10 xml files, or the files are missing",
                        false,
                        0,
                        null
                ),
                new ErrorTestFixturesParameters(
                        "Отсутствуют XML файлы",
                        "src/test/resources/test_fixtures/sourceXml/NoXml",
                        400,
                        "There are more than 10 xml files, or the files are missing",
                        false,
                        0,
                        null
                )
        );
    }
}