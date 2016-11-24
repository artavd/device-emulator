package artavd.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PortsRepositoryImpl implements PortsRepository {

    private static final Logger logger = LoggerFactory.getLogger(PortsRepositoryImpl.class);

    private final List<Port> registerPorts = new ArrayList<>();

    @Resource
    private PortsFactory portsFactory;

    public List<Port> getRegisterPorts() {
        return registerPorts;
    }

    @Override
    public Optional<Port> getPort(String name) {
        return getRegisterPorts().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();
    }

    @Override
    public Port getOrCreatePort(Map<String, String> parameters) {
        String name = parameters.get(PortParameters.NAME);
        return getPort(name).orElseGet(() -> {
            Port newPort = portsFactory.createPort(parameters);
            registerPorts.add(newPort);
            return newPort;
        });
    }

    @PreDestroy
    private void onDestroy() {
        logger.debug("Ports repository closing...");
        registerPorts.forEach(Port::disconnect);
    }
}
