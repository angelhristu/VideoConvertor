package vatistech.audiovideo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import vatistech.audiovideo.exception.VideoConversionException;
import vatistech.audiovideo.util.ProcessBuilderWrapper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoConversionService {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd_hh-mm-ss-a";

    private final ProcessBuilderWrapper processBuilderWrapper;
    private final ResourceLoader resourceLoader;

    private final ConcurrentHashMap<String, byte[]> videoCache = new ConcurrentHashMap<>();

    public ResponseEntity<StreamingResponseBody> convertVideo(String format) throws IOException {

        // Generate output file name
        String outputFileName = createOutputFile(format);
        String videoPath = processBuilderWrapper.getVideoPath();
        Resource resource = resourceLoader.getResource("classpath:/" + videoPath);

        // Check if the video is cached
        byte[] cachedVideoBytes = videoCache.get(format);
        StreamingResponseBody responseBody;

        // If the video is cached, return it
        if (cachedVideoBytes != null) {
            log.info("Returning cached video for format {}", format);
            responseBody = getCachedStreamingResponse(cachedVideoBytes);

        // If the video is already in the requested format, return it
        } else if (sameHostedVideoFormat(format, videoPath)) {
            log.info("The requested format {} is the same as the hosted video format", format);
            var bytes = resource.getInputStream().readAllBytes();
            videoCache.put(format, bytes);
            return new ResponseEntity<>(outputStream -> outputStream.write(bytes), generateHeaders(outputFileName), HttpStatus.OK);

        // If the video is not cached or the same hosted format, convert it
        } else {
            log.info("Start converting video to {}", format);
            var process = startProcess(outputFileName, resource);
            responseBody = getStreamingResponse(outputFileName, process, format);
        }

        var headers = generateHeaders(outputFileName);
        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
    }

    private Process startProcess(String outputFileName, Resource resource) throws IOException {

        // Start ffmpeg process
        processBuilderWrapper.command("ffmpeg", "-i", "-", outputFileName);
        Process process = processBuilderWrapper.start();

        // Pipe the input stream to the process
        try (InputStream inputStream = resource.getInputStream();
             OutputStream outputStream = process.getOutputStream()) {
            StreamUtils.copy(inputStream, outputStream);
        }
        return process;
    }

    private StreamingResponseBody getStreamingResponse(String outputFileName, Process process, String format) {
        return outputStream -> {

            var conversionErrorCode = getConversionErrorCode(process);
            if (conversionErrorCode != 0) {
                InputStream errorStream = process.getErrorStream();
                String errorMessage = new BufferedReader(new InputStreamReader(errorStream))
                        .lines().collect(Collectors.joining("\n"));
                log.error("FFmpeg error: {}", errorMessage);
                throw new VideoConversionException("Video conversion failed " + errorMessage);
            } else {

                try (InputStream inputStream = new FileInputStream(outputFileName)) {
                    byte[] outputBytes = StreamUtils.copyToByteArray(inputStream);
                    outputStream.write(outputBytes);
                    videoCache.put(format, outputBytes);
                    log.info("Video conversion completed successfully");

                } finally {
                    process.destroy();
                    Path path = Paths.get(outputFileName);
                    Files.deleteIfExists(path);
                }

            }
        };
    }

    private StreamingResponseBody getCachedStreamingResponse(byte[] videoBytes) {
        return outputStream -> outputStream.write(videoBytes);
    }

    private String createOutputFile(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        String timestamp = LocalDateTime.now().format(formatter);
        return timestamp + "." + format;
    }

    private int getConversionErrorCode(Process process) {
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            throw new VideoConversionException("Video conversion was interrupted");
        }
    }

    private HttpHeaders generateHeaders(String outputFileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + outputFileName + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return headers;
    }

    private boolean sameHostedVideoFormat(String format, String videoPath) {
        String[] split = videoPath.split("\\.");
        String extension = split[split.length - 1];
        return extension.equals(format);
    }


}
