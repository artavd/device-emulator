package artavd.devices.rest.response;

import artavd.devices.controllers.DeviceController;
import artavd.io.Port;

import java.util.List;

public class DispatchDto {

    public final String device;
    public final String[] ports;

    private DispatchDto(DeviceController device, List<Port> ports) {
        this.device = device.getDevice().getName();
        this.ports = ports.stream().map(Port::getName).toArray(String[]::new);
    }

    public static DispatchDto from(DeviceController device, List<Port> ports) {
        return new DispatchDto(device, ports);
    }
}
