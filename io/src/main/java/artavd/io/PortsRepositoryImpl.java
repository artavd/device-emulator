package artavd.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PortsRepositoryImpl implements PortsRepository {

    private static final Logger logger = LoggerFactory.getLogger(PortsRepositoryImpl.class);

    private final List<Port> registerPorts = new ArrayList<>();

    @Autowired
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
    public Port getOrCreatePort(String name) {
        return getPort(name).orElseGet(() -> {
            Port newPort = portsFactory.createPort(name);
            registerPorts.add(newPort);
            return newPort;
        });
    }

    @PreDestroy
    private void onDestroy() {
        logger.info("Ports repository closing...");
        registerPorts.forEach(Port::disconnect);
    }
}
