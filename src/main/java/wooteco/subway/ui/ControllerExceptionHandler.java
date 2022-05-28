package wooteco.subway.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wooteco.subway.dto.exceptionMessage.ExceptionMessageDto;
import wooteco.subway.dto.exceptionMessage.ExceptionMessagesDto;
import wooteco.subway.exception.EmptyResultException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new ExceptionMessageDto("이미 존재하는 데이터 입니다.").getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleEmptyResultException(EmptyResultException exception) {
        logger.error(exception.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    public ResponseEntity<String> handleSqlException(DuplicateKeyException exception) {
        logger.error(exception.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionMessageDto("이미 존재하는 데이터 입니다.").getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<List<String>> handleValidateException(MethodArgumentNotValidException exception) {
        logger.error(exception.getMessage());
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        List<String> exceptionMessages = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(
                new ExceptionMessagesDto(exceptionMessages).getMessages()
        );
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception exception) {
        logger.error(exception.getMessage());
        return ResponseEntity.internalServerError().body(new ExceptionMessageDto("서버 에러가 발생했습니다.").getMessage());
    }
}
