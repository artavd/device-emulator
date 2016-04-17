package artavd.devices.console;

import artavd.spring.shell.EnableCommandLineShell;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableCommandLineShell
public class EmulatorShellApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmulatorShellApplication.class, args);
    }
}