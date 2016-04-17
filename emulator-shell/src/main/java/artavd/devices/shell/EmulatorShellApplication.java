package artavd.devices.shell;

import artavd.spring.shell.EnableCommandLineShell;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("artavd.devices.shell")
@EnableCommandLineShell
public class EmulatorShellApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmulatorShellApplication.class, args);
    }
}