package com.vpolosov.trainee.mergexml.handler.filter;

import com.vpolosov.trainee.mergexml.model.ValidationFileHistory;
import com.vpolosov.trainee.mergexml.model.ValidationFileHistory_;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * Фильтр для сущности {@link ValidationFileHistory}
 *
 * @param fileNameStarts префикс имени файла, по которому будет проходить фильтрация.
 * @param isSuccess      статус, по которому будет проходить фильтрация.
 * @param validationDate дата, по которой будет проходить фильтрация.
 * @author Kergshi
 */
public record ValidationFileHistoryFilter(
        String fileNameStarts,
        Boolean isSuccess,
        @PastOrPresent
        LocalDateTime validationDate
) {
    /**
     * Формирует спецификацию для поиска записей в истории валидации файлов,
     * применяя несколько критериев фильтрации.
     * <p>
     * Спецификация будет включать следующие условия:
     * <ul>
     *     <li>Поиск по префиксу имени файла, если указан (используется метод {@code fileNameStartsSpec()}).</li>
     *     <li>Фильтр по статусу успешности валидации (используется метод {@code isSuccessSpec()}).</li>
     *     <li>Фильтр по дате валидации (используется метод {@code validationDateSpec()}).</li>
     * </ul>
     * Все критерии объединяются логической операцией {@code AND}.
     *
     * @return Спецификация для поиска записей в базе данных, удовлетворяющая указанным критериям.
     * Если ни одно условие не задано, возвращается спецификация без фильтров.
     */
    public Specification<ValidationFileHistory> getSpecification() {
        return Specification.where(fileNameStartsSpec())
                .and(isSuccessSpec())
                .and(validationDateSpec());
    }

    /**
     * Создает спецификацию для поиска историй валидации файлов,
     * у которых имя файла начинается с указанного префикса.
     * <p>
     * Если строка {@code fileNameStarts} пуста или равна {@code null}, возвращает {@code null},
     * что означает отсутствие данного критерия в запросе.
     *
     * @return Спецификация для поиска по префиксу имени файла, либо {@code null},
     * если строка {@code fileNameStarts} пуста.
     */
    private Specification<ValidationFileHistory> fileNameStartsSpec() {
        return ((root, query, cb) -> StringUtils.hasText(fileNameStarts)
                ? cb.like(cb.lower(root.get(ValidationFileHistory_.FILE_NAME)), fileNameStarts.toLowerCase() + "%")
                : null);
    }

    /**
     * Создает спецификацию для фильтрации записей по полю успешности валидации.
     * <p>
     * Если значение {@code isSuccess} не является {@code null}, спецификация будет фильтровать
     * записи, у которых поле {@code isSuccess} совпадает с заданным значением.
     * Если значение {@code isSuccess} равно {@code null}, возвращается {@code null},
     * что означает отсутствие данного критерия в запросе.
     *
     * @return Спецификация для фильтрации по успешности валидации, либо {@code null}, если критерий не задан.
     */
    private Specification<ValidationFileHistory> isSuccessSpec() {
        return ((root, query, cb) -> isSuccess != null
                ? cb.equal(root.get(ValidationFileHistory_.IS_SUCCESS), isSuccess)
                : null);
    }

    /**
     * Создает спецификацию для фильтрации записей по дате валидации.
     * <p>
     * Если значение {@code validationDate} не является {@code null}, спецификация будет фильтровать
     * записи, у которых дата валидации меньше или равна указанной дате.
     * Если значение {@code validationDate} равно {@code null}, возвращается {@code null},
     * что означает отсутствие данного критерия в запросе.
     *
     * @return Спецификация для фильтрации по дате валидации, либо {@code null}, если критерий не задан.
     */
    private Specification<ValidationFileHistory> validationDateSpec() {
        return ((root, query, cb) -> validationDate != null
                ? cb.lessThanOrEqualTo(root.get(ValidationFileHistory_.VALIDATION_DATE), validationDate)
                : null);
    }
}
