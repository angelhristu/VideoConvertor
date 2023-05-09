package vatistech.audiovideo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AudiovideoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AudiovideoApplication.class, args);
	}

}
