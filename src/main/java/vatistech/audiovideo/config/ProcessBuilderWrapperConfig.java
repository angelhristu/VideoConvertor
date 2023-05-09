package vatistech.audiovideo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vatistech.audiovideo.util.ProcessBuilderWrapper;

@Configuration
public class ProcessBuilderWrapperConfig {

    @Value("${converted-video.path}")
    public String videoPath;

    @Bean
    public ProcessBuilderWrapper processBuilder() {
        return new ProcessBuilderWrapper(new ProcessBuilder(), videoPath);
    }

}
