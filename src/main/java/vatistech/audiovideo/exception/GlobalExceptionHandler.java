package vatistech.audiovideo.exception;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(VideoConversionException.class)
    public ResponseEntity<ErrorDto> handleVideoConversionException(VideoConversionException e) {
        log.error("Application error in: [" + e.getClass().getName() + "]", e);
        var error = ErrorDto.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ErrorDto> handleRequestNotPermittedException(RequestNotPermitted e) {
        log.error("Application error in: [" + e.getClass().getName() + "]", e);
        var error = ErrorDto.builder()
                .message(e.getMessage())
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .build();
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }

}
