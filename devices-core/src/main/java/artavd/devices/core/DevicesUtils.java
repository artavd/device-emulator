package artavd.devices.core;

import artavd.devices.controllers.DeviceController;

public class DevicesUtils {

    public static DeviceController getDeviceController(DevicesRepository repository, String name) {
        return repository.getController(name)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Device Controller for '%s' device is not registered!", name)));
    }
}
