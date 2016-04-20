package artavd.devices.shell.commands;

import artavd.devices.io.Port;
import artavd.devices.io.PortsRepository;
import artavd.devices.io.TcpServerPort;
import artavd.devices.shell.SmartListTableModel;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.SimpleParser;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.shell.support.util.AnsiEscapeCode;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public final class PortCommands implements CommandMarker {

    private static final Logger LOGGER = HandlerUtils.getLogger(PortCommands.class);

    @Autowired
    private PortsRepository ports;

    @CliCommand(value = "port list")
    public void getPortList() {
        LOGGER.info(new SmartListTableModel<>(ports.getPorts())
                .withHeaders("PORT", "STATE", "BOUND")
                .withAccessor(Port::getName)
                .withAccessor(port -> port.getCurrentState().getName())
                .withAccessor(port -> true)
                .withColorizer(1, port -> port.getCurrentState().isError() ? Ansi.Color.RED : Ansi.Color.DEFAULT)
                .render());
    }

    @CliCommand(value = "port add")
    public String addPort(
            @CliOption(key = "name", mandatory = true) String name) {

        ports.registerPort(new TcpServerPort(name));
        return String.format("Port [ %s ] is registered", name);
    }
}
