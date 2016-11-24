package artavd.io;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static artavd.io.PortParameters.NAME;

public interface PortsRepository {

    List<Port> getRegisterPorts();

    Optional<Port> getPort(String name);

    Port getOrCreatePort(Map<String, String> parameters);

    default Port getOrCreatePort(String name) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(NAME, name);
        return getOrCreatePort(parameters);
    }
}
