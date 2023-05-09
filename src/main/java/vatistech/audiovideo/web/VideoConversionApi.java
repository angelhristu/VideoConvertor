package vatistech.audiovideo.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;

@Tag(name = "Video Conversion API", description = "API for managing video conversion")
public interface VideoConversionApi {

    @Operation(summary = "Convert video to specified format", description = "This API allows users to convert a video to the specified format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video successfully converted"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/convert")
    ResponseEntity<StreamingResponseBody> convertVideo(
            @Parameter(description = "Output format (e.g. 'mp4', 'avi', 'webm')", required = true,
                    schema = @Schema(type = "string", example = "mp4"))
            @RequestHeader("Accept") String format) throws IOException;
}
