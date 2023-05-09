package vatistech.audiovideo.web;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import vatistech.audiovideo.service.VideoConversionService;

import java.io.IOException;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VideoConversionController implements VideoConversionApi {

    private final VideoConversionService videoConversionService;

    @GetMapping("/convert")
    @RateLimiter(name = "convertVideo")
    public ResponseEntity<StreamingResponseBody> convertVideo(@RequestHeader("Accept") String format) throws IOException {
        return videoConversionService.convertVideo(format);
    }

}

