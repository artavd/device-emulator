package artavd.io;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public final class PortsFactoryImpl implements PortsFactory {

    private final List<PortCreator> portCreators = new ArrayList<>();

    public PortsFactoryImpl() {
        portCreators.add(new PortCreator() {
            private static final String DESCRIPTOR = "CONSOLE";

            @Override
            public boolean match(Map<String, String> parameters) {
                return parameters.get(PortsFactory.NAME).toUpperCase().startsWith(DESCRIPTOR) ||
                        Objects.equals(DESCRIPTOR, parameters.get(PortsFactory.TYPE).toUpperCase());
            }

            @Override
            public Port create(Map<String, String> parameters) {
                return new ConsolePort(parameters.get(PortsFactory.NAME));
            }
        });
    }

    @Override
    public Port createPort(Map<String, String> parameters) {
        return portCreators.stream()
                .filter(pc -> pc.match(parameters))
                .findAny()
                .map(pc -> pc.create(parameters))
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Unknown port parameters: %s", parameters)));
    }

    private interface PortCreator {
        boolean match(Map<String, String> parameters);
        Port create(Map<String, String> parameters);
    }
}
