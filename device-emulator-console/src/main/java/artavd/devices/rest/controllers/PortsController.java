package artavd.devices.rest.controllers;

import artavd.devices.rest.response.PortDto;
import artavd.devices.utils.CommonUtils;
import artavd.io.Port;
import artavd.io.PortState;
import artavd.io.PortsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ports")
public class PortsController {

    private static final Logger logger = LoggerFactory.getLogger(PortsController.class);

    @Value("${rest.timeouts.connectPort:${rest.timeouts.default}}")
    private int connectPortTimeout;

    @Value("${rest.timeouts.disconnectPort:${rest.timeouts.default}}")
    private int disconnectPortTimeout;

    private PortsRepository repository;

    public PortsController(PortsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    public List<PortDto> getPorts() {
        return repository.getRegisterPorts().stream()
                .map(PortDto::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    public PortDto getPort(@PathVariable String name) {
        Port requestedPort = ControllerUtils.getPort(name, repository);
        return PortDto.from(requestedPort);
    }

    @PutMapping("/{name}/add")
    public PortDto addPort(@PathVariable String name) {
        Port requestedPort = repository.getOrCreatePort(name);
        return PortDto.from(requestedPort);
    }

    @PostMapping("/{name}/connect")
    public PortDto connectPort(@PathVariable String name) {
        Port requestedPort = ControllerUtils.getPort(name, repository);
        doConnect(requestedPort);
        return PortDto.from(requestedPort);
    }

    @PostMapping("/{name}/disconnect")
    public PortDto disconnectPort(@PathVariable String name) {
        Port requestedPort = ControllerUtils.getPort(name, repository);
        doDisconnect(requestedPort);
        return PortDto.from(requestedPort);
    }

    private void doConnect(Port port) {
        port.connect();
        waitForTerminalState(port, connectPortTimeout, "is not connected");
    }

    private void doDisconnect(Port port) {
        port.disconnect();
        waitForTerminalState(port, disconnectPortTimeout, "is not disconnected");
    }

    private void waitForTerminalState(Port port, long timeoutInMilliseconds, String message) {
        try {
            CommonUtils.waitFor(
                    port.getStateFeed(), PortState::isTerminal,
                    timeoutInMilliseconds, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            logger.warn(
                    "Port [ {} ] {} after timeout {} milliseconds",
                    port.getName(), message, timeoutInMilliseconds);
        }
    }
}
