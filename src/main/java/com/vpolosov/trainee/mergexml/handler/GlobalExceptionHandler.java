package com.vpolosov.trainee.mergexml.handler;

import com.vpolosov.trainee.mergexml.handler.dto.ErrorResponseDTO;
import com.vpolosov.trainee.mergexml.handler.exception.MoreFiveHundredKbException;
import com.vpolosov.trainee.mergexml.handler.exception.NotExactlyOneXsdFileException;
import com.vpolosov.trainee.mergexml.handler.exception.NotExactlyTenFilesException;
import com.vpolosov.trainee.mergexml.handler.exception.ValidationException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений.
 *
 * @author Ali Takushinov
 * @author Maksim Litvinenko
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Перехватчик исключений для преобразования в статус код 400.
     *
     * @param e одно из указанных исключений.
     * @return сообщение об ошибке с типом {@code Bad Request}.
     */
    @ExceptionHandler({
        NotExactlyOneXsdFileException.class,
        NotExactlyTenFilesException.class,
        ValidationException.class,
        MoreFiveHundredKbException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(responseCode = "400", description = "Ошибки валидации документов.",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ErrorResponseDTO handleFileException(ValidationException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponseDTO("Bad Request", e.messages());
    }


    /**
     * Перехватчик исключений для преобразования в статус код 500.
     *
     * @param e исключение наследуемое от класса {@link Exception}.
     * @return сообщение об ошибке с типом {@code Server Error}.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера, ошибка чтения/записи файлов.",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ErrorResponseDTO handleInternalServerError(Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResponseDTO("Server Error", e.getMessage());
    }
}
