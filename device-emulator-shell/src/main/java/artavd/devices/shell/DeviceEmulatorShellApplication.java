package artavd.devices.shell;

import artavd.devices.EmulatorCoreConfiguration;
import artavd.spring.shell.EnableCommandLineShell;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("artavd.devices.shell")
@Import(EmulatorCoreConfiguration.class)
@EnableCommandLineShell
public class DeviceEmulatorShellApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder()
                .sources(DeviceEmulatorShellApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .registerShutdownHook(true)
                .build();

        application.run(args);
    }
}