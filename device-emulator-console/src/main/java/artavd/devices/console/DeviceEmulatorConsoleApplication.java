package artavd.devices.console;

import artavd.devices.EmulatorCoreConfiguration;
import artavd.devices.rest.RestApiAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DeviceEmulatorConsoleApplication {

    private static final Logger logger = LoggerFactory.getLogger(DeviceEmulatorConsoleApplication.class);
    private static final Options options = new Options();

    @Bean
    public static Options options() {
        return options;
    }

    public static void main(String[] args) {
        boolean areArgumentsParsed = options.tryParseArguments(args);
        if (!areArgumentsParsed) {
            logger.error(options.getUsage());
            System.exit(1);
        }

        logger.info("Device Emulator application is being started...");

        SpringApplicationBuilder applicationBuilder = new SpringApplicationBuilder();
        configureConsoleApplication(applicationBuilder);
        options.getRestApiPort().ifPresent(port -> configureRestApi(applicationBuilder, port));

        SpringApplication application = applicationBuilder.build();
        ConfigurableApplicationContext applicationContext = application.run(args);

        applicationContext.close();
    }

    private static void configureConsoleApplication(SpringApplicationBuilder applicationBuilder) {
        applicationBuilder
                .sources(DeviceEmulatorConsoleApplication.class, EmulatorCoreConfiguration.class)
                .bannerMode(Banner.Mode.OFF)
                .registerShutdownHook(true)
                .web(false);
    }

    private static void configureRestApi(SpringApplicationBuilder applicationBuilder, Integer port) {
        applicationBuilder
                .properties("server.port=" + port)
                .sources(RestApiAutoConfiguration.class)
                .web(true);
    }
}
