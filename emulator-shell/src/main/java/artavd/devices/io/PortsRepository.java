package artavd.devices.io;

import java.util.List;

public interface PortsRepository {

    List<Port> getPorts();

    boolean registerPort(Port port);
}
