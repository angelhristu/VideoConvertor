package vatistech.audiovideo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import vatistech.audiovideo.util.ProcessBuilderWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VideoConversionServiceTest {

    @InjectMocks
    private VideoConversionService videoConversionService;

    @Mock
    private ProcessBuilderWrapper processBuilderWrapper;

    @Mock
    private Process process;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource resource;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(processBuilderWrapper.command(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(processBuilderWrapper);
        when(processBuilderWrapper.start()).thenReturn(process);
        when(resourceLoader.getResource(any(String.class))).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("dummy content".getBytes()));
        when(processBuilderWrapper.getVideoPath()).thenReturn("test-video-path");
        when(process.getOutputStream()).thenReturn(new ByteArrayOutputStream());
    }

    @Test
    public void testConvertVideo() throws IOException {
        // Given
        String format = "mp4";

        // When
        ResponseEntity<StreamingResponseBody> response = videoConversionService.convertVideo(format);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION));
        assertNotNull(response.getHeaders().get(HttpHeaders.CONTENT_TYPE));
    }
}

