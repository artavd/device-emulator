package artavd.devices.rest.controllers;

import artavd.devices.rest.response.PortDto;
import artavd.io.Port;
import artavd.io.PortState;
import artavd.io.PortsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.Future;
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

    @Autowired
    private PortsRepository repository;

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
    public PortDto connectPort(@PathVariable String name) throws Exception {
        Port requestedPort = ControllerUtils.getPort(name, repository);
        doConnect(requestedPort);
        return PortDto.from(requestedPort);
    }

    @PostMapping("/{name}/disconnect")
    public PortDto disconnectPort(@PathVariable String name) throws Exception {
        Port requestedPort = ControllerUtils.getPort(name, repository);
        doDisconnect(requestedPort);
        return PortDto.from(requestedPort);
    }

    private void doConnect(Port port) throws Exception {
        Future<PortState> state = port.connect();
        try {
            state.get(connectPortTimeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            logger.warn(
                    "Port [ {} ] is not connected after timeout {} seconds",
                    port.getName(), connectPortTimeout);
        }
    }

    private void doDisconnect(Port port) throws Exception {
        Future<PortState> state = port.disconnect();
        try {
            state.get(disconnectPortTimeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            logger.warn(
                    "Port [ {} ] is not disconnected after timeout {} seconds",
                    port.getName(), disconnectPortTimeout);
        }
    }
}
