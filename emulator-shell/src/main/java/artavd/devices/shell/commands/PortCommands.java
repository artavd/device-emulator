package artavd.devices.shell.commands;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

@Component
final class PortCommands implements CommandMarker {

    @CliCommand(value = "ports")
    public String ports() {
        return "hello from 'ports' command";
    }
}
