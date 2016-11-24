package artavd.devices.rest.response;

import artavd.io.Port;

public class PortDto {
    public final String name;
    public final String type;
    public final String parameters;
    public final String state;

    private PortDto(Port port) {
        this.name = port.getName();
        this.type = port.getType();
        this.parameters = port.getParameters();
        this.state = port.getCurrentState().getName();
    }

    public static PortDto from(Port port) {
        return new PortDto(port);
    }
}
