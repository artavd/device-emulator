package artavd.io;

import java.util.Map;

public interface PortsFactory {

    Port createPort(Map<String, String> parameters);

    interface PortCreator {
        boolean match(String name);
        Port create(Map<String, String> parameters);
    }
}
