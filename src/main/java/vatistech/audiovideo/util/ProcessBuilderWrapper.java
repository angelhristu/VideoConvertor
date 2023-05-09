package vatistech.audiovideo.util;

import lombok.Getter;

import java.io.IOException;

@Getter
public class ProcessBuilderWrapper {

    private final ProcessBuilder processBuilder;
    private final String videoPath;

    public ProcessBuilderWrapper(ProcessBuilder processBuilder, String videoPath) {
        this.processBuilder = processBuilder;
        this.videoPath = videoPath;
    }

    public ProcessBuilderWrapper command(String... commands) {
        processBuilder.command(commands);
        return this;
    }

    public Process start() throws IOException {
        return processBuilder.start();
    }

}
