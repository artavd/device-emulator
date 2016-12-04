package artavd.io;

import java.util.Collection;
import java.util.Map;

public final class PortsFactoryImpl implements PortsFactory {

    private final Collection<PortCreator> portCreators;

    public PortsFactoryImpl(Collection<PortCreator> portCreators) {
        this.portCreators = portCreators;
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
}
