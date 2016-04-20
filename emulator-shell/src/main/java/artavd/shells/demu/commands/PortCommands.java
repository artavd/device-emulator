package artavd.shells.demu.commands;

import artavd.devices.DeviceDispatcher;
import artavd.devices.io.Port;
import artavd.devices.io.PortState;
import artavd.devices.io.PortsRepository;
import artavd.devices.io.TcpServerPort;
import artavd.shells.demu.SmartListTableModel;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Logger;

@Component
public final class PortCommands implements CommandMarker {

    private static final Logger LOGGER = HandlerUtils.getLogger(PortCommands.class);

    @Autowired
    private PortsRepository ports;

    @Autowired
    private DeviceDispatcher dispatcher;

    @CliCommand(value = "port list", help = "Print list of available ports with information about each one.")
    public void getPortList() {
        LOGGER.info(new SmartListTableModel<>(ports.getPorts())
                .withHeaders("PORT", "STATE", "BOUND")
                .withAccessor(Port::getName)
                .withAccessor(port -> port.getCurrentState().getName())
                .withAccessor(port -> !dispatcher.getBoundsDevices(port).isEmpty())
                .withColorizer(1, port -> toColor(port.getCurrentState()))
                .render());
    }

    @CliCommand(value = "port add", help = "Add specified port to repository. Usage: port add <port_name>")
    public void addPort(
            @CliOption(key = "name", mandatory = true, help = "The name of port to add to repository.") String name) {
        boolean added = ports.registerPort(new TcpServerPort(name));
        if (added) {
            LOGGER.info(String.format("Port [ %s ] added to repository.", name));
        }
        else {
            LOGGER.warning(String.format(
                    "Port [ %s ] is already registered. " +
                    "To see all available ports, use 'port list' command", name));
        }
    }

    @CliCommand(value = "port open", help = "Open specified port if it's not opened yet. Usage: port open <port_name>")
    public void openPort(
            @CliOption(key = "name", mandatory = true, help = "The name of port to open.") String name) {
        Optional<Port> port = ports.getPort(name);
        if (!port.isPresent()) {
            LOGGER.warning(String.format(
                    "Port [ %s ] is not registered. " +
                    "To see all available ports, use 'port list' command", name));
            return;
        }

        port.get().open();
        LOGGER.info(String.format("Port [ %s ] is opening...", name));
    }

    @CliCommand(value = "port close", help = "Close specified port. Usage: port close <port_name>")
    public void closePort(
            @CliOption(key = { "", "name" }, mandatory = true, help = "The name of port to close.") String name) {
        Optional<Port> port = ports.getPort(name);
        if (!port.isPresent()) {
            LOGGER.warning(String.format(
                    "Port [ %s ] is not registered. " +
                    "To see all available ports, use 'port list' command", name));
            return;
        }

        port.get().close();
        LOGGER.info(String.format("Port [ %s ] is closing...", name));
    }

    // TODO:
    // port status

    private static Ansi.Color toColor(PortState state) {
        if (state.equals(PortState.CLOSED) || state.isError()) {
            return Ansi.Color.RED;
        }

        if (state.equals(PortState.OPENED)) {
            return Ansi.Color.GREEN;
        }

        if (state.equals(PortState.CONNECTING) || state.equals(PortState.RECONNECTING)) {
            return Ansi.Color.YELLOW;
        }

        return null;
    }
}
