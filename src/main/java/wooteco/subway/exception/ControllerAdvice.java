package wooteco.subway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wooteco.subway.dto.ErrorResponse;

@RestControllerAdvice
public class ControllerAdvice {

    private final Logger logger;

    public ControllerAdvice() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @ExceptionHandler(SubwayException.class)
    public ResponseEntity<ErrorResponse> subwayExceptionHandler(SubwayException e) {
        logger.info(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundExceptionHandler(NotFoundException e) {
        logger.info(e.getMessage(), e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().body(new ErrorResponse("[ERROR] 예상치 못한 에러가 발생했습니다."));
    }
}