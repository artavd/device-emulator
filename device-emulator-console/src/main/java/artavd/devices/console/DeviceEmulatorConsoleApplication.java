package artavd.devices.console;

import artavd.devices.EmulatorCoreConfiguration;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@Import(EmulatorCoreConfiguration.class)
public class DeviceEmulatorConsoleApplication {

    private static final Logger logger = LoggerFactory.getLogger(DeviceEmulatorConsoleApplication.class);
    private static Options options;

    public static void main(String[] args) {
        options = new Options();
        boolean isArgumentParsed = options.tryParseArguments(args);
        if (!isArgumentParsed) {
            logger.error(options.getUsage());
            System.exit(1);
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

    @Bean
    public static Options options() {
        return options;
    }

    @Bean
    public static ApplicationRunner runner() {
        return new ApplicationRunner();
    }

    @Bean(name = "emulator")
    public ExecutorService emulatorExecutorService() {
        ThreadFactory emulatorThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("emulator-thread-%d")
                .daemon(true)
                .build();
        return Executors.newCachedThreadPool(emulatorThreadFactory);
    }

    @Bean(name = "ui")
    public ExecutorService uiExecutorService() {
        ThreadFactory uiThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("ui-thread")
                .build();
        return Executors.newSingleThreadExecutor(uiThreadFactory);
    }
}
