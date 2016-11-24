package artavd.devices.rest.response;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.Device;

public class DeviceDto {

    public final String name;
    public final String state;

    private DeviceDto(Device device) {
        this.name = device.getName();
        this.state = device.getCurrentState().toString();
    }

    public static DeviceDto from(Device device) {
        return new DeviceDto(device);
    }

    public static DeviceDto from(DeviceController controller) {
        return from(controller.getDevice());
    }
}
