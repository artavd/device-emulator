package artavd.devices.console.commands;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

@Component
public class PortCommands implements CommandMarker {

    @CliCommand(value = "ports")
    public String ports() {
        return "hello from 'ports' command";
    }
}
