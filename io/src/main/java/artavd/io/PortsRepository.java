package artavd.io;

import java.util.List;
import java.util.Optional;

public interface PortsRepository {

    List<Port> getRegisterPorts();

    Optional<Port> getPort(String name);

    Port getOrCreatePort(String name);
}
