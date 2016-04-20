package artavd.shells.demu;

import artavd.devices.DevicesConfiguration;
import artavd.spring.shell.EnableCommandLineShell;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("artavd.shells.demu")
@EnableCommandLineShell
@Import(DevicesConfiguration.class)
public class EmulatorShellApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmulatorShellApplication.class, args);
    }
}