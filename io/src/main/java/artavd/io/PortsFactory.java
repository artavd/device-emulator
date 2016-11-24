package artavd.io;

import java.util.Map;

public interface PortsFactory {

    Port createPort(Map<String, String> parameters);
}
