package artavd.devices.core;

import artavd.devices.controllers.DeviceController;

import java.util.List;

public interface DevicesRepository {

    List<Device> getDevices();

    Device getDevice(String name);

    DeviceController getController(Device device);

    default DeviceController getController(String name) {
        return getController(getDevice(name));
    }
}
