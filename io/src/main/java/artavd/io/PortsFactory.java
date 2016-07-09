package artavd.io;

import java.util.HashMap;
import java.util.Map;

public interface PortsFactory {

    String NAME = "PORT_NAME";
    String TYPE = "PORT_TYPE";
    String NUMBER = "PORT_NUMBER";

    Port createPort(Map<String, String> parameters);

    default Port createPort(String name) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(NAME, name);
        return createPort(parameters);
    }
}
