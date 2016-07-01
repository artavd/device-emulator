package artavd.io;

import java.util.List;
import java.util.Optional;

public interface PortsRepository {

    List<Port> getPorts();

    Optional<Port> getPort(String name);

    boolean registerPort(Port port);
}
