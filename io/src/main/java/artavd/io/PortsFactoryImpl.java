package artavd.io;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@Component
public final class PortsFactoryImpl implements PortsFactory {

    private final List<PortCreator> portCreators = new ArrayList<>();

    public PortsFactoryImpl() {
        portCreators.add(new PortCreator() {
            private static final String DESCRIPTOR = "CONSOLE";

            @Override
            public boolean match(Map<String, String> parameters) {
                return matchParameter(parameters, PortsFactory.NAME, x -> x.toUpperCase().startsWith(DESCRIPTOR)) ||
                        matchParameter(parameters, PortsFactory.TYPE, x -> x.toUpperCase().equals(DESCRIPTOR));
            }

            @Override
            public Port create(Map<String, String> parameters) {
                return new ConsolePort(parameters.get(PortsFactory.NAME));
            }

            private boolean matchParameter(Map<String, String> parameters, String name, Predicate<String> predicate) {
                String value = parameters.get(name);
                return value != null && predicate.test(value);
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
