package artavd.devices.rest.response;

import artavd.io.Port;
import artavd.io.PortState;

public class PortDto {
    public final String name;
    public final String type;
    public final String parameters;
    public final String state;

    private PortDto(Port port) {
        this.name = port.getName();
        this.type = port.getType();
        this.parameters = port.getParameters();

        PortState state = port.getCurrentState();
        StringBuilder stateBuilder = new StringBuilder();
        stateBuilder.append(state.getName());
        if (state.getDescription() != null) {
            stateBuilder.append(" - ").append(state.getDescription());
        }

        this.state = stateBuilder.toString();

    }

    public static PortDto from(Port port) {
        return new PortDto(port);
    }
}
