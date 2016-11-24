package artavd.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PortsFactoryImpl implements PortsFactory {

    private final List<PortCreator> portCreators = new ArrayList<>();

    public PortsFactoryImpl() {
        portCreators.add(new PortCreator() {
            private static final String DESCRIPTOR = "CONSOLE";

            @Override
            public boolean match(String name) {
                return name.toUpperCase().startsWith(DESCRIPTOR);
            }

            @Override
            public Port create(Map<String, String> parameters) {
                String name = parameters.get(PortParameters.NAME);
                String descriptor = name.toUpperCase().equals(DESCRIPTOR) ? null : name;
                return new ConsolePort(name, descriptor);
            }
        });
    }

    @Override
    public Port createPort(Map<String, String> parameters) {
        String name = parameters.get(PortParameters.NAME);
        if (name == null) {
            throw new IllegalArgumentException(String.format(
                    "Port parameters should contain name but was: %s", parameters));
        }

        return portCreators.stream()
                .filter(pc -> pc.match(name))
                .findAny()
                .map(pc -> pc.create(parameters))
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Unknown port parameters: %s", parameters)));
    }

    private interface PortCreator {
        boolean match(String name);
        Port create(Map<String, String> parameters);
    }
}
