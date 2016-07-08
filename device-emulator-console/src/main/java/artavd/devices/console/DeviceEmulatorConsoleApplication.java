package artavd.devices.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeviceEmulatorConsoleApplication {

    private static final Logger logger = LoggerFactory.getLogger(DeviceEmulatorConsoleApplication.class);

    public static Options options;

    @Bean
    public static Options options() {
        return options;
    }

    public static void main(String[] args) {
        options = new Options();
        boolean isArgumentParsed = options.tryParseArguments(args);
        if (!isArgumentParsed) {
            logger.info(options.getUsage());
        }

        logger.info("Device Emulator application starting...");
        SpringApplication application = new SpringApplicationBuilder()
                .sources(DeviceEmulatorConsoleApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .registerShutdownHook(true)
                .build();

        application.run(args);
    }
}
