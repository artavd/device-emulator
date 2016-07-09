package artavd.devices.core;

import artavd.devices.controllers.DeviceController;

public interface DevicesRepository {

    Device getDevice(String name);

    DeviceController getController(Device device);

    default DeviceController getController(String name) {
        return getController(getDevice(name));
    }
}
