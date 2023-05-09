package vatistech.audiovideo.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ErrorDto(String message, HttpStatus status) {
}
